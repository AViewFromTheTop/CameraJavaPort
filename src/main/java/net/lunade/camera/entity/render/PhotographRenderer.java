package net.lunade.camera.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.entity.Photograph;
import net.lunade.camera.image_transfer.ServerTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class PhotographRenderer extends EntityRenderer<Photograph> {

    public PhotographRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Photograph photograph, float f, float g, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int i) {
        if (photograph.serverTexture == null) {
            photograph.serverTexture = new ServerTexture(
                    "photographs",
                    photograph.getPhotographName() + ".png",
                    CameraConstants.id("photographs/empty"),
                    () -> {
                    }
            );
            Minecraft.getInstance().getTextureManager().register(CameraConstants.id("photographs/" + photograph.getPhotographName()), photograph.serverTexture);
        }
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180F - f));
        VertexConsumer photoConsumer = vertexConsumers.getBuffer(RenderType.entitySolid(this.getPhotographLocation(photograph)));
        this.renderPhotograph(
                matrices,
                photoConsumer,
                photograph
        );
        matrices.popPose();
        super.render(photograph, f, g, matrices, vertexConsumers, i);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Photograph photograph) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    public ResourceLocation getPhotographLocation(@NotNull Photograph photograph) {
        return CameraConstants.id("photographs/" + photograph.getPhotographName());
    }

    private void renderPhotograph(
            @NotNull PoseStack matrices, VertexConsumer photoConsumer, Photograph entity
    ) {
        PoseStack.Pose pose = matrices.last();
        float f = (float) (-2D) / 2.0F;
        float g = (float) (-2D) / 2.0F;
        double d = 1.0 / (double) (int) 2.0;
        double e = 1.0 / (double) (int) 2.0;

        for (int u = 0; u < (int) 2.0; u++) {
            for (int v = 0; v < (int) 2.0; v++) {
                float w = f + (float) (u + 1);
                float x = f + (float) u;
                float y = g + (float) (v + 1);
                float z = g + (float) v;
                int aa = entity.getBlockX();
                int ab = Mth.floor(entity.getY() + (double) ((y + z) / 2.0F));
                int ac = entity.getBlockZ();
                Direction direction = entity.getDirection();
                if (direction == Direction.NORTH) {
                    aa = Mth.floor(entity.getX() + (double) ((w + x) / 2.0F));
                }

                if (direction == Direction.WEST) {
                    ac = Mth.floor(entity.getZ() - (double) ((w + x) / 2.0F));
                }

                if (direction == Direction.SOUTH) {
                    aa = Mth.floor(entity.getX() - (double) ((w + x) / 2.0F));
                }

                if (direction == Direction.EAST) {
                    ac = Mth.floor(entity.getZ() + (double) ((w + x) / 2.0F));
                }

                int ad = LevelRenderer.getLightColor(entity.level(), new BlockPos(aa, ab, ac));

                float size = entity.getSize();
                float ae = (float)(d * (size - u));
                float af = (float)(d * (size - (u + 1F)));
                float ag = (float)(e * (size - v));
                float ah = (float)(e * (size - (v + 1F)));
                this.vertex(pose, photoConsumer, w, z, af, ag, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, x, z, ae, ag, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, x, y, ae, ah, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, w, y, af, ah, -0.03125F, 0, 0, -1, ad);
            }
        }
    }

    private void vertex(
            PoseStack.Pose pose, @NotNull VertexConsumer vertexConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
    ) {
        vertexConsumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, (float) normalX, (float) normalY, (float) normalZ);
    }
}
