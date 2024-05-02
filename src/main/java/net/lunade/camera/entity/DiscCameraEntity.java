package net.lunade.camera.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class DiscCameraEntity extends CameraEntity {

    public DiscCameraEntity(EntityType<? extends DiscCameraEntity> entityType, Level world) {
        super(entityType, world);
        this.setTrackedHeight(0.9F);
    }

    @Override
    public float getMaxHeight() {
        return 0.9F;
    }

    @Override
    public float getMinHeight() {
        return 0.9F;
    }

    @Override
    public float getBoundingBoxRadius() {
        return 0.275F;
    }

    @Override
    public boolean canBeAdjusted() {
        return false;
    }

}
