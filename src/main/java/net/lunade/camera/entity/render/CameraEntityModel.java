package net.lunade.camera.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lunade.camera.entity.CameraEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import org.jetbrains.annotations.NotNull;

public class CameraEntityModel<T extends CameraEntity> extends EntityModel<T> {
    private static final float moveBy = 15F / 1.75F;
    private static final float RAD = (float) (Math.PI / 180F);
    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart leg1;
    private final ModelPart leg2;
    private final ModelPart leg3;
    private final ModelPart leg4;

    public CameraEntityModel(@NotNull ModelPart root) {
        this.root = root.getChild("root");
        this.leg1 = this.root.getChild("leg1");
        this.leg2 = this.root.getChild("leg2");
        this.leg3 = this.root.getChild("leg3");
        this.leg4 = this.root.getChild("leg4");
        this.head = this.root.getChild("head");
    }

    @NotNull
    public static LayerDefinition createBodyLayer() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition partDefinition = modelData.getRoot();

        PartDefinition rootA = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition head = rootA.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 10.0F), PartPose.offset(0.0F, -22.0F, 0.0F));
        rootA.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.offset(0.0F, -24.0F, 1.0F));
        rootA.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.offset(0.0F, -24.0F, -1.0F));
        rootA.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.offset(1.0F, -24.0F, 0.0F));
        rootA.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), PartPose.offset(-1.0F, -24.0F, 0.0F));

        return LayerDefinition.create(modelData, 64, 32);
    }

    @Override
    public void setupAnim(@NotNull T entity, float limbSwing, float limbSwingAmount, float time, float netHeadYaw, float headPitch) {
        float angle = (15F + (((1.75F - entity.getTrackedHeight()) * moveBy) * 6.7F));

        this.leg1.xRot = angle * RAD;
        this.leg2.xRot = -angle * RAD;
        this.leg3.zRot = -angle * RAD;
        this.leg4.zRot = angle * RAD;

        this.head.yRot = netHeadYaw * RAD;
        this.head.xRot = headPitch * RAD;
    }

    @Override
    public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int i, int j, int k) {
        this.root.render(matrices, vertexConsumer, i, j, k);
    }
}
