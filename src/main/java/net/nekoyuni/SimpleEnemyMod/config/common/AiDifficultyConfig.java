package net.nekoyuni.SimpleEnemyMod.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class AiDifficultyConfig {

    public static ModConfigSpec.EnumValue<AiDifficultyConfig.AIDifficulty> DIFFICULTY;
    public enum AIDifficulty {
        NORMAL, ADVANCED
    }

    public static void init (ModConfigSpec.Builder builder){
        builder.push("ai_difficulty");

        DIFFICULTY = builder
                .comment(
                        "AI Difficulty preset.",
                        "Does NOT affect soldiers accuracy.",
                        "Only affects tactical maneuver timers and movement speeds.",
                        "",
                        "NORMAL: More static soldiers, slower combat pacing.",
                        "ADVANCED: More dynamic and aggressive soldiers."
                )
                .defineEnum("difficulty", AiDifficultyConfig.AIDifficulty.NORMAL);

        builder.pop();

    }

}
