package net.nekoyuni.SimpleEnemyMod.event.server;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.procedural.events.DynamicEventManager;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID)
public class SpawnEventHandler {

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);

        if (overworld == null) return;

        DynamicEventManager.tick(overworld);
    }
}
