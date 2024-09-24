package net.lunade.camera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.impl.PictureComponent;
import net.lunade.camera.impl.PictureItem;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class RegisterItems {
    public static final PictureItem PHOTO_ITEM = new PictureItem(new Item.Properties().stacksTo(1));

    public static final DataComponentType<String> PHOTO_COMPONENT = DataComponentType.<String>builder()
            .persistent(PictureComponent.CODEC)
            .networkSynchronized(PictureComponent.STREAM_CODEC)
            .build();

    public static void register() {
        Registry.register(BuiltInRegistries.ITEM, CameraConstants.id("picture"), PHOTO_ITEM);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PHOTO_ITEM));

        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, CameraConstants.id("picture_component"), PHOTO_COMPONENT);
    }
}
