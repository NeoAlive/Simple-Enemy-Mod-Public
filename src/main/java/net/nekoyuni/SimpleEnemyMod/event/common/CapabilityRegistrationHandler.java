package net.nekoyuni.SimpleEnemyMod.event.common;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.registry.ModEntities;

// NeoForge replacement for the Forge ITEM_HANDLER capability PmcUnitEntity exposed via getCapability.
// TaCZ's reload and addons (e.g. tacz_sewv) read the PMC inventory through this capability.
// RU/US units intentionally have no item handler, as in upstream.
@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.MOD)
public class CapabilityRegistrationHandler {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(Capabilities.ItemHandler.ENTITY, ModEntities.PMCUNIT.get(),
                (unit, context) -> unit.getInventory());
    }
}
