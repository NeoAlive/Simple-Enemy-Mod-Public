package net.nekoyuni.SimpleEnemyMod.event.common;

import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.commands.SemEventCommand;
import net.nekoyuni.SimpleEnemyMod.registry.ModCommands;

import static com.mojang.text2speech.Narrator.LOGGER;

@EventBusSubscriber(modid = SimpleEnemyMod.MODID, bus = EventBusSubscriber.Bus.GAME)
public class CommandRegistrationHandler {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());

    }

}
