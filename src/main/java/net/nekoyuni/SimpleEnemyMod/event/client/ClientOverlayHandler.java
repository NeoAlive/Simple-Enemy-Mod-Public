package net.nekoyuni.SimpleEnemyMod.event.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.client.system.SuppressionManager;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientOverlayHandler {

    private static final ResourceLocation SUPPRESSION_TEXTURE =
            ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, "textures/gui/suppression_effect.png");

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiLayersEvent event) {
        event.registerAboveAll(
                ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, "suppression_overlay"),
                (guiGraphics, deltaTracker) -> renderSuppressionOverlay(guiGraphics)
        );
    }

    private static void renderSuppressionOverlay(GuiGraphics guiGraphics) {
        float level = SuppressionManager.suppressionLevel;

        if (level <= 0.01f) {
            return;
        }

        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, level);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, SUPPRESSION_TEXTURE);

        guiGraphics.blit(SUPPRESSION_TEXTURE, 0, 0, screenWidth, screenHeight, 0.0F, 0.0F,
                screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}
