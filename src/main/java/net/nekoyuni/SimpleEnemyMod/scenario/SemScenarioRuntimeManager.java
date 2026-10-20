package net.nekoyuni.SimpleEnemyMod.scenario;

import net.minecraft.world.entity.Entity;

// NeoForge port: upstream's scenario/test suite is not ported. This no-op stub keeps the
// upstream call sites in the AI classes unchanged so future upstream merges stay clean.
public final class SemScenarioRuntimeManager {

    private static final SemScenarioRuntimeManager INSTANCE = new SemScenarioRuntimeManager();

    private SemScenarioRuntimeManager() {
    }

    public static SemScenarioRuntimeManager get() {
        return INSTANCE;
    }

    public void logTrackedEntityEvent(String type, Entity entity, String message) {
    }
}
