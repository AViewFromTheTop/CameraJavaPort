package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.entity.render.CameraEntityModel;
import net.lunade.camera.entity.render.CameraEntityRenderer;
import net.lunade.camera.entity.render.DiscCameraEntityModel;
import net.lunade.camera.entity.render.DiscCameraEntityRenderer;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

    public static ModelLayerLocation CAMERA_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(CameraMain.MOD_ID, "camera"), "main");
    public static ModelLayerLocation SMALL_CAMERA_MODEL_LAYER = new ModelLayerLocation(new ResourceLocation(CameraMain.MOD_ID, "disc_camera"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(CameraMain.CAMERA, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::createBodyLayer);
        EntityRendererRegistry.register(CameraMain.SMALL_CAMERA, DiscCameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SMALL_CAMERA_MODEL_LAYER, DiscCameraEntityModel::getTexturedModelData);
        CameraPossessPacket.receive();

        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            ClientCameraManager.tick();
        });
    }
}
