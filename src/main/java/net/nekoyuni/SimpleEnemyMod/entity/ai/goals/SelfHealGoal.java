package net.nekoyuni.SimpleEnemyMod.entity.ai.goals;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.inventory.PmcInventorySlots;
import net.nekoyuni.SimpleEnemyMod.inventory.UnitInventoryHandler;

import java.util.EnumSet;

// NeoForge port addition (not in upstream): PMC allies eat food from their inventory when hurt.
public class SelfHealGoal extends Goal {

    private static final long EAT_COOLDOWN_TICKS = 100L;

    private final PmcUnitEntity unit;
    private final float healthThreshold;
    private long nextEatAllowedTick = 0L;

    public SelfHealGoal(PmcUnitEntity unit, float healthThreshold) {
        this.unit = unit;
        this.healthThreshold = healthThreshold;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class)); // no movement/look lock, eats on the move
    }

    @Override
    public boolean canUse() {
        if (this.unit.level().getGameTime() < this.nextEatAllowedTick) return false;
        if (this.unit.getHealth() >= this.healthThreshold) return false;
        return findFoodSlot() >= 0;
    }

    @Override
    public boolean canContinueToUse() {
        return false; // one bite per activation, canUse re-checks next tick
    }

    @Override
    public void start() {
        int slot = findFoodSlot();
        if (slot < 0) return;

        UnitInventoryHandler inv = this.unit.getInventory();
        ItemStack food = inv.getStackInSlot(slot).copy();

        FoodProperties props = food.get(DataComponents.FOOD);
        if (props == null) return;

        // Nutrition roughly maps to health points
        this.unit.heal((float) props.nutrition());

        for (FoodProperties.PossibleEffect possible : props.effects()) {
            MobEffectInstance instance = possible.effect();
            if (instance != null) {
                this.unit.addEffect(new MobEffectInstance(instance));
            }
        }

        // Write back through the handler so the offhand mirror (slot 1) stays in sync
        food.shrink(1);
        inv.setStackInSlot(slot, food);

        this.unit.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        this.nextEatAllowedTick = this.unit.level().getGameTime() + EAT_COOLDOWN_TICKS;
    }

    // Offhand first (a deliberate "ration" slot), then general storage. Slots 0 and 2-5 are gear.
    private int findFoodSlot() {
        UnitInventoryHandler inv = this.unit.getInventory();

        if (isFood(inv.getStackInSlot(PmcInventorySlots.OFF_HAND))) {
            return PmcInventorySlots.OFF_HAND;
        }

        for (int i = PmcInventorySlots.STORAGE_START; i <= PmcInventorySlots.STORAGE_END; i++) {
            if (isFood(inv.getStackInSlot(i))) {
                return i;
            }
        }

        return -1;
    }

    private boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.FOOD);
    }
}
