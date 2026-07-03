package net.nekoyuni.SimpleEnemyMod.event.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.ViewportEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.client.system.SuppressionManager;

@EventBusSubscriber(modid = "simpleenemymod", value = Dist.CLIENT)
public class ClientCameraHandler {

    @SubscribeEvent
    public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
        float level = SuppressionManager.suppressionLevel;

        if (level > 0.1f) {

            double time = (Minecraft.getInstance().level.getGameTime() + event.getPartialTick()) * 0.05;

            float rollAmount = (float) Math.sin(time) * 2.0f * level;
            event.setRoll(event.getRoll() + rollAmount);

            float shakeAmount = (float) Math.cos(time * 2.5) * 0.5f * level;

            event.setYaw(event.getYaw() + shakeAmount);
            event.setPitch(event.getPitch() + (shakeAmount * 0.5f));
        }
    }

}
