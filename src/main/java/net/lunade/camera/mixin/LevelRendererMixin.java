package net.lunade.camera.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.llamalad7.mixinextras.sugar.ref.LocalFloatRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.impl.ClientCameraManager;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.util.FastColor;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Environment(EnvType.CLIENT)
@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow
    @Final
    private Minecraft minecraft;
    @Shadow
    @Final
    private RenderBuffers renderBuffers;
    @Shadow
    @Nullable
    private PostChain entityEffect;
    @Shadow
    private int renderedEntities;

    @ModifyExpressionValue(
            method = "renderLevel",
            at = @At(
                    value = "NEW",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;",
                    ordinal = 0
            )
    )
    public PoseStack cameraPort$capturePoseStack(PoseStack original, @Share("cameraPort$poseStack") LocalRef<PoseStack> poseStackRef) {
        poseStackRef.set(original);
        return original;
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;endLastBatch()V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION
    )
    public void cameraPort$renderPlayer(
		DeltaTracker deltaTracker,
		boolean renderBlockOutline,
		Camera camera,
		GameRenderer gameRenderer,
		LightTexture lightmapTextureManager,
		Matrix4f projectionMatrix,
		Matrix4f matrix4f,
		CallbackInfo info,
		@Share("cameraPort$playerRenderedSpecial") LocalBooleanRef playerRenderedSpecial,
		@Share("cameraPort$alreadyRendered") LocalBooleanRef alreadyRendered,
		@Share("cameraPort$tickRate") LocalFloatRef tickRate,
		@Share("cameraPort$poseStack") LocalRef<PoseStack> poseStackRef
    ) {
        if (ClientCameraManager.possessingCamera && !ClientCameraManager.isCameraHandheld && this.minecraft.player != null) {
            Player player = this.minecraft.player;
            Vec3 vec3 = camera.getPosition();
            double d = vec3.x();
            double e = vec3.y();
            double h = vec3.z();
            MultiBufferSource.BufferSource bufferSource = this.renderBuffers.bufferSource();
            Object multiBufferSource;
            ++this.renderedEntities;
            if (player.tickCount == 0) {
                player.xOld = player.getX();
                player.yOld = player.getY();
                player.zOld = player.getZ();
            }
            if (this.shouldShowEntityOutlines() && this.minecraft.shouldEntityAppearGlowing(player)) {
                playerRenderedSpecial.set(true);
                OutlineBufferSource outlineBufferSource = this.renderBuffers.outlineBufferSource();
                multiBufferSource = outlineBufferSource;
                int j = player.getTeamColor();
                outlineBufferSource.setColor(FastColor.ARGB32.red(j), FastColor.ARGB32.green(j), FastColor.ARGB32.blue(j), 255);
            } else {
                playerRenderedSpecial.set(false);
                multiBufferSource = bufferSource;
            }
            TickRateManager tickRateManager = this.minecraft.level.tickRateManager();
            tickRate.set(deltaTracker.getGameTimeDeltaPartialTick(!tickRateManager.isEntityFrozen(player)));
            this.renderEntity(player, d, e, h, tickRate.get(), poseStackRef.get(), (MultiBufferSource) multiBufferSource);
            alreadyRendered.set(false);
        }
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/PostChain;process(F)V",
                    ordinal = 0
            )
    )
    public void cameraPort$renderSpecialA(CallbackInfo ci, @Share("cameraPort$alreadyRendered") LocalBooleanRef alreadyRendered) {
        alreadyRendered.set(true);
    }

    @Inject(
            method = "renderLevel",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V",
                    ordinal = 0,
                    shift = At.Shift.BEFORE
            ),
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            args = "stringValue=destroyProgress"
                    )
            )
    )
    public void cameraPort$renderSpecialB(
            CallbackInfo ci,
            @Share("cameraPort$playerRenderedSpecial") LocalBooleanRef playerRenderedSpecial,
            @Share("cameraPort$alreadyRendered") LocalBooleanRef alreadyRendered,
            @Share("cameraPort$tickRate") LocalFloatRef tickRate
    ) {
        if (playerRenderedSpecial.get() && !alreadyRendered.get()) {
            this.entityEffect.process(tickRate.get());
            this.minecraft.getMainRenderTarget().bindWrite(false);
        }
    }

    @Shadow
    private void renderEntity(Entity entity, double d, double e, double f, float g, PoseStack poseStack, MultiBufferSource multiBufferSource) {
    }

    @Shadow
    public boolean shouldShowEntityOutlines() {
        throw new AssertionError("Mixin injection failed - Camera Port LevelRendererMixin.");
    }

}
