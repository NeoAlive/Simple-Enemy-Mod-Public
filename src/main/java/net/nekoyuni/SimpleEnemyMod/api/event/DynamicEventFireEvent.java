package net.nekoyuni.SimpleEnemyMod.api.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.nekoyuni.SimpleEnemyMod.procedural.events.system.DynamicEvent;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

/** Fired around a procedural {@link DynamicEvent} (patrol, combat, cave extraction, addon events). */
public abstract class DynamicEventFireEvent extends Event {

    private final ServerLevel level;
    private final ServerPlayer player;
    private final DynamicEvent event;
    private final BlockPos pos;

    protected DynamicEventFireEvent(ServerLevel level, ServerPlayer player, DynamicEvent event, BlockPos pos) {
        this.level = level;
        this.player = player;
        this.event = event;
        this.pos = pos;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public DynamicEvent getEvent() {
        return event;
    }

    public BlockPos getPos() {
        return pos;
    }

    /** Before the event executes; cancel to skip it (counts as not executed). */
    public static class Pre extends DynamicEventFireEvent implements ICancellableEvent {
        public Pre(ServerLevel level, ServerPlayer player, DynamicEvent event, BlockPos pos) {
            super(level, player, event, pos);
        }
    }

    /** After the event ran (or was cancelled in Pre, then {@link #isSuccess()} is false). */
    public static class Post extends DynamicEventFireEvent {

        private final boolean success;

        public Post(ServerLevel level, ServerPlayer player, DynamicEvent event, BlockPos pos, boolean success) {
            super(level, player, event, pos);
            this.success = success;
        }

        public boolean isSuccess() {
            return success;
        }
    }
}
