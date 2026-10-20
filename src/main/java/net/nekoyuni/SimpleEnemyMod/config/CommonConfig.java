package net.nekoyuni.SimpleEnemyMod.config;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.nekoyuni.SimpleEnemyMod.config.common.*;

public final class CommonConfig {

    public static final ModConfigSpec SPEC;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();

        GeneralConfig.init(builder);
        FactionsConfig.init(builder);
        UnitAttributesConfig.init(builder);

        AiDifficultyConfig.init(builder);
        AiShootingConfig.init(builder);
        TacticalAiConfig.init(builder);

        EventSpawnConfig.init(builder);
        DropsConfig.init(builder);
        EffectConfig.init(builder);
        MiscConfig.init(builder);

        SPEC = builder.build();
    }
}
