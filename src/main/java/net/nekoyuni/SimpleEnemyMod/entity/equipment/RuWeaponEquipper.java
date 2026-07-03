package net.nekoyuni.SimpleEnemyMod.entity.equipment;

import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.builder.AttachmentItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.nekoyuni.SimpleEnemyMod.data.UnitLoadout;
import net.nekoyuni.SimpleEnemyMod.event.common.UnitLoadoutManager;

import static net.nekoyuni.SimpleEnemyMod.entity.equipment.PmcUnitWeaponEquipper.getJsonFireMode;

public class RuWeaponEquipper {

    public static void equipRandomGun(LivingEntity entity, RandomSource random) {
        final String FACTION_ID = "ru_units";
        HolderLookup.Provider provider = entity.level().registryAccess();

        UnitLoadout selectedLoadout;
        try {
            selectedLoadout = UnitLoadoutManager.getRandomLoadout(FACTION_ID, random);
        } catch (IllegalStateException e) {
            System.err.println("ERROR: The unit could not be equipped." + e.getMessage());
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

        iGun.setMaxDummyAmmoAmount(gunStack, Integer.MAX_VALUE);
        iGun.setDummyAmmoAmount(gunStack, 9999);

        entity.setItemInHand(InteractionHand.MAIN_HAND, gunStack);
    }
}
