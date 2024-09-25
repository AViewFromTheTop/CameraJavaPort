package net.lunade.camera.component.tooltip.client;

import net.lunade.camera.component.tooltip.PictureTooltipComponent;
import net.lunade.camera.image_transfer.PhotographLoader;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import org.jetbrains.annotations.NotNull;

public class ClientPictureTooltipComponent implements ClientTooltipComponent {
    private final int size = 32;
    private final PictureTooltipComponent id;

    public ClientPictureTooltipComponent(PictureTooltipComponent id) {
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
    public void renderImage(Font textRenderer, int x, int y, @NotNull GuiGraphics graphics) {
        PhotographLoader.get(id.id().getPath().replace("photographs/", ""));
        graphics.blit(id.id(), x, y, 0, 0, size, size, size, size);
    }
}
