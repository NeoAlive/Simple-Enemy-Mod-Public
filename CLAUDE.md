# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An unofficial NeoForge 1.21.1 / Java 21 port of **Simple Enemy Mod** (original by NekoYuni, this port also credits NeoAlive), an addon for the **TaCZ** gun mod that adds armed NPC "units" (US/RU/PMC factions) with squad AI, a commander/order system, recruitable allies, and datapack-driven weapon loadouts. GPL-3.0-or-later. Not affiliated with the original author; see [README.md](README.md) for the attribution boilerplate.

The port tracks upstream SEM at **1.20.1-0.1.6-beta-hotfix** (version `1.21.1-0.1.6-hotfix.N`). It is deliberately kept structurally identical to upstream so that addons written against upstream, above all **tacz_sewv** (`/home/s4ndm4n/Progetti/tacz-simple-enemies-vehicular-warfare`, ~25 mixins on SEM classes), port with minimal changes. Read "Upstream sync model" and "sewv contract" below before refactoring anything.

The actual Gradle project root is this directory (`Simple-Enemy-Mod-Public/`), not the repo root.

## Build & run

Standard NeoForge ModDevGradle project, Java 21 toolchain, mod id `simpleenemymod`.

- `./gradlew build` — compile and build the jar (runs `check`, which includes `checkSewvContract`)
- `./gradlew runClient` — launch a dev client with the mod loaded
- `./gradlew runServer` — launch a dev dedicated server
- `./gradlew runData` — regenerate data/assets under `src/generated/resources` (this project's datagen output, wired via `ModDataGenerators`/`SimpleDataGenerators`)
- `./gradlew runGameTestServer` — run NeoForge game tests
- `./gradlew checkSewvContract` / `updateSewvContract` — verify / regenerate [contract/sewv-surface.txt](contract/sewv-surface.txt)

There is no `src/test` and no unit test task — this is a Minecraft client/server mod; verification is done in-game via the run configs above (VS Code launch configs in `.vscode/launch.json` mirror these four). `gametest/SemGameTests` holds server-side GameTests for the platform glue and the `api` hooks (template `data/simpleenemymod/structure/gametest_empty.nbt`); `./gradlew runGameTestServer` runs them and exits non-zero on failure. They only register in dev runs, where the `simpleenemymod` gametest namespace is enabled.

Mod metadata (version, id, dependency ranges) lives in [gradle.properties](gradle.properties) and gets templated into `src/main/templates/META-INF/neoforge.mods.toml` at build time — edit the properties file, not a generated toml.

Hard dependency: **TaCZ** (pulled via CurseMaven in [build.gradle](build.gradle), pinned by curse file id — bump that id to update TaCZ). Optional runtime deps, each gated by a `Compat.LOADED` flag checked via `ModList.get().isLoaded(...)`: GeckoLib, Curios, Cloth Config. Never call into an optional dependency's API without checking its `LOADED` flag first — see `compat/*/…Compat.java`.

## Architecture

### Package layout (`net.nekoyuni.SimpleEnemyMod`)

- `entity/unit/` — the three concrete NPC entities: `USunitEntity`, `RUunitEntity` (both hostile-by-default "enemy" factions), `PmcUnitEntity` (recruitable/friendly, implements `ICommandableMob` for the order system). All extend `AbstractUnit` (a `Monster`), which owns shared state: hurt/death animation flags, tactical state (`SoldierState`, `StrategyType`, cover/flanking bookkeeping), suppression/awareness state, and a movement lock so goals can suppress vanilla navigation. Each subclass implements the abstract hooks: `equipRandomGun()`, `setupRoleGoals()`, and the custom hurt/death/alert sound getters. `entity/unit/util/UnitFactionTags` holds all faction checks.
- `entity/ai/roles/` — `UnitRole` enum (`DEFAULT`, `SQUAD_LEADER`, `SQUAD_UNIT` and their `FRIENDLY_*` counterparts) each carries an `IRoleGoals` implementation that wires goal-selector goals onto an entity. `setupRoleGoals()` (called from `finalizeSpawn` and NBT load) clears all goals and re-adds a fixed base set (target selectors, faction-aware targeting) plus whatever the current `UnitRole`'s goals contribute.
- `entity/ai/goals/` — the `Goal` implementations: ranged combat (`RangedGunAttackGoal`, `MoveToAttackRangeGoal`, `AiGunSpreadHelper`), targeting (`VerticalAwareTargetGoal`, `NoPlayerHurtByTargetGoal`), squad behavior (`SmartSquadDoorGoal`, `SquadLeaderHandshakeGoal`, `SquadUnitHandshakeFollowGoal`, `SquadData`), tactical positioning (`TacticalManagerGoal` — the high-level state machine, `TacticalManeuverGoal`, `SeekCoverGoal`, `PeekFromCoverGoal`, `TacticalInvestigateSoundGoal`, `TerrainScanner`, `FormationUtils`), and player-order handling (`CommanderOrderGoal`, `AttackSpecificTargetGoal`). `SelfHealGoal` is a port-only PMC addition.
- `entity/ai/{awareness,decision,navigation,profile,sensing,squad,suppression,tactical}/` — upstream's 0.1.5/0.1.6 tactical AI: sound awareness, `CombatDecisionContext`/Schmitt-thresholded combat state, ladder/soft-obstacle navigation, role behaviour profiles, line-of-sight helpers, `SquadCombatMemory`, suppression, cover finding.
- `entity/ai/orders/` — `OrderType` and `ICommandableMob`, the contract the commander UI/packets use to issue orders to owned units.
- `entity/client/animation/` — a layered animation system independent of GeckoLib's own state machine: `LayeredAnimationManager` holds a priority-sorted list of `IAnimationLayer`s plus `IProceduralLayer`s applied from the model's `setupAnim`. `NpcMuzzleFlashRenderer` (fed by `TaczGunShootEventListener`) draws NPC muzzle flashes from `GunLayerRenderer`.
- `entity/equipment/` — per-faction weapon equippers that pick a `UnitLoadout` and hand the unit a TaCZ gun with attachments and ammo. Loadout pools are datapack JSON under `data/simpleenemymod/unit_loadouts/{pmc,ru,us}_units/loadouts.json` — add guns there, not in Java.
- `inventory/` — PMC 18-slot `UnitInventoryHandler` (slots 0-5 mirror mainhand/offhand/feet/legs/chest/head, 6-17 storage; see `PmcInventorySlots`), exposed as `Capabilities.ItemHandler.ENTITY` by `event/common/CapabilityRegistrationHandler`. TaCZ's NPC reload reads ammo through that capability. RU/US only expose NeoForge's fallback equipment wrapper (hands + armour), matching Forge's LivingEntity default upstream.
- `compat/` — one subpackage per optional dependency (`cloth`, `curios`, `geckolib`), each with a top-level `*Compat.LOADED` boolean gate. `compat/cloth` holds the Cloth Config screens mirroring `config/`.
- `config/` — `ClientConfig` plus `CommonConfig`, which only assembles the per-topic classes in `config/common/` (`FactionsConfig`, `TacticalAiConfig`, `UnitAttributesConfig`, …) into one spec. Registered via `ModConfigs.register` into `sem-client.toml`/`sem-common.toml`. `AIDifficultySettings` is a snapshot built `fromConfig()`.
- `procedural/events/` — `DynamicEventManager` ticks the overworld and, per registered `DynamicEvent` (`PatrolEvent`, `CombatEvent`, `CaveExtractionEvent`), rolls a per-player probability that ramps up on failure and resets on success (`EventProbabilityData`, `EventCooldownManager`, `ChunkSafetyEvaluator` to avoid player bases).
- `network/` — `CustomPacketPayload` records under `network/packets/`, registered in `ModNetworking`; bump `PROTOCOL_VERSION` when changing a payload's wire format.
- `registry/` — all `DeferredRegister` holders plus `ModDataGenerators`.
- `event/` — split by side/bus: `event/client`, `event/common`, `event/server`. Look here first for "when X happens in-game, do Y" logic.
- `api/` — **port-only** additive extension points for addons (events on `NeoForge.EVENT_BUS`, `SemFactionRelations`, `SemUnitPredicates`). Called from SEM classes through one-line `api/internal/SemHooks` calls; inert with no listeners.
- `scenario/` — no-op stubs only. Upstream's scenario/test suite (and `/sem test`) is intentionally not ported; the stubs keep upstream call sites unchanged.

### Cross-cutting notes

- **Faction identity is by entity-type tag.** `UnitFactionTags` checks the `simpleenemymod:{pmc,ru,us}_units` entity-type tags (`data/simpleenemymod/tags/entity_type/`) gated by `FactionsConfig.RU_UNITS_FRIENDLY`/`US_UNITS_FRIENDLY`. Addons add their own unit types to these tags. RU/US/PMC `setTarget` veto friendly targets (`isFriendlyToNpcFaction` / `isFriendlyToPmc`).
- **Squad/role state is synced via `SynchedEntityData`** on `PmcUnitEntity` (role, order, owner UUID, formation index, attack target); `AbstractUnit`'s own role field is a plain server-side field used by RU/US.
- **TaCZ integration is by convention**: guns/attachments are `ResourceLocation`s in JSON, resolved at equip time.

## Upstream sync model

- `vendor/upstream-forge` is a branch of **pure upstream Forge source**, rooted at `7911099` (tree-identical to upstream `fe67dc8`, the port's original base). Tag `upstream-0.1.6-beta-hotfix` marks the current sync point. The hotfix delta was never pushed upstream; it was reconstructed from the released jar (commit `86c085b`).
- To sync a new upstream version: on `vendor/upstream-forge` (e.g. in a `git worktree`), `git rm -r src && git checkout <upstream-commit> -- src`, commit, apply any jar-only delta, tag; then merge the branch into the port. Git uses the vendor history as the merge base, so only upstream's *changes* land on top of the NeoForge translation. Resolve conflicts as *upstream logic + port translation*, translate new files with the patterns below, then `./gradlew checkSewvContract` and `updateSewvContract` if the surface legitimately changed.
- Rule: **upstream code stays verbatim except for platform translation.** No renames, package moves or extract-method refactors in upstream classes. Port behaviour changes must be small and commented (`// NeoForge port: …`).

## Forge → NeoForge translation patterns

| Forge 1.20.1 | NeoForge 1.21.1 |
|---|---|
| `@Mod.EventBusSubscriber(bus = FORGE/MOD)` | `@EventBusSubscriber(bus = GAME/MOD)` |
| `TickEvent.*` + `phase == END` | `ServerTickEvent.Post` / `LevelTickEvent.Post` / `ClientTickEvent.Post` |
| `ForgeConfigSpec` | `ModConfigSpec` |
| `ForgeRegistries.ITEMS.getKey/getValue` | `BuiltInRegistries.ITEM.getKey/get` |
| `new ResourceLocation(ns, p)` / `(str)` | `ResourceLocation.fromNamespaceAndPath` / `parse` |
| `getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(h -> …)` | `Optional.ofNullable(getCapability(Capabilities.ItemHandler.ENTITY, null)).ifPresent(h -> …)` (keeps upstream's shape) |
| `defineSynchedData()` + `entityData.define` | `defineSynchedData(SynchedEntityData.Builder)` + `builder.define` |
| `finalizeSpawn(…, dataTag)` | 4-arg `finalizeSpawn` (no NBT tag) |
| `SimpleChannel` packet with `encode/decode/handle(msg, Supplier<Context>)` | `record … implements CustomPacketPayload` + `STREAM_CODEC`, `handle(msg, IPayloadContext)` |
| `SavedData.computeIfAbsent(load, new, name)` | `SavedData.Factory` + `HolderLookup.Provider` in `save`/`load` |
| TaCZ `GunItemBuilder.build()`, `installAttachment(gun, att)`, `getAttachment(gun, type)` | same with a leading `HolderLookup.Provider` (`entity.level().registryAccess()`) |
| `renderToBuffer(…, r, g, b, a)` | `renderToBuffer(…, int argb)` (white = `-1`) |
| `BlockPathTypes`, `Blocks.GRASS` | `PathType`, `Blocks.SHORT_GRASS` |
| `Block#use(…, hand, hit)` | keep upstream `use(…)` and delegate from `useItemOn` / `useWithoutItem` |
| `EntityTag` item NBT | `DataComponents.ENTITY_DATA` (`CustomData.update`) |
| `DyeableArmorItem`, `getArmorTexture(…, "overlay")` | `ItemTags.DYEABLE`, `ArmorMaterial.layers()` via `ClientHooks.getArmorTexture` |
| `data/*/tags/entity_types`, `loot_tables`, `recipes` | `tags/entity_type`, `loot_table`, `recipe` |

## sewv contract

tacz_sewv binds to SEM by class/method/field name and by INVOKE/CONSTANT anchors inside method bodies. Keep these stable:

- **Names** — every class and member in [contract/sewv-surface.txt](contract/sewv-surface.txt), enforced by `checkSewvContract`. Notably: `PacketIssueOrder`'s private fields and `handle`/`handleOnServer`, the `mob`/`unit` fields of the AI goals, `VillageGarrisonHandler.spawnGuard` (private static, called from the pending-garrison loop), `CommanderOrderGoal.calculateClassicFormationTarget`/`findPredecessor`, `DynamicEventManager.tryEventForPlayer`/`forceEvent`, `ClientGlowRenderHandler.spawnCircle`, model bone names `fakeRoot/unit/head/body/rightArm/leftArm/rightLeg/leftLeg`.
- **Anchors** (not checked by the task — review by hand): `PmcUnitEntity.setTarget` keeps its `instanceof Player` and `UnitFactionTags.isFriendlyToPmc` call; `AbstractUnit.setTarget` keeps its `playSound(SoundEvent,float,float)` call; `CommanderOrderGoal.canUse/canContinueToUse/tick` read `PathfinderMob.getTarget()`; `DynamicEventManager.tryEventForPlayer/forceEvent` call `SpawnHelper.getRandomPositionNearPlayer` and `DynamicEvent.execute`.
- **Data** — entity ids `simpleenemymod:{pmcunit,ruunit,usunit}`, faction tag ids, loadout JSON format, unit texture paths, event ids `far_combat`/`military_patrol`/`cave_extraction`, config classes `config.common.FactionsConfig`/`TacticalAiConfig` (read reflectively), the PMC slot map and capability.
- New addon needs go into `api/` as additive, inert hooks — not into refactors of upstream classes. A local, gitignored handoff map for the sewv port lives at `docs/SEWV_PORTING.md`.
