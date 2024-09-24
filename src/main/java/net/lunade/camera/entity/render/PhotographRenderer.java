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
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
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
    public void render(Photograph photograph, float f, float g, @NotNull PoseStack matrices, @NotNull MultiBufferSource photoConsumers, int i) {
        String photographName = photograph.getPhotographName();
        ResourceLocation photoLocation = this.getPhotographLocation(photograph);
        if (photograph.serverTexture == null && !photographName.isEmpty()) {
            photograph.serverTexture = new ServerTexture(
                    "photographs",
                    photograph.getPhotographName() + ".png",
                    CameraConstants.id("photographs/empty"),
                    () -> {}
            );
            Minecraft.getInstance().getTextureManager().register(photoLocation, photograph.serverTexture);
        }
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180F - f));
        VertexConsumer photoConsumer = photoConsumers.getBuffer(RenderType.entitySolid(photoLocation));
        final var backSprite = Minecraft.getInstance().getPaintingTextures().getBackSprite();
        VertexConsumer backConsumer = photoConsumers.getBuffer(RenderType.entitySolid(backSprite.atlasLocation()));
        this.renderPhotograph(
                matrices,
                photoConsumer,
                photograph,
                backConsumer,
                backSprite
        );
        matrices.popPose();
        super.render(photograph, f, g, matrices, photoConsumers, i);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Photograph photograph) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    public ResourceLocation getPhotographLocation(@NotNull Photograph photograph) {
        return CameraConstants.id("photographs/" + photograph.getPhotographName());
    }

    private void renderPhotograph(
            @NotNull PoseStack matrices, VertexConsumer photoConsumer, Photograph entity, VertexConsumer backConsumer, TextureAtlasSprite backSprite
    ) {
        PoseStack.Pose pose = matrices.last();
        float f = (float) (-2D) / 2.0F;
        float g = (float) (-2D) / 2.0F;
        float h = 0.03125F;
        float i = backSprite.getU0();
        float j = backSprite.getU1();
        float k = backSprite.getV0();
        float l = backSprite.getV1();
        float m = backSprite.getU0();
        float n = backSprite.getU1();
        float o = backSprite.getV0();
        float p = backSprite.getV(0.0625F);
        float q = backSprite.getU0();
        float r = backSprite.getU(0.0625F);
        float s = backSprite.getV0();
        float t = backSprite.getV1();
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
                matrices.pushPose();
                this.vertex(pose, photoConsumer, w, z, af, ag, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, x, z, ae, ag, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, x, y, ae, ah, -0.03125F, 0, 0, -1, ad);
                this.vertex(pose, photoConsumer, w, y, af, ah, -0.03125F, 0, 0, -1, ad);
                matrices.popPose();
                matrices.pushPose();
                this.vertex(pose, backConsumer, w, y, j, k, 0.03125F, 0, 0, 1, ad);
                this.vertex(pose, backConsumer, x, y, i, k, 0.03125F, 0, 0, 1, ad);
                this.vertex(pose, backConsumer, x, z, i, l, 0.03125F, 0, 0, 1, ad);
                this.vertex(pose, backConsumer, w, z, j, l, 0.03125F, 0, 0, 1, ad);
                this.vertex(pose, backConsumer, w, y, m, o, -0.03125F, 0, 1, 0, ad);
                this.vertex(pose, backConsumer, x, y, n, o, -0.03125F, 0, 1, 0, ad);
                this.vertex(pose, backConsumer, x, y, n, p, 0.03125F, 0, 1, 0, ad);
                this.vertex(pose, backConsumer, w, y, m, p, 0.03125F, 0, 1, 0, ad);
                this.vertex(pose, backConsumer, w, z, m, o, 0.03125F, 0, -1, 0, ad);
                this.vertex(pose, backConsumer, x, z, n, o, 0.03125F, 0, -1, 0, ad);
                this.vertex(pose, backConsumer, x, z, n, p, -0.03125F, 0, -1, 0, ad);
                this.vertex(pose, backConsumer, w, z, m, p, -0.03125F, 0, -1, 0, ad);
                this.vertex(pose, backConsumer, w, y, r, s, 0.03125F, -1, 0, 0, ad);
                this.vertex(pose, backConsumer, w, z, r, t, 0.03125F, -1, 0, 0, ad);
                this.vertex(pose, backConsumer, w, z, q, t, -0.03125F, -1, 0, 0, ad);
                this.vertex(pose, backConsumer, w, y, q, s, -0.03125F, -1, 0, 0, ad);
                this.vertex(pose, backConsumer, x, y, r, s, -0.03125F, 1, 0, 0, ad);
                this.vertex(pose, backConsumer, x, z, r, t, -0.03125F, 1, 0, 0, ad);
                this.vertex(pose, backConsumer, x, z, q, t, 0.03125F, 1, 0, 0, ad);
                this.vertex(pose, backConsumer, x, y, q, s, 0.03125F, 1, 0, 0, ad);
                matrices.popPose();
            }
        }
    }

    private void vertex(
            PoseStack.Pose pose, @NotNull VertexConsumer photoConsumer, float x, float y, float u, float v, float z, int normalX, int normalY, int normalZ, int light
    ) {
        photoConsumer.addVertex(pose, x, y, z)
                .setColor(-1)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, (float) normalX, (float) normalY, (float) normalZ);
    }
}
