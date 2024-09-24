package net.lunade.camera.impl;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;

public class PictureTooltipComponent implements ClientTooltipComponent {
    private final int size = 32;
    private final PictureTooltip id;

    public PictureTooltipComponent(PictureTooltip id) {
        this.id = id;
    }

    @Override
    public int getHeight() {
        return size;
    }

    @Override
    public int getWidth(Font textRenderer) {
        return size;
    }

    @Override
    public void renderImage(Font textRenderer, int x, int y, GuiGraphics graphics) {
        graphics.blit(id.id(), x, y, size, size, size, size);
    }
}
