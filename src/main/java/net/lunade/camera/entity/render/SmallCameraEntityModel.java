package net.lunade.camera.entity.render;

import net.lunade.camera.entity.SmallCameraEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;

public class SmallCameraEntityModel<A extends SmallCameraEntity> extends EntityModel<SmallCameraEntity> {
	final ModelPart root;
	final ModelPart head;
	private final ModelPart spyglass;
	final ModelPart disc;
	final ModelPart disc2;

	public SmallCameraEntityModel(ModelPart root) {
		this.root = root.getChild("root");
		this.disc = this.root.getChild("disc");
		this.disc2 = this.root.getChild("disc2");
		this.head = this.root.getChild("head");
		this.spyglass = this.head.getChild("spyglass");
	}

	public static TexturedModelData getTexturedModelData() {
		ModelData modelData = new ModelData();
		ModelPartData modelPartData = modelData.getRoot();

		ModelPartData modelPartData1 = modelPartData.addChild("root", ModelPartBuilder.create(), ModelTransform.pivot(0.0F,24.0F,0.0F));
		ModelPartData modelPartData2 = modelPartData1.addChild("head", ModelPartBuilder.create().uv(0,0).cuboid(-4.0F, -8.0F, -5.0F, 8.0F, 8.0F, 10.0F), ModelTransform.pivot(0.0F,15.0F,0.0F));
		modelPartData2.addChild("spyglass", ModelPartBuilder.create().uv(40,0).cuboid(0.0F, 0.0F, 0.0F, 2.0F, 2.0F, 11.0F), ModelTransform.pivot(-1.0F,-5.0F,-16.0F));
		modelPartData1.addChild("disc", ModelPartBuilder.create().uv(0,18).cuboid(-7.5F, 0.0F, 0.0F, 15.0F, 10.0F, 0.0F), ModelTransform.pivot(0.0F,16.5F,0.0F));
		modelPartData1.addChild("disc2", ModelPartBuilder.create().uv(0,18).cuboid(-7.5F, 0.0F, 0.0F, 15.0F, 10.0F, 0.0F), ModelTransform.pivot(0.0F,16.5F,0.0F));

		return TexturedModelData.of(modelData,64,32);
	}

	private static final float r = (float)(Math.PI / 180F);

	@Override
	public void setAngles(SmallCameraEntity entity, float limbSwing, float limbSwingAmount, float time, float netHeadYaw, float headPitch) {
		this.disc.yaw = 45 * r;
		this.disc2.yaw = -45 * r;

		this.head.yaw = netHeadYaw * r;
		this.head.pitch = headPitch * r;

		if (entity.hasSpyglass() != this.spyglass.visible) {
			this.spyglass.visible = entity.hasSpyglass();
		}

	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumer	buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha){
		head.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		matrixStack.scale(1.3f,0.9f,1.3f);
		disc.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
		disc2.render(matrixStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
	}
}