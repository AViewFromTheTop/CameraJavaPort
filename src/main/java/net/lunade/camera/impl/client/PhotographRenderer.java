package net.lunade.camera.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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

    public static void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, ResourceLocation photoLocation, int light) {
        Matrix4f matrix4f = matrices.last().pose();
        PhotographLoader.getAndLoadPhotograph(photoLocation);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(photoLocation));
        vertexConsumer.addVertex(matrix4f, 0F, 1F, -0.01F).setColor(-1).setUv(0F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 1F, -0.01F).setColor(-1).setUv(1F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 0F, -0.01F).setColor(-1).setUv(1F, 0F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 0F, 0F, -0.01F).setColor(-1).setUv(0F, 0F).setLight(light);
    }

    public static void render(
            int x, int y, int xOffset, int yOffset, @NotNull GuiGraphics graphics, ResourceLocation photoLocation, int renderSize, boolean renderFrame
    ) {
        PhotographLoader.getAndLoadPhotograph(photoLocation);
        int renderX = x + xOffset;
        int renderY = y + yOffset;
        if (renderFrame) {
            graphics.blit(
                    FRAME,
                    renderX - 2,
                    renderY - 2,
                    0,
                    0,
                    renderSize + 4,
                    renderSize + 4,
                    renderSize + 4,
                    renderSize + 4
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
