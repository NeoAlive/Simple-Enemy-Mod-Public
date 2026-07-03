package net.nekoyuni.SimpleEnemyMod.client.input;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.client.gui.screens.CommanderMenuScreen;

@EventBusSubscriber(modid = "simpleenemymod", value = Dist.CLIENT)
public class KeyInputHandler {

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {

        if (KeyBindings.COMMANDER_MENU_KEY.consumeClick()) {

             if (net.minecraft.client.gui.screens.Screen.hasControlDown()) {
                 Minecraft.getInstance().setScreen(new CommanderMenuScreen());
             }
        }
    }
}
