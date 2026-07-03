package net.nekoyuni.SimpleEnemyMod.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ClientConfig {

    public static final ModConfigSpec SPEC;
    public static ModConfigSpec.IntValue RENDER_DISTANCE;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        builder.push("unit_render_distance");

        RENDER_DISTANCE = builder
                .comment(
                        "Max Render Distance (in Blocks) for Units",
                        "Important!!: Render Distance => Detection Range"
                )
                .defineInRange("renderDistance", 128,32,192);

        builder.pop();

        SPEC = builder.build();
    }

}
