package net.lunade.camera.client.renderer.entity.state.impl;

import net.minecraft.resources.ResourceLocation;

public interface ItemFrameRenderStateInterface {
	void cameraPort$addPhotographLocation(ResourceLocation location);
	ResourceLocation cameraPort$getPhotographLocation();
}
