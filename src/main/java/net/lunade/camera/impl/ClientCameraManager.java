package net.lunade.camera.impl;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.Window;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraPortMain;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@Environment(EnvType.CLIENT)
public class ClientCameraManager {
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

        int windowWidth = client.getWindow().getWidth();
        grabCameraScreenshot(client.gameDirectory, windowWidth, windowWidth);
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
            gameRenderer.renderLevel(minecraft.getFrameTime(), 0L);

            try {
                Thread.sleep(10L);
            } catch (InterruptedException ignored) {
            }

            Screenshot.grab(gameDirectory, renderTarget, (text) -> minecraft.execute(() -> minecraft.gui.getChat().addMessage(text)));
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

}
