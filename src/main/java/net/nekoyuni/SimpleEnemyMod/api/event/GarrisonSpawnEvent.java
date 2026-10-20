package net.nekoyuni.SimpleEnemyMod.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/**
 * Fired before SEM spawns a single village garrison guard. A garrison spawns 2-4 guards around the
 * same {@link #getBasePos()}, each firing this event. Cancel to skip that guard.
 * Fired while SEM iterates its pending-garrison queue: do not spawn entities synchronously from here
 * (defer to the end of the tick instead).
 */
public class GarrisonSpawnEvent extends Event implements ICancellableEvent {

    private final ServerLevel level;
    private final BlockPos basePos;
    private final boolean ru;

    public GarrisonSpawnEvent(ServerLevel level, BlockPos basePos, boolean ru) {
        this.level = level;
        this.basePos = basePos;
        this.ru = ru;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public BlockPos getBasePos() {
        return basePos;
    }

    /** True for an RU garrison, false for US. */
    public boolean isRu() {
        return ru;
    }
}
