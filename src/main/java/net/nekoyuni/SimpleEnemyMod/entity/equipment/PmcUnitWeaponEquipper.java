package net.nekoyuni.SimpleEnemyMod.entity.equipment;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.nekoyuni.SimpleEnemyMod.data.UnitLoadout;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.event.common.UnitLoadoutManager;
import net.nekoyuni.SimpleEnemyMod.inventory.PmcInventorySlots;

public class PmcUnitWeaponEquipper {

    public static void equipRandomGun(LivingEntity entity, RandomSource random) {
        final String FACTION_ID = "pmc_units";
        HolderLookup.Provider provider = entity.level().registryAccess();
        UnitLoadout selectedLoadout;

        try {
            selectedLoadout = UnitLoadoutManager.getRandomLoadout(FACTION_ID, random);
        } catch (IllegalStateException e) {
            System.err.println("ERROR: The unit could not be equipped. " + e.getMessage());
            return;
        }

        ItemStack gunStack = GunItemBuilder.create()
                .setId(selectedLoadout.gunId)
                .setAmmoCount(selectedLoadout.ammoCount)
                .setFireMode(getJsonFireMode(selectedLoadout.fireMode))
                .setCount(1)
                .build(provider);

        IGun iGun = IGun.getIGunOrNull(gunStack);
        if (iGun == null) {
            System.err.println("ERROR: The created itemstack is not an IGun weapon. Check Loadout: " + selectedLoadout.gunId);
            return;
        }

        selectedLoadout.scopeId.ifPresent(scopeId -> {
            ItemStack scopeStack = AttachmentItemBuilder.create().setId(scopeId).build();
            iGun.installAttachment(provider, gunStack, scopeStack);
        });

        selectedLoadout.muzzleId.ifPresent(muzzleId -> {
            ItemStack muzzleStack = AttachmentItemBuilder.create().setId(muzzleId).build();
            iGun.installAttachment(provider, gunStack, muzzleStack);
        });

        selectedLoadout.gripId.ifPresent(gripId -> {
            ItemStack gripStack = AttachmentItemBuilder.create().setId(gripId).build();
            iGun.installAttachment(provider, gunStack, gripStack);
        });

        if (!(entity instanceof PmcUnitEntity pmcUnit)) {
            return;
        }

        IItemHandlerModifiable modifiable = pmcUnit.getInventory();
        modifiable.setStackInSlot(PmcInventorySlots.MAIN_HAND, gunStack);

        ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(selectedLoadout.gunId)
                .map(index -> index.getGunData().getAmmoId())
                .orElse(null);

        if (ammoId != null) {
            int reserveAmmo = selectedLoadout.ammoCount * 6;

            // Build one sample stack purely to read the legal max stack size for this ammo type.
            ItemStack sampleAmmoStack = AmmoItemBuilder.create().setId(ammoId).setCount(1).build();
            int maxStackSize = sampleAmmoStack.getMaxStackSize();

            int remaining = reserveAmmo;

            // Spread the reserve across the shared ammo slot pool (reserve slot + storage)
            // instead of cramming it all into one slot, which produces an NBT-illegal
            // stack count and causes the entity to fail saving (and despawn on quit).
            for (int slot : PmcInventorySlots.AMMO_SLOTS) {
                if (remaining <= 0) break;
                if (!modifiable.getStackInSlot(slot).isEmpty()) continue;

                int stackAmount = Math.min(remaining, maxStackSize);

                ItemStack ammoStack = AmmoItemBuilder.create()
                        .setId(ammoId)
                        .setCount(stackAmount)
                        .build();

                modifiable.setStackInSlot(slot, ammoStack);
                remaining -= stackAmount;
            }

            if (remaining > 0) {
                System.err.println("WARNING: Not enough empty ammo slots to place full reserve for "
                        + selectedLoadout.gunId + ". " + remaining + " rounds discarded.");
            }
        }
    }

    protected static FireMode getJsonFireMode(String fireModeStr) {
        if ("AUTO".equalsIgnoreCase(fireModeStr)) {
            return FireMode.AUTO;
        }
        return FireMode.SEMI;
    }
}