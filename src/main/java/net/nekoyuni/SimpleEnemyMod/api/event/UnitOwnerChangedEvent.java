package net.nekoyuni.SimpleEnemyMod.api.event;

import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.neoforged.bus.api.Event;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Fired on the server when a live PMC unit gets a new owner (e.g. recruited). Not fired while the
 * unit is loaded from disk.
 */
public class UnitOwnerChangedEvent extends Event {

    private final PmcUnitEntity unit;
    @Nullable
    private final UUID oldOwner;
    @Nullable
    private final UUID newOwner;

    public UnitOwnerChangedEvent(PmcUnitEntity unit, @Nullable UUID oldOwner, @Nullable UUID newOwner) {
        this.unit = unit;
        this.oldOwner = oldOwner;
        this.newOwner = newOwner;
    }

    public PmcUnitEntity getUnit() {
        return unit;
    }

    @Nullable
    public UUID getOldOwner() {
        return oldOwner;
    }

    @Nullable
    public UUID getNewOwner() {
        return newOwner;
    }
}
