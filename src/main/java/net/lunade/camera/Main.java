package net.lunade.camera;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.lunade.camera.entity.CameraEntity;
import net.lunade.camera.entity.SmallCameraEntity;
import net.minecraft.core.Registry;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;

public class Main implements ModInitializer {
	public static final String MOD_ID = "camera";

	public static final EntityType<CameraEntity> CAMERA = Registry.register(
			Registry.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "camera"),
			FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MISC).entityFactory(CameraEntity::new).defaultAttributes(SmallCameraEntity::createMobAttributes).dimensions(EntityDimensions.fixed(0.6f, 1.75f)).build()
	);

	public static final EntityType<SmallCameraEntity> SMALL_CAMERA = Registry.register(
			Registry.ENTITY_TYPE,
			new ResourceLocation(MOD_ID, "small_camera"),
			FabricEntityTypeBuilder.createMob().spawnGroup(MobCategory.MISC).entityFactory(SmallCameraEntity::new).defaultAttributes(SmallCameraEntity::createMobAttributes).dimensions(EntityDimensions.fixed(0.55f, 0.9f)).build()
	);

	public static final SoundEvent CAMERA_BREAK = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.break"));
	public static final SoundEvent CAMERA_FALL = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.fall"));
	public static final SoundEvent CAMERA_HIT = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.hit"));
	public static final SoundEvent CAMERA_PLACE = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.place"));
	public static final SoundEvent CAMERA_PRIME = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.prime"));
	public static final SoundEvent CAMERA_SNAP = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.snap"));
	public static final SoundEvent CAMERA_CRACK = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.crack"));
	public static final SoundEvent CAMERA_ADJUST = new SoundEvent(new ResourceLocation(MOD_ID, "entity.camera.adjust"));

	public static final CameraItem CAMERA_ITEM = new CameraItem(new FabricItemSettings().group(CreativeModeTab.TAB_TOOLS));
	public static final DiscCameraItem DISC_CAMERA_ITEM = new DiscCameraItem(new FabricItemSettings());

	@Override
	public void onInitialize() {
		Registry.register(Registry.SOUND_EVENT, CAMERA_BREAK.getLocation(), CAMERA_BREAK);
		Registry.register(Registry.SOUND_EVENT, CAMERA_FALL.getLocation(), CAMERA_FALL);
		Registry.register(Registry.SOUND_EVENT, CAMERA_HIT.getLocation(), CAMERA_HIT);
		Registry.register(Registry.SOUND_EVENT, CAMERA_PLACE.getLocation(), CAMERA_PLACE);
		Registry.register(Registry.SOUND_EVENT, CAMERA_PRIME.getLocation(), CAMERA_PRIME);
		Registry.register(Registry.SOUND_EVENT, CAMERA_SNAP.getLocation(), CAMERA_SNAP);
		Registry.register(Registry.SOUND_EVENT, CAMERA_CRACK.getLocation(), CAMERA_CRACK);
		Registry.register(Registry.SOUND_EVENT, CAMERA_ADJUST.getLocation(), CAMERA_ADJUST);
		Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID,"camera"), CAMERA_ITEM);
		Registry.register(Registry.ITEM, new ResourceLocation(MOD_ID,"disc_camera"), DISC_CAMERA_ITEM);
	}

}
