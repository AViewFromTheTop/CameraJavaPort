package net.lunade.camera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.lunade.camera.entity.CameraEntity;
import net.lunade.camera.entity.DiscCameraEntity;
import net.lunade.camera.impl.CameraItem;
import net.lunade.camera.networking.CameraPossessPacket;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class CameraPortMain implements ModInitializer {
    public static final String MOD_ID = "camera_port";

    public static final EntityType<CameraEntity> CAMERA = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "camera"),
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.MISC)
                    .entityFactory(CameraEntity::new)
                    .defaultAttributes(CameraEntity::addAttributes)
                    .dimensions(EntityDimensions.scalable(0.6F, 1.75F).withEyeHeight(1.619999999999999F))
                    .build()
    );
    //0.9257142857142857F
    public static final CameraItem CAMERA_ITEM = new CameraItem(CAMERA, 0, 0, new Item.Properties());
    public static final EntityType<DiscCameraEntity> DISC_CAMERA = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "disc_camera"),
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.MISC)
                    .entityFactory(DiscCameraEntity::new)
                    .defaultAttributes(CameraEntity::addAttributes)
                    .dimensions(EntityDimensions.scalable(0.55F, 0.9F).withEyeHeight(0.81F))
                    .build()
    );
    public static final CameraItem DISC_CAMERA_ITEM = new CameraItem(DISC_CAMERA, 0, 0, new Item.Properties());
    public static final SoundEvent CAMERA_BREAK = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.break"));
    public static final SoundEvent CAMERA_FALL = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.fall"));
    public static final SoundEvent CAMERA_HIT = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.hit"));
    public static final SoundEvent CAMERA_PLACE = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.place"));
    public static final SoundEvent CAMERA_PRIME = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.prime"));
    public static final SoundEvent CAMERA_SNAP = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.snap"));
    public static final SoundEvent CAMERA_ADJUST = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.adjust"));

    public static CameraItem registerCamera(CameraItem cameraItem, ResourceLocation resourceLocation) {
        Registry.register(BuiltInRegistries.ITEM, resourceLocation, cameraItem);
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, cameraItem));
        return cameraItem;
    }

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_BREAK.getLocation(), CAMERA_BREAK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_HIT.getLocation(), CAMERA_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PLACE.getLocation(), CAMERA_PLACE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PRIME.getLocation(), CAMERA_PRIME);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_SNAP.getLocation(), CAMERA_SNAP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_ADJUST.getLocation(), CAMERA_ADJUST);
        registerCamera(DISC_CAMERA_ITEM, new ResourceLocation(MOD_ID, "disc_camera"));
        registerCamera(CAMERA_ITEM, new ResourceLocation(MOD_ID, "camera"));

        PayloadTypeRegistry<RegistryFriendlyByteBuf> registry = PayloadTypeRegistry.playS2C();
        registry.register(CameraPossessPacket.PACKET_TYPE, CameraPossessPacket.CODEC);
    }
}
