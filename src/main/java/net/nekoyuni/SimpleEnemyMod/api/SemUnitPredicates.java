package net.nekoyuni.SimpleEnemyMod.api;

import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;

/**
 * Lets addons switch off parts of a unit's on-foot AI, e.g. while it crews a vehicle or holds a leash.
 * Checked at the start of the affected goals' {@code canUse}/{@code canContinueToUse}; if any
 * registered predicate matches, the goal does not run. Called every tick per unit, so keep them cheap.
 */
public final class SemUnitPredicates {

    private static final List<Predicate<Mob>> ON_FOOT_MOVEMENT_SUPPRESSORS = new CopyOnWriteArrayList<>();
    private static final List<Predicate<Mob>> SMALL_ARMS_SUPPRESSORS = new CopyOnWriteArrayList<>();

    private SemUnitPredicates() {
    }

    /**
     * Suppresses MoveToAttackRangeGoal, SeekCoverGoal, PeekFromCoverGoal, TacticalManeuverGoal and
     * TacticalInvestigateSoundGoal for matching units.
     */
    public static void registerOnFootMovementSuppressor(Predicate<Mob> predicate) {
        ON_FOOT_MOVEMENT_SUPPRESSORS.add(predicate);
    }

    /** Suppresses RangedGunAttackGoal (the unit's handheld gun) for matching units. */
    public static void registerSmallArmsSuppressor(Predicate<Mob> predicate) {
        SMALL_ARMS_SUPPRESSORS.add(predicate);
    }

    public static boolean isOnFootMovementSuppressed(Mob mob) {
        return matches(ON_FOOT_MOVEMENT_SUPPRESSORS, mob);
    }

    public static boolean isSmallArmsSuppressed(Mob mob) {
        return matches(SMALL_ARMS_SUPPRESSORS, mob);
    }

    private static boolean matches(List<Predicate<Mob>> predicates, Mob mob) {
        if (predicates.isEmpty()) {
            return false;
        }
        for (Predicate<Mob> predicate : predicates) {
            if (predicate.test(mob)) {
                return true;
            }
        }
        return false;
    }
}
