package net.nekoyuni.SimpleEnemyMod.api.event;

import net.minecraft.world.entity.LivingEntity;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.util.TriState;
import org.jetbrains.annotations.Nullable;

/** Target acquisition hooks for SEM units (fired on the logical side the unit lives on). */
public abstract class UnitTargetEvent extends Event {

    private final AbstractUnit unit;

    protected UnitTargetEvent(AbstractUnit unit) {
        this.unit = unit;
    }

    public AbstractUnit getUnit() {
        return unit;
    }

    /**
     * Fired at the start of {@code setTarget} on RU/US/PMC units for a non-null target, before SEM's
     * own friendly-target veto. Result FALSE rejects the target, TRUE accepts it even if SEM would
     * veto it, DEFAULT keeps SEM's behaviour.
     */
    public static class Pre extends UnitTargetEvent {

        private final LivingEntity target;
        private TriState result = TriState.DEFAULT;

        public Pre(AbstractUnit unit, LivingEntity target) {
            super(unit);
            this.target = target;
        }

        public LivingEntity getTarget() {
            return target;
        }

        public TriState getResult() {
            return result;
        }

        public void setResult(TriState result) {
            this.result = result;
        }
    }

    /** Fired after a unit's target actually changed (new target may be null). */
    public static class Post extends UnitTargetEvent {

        @Nullable
        private final LivingEntity oldTarget;
        @Nullable
        private final LivingEntity newTarget;

        public Post(AbstractUnit unit, @Nullable LivingEntity oldTarget, @Nullable LivingEntity newTarget) {
            super(unit);
            this.oldTarget = oldTarget;
            this.newTarget = newTarget;
        }

        @Nullable
        public LivingEntity getOldTarget() {
            return oldTarget;
        }

        @Nullable
        public LivingEntity getNewTarget() {
            return newTarget;
        }
    }
}
