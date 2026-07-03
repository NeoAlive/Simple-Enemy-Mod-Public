package net.nekoyuni.SimpleEnemyMod.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.nekoyuni.SimpleEnemyMod.SimpleEnemyMod;
import net.nekoyuni.SimpleEnemyMod.network.ClientPacketHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketPlayImpactSound(
        double x,
        double y,
        double z,
        float volume,
        float pitch,
        SoundSource source,
        long timestamp
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<PacketPlayImpactSound> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimpleEnemyMod.MODID, "play_impact_sound"));

    public static final StreamCodec<FriendlyByteBuf, PacketPlayImpactSound> STREAM_CODEC = StreamCodec.of(
            (buf, msg) -> {
                buf.writeDouble(msg.x);
                buf.writeDouble(msg.y);
                buf.writeDouble(msg.z);
                buf.writeFloat(msg.volume);
                buf.writeFloat(msg.pitch);
                buf.writeEnum(msg.source);
                buf.writeLong(msg.timestamp);
            },
            buf -> new PacketPlayImpactSound(
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readDouble(),
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readEnum(SoundSource.class),
                    buf.readLong()
            )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(PacketPlayImpactSound msg, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.handleImpactSound(msg));
    }
}
