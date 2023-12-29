package net.lunade.camera.entity.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.MainClient;
import net.lunade.camera.entity.SmallCameraEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class SmallCameraEntityRenderer extends MobEntityRenderer<SmallCameraEntity, SmallCameraEntityModel<SmallCameraEntity>> {

    public SmallCameraEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new SmallCameraEntityModel<>(context.getPart(MainClient.SMALL_CAMERA_MODEL_LAYER)), 0.5f);
    }

    @Override
    public void render(SmallCameraEntity cameraEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i) {
        super.render(cameraEntity, f, g, matrixStack, vertexConsumerProvider, i);
    }

    @Override
    public float getAnimationCounter(SmallCameraEntity cameraEntity, float f) {
        float g = cameraEntity.getLerpedTimer(f);
        return (int)(g * 10.0F) % 2 == 0 ? 0.0F : MathHelper.clamp(g, 0.5F, 1.0F);
    }

    @Override
    public Identifier getTexture(SmallCameraEntity entity) {
        return new Identifier("camera", "textures/entity/camera.png");
    }
}