package net.lunade.camera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.item.CameraItem;
import net.lunade.camera.item.PhotographItem;
import net.lunade.camera.item.PortfolioItem;
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
    public static final PhotographItem PHOTOGRAPH = new PhotographItem(new Item.Properties().stacksTo(1));
    public static final PortfolioItem PORTFOLIO = new PortfolioItem(new Item.Properties().stacksTo(1));

    public static final DataComponentType<PhotographComponent> PHOTO_COMPONENT = DataComponentType.<PhotographComponent>builder()
            .persistent(PhotographComponent.CODEC)
            .networkSynchronized(PhotographComponent.STREAM_CODEC)
            .build();


    public static void register() {
        registerCameraItem(DISC_CAMERA_ITEM, CameraPortConstants.id("disc_camera"));
        registerCameraItem(CAMERA_ITEM, CameraPortConstants.id("camera"));

        Registry.register(BuiltInRegistries.ITEM, CameraPortConstants.id("photograph"), PHOTOGRAPH);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PHOTOGRAPH));

        Registry.register(BuiltInRegistries.ITEM, CameraPortConstants.id("portfolio"), PORTFOLIO);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PORTFOLIO));

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CameraPortConstants.id("photograph_component"), PHOTO_COMPONENT);
    }

    public static void registerCameraItem(CameraItem cameraItem, ResourceLocation resourceLocation) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, cameraItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, cameraItem));
    }
}
