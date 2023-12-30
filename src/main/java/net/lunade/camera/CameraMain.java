package net.lunade.camera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.lunade.camera.entity.CameraEntity;
import net.lunade.camera.entity.DiscCameraEntity;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class CameraMain implements ModInitializer {
    public static final String MOD_ID = "camera_port";

    public static final EntityType<CameraEntity> CAMERA = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "camera"),
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.MISC)
                    .entityFactory(CameraEntity::new)
                    .defaultAttributes(CameraEntity::addAttributes)
                    .dimensions(EntityDimensions.fixed(0.6F, 1.75F))
                    .build()
    );

    public static final EntityType<DiscCameraEntity> SMALL_CAMERA = Registry.register(
            BuiltInRegistries.ENTITY_TYPE,
            new ResourceLocation(MOD_ID, "small_camera"),
            FabricEntityTypeBuilder.createMob()
                    .spawnGroup(MobCategory.MISC)
                    .entityFactory(DiscCameraEntity::new)
                    .defaultAttributes(CameraEntity::addAttributes)
                    .dimensions(EntityDimensions.fixed(0.55f, 0.9f))
                    .build()
    );

    public static final CameraItem CAMERA_ITEM = new CameraItem(CAMERA, 0, 0, new FabricItemSettings());
    public static final CameraItem DISC_CAMERA_ITEM = new CameraItem(SMALL_CAMERA, 0, 0, new FabricItemSettings());
    public static final SoundEvent CAMERA_BREAK = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.break"));
    public static final SoundEvent CAMERA_FALL = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.fall"));
    public static final SoundEvent CAMERA_HIT = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.hit"));
    public static final SoundEvent CAMERA_PLACE = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.place"));
    public static final SoundEvent CAMERA_PRIME = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.prime"));
    public static final SoundEvent CAMERA_SNAP = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.snap"));
    public static final SoundEvent CAMERA_CRACK = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.crack"));
    public static final SoundEvent CAMERA_ADJUST = SoundEvent.createVariableRangeEvent(new ResourceLocation(MOD_ID, "entity.camera.adjust"));

    @Override
    public void onInitialize() {
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_BREAK.getLocation(), CAMERA_BREAK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_HIT.getLocation(), CAMERA_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PLACE.getLocation(), CAMERA_PLACE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PRIME.getLocation(), CAMERA_PRIME);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_SNAP.getLocation(), CAMERA_SNAP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_ADJUST.getLocation(), CAMERA_ADJUST);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "camera"), CAMERA_ITEM);
        Registry.register(BuiltInRegistries.ITEM, new ResourceLocation(MOD_ID, "disc_camera"), DISC_CAMERA_ITEM);
    }
}
