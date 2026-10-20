package net.nekoyuni.SimpleEnemyMod.event.common;

import com.tacz.guns.api.TimelessAPI;
import com.tacz.guns.api.item.IGun;
import com.tacz.guns.api.item.attachment.AttachmentType;
import com.tacz.guns.api.item.builder.AmmoItemBuilder;
import com.tacz.guns.api.item.builder.GunItemBuilder;
import com.tacz.guns.api.item.gun.FireMode;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.nekoyuni.SimpleEnemyMod.config.common.DropsConfig;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class UnitDropsHandler {

    @SubscribeEvent
    public static void onUnitDrops(LivingDropsEvent event) {

        if (!(event.getEntity() instanceof AbstractUnit unit)) return;

        // Always strip equipped/inventory guns. US/RU guns carry dummy ammo (~9999) for AI
        // infinite fire; falling through to vanilla Mob equipment drops leaks those stacks.
        event.getDrops().removeIf(itemEntity ->
                IGun.getIGunOrNull(itemEntity.getItem()) != null
        );

        if (unit instanceof PmcUnitEntity pmcUnit && pmcUnit.getOwnerUUID() != null) {
            dropPmcWeapon(pmcUnit, event);
            return;
        }

        if (!DropsConfig.ENABLE_CUSTOM_DROPS.get()) return;

        ItemStack sourceGun = findGunStack(unit);
        if (sourceGun.isEmpty()) return;

        IGun iGun = IGun.getIGunOrNull(sourceGun);
        if (iGun == null) return;

        var provider = unit.level().registryAccess();
        ResourceLocation gunId = iGun.getGunId(sourceGun);
        FireMode fireMode = iGun.getFireMode(sourceGun);

        ResourceLocation ammoId = TimelessAPI.getCommonGunIndex(gunId)
                .map(index -> index.getGunData().getAmmoId())
                .orElse(null);

        if (ammoId == null) return;

        if (unit.getRandom().nextFloat() <= DropsConfig.GUN_DROP_CHANCE.get()) {

            int maxAmmo = TimelessAPI.getCommonGunIndex(gunId)
                    .map(index -> index.getGunData().getAmmoAmount())
                    .orElse(30);

            int partialAmmo = unit.getRandom().nextInt(Math.max(1, maxAmmo / 4));

            ItemStack cleanGun = GunItemBuilder.create()
                    .setId(gunId)
                    .setAmmoCount(partialAmmo)
                    .setFireMode(fireMode)
                    .setCount(1)
                    .build(provider);

            IGun iCleanGun = IGun.getIGunOrNull(cleanGun);
            if (iCleanGun != null) {
                for (AttachmentType type : AttachmentType.values()) {
                    ItemStack attachment = iGun.getAttachment(provider, sourceGun, type);
                    if (!attachment.isEmpty()) {
                        iCleanGun.installAttachment(provider, cleanGun, attachment.copy());
                    }
                }
            }

            ItemEntity cleanGunEntity = new ItemEntity(
                    unit.level(), unit.getX(), unit.getY(), unit.getZ(), cleanGun
            );
            event.getDrops().add(cleanGunEntity);

        }

        if (unit.getRandom().nextFloat() <= DropsConfig.AMMO_DROP_CHANCE.get()) {

            int ammoCount = 10 + unit.getRandom().nextInt(21);
            ItemStack ammoDrop = AmmoItemBuilder.create()
                    .setId(ammoId)
                    .setCount(ammoCount)
                    .build();

            ItemEntity ammoEntity = new ItemEntity(
                    unit.level(), unit.getX(), unit.getY(), unit.getZ(), ammoDrop
            );
            event.getDrops().add(ammoEntity);
        }
    }


    private static ItemStack findGunStack(AbstractUnit unit) {
        ItemStack[] result = { ItemStack.EMPTY };

        IItemHandler handler = unit.getCapability(Capabilities.ItemHandler.ENTITY, null);
        if (handler != null) {
            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (IGun.getIGunOrNull(stack) != null) {
                    result[0] = stack;
                    break;
                }
            }
        }

        if (!result[0].isEmpty()) return result[0];

        ItemStack mainHand = unit.getMainHandItem();
        if (IGun.getIGunOrNull(mainHand) != null) {
            return mainHand;
        }

        ItemStack offHand = unit.getOffhandItem();
        if (IGun.getIGunOrNull(offHand) != null) {
            return offHand;
        }

        return ItemStack.EMPTY;
    }

    private static void dropPmcWeapon(PmcUnitEntity pmcUnit, LivingDropsEvent event) {

        ItemStack sourceGun = findGunStack(pmcUnit);
        if (sourceGun.isEmpty()) return;

        ItemEntity gunEntity = new ItemEntity(
                pmcUnit.level(),
                pmcUnit.getX(), pmcUnit.getY(), pmcUnit.getZ(),
                sourceGun.copy()
        );
        event.getDrops().add(gunEntity);
    }
}
