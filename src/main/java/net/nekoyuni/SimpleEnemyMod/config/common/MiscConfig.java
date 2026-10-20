package net.nekoyuni.SimpleEnemyMod.config.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.minecraft.core.registries.BuiltInRegistries;

public class MiscConfig {

    public static ModConfigSpec.ConfigValue<String> RECRUIT_ITEM;
    public static ModConfigSpec.IntValue RECRUIT_PRICE;

    public static void init(ModConfigSpec.Builder builder){

        builder.push("misc_config");

        RECRUIT_ITEM = builder
                .comment("Item ID required to recruit a PMC")
                .define("recruitItem", "minecraft:emerald");

        RECRUIT_PRICE = builder
                .comment("Amount of items required to recruit a PMC")
                .defineInRange("recruitPrice", 16, 1, 64);

        builder.pop();
    }

    public static Item getRecruitItem() {
        ResourceLocation id = ResourceLocation.parse(RECRUIT_ITEM.get());
        return BuiltInRegistries.ITEM.get(id);
    }

}
