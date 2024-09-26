package net.lunade.camera.screen;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.impl.client.PhotographLoader;
import net.lunade.camera.menu.PrinterMenu;
import net.lunade.camera.networking.PrinterAskForSlotsPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class PrinterScreen extends AbstractContainerScreen<PrinterMenu> {
    int index = 0;
    private boolean displayRecipes = false;
    private static final ResourceLocation TEXTURE = CameraConstants.id("textures/gui/printer.png");
    private static final ResourceLocation TEXTURE_FILM = CameraConstants.id("textures/gui/printer_test.png");
    private static final ResourceLocation MOVE_RIGHT = CameraConstants.id("textures/gui/sprites/printer/move_right.png");
    private static final ResourceLocation MOVE_RIGHT_SELECTED = CameraConstants.id("textures/gui/sprites/printer/move_right_highlighted.png");
    private static final ResourceLocation MOVE_LEFT = CameraConstants.id("textures/gui/sprites/printer/move_left.png");
    private static final ResourceLocation MOVE_LEFT_SELECTED = CameraConstants.id("textures/gui/sprites/printer/move_left_highlighted.png");

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
        graphics.blit(this.displayRecipes ? TEXTURE_FILM : TEXTURE, i, j, 0, 0,this.imageWidth, this.imageHeight);
        if (this.displayRecipes) {
            final int size = PhotographLoader.getSize();
            final var middle = PhotographLoader.getInfiniteLocalPhotograph(index);
            if (middle != null) graphics.blit(middle, i + 64, j + 53, 0, 0, 48, 48, 48, 48);
            if (size != 1) {
                final var right = PhotographLoader.getInfiniteLocalPhotograph(index + 1);
                if (right != null) {
                    // Render right photograph
                    graphics.blit(right, i + 119, j + 61, 0, 0, 32, 32, 32, 32);
                    // Render right arrow
                    boolean selected = isIn(i + 119, j + 61, 32, 32, mouseX, mouseY);
                    graphics.blit(selected ? MOVE_RIGHT_SELECTED : MOVE_RIGHT, i + 119, j + 61, 0, 0, 32, 32, 32, 32);
                }
                final var left = PhotographLoader.getInfiniteLocalPhotograph(index - 1);
                if (left != null) {
                    // Render left photograph
                    graphics.blit(left, i + 25, j + 61, 0, 0, 32, 32, 32, 32);
                    // Render left arrow
                    boolean selected = isIn(i + 25, j + 61, 32, 32, mouseX, mouseY);
                    graphics.blit(selected ? MOVE_LEFT_SELECTED : MOVE_LEFT, i + 25, j + 61, 0, 0, 32, 32, 32, 32);
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
            if (this.index == PhotographLoader.getSize() - 1) {
                this.index = 0;
            } else {
                this.index++;
            }
            send(PhotographLoader.getSize(), PhotographLoader.getPhotograph(this.index).getPath());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
            return true;
        } else if (isIn(i + 25, j + 61, 32, 32, (int) mouseX, (int) mouseY)) {
            if (this.index == 0) {
                this.index = PhotographLoader.getSize() - 1;
            } else {
                this.index--;
            }
            send(PhotographLoader.getSize(), PhotographLoader.getPhotograph(this.index).getPath());
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1F));
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
