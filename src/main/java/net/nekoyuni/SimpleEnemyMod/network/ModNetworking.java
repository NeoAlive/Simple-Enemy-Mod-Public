package net.nekoyuni.SimpleEnemyMod.network;

import com.mojang.logging.LogUtils;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.nekoyuni.SimpleEnemyMod.network.packets.PacketIssueOrder;
import net.nekoyuni.SimpleEnemyMod.network.packets.PacketPlayImpactSound;
import net.nekoyuni.SimpleEnemyMod.network.packets.PacketSuppression;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.slf4j.Logger;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String PROTOCOL_VERSION = "1.0.1";

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);

        registrar.playToClient(
                PacketPlayImpactSound.TYPE,
                PacketPlayImpactSound.STREAM_CODEC,
                PacketPlayImpactSound::handleClient
        );

        registrar.playToServer(
                PacketIssueOrder.TYPE,
                PacketIssueOrder.STREAM_CODEC,
                PacketIssueOrder::handleServer
        );

        registrar.playToClient(
                PacketSuppression.TYPE,
                PacketSuppression.STREAM_CODEC,
                PacketSuppression::handleClient
        );

        LOGGER.info("[SEM] Network payloads registered. Protocol version: {}", PROTOCOL_VERSION);
    }

    public static void sendToPlayer(CustomPacketPayload packet, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, packet);
    }

    public static void sendToServer(CustomPacketPayload packet) {
        PacketDistributor.sendToServer(packet);
    }
}
