package net.lunade.camera.client.model;

import net.lunade.camera.client.renderer.entity.state.CameraRenderState;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class DiscCameraModel extends EntityModel<CameraRenderState> {
	final ModelPart head;
	final ModelPart disc;
	final ModelPart disc2;

	public DiscCameraModel(@NotNull ModelPart root) {
		super(root);
		this.disc = root.getChild("disc1");
		this.disc2 = root.getChild("disc2");
		this.head = root.getChild("head");
	}

	@NotNull
	public static LayerDefinition getTexturedModelData() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition partDefinition = modelData.getRoot();

		CameraModel.createHead(partDefinition, 15F);
		CubeListBuilder discCube = createDiscCube();
		createDisc(partDefinition, 1, discCube, 45F * Mth.DEG_TO_RAD);
		createDisc(partDefinition, 2, discCube, -45F * Mth.DEG_TO_RAD);

		return LayerDefinition.create(modelData, 64, 32);
	}

	public static @NotNull CubeListBuilder createDiscCube() {
		return CubeListBuilder.create().texOffs(0, 18).addBox(-7.5F, 0F, 0F, 15F, 10F, 0F);
	}

	public static @NotNull PartDefinition createDisc(PartDefinition root, int index, CubeListBuilder cubeListBuilder, float yRot) {
		return root.addOrReplaceChild("disc" + index, cubeListBuilder, PartPose.offsetAndRotation(0F, 16.5F, 0F, 0F, yRot, 0F).scaled(1.3F, 0.9F, 1.3F));
	}

	@Override
	public void setupAnim(@NotNull CameraRenderState renderState) {
		super.setupAnim(renderState);
		this.disc.yRot = 45F * Mth.DEG_TO_RAD;
		this.disc2.yRot = -45F * Mth.DEG_TO_RAD;

		this.head.yRot = renderState.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = renderState.xRot * Mth.DEG_TO_RAD;
	}
}
