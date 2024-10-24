package net.lunade.camera.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.image_transfer.client.ServerTexture;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.entity.Photograph;
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
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PhotographEntityRenderer extends EntityRenderer<Photograph> {

    public PhotographEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(Photograph photograph, float f, float g, @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int i) {
        String photographName = photograph.getPhotographName();
        ResourceLocation photoLocation = this.getPhotographLocation(photograph);
        if (photograph.serverTexture == null && !photographName.isEmpty()) {
            photograph.serverTexture = new ServerTexture(
                    "photographs",
                    photograph.getPhotographName() + ".png",
                    CameraPortConstants.id("photographs/empty"),
                    () -> {}
            );
            Minecraft.getInstance().getTextureManager().register(photoLocation, photograph.serverTexture);
        }
        matrices.pushPose();
        matrices.mulPose(Axis.YP.rotationDegrees(180F - f));
        this.renderPhotograph(
                matrices,
                photograph,
                vertexConsumers.getBuffer(RenderType.entitySolid(photoLocation)),
                null,
                false
        );

        final var backSprite = Minecraft.getInstance().getPaintingTextures().getBackSprite();
        this.renderPhotograph(
                matrices,
                photograph,
                vertexConsumers.getBuffer(RenderType.entitySolid(backSprite.atlasLocation())),
                backSprite,
                true
        );
        matrices.popPose();
        super.render(photograph, f, g, matrices, vertexConsumers, i);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(Photograph photograph) {
        return Minecraft.getInstance().getPaintingTextures().getBackSprite().atlasLocation();
    }

    public ResourceLocation getPhotographLocation(@NotNull Photograph photograph) {
        return CameraPortConstants.id("photographs/" + photograph.getPhotographName());
    }

    private void renderPhotograph(
            @NotNull PoseStack matrices, @NotNull Photograph entity, VertexConsumer vertexConsumer, @Nullable TextureAtlasSprite backSprite, boolean renderingSide
    ) {
        if (renderingSide && backSprite == null) throw new IllegalArgumentException("backSprite cannot be null while rendering the side of a photograph!");
        PoseStack.Pose pose = matrices.last();
        float photographSize = entity.getSize();
        float f = (-photographSize) / 2F;
        float i = !renderingSide ? 0F : backSprite.getU0();
        float j = !renderingSide ? 0F : backSprite.getU1();
        float k = !renderingSide ? 0F : backSprite.getV0();
        float l = !renderingSide ? 0F : backSprite.getV1();
        float m = !renderingSide ? 0F : backSprite.getU0();
        float n = !renderingSide ? 0F : backSprite.getU1();
        float o = !renderingSide ? 0F : backSprite.getV0();
        float p = !renderingSide ? 0F : backSprite.getV(0.0625F);
        float q = !renderingSide ? 0F : backSprite.getU0();
        float r = !renderingSide ? 0F : backSprite.getU(0.0625F);
        float s = !renderingSide ? 0F : backSprite.getV0();
        float t = !renderingSide ? 0F : backSprite.getV1();
        double d = 1D / photographSize;
        double e = 1D / photographSize;

        for (int u = 0; u < 2; u++) {
            for (int v = 0; v < 2; v++) {
                float w = f + (u + 1F);
                float x = f + (float) u;
                float y = f + (v + 1F);
                float z = f + (float) v;
                int aa = entity.getBlockX();
                int ab = Mth.floor(entity.getY() + (double) ((y + z) / 2F));
                int ac = entity.getBlockZ();
                Direction direction = entity.getDirection();
                
                if (direction == Direction.NORTH) aa = Mth.floor(entity.getX() + (double) ((w + x) / 2F));
                if (direction == Direction.WEST) ac = Mth.floor(entity.getZ() - (double) ((w + x) / 2F));
                if (direction == Direction.SOUTH) aa = Mth.floor(entity.getX() - (double) ((w + x) / 2F));
                if (direction == Direction.EAST) ac = Mth.floor(entity.getZ() + (double) ((w + x) / 2F));

                int ad = LevelRenderer.getLightColor(entity.level(), new BlockPos(aa, ab, ac));

                float size = entity.getSize();
                float ae = (float)(d * (size - u));
                float af = (float)(d * (size - (u + 1F)));
                float ag = (float)(e * (size - v));
                float ah = (float)(e * (size - (v + 1F)));

                if (!renderingSide) {
                    this.vertex(pose, vertexConsumer, w, z, af, ag, -0.03125F, 0, 0, -1, ad);
                    this.vertex(pose, vertexConsumer, x, z, ae, ag, -0.03125F, 0, 0, -1, ad);
                    this.vertex(pose, vertexConsumer, x, y, ae, ah, -0.03125F, 0, 0, -1, ad);
                    this.vertex(pose, vertexConsumer, w, y, af, ah, -0.03125F, 0, 0, -1, ad);
                } else {
                    this.vertex(pose, vertexConsumer, w, y, j, k, 0.03125F, 0, 0, 1, ad);
                    this.vertex(pose, vertexConsumer, x, y, i, k, 0.03125F, 0, 0, 1, ad);
                    this.vertex(pose, vertexConsumer, x, z, i, l, 0.03125F, 0, 0, 1, ad);
                    this.vertex(pose, vertexConsumer, w, z, j, l, 0.03125F, 0, 0, 1, ad);
                    this.vertex(pose, vertexConsumer, w, y, m, o, -0.03125F, 0, 1, 0, ad);
                    this.vertex(pose, vertexConsumer, x, y, n, o, -0.03125F, 0, 1, 0, ad);
                    this.vertex(pose, vertexConsumer, x, y, n, p, 0.03125F, 0, 1, 0, ad);
                    this.vertex(pose, vertexConsumer, w, y, m, p, 0.03125F, 0, 1, 0, ad);
                    this.vertex(pose, vertexConsumer, w, z, m, o, 0.03125F, 0, -1, 0, ad);
                    this.vertex(pose, vertexConsumer, x, z, n, o, 0.03125F, 0, -1, 0, ad);
                    this.vertex(pose, vertexConsumer, x, z, n, p, -0.03125F, 0, -1, 0, ad);
                    this.vertex(pose, vertexConsumer, w, z, m, p, -0.03125F, 0, -1, 0, ad);
                    this.vertex(pose, vertexConsumer, w, y, r, s, 0.03125F, -1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, w, z, r, t, 0.03125F, -1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, w, z, q, t, -0.03125F, -1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, w, y, q, s, -0.03125F, -1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, x, y, r, s, -0.03125F, 1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, x, z, r, t, -0.03125F, 1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, x, z, q, t, 0.03125F, 1, 0, 0, ad);
                    this.vertex(pose, vertexConsumer, x, y, q, s, 0.03125F, 1, 0, 0, ad);
                }
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
