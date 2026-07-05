package net.nekoyuni.SimpleEnemyMod.entity.ai.goals;

import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.inventory.UnitInventoryHandler;

import java.util.EnumSet;

public class SelfHealGoal extends Goal {

    private final PmcUnitEntity unit;
    private final float healthThreshold;
    private int eatCooldown = 0;
    private long nextEatAllowedTick = 0L;

    public SelfHealGoal(PmcUnitEntity unit, float healthThreshold) {
        this.unit = unit;
        this.healthThreshold = healthThreshold;
        this.setFlags(EnumSet.noneOf(Goal.Flag.class)); // no movement/look lock — scoff on the move
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

        ItemStack food;
        boolean isOffhand = (slot == -2);

        if (isOffhand) {
            food = this.unit.getItemBySlot(EquipmentSlot.OFFHAND);
        } else {
            food = inv.getStackInSlot(slot);
        }

        FoodProperties props = food.get(DataComponents.FOOD);
        if (props == null) return;

        // Nutrition roughly maps to health points — a decent, non-OP heal
        this.unit.heal((float) props.nutrition());

        // Apply any effects the food carries (golden apple regen, rotten flesh hunger, etc.)
        for (FoodProperties.PossibleEffect possible : props.effects()) {
            MobEffectInstance instance = possible.effect();
            if (instance != null) {
                this.unit.addEffect(new MobEffectInstance(instance));
            }
        }

        // Eat one, make the noise, set cooldown so it's not an all-you-can-eat buffet
        food.shrink(1);
        this.unit.playSound(SoundEvents.GENERIC_EAT, 1.0F, 1.0F);
        this.nextEatAllowedTick = this.unit.level().getGameTime() + 100L;
    }

    /**
     * Returns: -2 for offhand, 0+ for an inventory slot index, -1 if no food anywhere.
     * Offhand gets priority so you can deliberately load a "medic ration" there.
     */
    private int findFoodSlot() {
    // Offhand first — your deliberate "medic ration" slot
    if (isFood(this.unit.getItemBySlot(EquipmentSlot.OFFHAND))) {
        return -2;
    }

    // Rummage general storage ONLY — slots 0-5 are reserved gear, hands off
    UnitInventoryHandler inv = this.unit.getInventory();
    for (int i = 6; i < inv.getSlots(); i++) {   // <-- starts at 6, not 0
        if (isFood(inv.getStackInSlot(i))) {
            return i;
        }
    }

    return -1;
}

    private boolean isFood(ItemStack stack) {
        return !stack.isEmpty() && stack.has(DataComponents.FOOD);
    }

    public void tickCooldown() {
    if (this.eatCooldown > 0) {
        this.eatCooldown--;
    }
}
}