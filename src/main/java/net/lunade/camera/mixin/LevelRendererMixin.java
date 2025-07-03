package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.impl.client.CameraScreenshotManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private List<Entity> visibleEntities;

	@ModifyExpressionValue(
		method = "renderLevel",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/LevelRenderer;collectVisibleEntities(Lnet/minecraft/client/Camera;Lnet/minecraft/client/renderer/culling/Frustum;Ljava/util/List;)Z"
		)
	)
	public boolean cameraPort$renderPlayer(
		boolean original,
		@Share("cameraPort$playerRenderedSpecial") LocalBooleanRef playerRenderedSpecial,
		@Share("cameraPort$alreadyRendered") LocalBooleanRef alreadyRendered,
		@Share("cameraPort$tickRate") LocalFloatRef tickRate,
		@Share("cameraPort$poseStack") LocalRef<PoseStack> poseStackRef
	) {
		if (CameraScreenshotManager.possessingCamera
			&& !CameraScreenshotManager.isCameraHandheld
			&& this.minecraft.player != null
			&& !this.visibleEntities.contains(this.minecraft.player)
		) {
			this.visibleEntities.add(this.minecraft.player);
			return true;
		}
		return original;
	}

}
