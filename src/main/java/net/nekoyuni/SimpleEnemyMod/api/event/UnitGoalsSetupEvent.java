package net.nekoyuni.SimpleEnemyMod.api.event;

import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.nekoyuni.SimpleEnemyMod.entity.ai.roles.utils.UnitRole;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import net.neoforged.bus.api.Event;

/**
 * Fired at the end of {@code setupRoleGoals()} on RU/US/PMC units, after SEM rebuilt the goal sets.
 * Add, remove or replace goals here. SEM runs it from {@code finalizeSpawn} and when the unit is loaded
 * from NBT, both before the unit is added to the level.
 */
public class UnitGoalsSetupEvent extends Event {

    private final AbstractUnit unit;
    private final UnitRole role;
    private final GoalSelector goalSelector;
    private final GoalSelector targetSelector;

    public UnitGoalsSetupEvent(AbstractUnit unit, UnitRole role, GoalSelector goalSelector, GoalSelector targetSelector) {
        this.unit = unit;
        this.role = role;
        this.goalSelector = goalSelector;
        this.targetSelector = targetSelector;
    }

    public AbstractUnit getUnit() {
        return unit;
    }

    public UnitRole getRole() {
        return role;
    }

    public GoalSelector getGoalSelector() {
        return goalSelector;
    }

    public GoalSelector getTargetSelector() {
        return targetSelector;
    }
}
