package net.lunade.camera;

import net.minecraft.world.item.Item;

public class DiscCameraItem extends CameraItem {

    public DiscCameraItem(Item.Properties settings) {
        super(settings);
        this.cameraEntity = Main.SMALL_CAMERA;
    }
}
