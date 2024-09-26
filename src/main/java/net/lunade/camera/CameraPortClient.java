package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.entity.render.*;
import net.lunade.camera.networking.CameraClientNetworking;
import net.lunade.camera.registry.CameraEntityTypes;
import net.minecraft.client.model.geom.ModelLayerLocation;

@Environment(EnvType.CLIENT)
public class CameraPortClient implements ClientModInitializer {
    public static ModelLayerLocation CAMERA_MODEL_LAYER = new ModelLayerLocation(CameraConstants.id("camera"), "main");
    public static ModelLayerLocation DISC_CAMERA_MODEL_LAYER = new ModelLayerLocation(CameraConstants.id("disc_camera"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(CameraEntityTypes.CAMERA, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::createBodyLayer);
        EntityRendererRegistry.register(CameraEntityTypes.DISC_CAMERA, DiscCameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(DISC_CAMERA_MODEL_LAYER, DiscCameraEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(CameraEntityTypes.PHOTOGRAPH, PhotographEntityRenderer::new);

        CameraClientNetworking.init();
    }
}
