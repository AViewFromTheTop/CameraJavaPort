package net.lunade.camera.client.renderer.entity.state;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;

@Environment(EnvType.CLIENT)
public class CameraRenderState extends LivingEntityRenderState {
	public float trackedHeight;
	public float lerpedTimer;
}
