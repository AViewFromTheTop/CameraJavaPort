package net.lunade.camera.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.entity.CameraEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(WorldRenderer.class)
public class WorldRendererMixin {

    @Shadow
    private BufferBuilderStorage bufferBuilders;

    @Inject(method = "render",at = @At(value = "INVOKE"))
    public void render(MatrixStack matrixStack, float f, long l, boolean bl, Camera camera, GameRenderer gameRenderer, LightmapTextureManager lightmapTextureManager, Matrix4f matrix4f, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (camera!=null && (client.getCameraEntity() instanceof CameraEntity)) {
            Vec3d vec3d = camera.getPos();
            assert client.player != null;
            if (client.player.shouldRender(vec3d.getX(), vec3d.getY(), vec3d.getZ())) {
                matrixStack.push();
                this.renderEntity(client.player, vec3d.getX(), vec3d.getY(), vec3d.getZ(), f, matrixStack, this.bufferBuilders.getEntityVertexConsumers());
                matrixStack.pop();
            }
        }
    }

    @Shadow
    private void renderEntity(Entity entity, double d, double e, double f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) { }
    
}
