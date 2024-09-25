package net.lunade.camera.impl.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.CameraPortMain;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public class CameraScreenshotManager {
    public static boolean wasGuiHidden = false;
    public static boolean possessingCamera = false;
    public static boolean isCameraHandheld = false;
    @Nullable
    public static Entity previousCameraEntity = null;

    public static void executeScreenshot(@Nullable Entity entity, boolean handheld) {
        Minecraft client = Minecraft.getInstance();
        isCameraHandheld = handheld;
        previousCameraEntity = client.getCameraEntity();
        if (entity != null) {
            client.setCameraEntity(entity);
        }
        wasGuiHidden = client.options.hideGui;
        client.options.hideGui = true;
        possessingCamera = true;

        if (client.level != null) {
            Entity camEntity = client.getCameraEntity();
            if (camEntity != null) {
                client.level.playLocalSound(client.player, CameraPortMain.CAMERA_SNAP, SoundSource.PLAYERS, 0.5F, 1F);
            }
        }

        grabCameraScreenshot(client.gameDirectory, 256, 256);
        isCameraHandheld = false;

        if (client.level != null) {
            Entity camEntity = client.getCameraEntity();
            if (!isCameraHandheld) {
                if (camEntity != null) {
                    int smokeCount = client.level.getRandom().nextInt(1, 5);
                    for (int i = 0; i < smokeCount; i++) {
                        client.level.addParticle(ParticleTypes.LARGE_SMOKE, camEntity.getX(), camEntity.getEyeY(), camEntity.getZ(), 0, 0.15, 0);
                    }
                    Vec3 forward = camEntity.getForward();
                    client.level.addParticle(ParticleTypes.FLASH, camEntity.getX() + forward.z, camEntity.getEyeY() + forward.y, camEntity.getZ() + forward.z, 0, 0, 0);
                }
            }
        }

        if (previousCameraEntity != null) {
            client.setCameraEntity(previousCameraEntity);
            previousCameraEntity = null;
        }
        client.options.hideGui = wasGuiHidden;
        possessingCamera = false;
    }

    public static void grabCameraScreenshot(File gameDirectory, int width, int height) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();
        int prevWidth = window.getWidth();
        int prevHeight = window.getHeight();
        RenderTarget renderTarget = new TextureTarget(width, height, true, Minecraft.ON_OSX);
        LevelRenderer levelRenderer = minecraft.levelRenderer;
        GameRenderer gameRenderer = minecraft.gameRenderer;
        gameRenderer.setRenderBlockOutline(false);

        try {
            gameRenderer.setPanoramicMode(true);
            levelRenderer.graphicsChanged();
            window.setWidth(width);
            window.setHeight(height);
            renderTarget.bindWrite(true);
            gameRenderer.renderLevel(minecraft.getTimer());

            try {
                Thread.sleep(10L);
            } catch (InterruptedException ignored) {
            }

            grab(gameDirectory, renderTarget, (text) -> minecraft.execute(() -> minecraft.gui.getChat().addMessage(text)));
        } catch (Exception ignored) {
        } finally {
            gameRenderer.setRenderBlockOutline(true);
            window.setWidth(prevWidth);
            window.setHeight(prevHeight);
            renderTarget.destroyBuffers();
            gameRenderer.setPanoramicMode(false);
            levelRenderer.graphicsChanged();
            minecraft.getMainRenderTarget().bindWrite(true);
        }
    }

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
        File file = new File(gameDirectory, "photographs/.local");
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