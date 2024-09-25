package net.lunade.camera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.item.CameraItem;
import net.lunade.camera.networking.CameraNetworking;
import net.lunade.camera.registry.CameraBlocks;
import net.lunade.camera.registry.CameraEntityTypes;
import net.lunade.camera.registry.CameraItems;
import net.lunade.camera.registry.CameraMenuTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CameraPortMain implements ModInitializer {
    public static final CameraItem CAMERA_ITEM = new CameraItem(CameraEntityTypes.CAMERA, 0, 0, new Item.Properties());
    public static final CameraItem DISC_CAMERA_ITEM = new CameraItem(CameraEntityTypes.DISC_CAMERA, 0, 0, new Item.Properties());

    public static final SoundEvent CAMERA_BREAK = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.break"));
    public static final SoundEvent CAMERA_FALL = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.fall"));
    public static final SoundEvent CAMERA_HIT = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.hit"));
    public static final SoundEvent CAMERA_PLACE = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.place"));
    public static final SoundEvent CAMERA_PRIME = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.prime"));
    public static final SoundEvent CAMERA_SNAP = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.snap"));
    public static final SoundEvent CAMERA_ADJUST = SoundEvent.createVariableRangeEvent(CameraConstants.id("entity.camera.adjust"));

    public static CameraItem registerCamera(CameraItem cameraItem, ResourceLocation resourceLocation) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, cameraItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, cameraItem));
        return cameraItem;
    }

    @Override
    public void onInitialize() {
        CameraEntityTypes.init();
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_BREAK.getLocation(), CAMERA_BREAK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_HIT.getLocation(), CAMERA_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PLACE.getLocation(), CAMERA_PLACE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PRIME.getLocation(), CAMERA_PRIME);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_SNAP.getLocation(), CAMERA_SNAP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_ADJUST.getLocation(), CAMERA_ADJUST);
        registerCamera(DISC_CAMERA_ITEM, CameraConstants.id("disc_camera"));
        registerCamera(CAMERA_ITEM, CameraConstants.id("camera"));
        CameraBlocks.register();
        CameraItems.register();
        CameraMenuTypes.register();

        CameraNetworking.init();
    }
}
