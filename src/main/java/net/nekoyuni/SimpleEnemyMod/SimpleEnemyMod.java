package net.nekoyuni.SimpleEnemyMod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.CreativeModeTabs;
import net.nekoyuni.SimpleEnemyMod.compat.cloth.ClothConfigCompat;
import net.nekoyuni.SimpleEnemyMod.compat.cloth.ClothConfigScreenHelper;
import net.nekoyuni.SimpleEnemyMod.config.ModConfigs;
import net.nekoyuni.SimpleEnemyMod.procedural.events.DynamicEventManager;
import net.nekoyuni.SimpleEnemyMod.registry.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.slf4j.Logger;

@Mod(SimpleEnemyMod.MODID)
public class SimpleEnemyMod {

    public static final String MODID = "simpleenemymod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SimpleEnemyMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::addCreative);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModConfigs.register(modContainer);

        DynamicEventManager.register();

        if (ClothConfigCompat.LOADED) {
            modContainer.registerExtensionPoint(IConfigScreenFactory.class,
                    (IConfigScreenFactory) (minecraft, parentScreen) -> ClothConfigScreenHelper.createConfigScreen(parentScreen));
        }
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            event.accept(ModItems.US_UNIT_SPAWN_EGG.get());
            event.accept(ModItems.US_SQUAD_LEADER_SPAWN_EGG.get());
            event.accept(ModItems.US_SQUAD_UNIT_SPAWN_EGG.get());
            event.accept(ModItems.RU_UNIT_SPAWN_EGG.get());
            event.accept(ModItems.RU_SQUAD_LEADER_SPAWN_EGG.get());
            event.accept(ModItems.RU_SQUAD_UNIT_SPAWN_EGG.get());
            event.accept(ModItems.PMC_UNIT_SPAWN_EGG.get());
            event.accept(ModItems.PMC_SQUAD_LEADER_SPAWN_EGG.get());
            event.accept(ModItems.PMC_SQUAD_UNIT_SPAWN_EGG.get());
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("NYAHELLO from server starting");
    }
}
