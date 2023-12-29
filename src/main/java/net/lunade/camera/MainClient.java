package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.entity.render.CameraEntityModel;
import net.lunade.camera.entity.render.CameraEntityRenderer;
import net.lunade.camera.entity.render.SmallCameraEntityModel;
import net.lunade.camera.entity.render.SmallCameraEntityRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class MainClient implements ClientModInitializer {

    public static EntityModelLayer CAMERA_MODEL_LAYER = new EntityModelLayer(new Identifier("camera", "camera"), "main");
    public static EntityModelLayer SMALL_CAMERA_MODEL_LAYER = new EntityModelLayer(new Identifier("camera", "small_camera"), "main");

    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.register(Main.CAMERA, CameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::getTexturedModelData);
        EntityRendererRegistry.register(Main.SMALL_CAMERA, SmallCameraEntityRenderer::new);
        EntityModelLayerRegistry.registerModelLayer(SMALL_CAMERA_MODEL_LAYER, SmallCameraEntityModel::getTexturedModelData);
    }
}
