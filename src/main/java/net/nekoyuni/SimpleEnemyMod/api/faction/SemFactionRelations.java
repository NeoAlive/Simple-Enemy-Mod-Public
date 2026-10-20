package net.nekoyuni.SimpleEnemyMod.api.faction;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.common.util.TriState;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Overrides for SEM's tag-based faction checks in {@code UnitFactionTags}, e.g. for an addon's
 * diplomacy system. The first override that returns a non-DEFAULT answer wins; DEFAULT everywhere
 * falls through to SEM's own logic. Consulted from targeting and damage code, so keep them cheap.
 */
public final class SemFactionRelations {

    @FunctionalInterface
    public interface NpcFriendlinessOverride {
        /** Is {@code other} friendly to the SEM unit {@code npc}? */
        TriState isFriendly(LivingEntity npc, LivingEntity other);
    }

    @FunctionalInterface
    public interface FriendlyFireOverride {
        /** Is damage from {@code attacker} to {@code victim} friendly fire (and therefore blocked)? */
        TriState isFriendlyFire(Entity victim, Entity attacker);
    }

    private static final List<NpcFriendlinessOverride> NPC_FRIENDLINESS = new CopyOnWriteArrayList<>();
    private static final List<FriendlyFireOverride> FRIENDLY_FIRE = new CopyOnWriteArrayList<>();

    private SemFactionRelations() {
    }

    /** Consulted by {@code UnitFactionTags.isFriendlyToNpcFaction}. */
    public static void registerNpcFriendlinessOverride(NpcFriendlinessOverride override) {
        NPC_FRIENDLINESS.add(override);
    }

    /** Consulted by {@code UnitFactionTags.isFriendlyFire}. */
    public static void registerFriendlyFireOverride(FriendlyFireOverride override) {
        FRIENDLY_FIRE.add(override);
    }

    public static TriState resolveNpcFriendliness(LivingEntity npc, LivingEntity other) {
        if (NPC_FRIENDLINESS.isEmpty()) {
            return TriState.DEFAULT;
        }
        for (NpcFriendlinessOverride override : NPC_FRIENDLINESS) {
            TriState result = override.isFriendly(npc, other);
            if (!result.isDefault()) {
                return result;
            }
        }
        return TriState.DEFAULT;
    }

    public static TriState resolveFriendlyFire(Entity victim, Entity attacker) {
        if (FRIENDLY_FIRE.isEmpty()) {
            return TriState.DEFAULT;
        }
        for (FriendlyFireOverride override : FRIENDLY_FIRE) {
            TriState result = override.isFriendlyFire(victim, attacker);
            if (!result.isDefault()) {
                return result;
            }
        }
        return TriState.DEFAULT;
    }
}
