package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.entity.render.CameraEntityModel;
import net.lunade.camera.entity.render.CameraEntityRenderer;
import net.lunade.camera.entity.render.DiscCameraEntityModel;
import net.lunade.camera.entity.render.DiscCameraEntityRenderer;
import net.lunade.camera.impl.ClientCameraManager;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class CameraPortClient implements ClientModInitializer {

    public static ModelLayerLocation CAMERA_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(CamerPortMain.MOD_ID, "camera"), "main");
    public static ModelLayerLocation DISC_CAMERA_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(CamerPortMain.MOD_ID, "disc_camera"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(CamerPortMain.CAMERA, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::createBodyLayer);
        EntityRendererRegistry.register(CamerPortMain.DISC_CAMERA, DiscCameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DISC_CAMERA_MODEL_LAYER, DiscCameraEntityModel::getTexturedModelData);
        receiveCameraPossessPacket();

        ClientTickEvents.START_CLIENT_TICK.register(client -> ClientCameraManager.tick());
    }

    public static void receiveCameraPossessPacket() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPossessPacket.PACKET_TYPE, (packet, ctx) -> {
            ClientCameraManager.changeToCamera(ctx.player().level().getEntity(packet.entityId()), false);
        });
    }
}
