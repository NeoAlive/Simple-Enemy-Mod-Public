package net.nekoyuni.SimpleEnemyMod.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class EffectConfig {

    public static ModConfigSpec.BooleanValue ENABLE_SUPPRESSION;

    public static void init (ModConfigSpec.Builder builder) {
        builder.push("Visual Effects");

        ENABLE_SUPPRESSION = builder
                .comment("Activate or deactivate the visual suppression effect")
                .define("enableSuppression", true);

        builder.pop();

    }

}
