package net.nekoyuni.SimpleEnemyMod.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class UnitAttributesConfig {

    public static ModConfigSpec.DoubleValue UNIT_HEALTH;
    public static ModConfigSpec.DoubleValue UNIT_ARMOR;
    public static ModConfigSpec.DoubleValue UNIT_SPEED;
    public static ModConfigSpec.DoubleValue UNIT_DETECTION_RANGE;

    public static void init(ModConfigSpec.Builder builder){
        builder.push("unit_attributes");

        UNIT_HEALTH = builder
                .comment("Max Health for All Units")
                .defineInRange("health", 20.0, 1.0, 200.0);
        builder.comment("--------------------------------------------------");

        UNIT_ARMOR = builder
                .comment("Unit Armor for All Units")
                .defineInRange("armor", 3.0, 1.0, 100.0);
        builder.comment("--------------------------------------------------");

        UNIT_SPEED = builder
                .comment("Walk Speed for All Units")
                .defineInRange("speed", 0.27, 0.05, 1.5);
        builder.comment("--------------------------------------------------");

        UNIT_DETECTION_RANGE = builder
                .comment("Detection Range for All Units")
                .defineInRange("detectionRange", 96.00, 32.00, 384.0);

        builder.pop();

    }

}
