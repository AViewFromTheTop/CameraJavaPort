package net.lunade.camera.entity.render.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraPortClient;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.entity.CameraEntity;
import net.lunade.camera.entity.render.model.CameraEntityModel;
import net.lunade.camera.entity.render.state.CameraRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class CameraEntityRenderer extends MobRenderer<CameraEntity, CameraRenderState, CameraEntityModel> {
	private static final ResourceLocation TEXTURE = CameraPortConstants.id("textures/entity/camera.png");

	public CameraEntityRenderer(EntityRendererProvider.Context context) {
		super(context, new CameraEntityModel(context.bakeLayer(CameraPortClient.CAMERA_MODEL_LAYER)), 0.5F);
	}

	@Override
	public @NotNull CameraRenderState createRenderState() {
		return new CameraRenderState();
	}

	@Override
	public void extractRenderState(CameraEntity entity, CameraRenderState renderState, float partialTick) {
		super.extractRenderState(entity, renderState, partialTick);
		renderState.trackedHeight = entity.getTrackedHeight();
		renderState.lerpedTimer = entity.getLerpedTimer(partialTick);
	}

	@Override
	protected float getWhiteOverlayProgress(@NotNull CameraRenderState renderState) {
		float timer = renderState.lerpedTimer;
		float timedTimer = (timer * (float) Math.PI) * 0.1F;
		float sin = (float) (Math.sin(timedTimer - (float) Math.PI * 0.5F) + 1F) * 0.5F;
		return sin;
	}

	@NotNull
	@Override
	public ResourceLocation getTextureLocation(CameraRenderState renderState) {
		return TEXTURE;
	}
}
