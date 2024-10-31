package net.lunade.camera.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.menu.PrinterMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.NotNull;

public record PrinterAskForSlotsPacket(int count, String id) implements CustomPacketPayload {
    public static final Type<PrinterAskForSlotsPacket> PACKET_TYPE = CustomPacketPayload.createType(
            CameraPortConstants.safeString("printer_ask_for_slots")
    );
    public static final StreamCodec<FriendlyByteBuf, PrinterAskForSlotsPacket> CODEC = StreamCodec.ofMember(PrinterAskForSlotsPacket::write, PrinterAskForSlotsPacket::new);

    public PrinterAskForSlotsPacket(@NotNull FriendlyByteBuf buf) {
        this(buf.readInt(), buf.readUtf());
    }

    public void write(@NotNull FriendlyByteBuf buf) {
        buf.writeInt(count);
        buf.writeUtf(id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_TYPE;
    }

    public static void handle(PrinterAskForSlotsPacket packet, ServerPlayNetworking.@NotNull Context context) {
        ServerPlayer player = context.player();
        if (player != null) {
            if (player.containerMenu instanceof PrinterMenu printer) {
                printer.setupData(packet.count, packet.id);
            }
        }
    }
}
