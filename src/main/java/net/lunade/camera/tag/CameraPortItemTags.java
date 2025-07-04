package net.lunade.camera.tag;

import net.lunade.camera.CameraPortConstants;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public final class CameraPortItemTags {

	private CameraPortItemTags() {
		throw new UnsupportedOperationException("CameraPortItemTags contains only static declarations.");
	}

	@NotNull
	private static TagKey<Item> bind(@NotNull String path) {
		return TagKey.create(Registries.ITEM, CameraPortConstants.id(path));
	}
}
