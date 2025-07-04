package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.client.model.CameraModel;
import net.lunade.camera.client.model.DiscCameraModel;
import net.lunade.camera.client.renderer.entity.CameraRenderer;
import net.lunade.camera.client.renderer.entity.DiscCameraRenderer;
import net.lunade.camera.networking.CameraClientNetworking;
import net.lunade.camera.registry.CameraPortEntityTypes;
import net.lunade.camera.registry.CameraPortScreens;
import net.minecraft.client.model.geom.ModelLayerLocation;

@Environment(EnvType.CLIENT)
public class CameraPortClient implements ClientModInitializer {
	public static ModelLayerLocation CAMERA_MODEL_LAYER = new ModelLayerLocation(CameraPortConstants.id("camera"), "main");
	public static ModelLayerLocation DISC_CAMERA_MODEL_LAYER = new ModelLayerLocation(CameraPortConstants.id("disc_camera"), "main");

	@Override
	public void onInitializeClient() {
		EntityRendererRegistry.register(CameraPortEntityTypes.CAMERA, CameraRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraModel::createBodyLayer);
		EntityRendererRegistry.register(CameraPortEntityTypes.DISC_CAMERA, DiscCameraRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(DISC_CAMERA_MODEL_LAYER, DiscCameraModel::getTexturedModelData);

		CameraPortScreens.init();

		CameraClientNetworking.init();
	}
}
