package net.lunade.camera.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraConstants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PhotographRenderer {
    private static final ResourceLocation FRAME = CameraConstants.id("textures/gui/sprites/photograph/frame.png");

    public static void render(
            @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, ResourceLocation photoLocation, int light, boolean renderFrame
    ) {
        Matrix4f matrix4f = matrices.last().pose();
        matrices.mulPose(Axis.ZP.rotationDegrees(180F));
        matrices.translate(-0.5F, -0.5F, 0F);

        PhotographLoader.getAndLoadPhotograph(photoLocation);
        if (renderFrame) {
            VertexConsumer frameConsumer = vertexConsumers.getBuffer(RenderType.text(FRAME));
            frameConsumer.addVertex(matrix4f, -0.0625F, 1.0625F, 0F).setColor(-1).setUv(0F, 1F).setLight(light);
            frameConsumer.addVertex(matrix4f, 1.0625F, 1.0625F, 0F).setColor(-1).setUv(1F, 1F).setLight(light);
            frameConsumer.addVertex(matrix4f, 1.0625F, -0.0625F, 0F).setColor(-1).setUv(1F, 0F).setLight(light);
            frameConsumer.addVertex(matrix4f, -0.0625F, -0.0625F, 0F).setColor(-1).setUv(0F, 0F).setLight(light);
        }
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(photoLocation));
        vertexConsumer.addVertex(matrix4f, 0F, 1F, -0.00007812F).setColor(-1).setUv(0F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 1F, -0.00007812F).setColor(-1).setUv(1F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 0F, -0.00007812F).setColor(-1).setUv(1F, 0F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 0F, 0F, -0.00007812F).setColor(-1).setUv(0F, 0F).setLight(light);
    }

    public static void render(
            int x, int y, int xOffset, int yOffset, @NotNull GuiGraphics graphics, ResourceLocation photoLocation, int renderSize, boolean renderFrame
    ) {
        PhotographLoader.getAndLoadPhotograph(photoLocation);
        int renderX = x + xOffset;
        int renderY = y + yOffset;
        if (renderFrame) {
            double frameOffsetScale = renderSize / 80D;
            int posOffset = (int) (5 * frameOffsetScale);
            int sizeOffset = (int) (10 * frameOffsetScale);
            int frameRenderSize = renderSize + sizeOffset;
            graphics.blit(
                    FRAME,
                    renderX - posOffset,
                    renderY - posOffset,
                    0,
                    0,
                    frameRenderSize,
                    frameRenderSize,
                    frameRenderSize,
                    frameRenderSize
            );
        }
        graphics.blit(
                photoLocation,
                renderX,
                renderY,
                0,
                0,
                renderSize,
                renderSize,
                renderSize,
                renderSize
        );
    }
}
