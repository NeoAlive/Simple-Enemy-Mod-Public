package net.nekoyuni.SimpleEnemyMod.config.common;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DropsConfig {

    public static ModConfigSpec.BooleanValue ENABLE_CUSTOM_DROPS;
    public static ModConfigSpec.DoubleValue GUN_DROP_CHANCE;
    public static ModConfigSpec.DoubleValue AMMO_DROP_CHANCE;

    public static void init(ModConfigSpec.Builder builder){
        builder.push("Drops");

        ENABLE_CUSTOM_DROPS = builder
                .comment("When enabled, Units may drop cleaned TaCZ weapons/ammo using the chances below.", "When disabled, Units drop no guns (vanilla equipment drops are suppressed).")
                .define("enableCustomDrops", true);

        GUN_DROP_CHANCE = builder
                .comment("Probability that a Unit will drop its TACZ weapon upon death (0.0 to 1.0)")
                .defineInRange("gunDropChance", 1.0, 0.0, 1.0);

        AMMO_DROP_CHANCE = builder
                .comment("Probability that a Unit will drop extra ammo upon death (0.0 to 1.0).")
                .defineInRange("ammoDropChance", 0.5, 0.0, 1.0);

        builder.pop();

    }
}
