package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.impl.client.PhotographRenderer;
import net.lunade.camera.registry.CameraItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ItemFrameRenderer.class)
public class ItemFrameRendererMixin {

    @ModifyExpressionValue(
            method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/decoration/ItemFrame;getRotation()I",
                    ordinal = 1
            )
    )
    public int cameraPort$fixRotationAndCapturePhotographComponent(
            int original,
            @Local ItemStack itemStack,
            @Share("cameraPort$photographComponent") LocalRef<PhotographComponent> photographComponentRef
    ) {
        PhotographComponent photographComponent = itemStack.get(CameraItems.PHOTO_COMPONENT);
        if (photographComponent != null) {
            photographComponentRef.set(photographComponent);
            return original % 4 * 2;
        }
        return original;
    }

    @WrapOperation(
            method = "render(Lnet/minecraft/world/entity/decoration/ItemFrame;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderStatic(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemDisplayContext;IILcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/world/level/Level;I)V"
            )
    )
    public void cameraPort$render(
            ItemRenderer instance,
            ItemStack stack,
            ItemDisplayContext modelTransformationMode,
            int light,
            int overlay,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            Level world,
            int seed,
            Operation<Void> original,
            @Share("cameraPort$photographComponent") LocalRef<PhotographComponent> photographComponentRef
    ) {
        PhotographComponent photographComponent = photographComponentRef.get();
        if (photographComponent != null) {
            // 0.625F
            matrices.scale(1.25F, 1.25F, 1.25F);
            matrices.translate(0F, 0F, 0.03125F);
            PhotographRenderer.render(matrices, vertexConsumers, photographComponent.location(), light, false);
        } else {
            original.call(instance, stack, modelTransformationMode, light, overlay, matrices, vertexConsumers, world, seed);
        }
    }
}
