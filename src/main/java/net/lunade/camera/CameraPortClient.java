package net.lunade.camera;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.lunade.camera.entity.render.CameraEntityModel;
import net.lunade.camera.entity.render.CameraEntityRenderer;
import net.lunade.camera.entity.render.DiscCameraEntityModel;
import net.lunade.camera.entity.render.DiscCameraEntityRenderer;
import net.lunade.camera.entity.render.PhotographEntityRenderer;
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
		EntityRendererRegistry.register(CameraPortEntityTypes.CAMERA, CameraEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(CAMERA_MODEL_LAYER, CameraEntityModel::createBodyLayer);
		EntityRendererRegistry.register(CameraPortEntityTypes.DISC_CAMERA, DiscCameraEntityRenderer::new);
		EntityModelLayerRegistry.registerModelLayer(DISC_CAMERA_MODEL_LAYER, DiscCameraEntityModel::getTexturedModelData);
		EntityRendererRegistry.register(CameraPortEntityTypes.PHOTOGRAPH, PhotographEntityRenderer::new);

		CameraPortScreens.init();

		CameraClientNetworking.init();
	}
}
