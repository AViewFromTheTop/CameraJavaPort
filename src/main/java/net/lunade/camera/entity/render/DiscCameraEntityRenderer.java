package net.lunade.camera.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraMain;
import net.lunade.camera.MainClient;
import net.lunade.camera.entity.DiscCameraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class DiscCameraEntityRenderer<T extends DiscCameraEntity> extends MobRenderer<T, DiscCameraEntityModel<T>> {

    public DiscCameraEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new DiscCameraEntityModel<>(context.bakeLayer(MainClient.SMALL_CAMERA_MODEL_LAYER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(T entity) {
        return new ResourceLocation(CameraMain.MOD_ID, "camera");
    }

    @Override
    public float getWhiteOverlayProgress(@NotNull T entity, float f) {
        float timer = entity.getLerpedTimer(f);
        float timedTimer = (timer * (float) Math.PI) * 0.1F;
        float sin = (float) (Math.sin(timedTimer - (float) Math.PI * 0.5F) + 1F) * 0.5F;
        return sin;
    }

}