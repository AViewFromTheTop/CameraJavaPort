package net.lunade.camera.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraPortClient;
import net.lunade.camera.CameraPortMain;
import net.lunade.camera.entity.CameraEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class CameraEntityRenderer<T extends CameraEntity> extends MobRenderer<T, CameraEntityModel<T>> {

    public CameraEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new CameraEntityModel<>(context.bakeLayer(CameraPortClient.CAMERA_MODEL_LAYER)), 0.5f);
    }

    @Override
    public Vec3 getRenderOffset(@NotNull T entity, float f) {
        return new Vec3(0, entity.getTrackedHeight() - entity.getMaxHeight(), 0);
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
        return ResourceLocation.parse(CameraPortMain.MOD_ID + ":textures/entity/camera.png");
    }
}
