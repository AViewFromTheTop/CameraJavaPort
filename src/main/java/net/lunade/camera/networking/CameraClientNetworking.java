package net.lunade.camera.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lunade.camera.impl.client.CameraScreenshotManager;

public class CameraClientNetworking {

	public static void init() {
		ClientPlayNetworking.registerGlobalReceiver(CameraPossessPacket.PACKET_TYPE, (packet, ctx) -> {
			CameraScreenshotManager.executeScreenshot(ctx.player().level().getEntity(packet.entityId()), false);
		});
	}
}
