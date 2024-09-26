package net.lunade.camera.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.impl.client.PhotographLoader;
import net.lunade.camera.menu.PrinterMenu;
import net.lunade.camera.networking.PrinterAskForSlotsPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class PrinterScreen extends AbstractContainerScreen<PrinterMenu> {
    int index = 0;
    private boolean displayRecipes = false;
    private static final ResourceLocation TEXTURE = CameraConstants.id("textures/gui/printer_test.png");

    public PrinterScreen(PrinterMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        final int size = PhotographLoader.loadLocalPhotographs();
        if (PhotographLoader.hasAnyLocalPhotographs()) {
            final String selected = PhotographLoader.getPhotograph(0).getPath();
            send(size, selected);
        } else {
            send(0, "");
        }
        menu.registerUpdateListener(this::containerChanged);
        --this.titleLabelY;
        this.inventoryLabelY += 56;
        this.imageHeight = 222;
    }

    private void send(int size, String selected) {
        ClientPlayNetworking.send(new PrinterAskForSlotsPacket(size, selected));
        this.menu.onClient(selected);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float delta, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(TEXTURE, i, j, 0, 0,this.imageWidth, this.imageHeight);
        if (this.displayRecipes) {
            final int size = PhotographLoader.getSize();
            final var middle = PhotographLoader.getInfiniteLocalPhotograph(index);
            if (middle != null)
                graphics.blit(middle, i + 64, j + 53, 0, 0, 48, 48, 48, 48);
            if (size != 1) {
                final var right = PhotographLoader.getInfiniteLocalPhotograph(index + 1);
                if (right != null) {
                    graphics.blit(right, i + 119, j + 61, 0, 0, 32, 32, 32, 32);
                    boolean next = isIn(i + 119, j + 61, 32, 32, mouseX, mouseY);
                    graphics.blit(TEXTURE, i + 119, j + 61, 208, next ? 32 : 0, 32, 32);
                }
                final var left = PhotographLoader.getInfiniteLocalPhotograph(index - 1);
                if (left != null) {
                    graphics.blit(left, i + 25, j + 61, 0, 0, 32, 32, 32, 32);
                    boolean next = isIn(i + 25, j + 61, 32, 32, mouseX, mouseY);
                    graphics.blit(TEXTURE, i + 25, j + 61, 176, next ? 32 : 0, 32, 32);
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.leftPos;
        int j = this.topPos;
        if (isIn(i + 119, j + 61, 32, 32, (int) mouseX, (int) mouseY)) {
            if (this.index == PhotographLoader.getSize() - 1) this.index = 0;
            else this.index++;
            send(PhotographLoader.getSize(), PhotographLoader.getPhotograph(this.index).getPath());
            return true;
        } else if (isIn(i + 25, j + 61, 32, 32, (int) mouseX, (int) mouseY)) {
            if (this.index == 0) this.index = PhotographLoader.getSize() - 1;
            else this.index--;
            send(PhotographLoader.getSize(), PhotographLoader.getPhotograph(this.index).getPath());
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @SuppressWarnings("all")
    private static boolean isIn(int minX, int minY, int w, int h, int x, int y) {
        return x >= minX && x <= minX + w && y >= minY && y <= minY + h;
    }

    private void containerChanged() {
        this.displayRecipes = this.menu.hasInputItem() && this.menu.getInputItem().is(Items.PAPER);
        if (!this.displayRecipes) {
            this.index = 0;
        }
    }
}
