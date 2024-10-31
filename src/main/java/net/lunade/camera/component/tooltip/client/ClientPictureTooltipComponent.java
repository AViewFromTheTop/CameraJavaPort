package net.lunade.camera.component.tooltip.client;

import net.lunade.camera.component.tooltip.PictureTooltipComponent;
import net.lunade.camera.impl.client.PhotographRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class ClientPictureTooltipComponent implements ClientTooltipComponent {
	private static final int PHOTOGRAPH_RENDER_SIZE = 32;
	private static final int PHOTOGRAPH_RENDER_OFFSET_X = 3;
	private static final int TOOLTIP_WIDTH = PHOTOGRAPH_RENDER_SIZE + (PHOTOGRAPH_RENDER_OFFSET_X * 2);
	private final PictureTooltipComponent component;

	public ClientPictureTooltipComponent(PictureTooltipComponent component) {
		this.component = component;
	}

	@Override
	public int getHeight() {
		return PHOTOGRAPH_RENDER_SIZE + 6;
	}

	@Override
	public int getWidth(Font textRenderer) {
		return TOOLTIP_WIDTH;
	}

	@Override
	public void renderImage(Font textRenderer, int x, int y, @NotNull GuiGraphics graphics) {
		ResourceLocation photoLocation = this.component.id();
		PhotographRenderer.render(x, y, PHOTOGRAPH_RENDER_OFFSET_X, 0, graphics, photoLocation, PHOTOGRAPH_RENDER_SIZE, true);
	}

	@Override
	public void renderText(@NotNull Font textRenderer, int x, int y, Matrix4f modelMatrix, MultiBufferSource.BufferSource vertexConsumer) {
		//  textRenderer.drawInBatch(this.text, (float)x, (float)y, -1, true, modelMatrix, vertexConsumer, Font.DisplayMode.NORMAL, 0, 15728880);
	}
}
