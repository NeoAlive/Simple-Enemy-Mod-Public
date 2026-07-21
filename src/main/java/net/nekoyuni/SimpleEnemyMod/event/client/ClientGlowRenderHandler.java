package net.nekoyuni.SimpleEnemyMod.event.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.nekoyuni.SimpleEnemyMod.client.system.ClientGlowManager;
import net.nekoyuni.SimpleEnemyMod.entity.unit.AbstractUnit;
import org.joml.Vector3f;

@EventBusSubscriber(modid = "simpleenemymod", value = Dist.CLIENT)
public class ClientGlowRenderHandler {

    private static final int PARTICLE_COUNT = 12;
    private static final double RADIUS = 0.6;
    private static final int TICK_INTERVAL = 5;
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        tickCounter++;
        if (tickCounter % TICK_INTERVAL != 0) return;

        for (int id : ClientGlowManager.getAll()) {
            Entity entity = mc.level.getEntity(id);
            if (entity == null) continue;

            spawnCircle(mc.level, entity);
        }
    }

    private static void spawnCircle(Level level, Entity entity) {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            double angle = (2 * Math.PI / PARTICLE_COUNT) * i;
            double offsetX = Math.cos(angle) * RADIUS;
            double offsetZ = Math.sin(angle) * RADIUS;

            level.addParticle(
                    new DustParticleOptions(new Vector3f(0.0f, 1.0f, 0.3f), 1.0f),
                    entity.getX() + offsetX,
                    entity.getY() + 0.05,
                    entity.getZ() + offsetZ,
                    0, 0, 0
            );
        }
    }

    @SubscribeEvent
    public static void onRenderLiving(RenderLivingEvent.Pre<?, ?> event) {
        if (event.getEntity() instanceof AbstractUnit unit) {
            // Drive the outline every frame from selection membership so deselecting clears it.
            unit.setGlowingFlag(ClientGlowManager.shouldGlow(unit.getId()));
        }
    }
}
