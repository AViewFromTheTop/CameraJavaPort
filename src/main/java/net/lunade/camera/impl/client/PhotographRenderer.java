package net.lunade.camera.impl.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@Environment(EnvType.CLIENT)
public class PhotographRenderer {

    public static void render(@NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, ResourceLocation photoLocation, int light) {
        Matrix4f matrix4f = matrices.last().pose();
        PhotographLoader.getAndLoadPhotograph(photoLocation);
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.text(photoLocation));
        vertexConsumer.addVertex(matrix4f, 0F, 1F, -0.01F).setColor(-1).setUv(0F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 1F, -0.01F).setColor(-1).setUv(1F, 1F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 1F, 0F, -0.01F).setColor(-1).setUv(1F, 0F).setLight(light);
        vertexConsumer.addVertex(matrix4f, 0F, 0F, -0.01F).setColor(-1).setUv(0F, 0F).setLight(light);
    }

    public static void render(int x, int y, int xOffset, int yOffset, @NotNull GuiGraphics graphics, ResourceLocation photoLocation, int renderSize) {
        PhotographLoader.getAndLoadPhotograph(photoLocation);
        graphics.blit(
                photoLocation,
                x + xOffset,
                y + yOffset,
                0,
                0,
                renderSize,
                renderSize,
                renderSize,
                renderSize
        );
    }
}
