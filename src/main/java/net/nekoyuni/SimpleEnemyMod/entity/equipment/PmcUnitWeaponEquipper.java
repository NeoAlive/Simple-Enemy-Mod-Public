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
import net.neoforged.neoforge.capabilities.Capabilities;
import java.util.Optional;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.nekoyuni.SimpleEnemyMod.data.UnitLoadout;
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

        // Scope
        selectedLoadout.scopeId.ifPresent(scopeId -> {
            ItemStack scopeStack = AttachmentItemBuilder.create().setId(scopeId).build();
            iGun.installAttachment(provider, gunStack, scopeStack);
        });

        // Muzzle
        selectedLoadout.muzzleId.ifPresent(muzzleId -> {
            ItemStack muzzleStack = AttachmentItemBuilder.create().setId(muzzleId).build();
            iGun.installAttachment(provider, gunStack, muzzleStack);
        });

        // Grip
        selectedLoadout.gripId.ifPresent(gripId -> {
            ItemStack gripStack = AttachmentItemBuilder.create().setId(gripId).build();
            iGun.installAttachment(provider, gunStack, gripStack);
        });

        // iGun.setMaxDummyAmmoAmount(gunStack, Integer.MAX_VALUE);
        // iGun.setDummyAmmoAmount(gunStack, 9999);


        Optional.ofNullable(entity.getCapability(Capabilities.ItemHandler.ENTITY, null)).ifPresent(handler -> {
            if (!(handler instanceof IItemHandlerModifiable modifiable)) return;

            modifiable.setStackInSlot(0, gunStack);

            ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(selectedLoadout.gunId)
                    .map(index -> index.getGunData().getAmmoId())
                    .orElse(null);

            if (ammoId != null) {
                int reserveAmmo = selectedLoadout.ammoCount * 6;

                // NeoForge port: 1.21 cannot save a stack above its max size (the unit fails to save and
                // despawns), so the reserve is spread over the ammo slots instead of one oversized stack.
                int maxStackSize = AmmoItemBuilder.create().setId(ammoId).setCount(1).build().getMaxStackSize();
                int remaining = reserveAmmo;

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
            }
        });
    }

    protected static FireMode getJsonFireMode(String fireModeStr) {

        if ("AUTO".equalsIgnoreCase(fireModeStr)) {
            return FireMode.AUTO;
        }
        return FireMode.SEMI;
    }

}
