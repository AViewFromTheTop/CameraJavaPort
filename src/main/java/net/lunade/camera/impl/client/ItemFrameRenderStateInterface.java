package net.lunade.camera.impl.client;

import net.minecraft.resources.ResourceLocation;

public interface ItemFrameRenderStateInterface {
	void cameraPort$addPhotographLocation(ResourceLocation location);
	ResourceLocation cameraPort$getPhotographLocation();
}
