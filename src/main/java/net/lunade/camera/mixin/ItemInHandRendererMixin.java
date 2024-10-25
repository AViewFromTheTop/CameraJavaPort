package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.impl.client.PhotographRenderer;
import net.lunade.camera.registry.CameraPortItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRendererMixin {

    @Shadow @Final
    private Minecraft minecraft;

    @Shadow protected abstract void renderPlayerArm(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float equipProgress, float swingProgress, HumanoidArm arm);

    @Inject(
            method = "renderArmWithItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;isEmpty()Z",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            cancellable = true
    )
    private void cameraPort$renderArmWithItem(
            AbstractClientPlayer player,
            float tickDelta,
            float pitch,
            InteractionHand hand,
            float swingProgress,
            ItemStack item,
            float equipProgress,
            PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            CallbackInfo info,
            @Local HumanoidArm humanoidArm
    ) {
        if (item.is(CameraPortItems.PHOTOGRAPH)) {
            PhotographComponent photographComponent = item.get(CameraPortItems.PHOTO_COMPONENT);
            if (photographComponent != null) {
                this.cameraPort$renderPhotographInHand(matrices, vertexConsumers, light, equipProgress, humanoidArm, swingProgress, photographComponent);
                matrices.popPose();
                info.cancel();
            }
        }
    }

    @Unique
    private void cameraPort$renderPhotographInHand(
            @NotNull PoseStack matrices,
            MultiBufferSource vertexConsumers,
            int light,
            float equipProgress,
            HumanoidArm arm,
            float swingProgress,
            PhotographComponent photographComponent
    ) {
        float armOffset = arm == HumanoidArm.RIGHT ? 1F : -1F;
        matrices.translate(armOffset * 0.125F, -0.125F, 0F);
        if (!this.minecraft.player.isInvisible()) {
            matrices.pushPose();
            matrices.mulPose(Axis.ZP.rotationDegrees(armOffset * 10F));
            this.renderPlayerArm(matrices, vertexConsumers, light, equipProgress, swingProgress, arm);
            matrices.popPose();
        }

        matrices.pushPose();
        matrices.translate(armOffset * 0.51F, -0.08F + equipProgress * -1.2F, -0.75F);
        float g = Mth.sqrt(swingProgress);
        float h = Mth.sin(g * Mth.PI);
        float i = -0.5F * h;
        float j = 0.4F * Mth.sin(g * (Mth.PI * 2F));
        float k = -0.3F * Mth.sin(swingProgress * Mth.PI);
        matrices.translate(armOffset * i, j - 0.3F * h, k);
        matrices.mulPose(Axis.XP.rotationDegrees(h * -45F));
        matrices.mulPose(Axis.YP.rotationDegrees(armOffset * h * -30F));
        this.cameraPort$renderPhotograph(matrices, vertexConsumers, light, photographComponent);
        matrices.popPose();
    }

    @Unique
    private void cameraPort$renderPhotograph(
            @NotNull PoseStack matrices, @NotNull MultiBufferSource vertexConsumers, int swingProgress, @NotNull PhotographComponent photographComponent
    ) {
        matrices.mulPose(Axis.YP.rotationDegrees(180F));
        matrices.scale(0.38F, 0.38F, 0.38F);
        PhotographRenderer.render(matrices, vertexConsumers, photographComponent.location(), swingProgress, true);
    }
}
