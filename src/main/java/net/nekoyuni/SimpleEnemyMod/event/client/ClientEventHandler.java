package net.nekoyuni.SimpleEnemyMod.event.client;

import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.client.gui.screens.PmcUnitScreen;
import net.nekoyuni.SimpleEnemyMod.client.input.KeyBindings;
import net.nekoyuni.SimpleEnemyMod.entity.client.pmc_unit.PmcUnitModel;
import net.nekoyuni.SimpleEnemyMod.entity.client.pmc_unit.PmcUnitModelLayers;
import net.nekoyuni.SimpleEnemyMod.entity.client.pmc_unit.PmcUnitRenderer;
import net.nekoyuni.SimpleEnemyMod.entity.client.ru_unit.RUunitModel;
import net.nekoyuni.SimpleEnemyMod.entity.client.ru_unit.RUunitModelLayers;
import net.nekoyuni.SimpleEnemyMod.entity.client.ru_unit.RUunitRenderer;
import net.nekoyuni.SimpleEnemyMod.entity.client.us_unit.USunitModel;
import net.nekoyuni.SimpleEnemyMod.entity.client.us_unit.USunitModelLayers;
import net.nekoyuni.SimpleEnemyMod.entity.client.us_unit.USunitRenderer;
import net.nekoyuni.SimpleEnemyMod.registry.ModEntities;
import net.nekoyuni.SimpleEnemyMod.registry.ModMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onRegisterLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(USunitModelLayers.USUNIT_LAYER, USunitModel::createBodyLayer);
        event.registerLayerDefinition(RUunitModelLayers.RUUNIT_LAYER, RUunitModel::createBodyLayer);
        event.registerLayerDefinition(PmcUnitModelLayers.PMCUNIT_LAYER, PmcUnitModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(ModEntities.USUNIT.get(), USunitRenderer::new);
        event.registerEntityRenderer(ModEntities.RUUNIT.get(), RUunitRenderer::new);
        event.registerEntityRenderer(ModEntities.PMCUNIT.get(), PmcUnitRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.PMC_UNIT_MENU.get(), PmcUnitScreen::new);
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(KeyBindings.COMMANDER_MENU_KEY);
    }
}
