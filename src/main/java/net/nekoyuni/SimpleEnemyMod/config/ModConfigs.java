package net.nekoyuni.SimpleEnemyMod.config;

import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;

public class ModConfigs {

    public static void register(ModContainer container) {
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC, "sem-client.toml");
        container.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC, "sem-common.toml");
    }
}
