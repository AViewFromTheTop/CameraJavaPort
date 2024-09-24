package net.lunade.camera;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.lunade.camera.entity.CameraEntity;
import net.lunade.camera.entity.DiscCameraEntity;
import net.lunade.camera.entity.Photograph;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import org.jetbrains.annotations.NotNull;

public class CameraEntityTypes {
    public static final EntityType<CameraEntity> CAMERA = register(
            "camera",
            EntityType.Builder.of(CameraEntity::new, MobCategory.MISC)
                    .sized(0.6F, 1.75F)
                    .eyeHeight(1.619999999999999F)
                    .clientTrackingRange(8)
                    .build(CameraConstants.string("camera"))
    );

    public static final EntityType<DiscCameraEntity> DISC_CAMERA = register(
            "disc_camera",
            EntityType.Builder.of(DiscCameraEntity::new, MobCategory.MISC)
                    .sized(0.55F, 0.9F)
                    .eyeHeight(0.81F)
                    .clientTrackingRange(8)
                    .build(CameraConstants.string("disc_camera"))
    );

    public static final EntityType<Photograph> PHOTOGRAPH = register(
            "photograph",
            EntityType.Builder.<Photograph>of(Photograph::new, MobCategory.MISC)
                    .sized(0.5F, 0.5F)
                    .eyeHeight(0.81F)
                    .clientTrackingRange(8)
                    .updateInterval(Integer.MAX_VALUE)
                    .build(CameraConstants.string("photograph"))
    );

    public static void init() {
        FabricDefaultAttributeRegistry.register(CAMERA, CameraEntity.addAttributes());
        FabricDefaultAttributeRegistry.register(DISC_CAMERA, DiscCameraEntity.addAttributes());
    }

    @NotNull
    private static <E extends Entity, T extends EntityType<E>> T register(@NotNull String path, @NotNull T entityType) {
        return Registry.register(BuiltInRegistries.ENTITY_TYPE, CameraConstants.id(path), entityType);
    }
}
