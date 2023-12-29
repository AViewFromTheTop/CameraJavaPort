package net.lunade.camera.entity.render;

import net.lunade.camera.entity.CameraEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class CameraEntityModel<T extends CameraEntity> extends EntityModel<CameraEntity> {
	private final ModelPart root;
	private final ModelPart head;
	private final ModelPart spyglass;
	private final ModelPart leg1;
	private final ModelPart leg2;
	private final ModelPart leg3;
	private final ModelPart leg4;

	public CameraEntityModel(ModelPart root) {
		this.root = root.getChild("root");
		this.leg1 = this.root.getChild("leg1");
		this.leg2 = this.root.getChild("leg2");
		this.leg3 = this.root.getChild("leg3");
		this.leg4 = this.root.getChild("leg4");
		this.head = this.root.getChild("head");
		this.spyglass = this.head.getChild("spyglass");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData modelPartData1 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
		ModelPartData modelPartData2 = modelPartData1.addChild("head", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 10.0F), ModelTransform.pivot(0.0F,-22.0F,0.0F));
		modelPartData2.addChild("spyglass", ModelPartBuilder.create().uv(40,0).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 11.0F), ModelTransform.pivot(-1.0F,-5.0F,-16.0F));
		modelPartData1.addChild("leg1", ModelPartBuilder.create().uv(36,0).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), ModelTransform.pivot(0.0F,-24.0F,1.0F));
		modelPartData1.addChild("leg2", ModelPartBuilder.create().uv(36,0).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), ModelTransform.pivot(0.0F,-24.0F,-1.0F));
		modelPartData1.addChild("leg3", ModelPartBuilder.create().uv(36,0).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), ModelTransform.pivot(1.0F,-24.0F,0.0F));
		modelPartData1.addChild("leg4", ModelPartBuilder.create().uv(36,0).cuboid(-0.5F, 0.0F, -0.5F, 1.0F, 25.0F, 1.0F), ModelTransform.pivot(-1.0F,-24.0F,0.0F));

		return TexturedModelData.of(modelData,64,32);
	}

	private static final float moveBy = 15F / 1.75F;
	private static final float r = (float)(Math.PI / 180);

	@Override
	public void setAngles(CameraEntity entity, float limbSwing, float limbSwingAmount, float time, float netHeadYaw, float headPitch) {

		float angle = (15 + (((1.75f - entity.getTrackedHeight()) * moveBy) * 6.7F));

		this.leg1.pitch = angle * r;
		this.leg2.pitch = -angle * r;
		this.leg3.roll = -angle * r;
		this.leg4.roll = angle * r;

		this.head.yaw= netHeadYaw * r;
		this.head.pitch= headPitch * r;

		if (entity.hasSpyglass() != this.spyglass.visible) {
			this.spyglass.visible = entity.hasSpyglass();
		}

	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		root.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}