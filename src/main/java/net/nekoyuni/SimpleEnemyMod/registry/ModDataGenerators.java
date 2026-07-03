package net.nekoyuni.SimpleEnemyMod.registry;

import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.compat.curios.CuriosCompat;
import net.nekoyuni.SimpleEnemyMod.compat.curios.SimpleDataGenerators;

@EventBusSubscriber(modid = "simpleenemymod", bus = EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {

        if (CuriosCompat.LOADED) {
            SimpleDataGenerators.onGatherData(event);
        }

    }
}
