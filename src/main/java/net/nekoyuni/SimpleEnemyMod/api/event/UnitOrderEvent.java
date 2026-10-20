package net.nekoyuni.SimpleEnemyMod.api.event;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.nekoyuni.SimpleEnemyMod.entity.ai.orders.OrderType;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Fired on the server when a player's order for an owned unit passed SEM's ownership check, before the
 * unit is changed. Cancel to reject it; an optional failure message is shown to the sender.
 * Orders can be replayed with {@code PacketIssueOrder.handleOnServer}.
 */
public class UnitOrderEvent extends Event implements ICancellableEvent {

    private final LivingEntity unit;
    private final ServerPlayer sender;
    private final OrderType order;
    private final Vec3 targetPos;
    private final int formationIndex;
    private final int targetEntityId;
    @Nullable
    private Component failureMessage;

    public UnitOrderEvent(LivingEntity unit, ServerPlayer sender, OrderType order, Vec3 targetPos,
                          int formationIndex, int targetEntityId) {
        this.unit = unit;
        this.sender = sender;
        this.order = order;
        this.targetPos = targetPos;
        this.formationIndex = formationIndex;
        this.targetEntityId = targetEntityId;
    }

    /** The commanded unit; an ICommandableMob (in SEM, a PmcUnitEntity). */
    public LivingEntity getUnit() {
        return unit;
    }

    public ServerPlayer getSender() {
        return sender;
    }

    public OrderType getOrder() {
        return order;
    }

    public Vec3 getTargetPos() {
        return targetPos;
    }

    public int getFormationIndex() {
        return formationIndex;
    }

    public int getTargetEntityId() {
        return targetEntityId;
    }

    @Nullable
    public Component getFailureMessage() {
        return failureMessage;
    }

    /** Cancels the order and shows {@code message} to the sender on the action bar. */
    public void reject(Component message) {
        this.failureMessage = message;
        this.setCanceled(true);
    }
}
