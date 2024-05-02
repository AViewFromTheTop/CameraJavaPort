package net.lunade.camera.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CamerPortMain;
import net.lunade.camera.CameraPortClient;
import net.lunade.camera.entity.DiscCameraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class DiscCameraEntityRenderer<T extends DiscCameraEntity> extends MobRenderer<T, DiscCameraEntityModel<T>> {

    public DiscCameraEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DiscCameraEntityModel<>(context.bakeLayer(CameraPortClient.DISC_CAMERA_MODEL_LAYER)), 0.5f);
    }

    @Override
    public float getWhiteOverlayProgress(@NotNull T entity, float f) {
        float timer = entity.getLerpedTimer(f);
        float timedTimer = (timer * (float) Math.PI) * 0.1F;
        float sin = (float) (Math.sin(timedTimer - (float) Math.PI * 0.5F) + 1F) * 0.5F;
        return sin;
    }

    @NotNull
    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(CamerPortMain.MOD_ID, "textures/entity/camera.png");
    }
}