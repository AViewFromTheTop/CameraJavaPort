package net.lunade.camera.networking;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.lunade.camera.CameraMain;
import net.lunade.camera.ClientCameraManager;
import net.lunade.camera.entity.CameraEntity;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record CameraPossessPacket(int entityId) implements FabricPacket {

	public static final PacketType<CameraPossessPacket> PACKET_TYPE = PacketType.create(
			new ResourceLocation(CameraMain.MOD_ID, "camera_possess"),
			CameraPossessPacket::new
	);

	public CameraPossessPacket(@NotNull FriendlyByteBuf buf) {
		this(buf.readVarInt());
	}

	public static void sendTo(@NotNull ServerPlayer serverPlayer, @NotNull CameraEntity cameraEntity) {
		CameraPossessPacket sensorHiccupPacket = new CameraPossessPacket(
				cameraEntity.getId()
		);
		ServerPlayNetworking.send(serverPlayer, sensorHiccupPacket);
	}

	@Environment(EnvType.CLIENT)
	public static void receive() {
		ClientPlayNetworking.registerGlobalReceiver(PACKET_TYPE, (packet, player, responseSender) -> {
			ClientLevel clientLevel = player.clientLevel;
            ClientCameraManager.changeToCamera(clientLevel.getEntity(packet.entityId()), false);
        });
	}

	@Override
	public void write(@NotNull FriendlyByteBuf buf) {
		buf.writeVarInt(this.entityId);
	}

	@Override
	public PacketType<?> getType() {
		return PACKET_TYPE;
	}
}
