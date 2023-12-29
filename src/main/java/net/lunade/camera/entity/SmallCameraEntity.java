package net.lunade.camera.entity;

import net.lunade.camera.Main;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class SmallCameraEntity extends CameraEntity {

    public SmallCameraEntity(EntityType<? extends SmallCameraEntity> entityType, Level world) {
        super(entityType, world);
        this.maxHeight = 0.9F;
        this.minHeight = 0.9F;
        this.extendBy = 0.275F;
        this.setTrackedHeight(0.9F);
        this.cameraItem = Main.DISC_CAMERA_ITEM;
        this.oppositeCamera = Main.CAMERA;
    }

    protected float getActiveEyeHeight(Pose entityPose, EntityDimensions entityDimensions) {
        return 0.81F;
    }

    public ItemStack getPickBlockStack() {
        return new ItemStack(this.cameraItem);
    }

}
