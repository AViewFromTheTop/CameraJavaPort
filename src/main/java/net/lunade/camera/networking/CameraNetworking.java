package net.lunade.camera.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.lunade.camera.image_transfer.FileTransferPacket;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class CameraNetworking {

    public static void init() {
        PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.playS2C();
        PayloadTypeRegistry<RegistryFriendlyByteBuf> c2sRegistry = PayloadTypeRegistry.playC2S();

        registry.register(CameraPossessPacket.PACKET_TYPE, CameraPossessPacket.CODEC);

        registry.register(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC);
        c2sRegistry.register(FileTransferPacket.PACKET_TYPE, FileTransferPacket.STREAM_CODEC);

        c2sRegistry.register(PrinterAskForSlotsPacket.PACKET_TYPE, PrinterAskForSlotsPacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(PrinterAskForSlotsPacket.PACKET_TYPE, PrinterAskForSlotsPacket::handle);

        ServerPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE,
                (packet, ctx) -> {
                    if (packet.request()) {
                        Path path = ctx.server().getServerDirectory().resolve(packet.transferPath()).resolve(packet.fileName());
                        try {
                            FileTransferPacket fileTransferPacket = FileTransferPacket.create(packet.transferPath(), path.toFile());
                            ServerPlayNetworking.send(ctx.player(), fileTransferPacket);
                        } catch (IOException ignored) {
                        }
                    } else {
                        try {
                            Path path = ctx.server().getServerDirectory().resolve(packet.transferPath()).resolve(packet.fileName());
                            FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.bytes()), path.toFile());
                        } catch (IOException ignored) {
                        }
                    }
                }
        );
    }
}
