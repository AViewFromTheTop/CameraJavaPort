package net.lunade.camera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.component.PictureComponent;
import net.lunade.camera.item.CameraItem;
import net.lunade.camera.item.PictureItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CameraItems {
    public static final CameraItem CAMERA_ITEM = new CameraItem(CameraEntityTypes.CAMERA, 0, 0, new Item.Properties());
    public static final CameraItem DISC_CAMERA_ITEM = new CameraItem(CameraEntityTypes.DISC_CAMERA, 0, 0, new Item.Properties());
    public static final PictureItem PICTURE = new PictureItem(new Item.Properties().stacksTo(1));

    public static final DataComponentType<PictureComponent> PHOTO_COMPONENT = DataComponentType.<PictureComponent>builder()
            .persistent(PictureComponent.CODEC)
            .networkSynchronized(PictureComponent.STREAM_CODEC)
            .build();


    public static void register() {
        registerCameraItem(DISC_CAMERA_ITEM, CameraConstants.id("disc_camera"));
        registerCameraItem(CAMERA_ITEM, CameraConstants.id("camera"));

        Registry.register(BuiltInRegistries.ITEM, CameraConstants.id("picture"), PICTURE);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PICTURE));

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CameraConstants.id("picture_component"), PHOTO_COMPONENT);
    }

    public static void registerCameraItem(CameraItem cameraItem, ResourceLocation resourceLocation) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, cameraItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, cameraItem));
    }
}
