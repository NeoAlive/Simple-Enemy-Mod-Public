package net.nekoyuni.SimpleEnemyMod.client.system;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = "simpleenemymod", value = Dist.CLIENT)
public class SuppressionManager {

    public static float suppressionLevel = 0.0f;
    private static final float DECAY_RATE = 0.0025f;

    public static void addSuppression(float amount) {
        suppressionLevel += amount;

        if (suppressionLevel > 1.0f) {
            suppressionLevel = 1.0f;
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (suppressionLevel > 0) {
            suppressionLevel -= DECAY_RATE;

            if (suppressionLevel < 0) {
                suppressionLevel = 0.0f;
            }
        }
    }
}
