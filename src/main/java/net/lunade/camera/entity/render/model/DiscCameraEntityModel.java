package net.lunade.camera.entity.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.lunade.camera.entity.render.state.CameraRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DiscCameraEntityModel extends EntityModel<CameraRenderState> {
	final ModelPart root;
	final ModelPart head;
	final ModelPart disc;
	final ModelPart disc2;

	public DiscCameraEntityModel(@NotNull ModelPart root) {
		super(root);
		this.root = root.getChild("root");
		this.disc = this.root.getChild("disc");
		this.disc2 = this.root.getChild("disc2");
		this.head = this.root.getChild("head");
	}

	@NotNull
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition root = modelData.getRoot();

		PartDefinition rootA = root.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0F, 24F, 0F));
		PartDefinition head = rootA.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -5F, 8F, 8F, 10F), PartPose.offset(0F, 15F, 0F));
		rootA.addOrReplaceChild("disc", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0F, 0F, 15F, 10F, 0F), PartPose.offset(0F, 16.5F, 0F));
		rootA.addOrReplaceChild("disc2", CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0F, 0F, 15F, 10F, 0F), PartPose.offset(0F, 16.5F, 0F));

		return LayerDefinition.create(modelData, 64, 32);
	}

	@Override
	public void setupAnim(@NotNull CameraRenderState renderState) {
		super.setupAnim(renderState);
		this.disc.yRot = 45F * Mth.DEG_TO_RAD;
		this.disc2.yRot = -45F * Mth.DEG_TO_RAD;

		this.head.yRot = renderState.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = renderState.xRot * Mth.DEG_TO_RAD;
	}

	@Override
	public void renderToBuffer(PoseStack matrices, VertexConsumer vertexConsumer, int i, int j, int k) {
		this.head.render(matrices, vertexConsumer, i, j, k);
		matrices.scale(1.3F, 0.9F, 1.3F);
		this.disc.render(matrices, vertexConsumer, i, j, k);
		this.disc2.render(matrices, vertexConsumer, i, j, k);
	}
}
