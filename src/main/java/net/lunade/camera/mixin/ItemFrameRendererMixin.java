package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.impl.client.ItemFrameRenderStateInterface;
import net.lunade.camera.impl.client.PhotographRenderer;
import net.lunade.camera.registry.CameraPortItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.state.ItemFrameRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.ItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin<T extends ItemFrame> {

	@ModifyExpressionValue(
		method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At(
			value = "FIELD",
			target = "Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;rotation:I",
			ordinal = 1
		)
	)
	public int cameraPort$fixRotationAndCapturePhotographComponent(
		int original,
		@Local(argsOnly = true) ItemFrameRenderState renderState,
		@Share("cameraPort$photoLocation") LocalRef<ResourceLocation> photoLocationRef
	) {
		if (renderState instanceof ItemFrameRenderStateInterface renderStateInterface) {
			ResourceLocation photographLocation = renderStateInterface.cameraPort$getPhotographLocation();
			if (photographLocation != null) {
				photoLocationRef.set(photographLocation);
				return original % 4 * 2;
			}
		}
		return original;
	}

	@WrapOperation(
		method = "Lnet/minecraft/client/renderer/entity/ItemFrameRenderer;render(Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/item/ItemStackRenderState;render(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;II)V"
		)
	)
	public void cameraPort$render(
		ItemStackRenderState instance, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, Operation<Void> original,
		@Share("cameraPort$photoLocation") LocalRef<ResourceLocation> photoLocationRef
	) {
		ResourceLocation photographLocation = photoLocationRef.get();
		if (photographLocation != null) {
			// 0.625F
			poseStack.scale(1.25F, 1.25F, 1.25F);
			poseStack.translate(0F, 0F, 0.03125F);
			PhotographRenderer.render(poseStack, multiBufferSource, photographLocation, i, false);
		} else {
			original.call(instance, poseStack, multiBufferSource, i, j);
		}
	}

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/decoration/ItemFrame;Lnet/minecraft/client/renderer/entity/state/ItemFrameRenderState;F)V", at = @At("TAIL"))
	public void cameraPort$addPhotoToRenderState(T itemFrame, ItemFrameRenderState renderState, float f, CallbackInfo info) {
		if (renderState instanceof ItemFrameRenderStateInterface renderStateInterface) {
			PhotographComponent photographComponent = itemFrame.getItem().get(CameraPortItems.PHOTO_COMPONENT);
			if (photographComponent != null) {
				renderStateInterface.cameraPort$addPhotographLocation(photographComponent.location());
			} else {
				renderStateInterface.cameraPort$addPhotographLocation(null);
			}
		}
	}
}
