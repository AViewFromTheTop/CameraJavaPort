package net.lunade.camera.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lunade.camera.image_transfer.FileTransferPacket;
import net.lunade.camera.image_transfer.ServerTexture;
import net.lunade.camera.impl.ClientCameraManager;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Path;

public class CameraClientNetworking {

    public static void init() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPossessPacket.PACKET_TYPE, (packet, ctx) -> {
            ClientCameraManager.executeScreenshot(ctx.player().level().getEntity(packet.entityId()), false);
        });

        ClientPlayNetworking.registerGlobalReceiver(FileTransferPacket.PACKET_TYPE, (packet, ctx) -> {
            if (packet.request()) {
                Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
                try {
                    FileTransferPacket fileTransferPacket = FileTransferPacket.create(packet.transferPath(), path.toFile());
                    ClientPlayNetworking.send(fileTransferPacket);
                } catch (IOException ignored) {
                }
            } else {
                try {
                    Path path = ctx.client().gameDirectory.toPath().resolve(packet.transferPath()).resolve(packet.fileName());
                    FileUtils.copyInputStreamToFile(new ByteArrayInputStream(packet.bytes()), path.toFile());
                    ServerTexture serverTexture = ServerTexture.WAITING_TEXTURES.get(packet.transferPath() + "/" + packet.fileName());
                    if (serverTexture != null) {
                        serverTexture.runFutureForTexture();
                    }
                } catch (IOException ignored) {
                }
            }
        });
    }
}
