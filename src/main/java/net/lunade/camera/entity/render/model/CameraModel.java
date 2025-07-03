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

public class CameraModel extends EntityModel<CameraRenderState> {
	private static final float HEIGHT_INCREMENT = 1.75F;
	private static final float HEIGHT_SCALE = 15F / HEIGHT_INCREMENT;
	private static final float LEG_ANGLE_MULTIPLIER = 6.7F;
	private static final float ROOT_ADJUSTMENT_BY_HEIGHT = LEG_ANGLE_MULTIPLIER / 3.575F;
	private final ModelPart head;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;

	public CameraModel(@NotNull ModelPart root) {
		super(root);
		this.leg1 = root.getChild("leg1");
		this.leg2 = root.getChild("leg2");
		this.leg3 = root.getChild("leg3");
		this.leg4 = root.getChild("leg4");
		this.head = root.getChild("head");
	}

	@NotNull
	public static LayerDefinition createBodyLayer() {
		MeshDefinition modelData = new MeshDefinition();
		PartDefinition partDefinition = modelData.getRoot();

		createHead(partDefinition, 2F);
		CubeListBuilder cubeListBuilder = createLegCube();
		createLeg(partDefinition, 1, cubeListBuilder, 0F, 1F);
		createLeg(partDefinition, 2, cubeListBuilder, 0F, -1F);
		createLeg(partDefinition, 3, cubeListBuilder, 1F, 0F);
		createLeg(partDefinition, 4, cubeListBuilder, -1F, 0F);

		return LayerDefinition.create(modelData, 64, 32);
	}

	public static @NotNull PartDefinition createHead(@NotNull PartDefinition root, float verticalOffset) {
		return root.addOrReplaceChild(
			"head",
			CubeListBuilder.create()
				.texOffs(0, 0)
				.addBox(-4F, -8F, -5F, 8F, 8F, 10F),
			PartPose.offset(0F, verticalOffset, 0F)
		);
	}

	public static @NotNull PartDefinition createLeg(@NotNull PartDefinition root, int index, CubeListBuilder cubeListBuilder, float xOffset, float zOffset) {
		return root.addOrReplaceChild("leg" + index, cubeListBuilder, PartPose.offset(xOffset, 0F, zOffset));
	}

	public static @NotNull CubeListBuilder createLegCube() {
		return CubeListBuilder.create().texOffs(36, 0).addBox(-0.5F, 0F, -0.5F, 1F, 25F, 1F);
	}

	@Override
	public void setupAnim(@NotNull CameraRenderState renderState) {
		super.setupAnim(renderState);

		float inverseHeight = (HEIGHT_INCREMENT - renderState.trackedHeight) * HEIGHT_SCALE;
		this.root().y += inverseHeight * ROOT_ADJUSTMENT_BY_HEIGHT;

		float angle = (15F + (inverseHeight * LEG_ANGLE_MULTIPLIER)) * Mth.DEG_TO_RAD;
		this.leg1.xRot = angle;
		this.leg2.xRot = -angle;
		this.leg3.zRot = -angle;
		this.leg4.zRot = angle;

		this.head.yRot = renderState.yRot * Mth.DEG_TO_RAD;
		this.head.xRot = renderState.xRot * Mth.DEG_TO_RAD;
	}
}
