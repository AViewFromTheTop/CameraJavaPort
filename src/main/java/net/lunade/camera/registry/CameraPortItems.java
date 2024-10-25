package net.lunade.camera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.component.WritablePortfolioContent;
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

public class CameraPortItems {
    public static final CameraItem CAMERA_ITEM = new CameraItem(CameraPortEntityTypes.CAMERA, 0, 0, new Item.Properties());
    public static final CameraItem DISC_CAMERA_ITEM = new CameraItem(CameraPortEntityTypes.DISC_CAMERA, 0, 0, new Item.Properties());
    public static final PhotographItem PHOTOGRAPH = new PhotographItem(new Item.Properties().stacksTo(1));

    public static final DataComponentType<PhotographComponent> PHOTO_COMPONENT = DataComponentType.<PhotographComponent>builder()
            .persistent(PhotographComponent.CODEC)
            .networkSynchronized(PhotographComponent.STREAM_CODEC)
            .build();

    public static final DataComponentType<WritablePortfolioContent> WRITABLE_PORTFOLIO_CONTENT = DataComponentType.<WritablePortfolioContent>builder()
            .persistent(WritablePortfolioContent.CODEC)
            .networkSynchronized(WritablePortfolioContent.STREAM_CODEC)
            .build();

    public static final PortfolioItem PORTFOLIO = new PortfolioItem(new Item.Properties().stacksTo(1).component(WRITABLE_PORTFOLIO_CONTENT, WritablePortfolioContent.EMPTY));


    public static void register() {
        registerCameraItem(DISC_CAMERA_ITEM, CameraPortConstants.id("disc_camera"));
        registerCameraItem(CAMERA_ITEM, CameraPortConstants.id("camera"));

        Registry.register(BuiltInRegistries.ITEM, CameraPortConstants.id("photograph"), PHOTOGRAPH);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PHOTOGRAPH));

        Registry.register(BuiltInRegistries.ITEM, CameraPortConstants.id("portfolio"), PORTFOLIO);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register((entries) -> entries.addAfter(Items.WRITABLE_BOOK, PORTFOLIO));

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CameraPortConstants.id("photograph_component"), PHOTO_COMPONENT);
    }

    public static void registerCameraItem(CameraItem cameraItem, ResourceLocation resourceLocation) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, cameraItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, cameraItem));
    }
}
