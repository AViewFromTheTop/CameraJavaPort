package net.lunade.camera.networking;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.frozenblock.lib.file.transfer.FileTransferFilter;
import net.minecraft.network.RegistryFriendlyByteBuf;

public class CameraNetworking {

	public static void init() {
		PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.playS2C();
		PayloadTypeRegistry<RegistryFriendlyByteBuf> c2sRegistry = PayloadTypeRegistry.playC2S();

		registry.register(CameraPossessPacket.PACKET_TYPE, CameraPossessPacket.CODEC);
		c2sRegistry.register(PrinterAskForSlotsPacket.PACKET_TYPE, PrinterAskForSlotsPacket.CODEC);
		ServerPlayNetworking.registerGlobalReceiver(PrinterAskForSlotsPacket.PACKET_TYPE, PrinterAskForSlotsPacket::handle);

		FileTransferFilter.whitelistDestinationPath("photographs", false);
		FileTransferFilter.whitelistDestinationPath("photographs", true);

		FileTransferFilter.whitelistRequestPath("photographs", false);
		FileTransferFilter.whitelistRequestPath("photographs", true);
	}
}
