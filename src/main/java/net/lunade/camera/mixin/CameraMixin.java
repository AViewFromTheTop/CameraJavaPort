package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.lunade.camera.impl.client.CameraScreenshotManager;
import net.minecraft.client.Camera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Camera.class)
public class CameraMixin {

	@ModifyReturnValue(method = "isDetached", at = @At("RETURN"))
	public boolean cameraPort$isDetached(boolean original) {
		return original && !CameraScreenshotManager.isUsingSelfRenderingCamera();
	}

}
