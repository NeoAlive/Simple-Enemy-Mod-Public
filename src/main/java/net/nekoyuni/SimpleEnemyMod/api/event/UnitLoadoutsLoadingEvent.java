package net.nekoyuni.SimpleEnemyMod.api.event;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;

import java.util.Map;

/**
 * Fired when SEM (re)loads {@code unit_loadouts} datapack files, before they are parsed. The map is
 * mutable: keys are {@code <namespace>:<faction>/<file>} (e.g. {@code simpleenemymod:ru_units/loadouts}),
 * values the raw JSON. Add, replace or edit entries to change the loadout pools.
 */
public class UnitLoadoutsLoadingEvent extends Event {

    private final Map<ResourceLocation, JsonElement> files;

    public UnitLoadoutsLoadingEvent(Map<ResourceLocation, JsonElement> files) {
        this.files = files;
    }

    public Map<ResourceLocation, JsonElement> getFiles() {
        return files;
    }
}
