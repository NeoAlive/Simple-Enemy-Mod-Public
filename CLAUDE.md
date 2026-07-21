# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An unofficial NeoForge 1.21.1 / Java 21 port of **Simple Enemy Mod** (original by NekoYuni, this port also credits NeoAlive), an addon for the **TaCZ** gun mod that adds armed NPC "units" (US/RU/PMC factions) with squad AI, a commander/order system, recruitable allies, and datapack-driven weapon loadouts. GPL-3.0-or-later. Not affiliated with the original author; see [README.md](README.md) for the attribution boilerplate.

The actual Gradle project root is this directory (`Simple-Enemy-Mod-Public/`), not the repo root.

## Build & run

Standard NeoForge ModDevGradle project, Java 21 toolchain, mod id `simpleenemymod`.

- `./gradlew build` — compile and build the jar
- `./gradlew runClient` — launch a dev client with the mod loaded
- `./gradlew runServer` — launch a dev dedicated server
- `./gradlew runData` — regenerate data/assets under `src/generated/resources` (this project's datagen output, wired via `ModDataGenerators`/`SimpleDataGenerators`)
- `./gradlew runGameTestServer` — run NeoForge game tests

There is no `src/test` and no unit test task — this is a Minecraft client/server mod; verification is done in-game via the run configs above (VS Code launch configs in `.vscode/launch.json` mirror these four).

Mod metadata (version, id, dependency ranges) lives in [gradle.properties](gradle.properties) and gets templated into `src/main/templates/META-INF/neoforge.mods.toml` at build time — edit the properties file, not a generated toml.

Hard dependency: **TaCZ** (pulled via CurseMaven in [build.gradle](build.gradle), pinned by curse file id — bump that id to update TaCZ). Optional runtime deps, each gated by a `Compat.LOADED` flag checked via `ModList.get().isLoaded(...)`: GeckoLib, Curios, Cloth Config. Never call into an optional dependency's API without checking its `LOADED` flag first — see `compat/*/…Compat.java`.

## Architecture

### Package layout (`net.nekoyuni.SimpleEnemyMod`)

- `entity/unit/` — the three concrete NPC entities: `USunitEntity`, `RUunitEntity` (both hostile-by-default "enemy" factions), `PmcUnitEntity` (recruitable/friendly, implements `ICommandableMob` for the order system). All extend `AbstractUnit` (a `Monster`), which owns shared state: hurt/death animation flags, tactical state (`SoldierState`, `StrategyType`, cover/flanking bookkeeping), and a movement lock so goals can suppress vanilla navigation. Each subclass implements the abstract hooks: `equipRandomGun()`, `setupRoleGoals()`, and the custom hurt/death/alert sound getters.
- `entity/ai/roles/` — `UnitRole` enum (`DEFAULT`, `SQUAD_LEADER`, `SQUAD_UNIT` and their `FRIENDLY_*` counterparts) each carries an `IRoleGoals` implementation that wires goal-selector goals onto an entity. `setupRoleGoals()` on the entity clears all goals and re-adds a fixed base set (target selectors, self-heal, faction-aware targeting) plus whatever the current `UnitRole`'s goals contribute. Role changes at runtime call `setupRoleGoals()` again to rebuild the goal set.
- `entity/ai/goals/` — the actual `Goal` implementations: ranged combat (`RangedGunAttackGoal`, `MoveToAttackRangeGoal`, `AiGunSpreadHelper`), squad behavior (`SmartSquadDoorGoal`, `SquadLeaderHandshakeGoal`, `SquadUnitHandshakeFollowGoal`, `SquadData` for NBT-backed leader/formation linkage), tactical positioning (`TacticalManagerGoal` — the high-level state machine driving retreat/flank/rush decisions, `TacticalManeuverGoal`, `SeekCoverGoal`, `PeekFromCoverGoal`, `TerrainScanner`, `FormationUtils`), and player-order handling (`CommanderOrderGoal`, `AttackSpecificTargetGoal`). `AIDifficultySettings` reads tunable timing/speed constants out of config for these goals.
- `entity/ai/orders/` — `OrderType` (e.g. `FREE_FIRE`, `CEASE_FIRE`, `MOVE_TO_POSITION`) and `ICommandableMob`, the contract the commander UI/packets use to issue orders to owned units.
- `entity/client/animation/` — a layered animation system independent of GeckoLib's own state machine: `LayeredAnimationManager` holds a priority-sorted list of `IAnimationLayer`s (idle/walk/hurt/death/action, see `layer/`) plus a separate list of `IProceduralLayer`s (aiming/head-tracking/weapon-pose adjustments applied on top, see `procedural/`). Each tick it picks the highest-priority layer whose `canPlay()` returns true and plays it; procedural layers are applied afterward from the model's `setupAnim`. `ModAnimationsDefinitions`/`UnitAnimationConfig` configure per-unit-type animation sets.
- `entity/equipment/` — per-faction weapon equippers (`UsWeaponEquipper`, `RuWeaponEquipper`, `PmcUnitWeaponEquipper`) that pick a `UnitLoadout` and hand the unit a TaCZ gun item (by `ResourceLocation` id) with attachments (scope/muzzle/grip) and ammo. Loadout pools are datapack-defined JSON under `src/main/resources/data/simpleenemymod/unit_loadouts/{pmc,ru,us}_units/loadouts.json` — add new guns/attachments there, not in Java, as long as the item exists in TaCZ.
- `compat/` — one subpackage per optional dependency (`cloth`, `curios`, `geckolib`), each with a top-level `*Compat.LOADED` boolean gate. `compat/cloth` additionally holds the in-game config screen (Cloth Config GUI) definitions mirroring `config/CommonConfig`.
- `config/` — NeoForge `ModConfigSpec`-based `ClientConfig`/`CommonConfig`, registered together via `ModConfigs.register` into `sem-client.toml`/`sem-common.toml`. `AIDifficultySettings` is a plain snapshot object built `fromConfig()` rather than goals reading `CommonConfig` directly each tick.
- `procedural/events/` — `DynamicEventManager` ticks once per `ServerLevel` (overworld only) and, per registered `DynamicEvent` (`PatrolEvent`, `CombatEvent`, `CaveExtractionEvent`), rolls a per-event probability that ramps up on failure and resets on success (state persisted via `EventProbabilityData`, a `SavedData`). This is the "random encounter" spawner layer, distinct from the vanilla-style `SpawnEventHandler`/`SpawnHelper` used for direct mob spawning and village garrisons (`VillageGarrisonHandler`).
- `network/` — packets under `network/packets/` registered centrally in `ModNetworking` (`RegisterPayloadHandlersEvent`); bump `PROTOCOL_VERSION` when changing a payload's wire format.
- `registry/` — all `DeferredRegister` holders (`ModItems`, `ModBlocks`, `ModEntities`, `ModSounds`, `ModMenuTypes`, `ModCommands`, `ModCreativeTabs`) plus `ModDataGenerators`, the entry point for `runData`. New items/blocks/entities/sounds get added here, then registered from `SimpleEnemyMod`'s constructor.
- `event/` — split by side/bus: `event/client` (camera, click, glow rendering, overlay, TaCZ input hooks), `event/common` (entity/command registration, drops, loadout manager, village garrison), `event/server` (bullet impact, friendly fire, suppression, sound, spawn). This is where to look first for "when X happens in-game, do Y" logic rather than adding ad-hoc listeners elsewhere.

### Cross-cutting notes

- **Faction identity is by Java type, not data.** "Is this unit friendly to that one" checks are `instanceof RUunitEntity` / `instanceof USunitEntity` gated by `CommonConfig.RU_UNITS_FRIENDLY`/`US_UNITS_FRIENDLY`, scattered across each entity's `setupRoleGoals()`. When adding a new faction or changing friend/foe logic, grep all three unit classes — the check is duplicated per-class, not centralized.
- **Squad/role state is synced via `SynchedEntityData`** on `PmcUnitEntity` (role, order, owner UUID, formation index, attack target) so client and server agree on commander UI state; `AbstractUnit`'s own role field is a plain server-side field used by the non-commandable enemy units instead.
- **TaCZ integration is by convention, not compile-time API surface for content**: guns/attachments are referenced as `ResourceLocation`s (`tacz:rpk`, etc.) from JSON, resolved at equip time — check `UnitLoadout`/`*WeaponEquipper` before assuming a new gun needs Java changes.
- `PmcUnitEntity.setupRoleGoals()` currently contains a large dead/commented-out block duplicating the same switch logic — don't treat the comment as authoritative; the live code above it is what runs.
