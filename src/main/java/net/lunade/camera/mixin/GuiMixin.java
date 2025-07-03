package net.lunade.camera.mixin;

import net.lunade.camera.impl.client.CameraScreenshotManager;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {

	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	public void cameraPort$removeOverlays(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo info) {
		if (CameraScreenshotManager.isUsingSelfRenderingCamera()) info.cancel();
	}

}
