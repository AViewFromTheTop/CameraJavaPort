package net.lunade.camera.impl;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.lunade.camera.CameraConstants;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

public class PhotographScreenshot {

    public static void grab(File gameDirectory, RenderTarget framebuffer, Consumer<Component> messageReceiver) {
        grab(gameDirectory, null, framebuffer, messageReceiver);
    }

    private static void grab(File gameDirectory, @Nullable String fileName, RenderTarget framebuffer, Consumer<Component> messageReceiver) {
        if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> _grab(gameDirectory, fileName, framebuffer, messageReceiver));
        } else {
            _grab(gameDirectory, fileName, framebuffer, messageReceiver);
        }

    }

    private static void _grab(File gameDirectory, @Nullable String fileName, RenderTarget framebuffer, Consumer<Component> messageReceiver) {
        NativeImage nativeImage = takeScreenshot(framebuffer);
        File file = new File(gameDirectory, "photographs");
        file.mkdir();
        File file2;
        if (fileName == null) {
            file2 = getFile(file);
        } else {
            file2 = new File(file, fileName);
        }

        Util.ioPool().execute(() -> {
            try {
                nativeImage.writeToFile(file2);
                Component component = Component.literal(file2.getName()).withStyle(ChatFormatting.UNDERLINE)
                        .withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, file2.getAbsolutePath())));
                messageReceiver.accept(Component.translatable("screenshot.success", component));
            } catch (Exception var7) {
                Exception exception = var7;
                CameraConstants.warn("Couldn't save screenshot " + exception, true);
                messageReceiver.accept(Component.translatable("screenshot.failure", exception.getMessage()));
            } finally {
                nativeImage.close();
            }

        });
    }

    private static @NotNull NativeImage takeScreenshot(@NotNull RenderTarget framebuffer) {
        int i = framebuffer.width;
        int j = framebuffer.height;
        NativeImage nativeImage = new NativeImage(i, j, false);
        RenderSystem.bindTexture(framebuffer.getColorTextureId());
        nativeImage.downloadTexture(0, true);
        nativeImage.flipY();
        return nativeImage;
    }

    private static @NotNull File getFile(File directory) {
        String string = Minecraft.getInstance().getGameProfile().getId().toString();
        int i = 1;

        while(true) {
            File file = new File(directory, string + (i == 1 ? "" : "_" + i) + ".png");
            if (!file.exists()) {
                return file;
            }

            ++i;
        }
    }
}
