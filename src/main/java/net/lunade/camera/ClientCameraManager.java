package net.lunade.camera;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class ClientCameraManager {
    public static boolean wasGuiHidden = false;
    public static boolean possessingCamera = false;
    public static boolean isCameraHandheld = false;
    @Nullable
    public static Entity previousCameraEntity = null;

    public static void tick() {
        if (possessingCamera) {
            changeFromCamera();
        }
    }

    public static void changeToCamera(@Nullable Entity entity, boolean handheld) {
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
                client.level.playSound(client.player, client.player, CameraMain.CAMERA_SNAP, SoundSource.PLAYERS, 0.5F, 1F);
            }
        }
    }

    public static void changeFromCamera() {
        Minecraft client = Minecraft.getInstance();
        isCameraHandheld = false;
        Screenshot.grab(client.gameDirectory, client.getMainRenderTarget(), (text) -> client.execute(() -> client.gui.getChat().addMessage(text)));

        if (client.level != null) {
            Entity camEntity = client.getCameraEntity();
            if (camEntity != null) {
                int smokeCount = client.level.getRandom().nextInt(1, 5);
                for (int i = 0; i < smokeCount; i++) {
                    client.level.addParticle(ParticleTypes.LARGE_SMOKE, camEntity.getX(), camEntity.getEyeY(), camEntity.getZ(), 0, 0.15, 0);
                }
                Vec3 forward = camEntity.getForward();
                client.level.addParticle(ParticleTypes.FLASH, camEntity.getX() + forward.z, camEntity.getEyeY() + forward.y, camEntity.getZ() + forward.z, 0, 0, 0);
            }
        }

        if (previousCameraEntity != null) {
            client.setCameraEntity(previousCameraEntity);
            previousCameraEntity = null;
        }
        client.options.hideGui = wasGuiHidden;
        possessingCamera = false;
    }

}
