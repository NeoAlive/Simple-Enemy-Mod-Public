package net.nekoyuni.SimpleEnemyMod.event.server;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.procedural.events.DynamicEventManager;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID)
public class SpawnEventHandler {

    @SubscribeEvent
    public static void onWorldTick(LevelTickEvent.Post event) {
        if (!event.getLevel().isClientSide()) {
            DynamicEventManager.tick((ServerLevel) event.getLevel());
        }
    }
}
