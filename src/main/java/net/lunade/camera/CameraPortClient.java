package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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

    public static ModelLayerLocation CAMERA_MODEL_LAYER = new ModelLayerLocation(ResourceLocation.tryBuild(CameraPortMain.MOD_ID, "camera"), "main");
    public static ModelLayerLocation DISC_CAMERA_MODEL_LAYER = new ModelLayerLocation(ResourceLocation.tryBuild(CameraPortMain.MOD_ID, "disc_camera"), "main");

    public static void receiveCameraPossessPacket() {
        ClientPlayNetworking.registerGlobalReceiver(CameraPossessPacket.PACKET_TYPE, (packet, ctx) -> {
            ClientCameraManager.executeScreenshot(ctx.player().level().getEntity(packet.entityId()), false);
        });
    }

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(CameraPortMain.CAMERA, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::createBodyLayer);
        EntityRendererRegistry.register(CameraPortMain.DISC_CAMERA, DiscCameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DISC_CAMERA_MODEL_LAYER, DiscCameraEntityModel::getTexturedModelData);
        receiveCameraPossessPacket();
    }
}
