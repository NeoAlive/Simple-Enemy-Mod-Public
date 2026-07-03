package net.nekoyuni.SimpleEnemyMod.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.network.ClientPacketHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSuppression(float amount) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketSuppression> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, "suppression"));

    public static final StreamCodec<FriendlyByteBuf, PacketSuppression> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> buf.writeFloat(msg.amount),
            buf -> new PacketSuppression(buf.readFloat())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(PacketSuppression msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleSuppression(msg));
    }
}
