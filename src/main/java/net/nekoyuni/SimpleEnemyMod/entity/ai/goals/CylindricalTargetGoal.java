package net.nekoyuni.SimpleEnemyMod.entity.ai.goals;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.Predicate;

public class CylindricalTargetGoal<T extends LivingEntity> extends TargetGoal {

    private final Class<T> targetType;
    private final Predicate<LivingEntity> filter;
    private final double horizontalRange;
    private final double verticalRange;
    private LivingEntity foundTarget;

    private final TargetingConditions targetingConditions;

    public CylindricalTargetGoal(Mob mob, Class<T> targetType, boolean mustSee,
                                  double horizontalRange, double verticalRange,
                                  Predicate<LivingEntity> filter) {
        super(mob, mustSee);
        this.targetType = targetType;
        this.filter = filter;
        this.horizontalRange = horizontalRange;
        this.verticalRange = verticalRange;

        this.targetingConditions = TargetingConditions.forCombat()
                .range(Math.max(horizontalRange, verticalRange) + 8.0) // generous outer bound, real filtering happens below
                .selector(filter);
    }

    @Override
    public boolean canUse() {
        AABB searchBox = new AABB(
                this.mob.getX() - horizontalRange, this.mob.getY() - verticalRange, this.mob.getZ() - horizontalRange,
                this.mob.getX() + horizontalRange, this.mob.getY() + verticalRange, this.mob.getZ() + horizontalRange
        );

        List<T> candidates = this.mob.level().getEntitiesOfClass(targetType, searchBox,
                e -> e != this.mob && e.isAlive());

        LivingEntity best = null;
        double bestDistSq = Double.MAX_VALUE;

        for (T candidate : candidates) {
            double dx = candidate.getX() - this.mob.getX();
            double dz = candidate.getZ() - this.mob.getZ();
            double dy = candidate.getY() - this.mob.getY();

            if (Math.sqrt(dx * dx + dz * dz) > horizontalRange) continue;
            if (Math.abs(dy) > verticalRange) continue;
            if (!filter.test(candidate)) continue;
            if (!this.targetingConditions.test(this.mob, candidate)) continue;

            double distSq = dx * dx + dy * dy + dz * dz;
            if (distSq < bestDistSq) {
                bestDistSq = distSq;
                best = candidate;
            }
        }

        this.foundTarget = best;
        return best != null;
    }

    @Override
    public void start() {
        this.mob.setTarget(this.foundTarget);
        super.start();
    }
}