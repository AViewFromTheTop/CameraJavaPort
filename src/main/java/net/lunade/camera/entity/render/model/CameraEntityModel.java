package net.lunade.camera.entity.render.model;

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

public class CameraEntityModel extends EntityModel<CameraRenderState> {
	private static final float HEIGHT_INCREMENT = 15F / 1.75F;
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;

	public CameraEntityModel(@NotNull ModelPart root) {
		super(root);
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

		PartDefinition rootA = partDefinition.addOrReplaceChild("root", CubeListBuilder.create(), PartPose.offset(0F, 24F, 0F));
		PartDefinition head = rootA.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-4F, -8F, -5F, 8F, 8F, 10F), PartPose.offset(0F, -22F, 0F));
		rootA.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0F, -0.5F, 1F, 25F, 1F), PartPose.offset(0F, -24F, 1F));
		rootA.addOrReplaceChild("leg2", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0F, -0.5F, 1F, 25F, 1F), PartPose.offset(0F, -24F, -1F));
		rootA.addOrReplaceChild("leg3", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0F, -0.5F, 1F, 25F, 1F), PartPose.offset(1F, -24F, 0F));
		rootA.addOrReplaceChild("leg4", CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0F, -0.5F, 1F, 25F, 1F), PartPose.offset(-1F, -24F, 0F));

		return LayerDefinition.create(modelData, 64, 32);
	}

	@Override
	public void setupAnim(@NotNull CameraRenderState renderState) {
		super.setupAnim(renderState);
		float angle = (15F + (((1.75F - renderState.trackedHeight) * HEIGHT_INCREMENT) * 6.7F));

		this.leg1.xRot = angle * Mth.DEG_TO_RAD;
		this.leg2.xRot = -angle * Mth.DEG_TO_RAD;
		this.leg3.zRot = -angle * Mth.DEG_TO_RAD;
		this.leg4.zRot = angle * Mth.DEG_TO_RAD;

		this.head.yRot = renderState.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = renderState.yRot * Mth.DEG_TO_RAD;
	}
}
