package net.lunade.camera.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lunade.camera.entity.DiscCameraEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class DiscCameraEntityModel<A extends DiscCameraEntity> extends EntityModel<A> {
    private static final float RAD = (float) (Math.PI / 180F);
    final ModelPart root;
    final ModelPart head;
    final ModelPart disc;
    final ModelPart disc2;

    public DiscCameraEntityModel(@NotNull ModelPart root) {
        this.root = root.getChild("root");
        this.disc = this.root.getChild("disc");
        this.disc2 = this.root.getChild("disc2");
        this.head = this.root.getChild("head");
    }

    @NotNull
    public static LayerDefinition getTexturedModelData() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition rootA = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition head = rootA.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 10.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        rootA.addOrReplaceChild("disc", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0.0F, 0.0F, 15.0F, 10.0F, 0.0F), PartPose.offset(0.0F, 16.5F, 0.0F));
        rootA.addOrReplaceChild("disc2", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0.0F, 0.0F, 15.0F, 10.0F, 0.0F), PartPose.offset(0.0F, 16.5F, 0.0F));

        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull A entity, float limbSwing, float limbSwingAmount, float time, float netHeadYaw, float headPitch) {
        this.disc.yRot = 45 * RAD;
        this.disc2.yRot = -45 * RAD;

        this.head.yRot = netHeadYaw * RAD;
        this.head.xRot = headPitch * RAD;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int i, int j, int k) {
        head.render(matrices, vertexConsumer, i, j, k);
        matrices.scale(1.3F, 0.9F, 1.3F);
        disc.render(matrices, vertexConsumer, i, j, k);
        disc2.render(matrices, vertexConsumer, i, j, k);
    }
}
