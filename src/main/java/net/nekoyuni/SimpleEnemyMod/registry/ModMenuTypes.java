package net.nekoyuni.SimpleEnemyMod.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.inventory.PmcUnitMenu;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, SimpleEnemyMod.MODID);

    public static final DeferredHolder<MenuType<?>, MenuType<PmcUnitMenu>> PMC_UNIT_MENU =
            MENUS.register("pmc_unit_menu", () -> IMenuTypeExtension.create(PmcUnitMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
