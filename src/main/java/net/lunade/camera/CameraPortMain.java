package net.lunade.camera;

import net.fabricmc.api.ModInitializer;
import net.lunade.camera.networking.CameraNetworking;
import net.lunade.camera.registry.CameraPortBlocks;
import net.lunade.camera.registry.CameraPortEntityTypes;
import net.lunade.camera.registry.CameraPortItems;
import net.lunade.camera.registry.CameraPortMenuTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;

public class CameraPortMain implements ModInitializer {
    public static final SoundEvent CAMERA_BREAK = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.break"));
    public static final SoundEvent CAMERA_FALL = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.fall"));
    public static final SoundEvent CAMERA_HIT = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.hit"));
    public static final SoundEvent CAMERA_PLACE = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.place"));
    public static final SoundEvent CAMERA_PRIME = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.prime"));
    public static final SoundEvent CAMERA_SNAP = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.snap"));
    public static final SoundEvent CAMERA_ADJUST = SoundEvent.createVariableRangeEvent(CameraPortConstants.id("entity.camera.adjust"));

    @Override
    public void onInitialize() {
        CameraPortEntityTypes.init();
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_BREAK.getLocation(), CAMERA_BREAK);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_HIT.getLocation(), CAMERA_HIT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PLACE.getLocation(), CAMERA_PLACE);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_PRIME.getLocation(), CAMERA_PRIME);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_SNAP.getLocation(), CAMERA_SNAP);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CAMERA_ADJUST.getLocation(), CAMERA_ADJUST);
        CameraPortBlocks.register();
        CameraPortItems.register();
        CameraPortMenuTypes.register();

        CameraNetworking.init();
    }
}
