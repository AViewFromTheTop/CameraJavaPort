package net.lunade.camera.datagen.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.lunade.camera.registry.CameraPortEntityTypes;
import net.lunade.camera.tag.CameraPortEntityTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagEntry;
import org.jetbrains.annotations.NotNull;

public final class CameraPortEntityTagProvider extends FabricTagProvider.EntityTypeTagProvider {

	public CameraPortEntityTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider arg) {
		this.valueLookupBuilder(CameraPortEntityTags.CAMERAS)
			.add(CameraPortEntityTypes.CAMERA)
			.add(CameraPortEntityTypes.DISC_CAMERA);

		this.getOrCreateRawBuilder(EntityTypeTags.CAN_BREATHE_UNDER_WATER)
			.add(TagEntry.optionalTag(CameraPortEntityTags.CAMERAS.location()));
	}
}
