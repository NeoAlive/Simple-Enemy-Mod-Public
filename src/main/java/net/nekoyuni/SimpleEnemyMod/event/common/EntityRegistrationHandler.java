package net.nekoyuni.SimpleEnemyMod.event.common;

import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.nekoyuni.SimpleEnemyMod.registry.ModEntities;
import net.nekoyuni.SimpleEnemyMod.entity.unit.RUunitEntity;
import net.nekoyuni.SimpleEnemyMod.entity.unit.USunitEntity;



@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class EntityRegistrationHandler {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(ModEntities.USUNIT.get(), USunitEntity.createAttributes().build());
        event.put(ModEntities.RUUNIT.get(), RUunitEntity.createAttributes().build());
        event.put(ModEntities.PMCUNIT.get(), PmcUnitEntity.createAttributes().build());

    }

}
