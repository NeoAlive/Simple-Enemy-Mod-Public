package net.nekoyuni.SimpleEnemyMod.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.entity.ai.orders.ICommandableMob;
import net.nekoyuni.SimpleEnemyMod.entity.ai.orders.OrderType;
import net.nekoyuni.SimpleEnemyMod.entity.unit.PmcUnitEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketIssueOrder(
        int entityId,
        OrderType order,
        Vec3 targetPos,
        int formationIndex,
        int targetEntityId
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketIssueOrder> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, "issue_order"));

    public static final StreamCodec<FriendlyByteBuf, PacketIssueOrder> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeVarInt(msg.entityId);
                buf.writeEnum(msg.order);
                buf.writeDouble(msg.targetPos.x);
                buf.writeDouble(msg.targetPos.y);
                buf.writeDouble(msg.targetPos.z);
                buf.writeVarInt(msg.formationIndex);
                buf.writeVarInt(msg.targetEntityId);
            },
            buf -> new PacketIssueOrder(
                    buf.readVarInt(),
                    buf.readEnum(OrderType.class),
                    new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble()),
                    buf.readVarInt(),
                    buf.readVarInt()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleServer(PacketIssueOrder msg, IPayloadContext context) {
        context.enqueueWork(() -> {
            ServerPlayer sender = (ServerPlayer) context.player();
            if (sender == null) {
                return;
            }

            Entity target = sender.level().getEntity(msg.entityId);
            if (!(target instanceof LivingEntity)) {
                return;
            }

            if (!(target instanceof ICommandableMob commandable)) {
                return;
            }

            if (!sender.getUUID().equals(commandable.getOwnerUUID())) {
                return;
            }

            if (target instanceof PmcUnitEntity pmcUnit) {
                if (msg.order == OrderType.ATTACK_THAT_TARGET) {
                    pmcUnit.setAttackTargetId(msg.targetEntityId);
                } else if (msg.order == OrderType.MOVE_TO_POSITION) {
                    pmcUnit.setMoveToTarget(msg.targetPos);
                }

                if (msg.order == OrderType.FREE_FIRE) {
                    pmcUnit.setAttackTargetId(-1);
                }

                if (msg.order == OrderType.CEASE_FIRE) {
                    pmcUnit.setTarget(null);
                    pmcUnit.setAttackTargetId(-1);
                    pmcUnit.setLastHurtByMob(null);
                }

                pmcUnit.releaseMovementLock();
                pmcUnit.setFormationIndex(msg.formationIndex);
                pmcUnit.resetCommanderGoalCooldown();
            }

            commandable.setOrder(msg.order);
        });
    }
}
