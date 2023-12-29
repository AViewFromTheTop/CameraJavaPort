package net.lunade.camera.entity;

import net.lunade.camera.ClientCameraManager;
import net.minecraft.core.particles.ParticleTypes;

public class ClientCamMethods {
    
    public static void clientCamEntityTick(CameraEntity camera) {
        if (camera.level.isClientSide && camera.ready && camera.getTimer() <= 1 && camera.getTimer() >= 0) {
            Minecraft client = Minecraft.getInstance();
            if (camera.getTimer() == 1) {
                camera.level.addParticle(ParticleTypes.FLASH, getX(), getEyeY(), getZ(), 0, 0, 0);
                camera.prevVignette = client.inGameHud.vignetteDarkness;
                camera.wasHidden = client.options.hudHidden;
                camera.prevFOV = client.options.getFov().getValue();
                int fov = 70;
                if (camera.hasSpyglass()) {
                    fov= (int) (camera.prevFOV * 0.28571429);
                }
                ClientCameraManager.changeToCamera(camera, fov);
            }
            if (camera.getTimer() == 0) {
                camera.ready = false;
                ClientCameraManager.changeFromCamera(prevVignette, wasHidden, camera.prevFOV);
            }
        }
    }
    
}
