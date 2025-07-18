package net.lunade.camera.datagen.tag;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public final class CameraPortItemTagProvider extends FabricTagProvider.ItemTagProvider {

	public CameraPortItemTagProvider(@NotNull FabricDataOutput output, @NotNull CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@NotNull
	private TagKey<Item> getTag(String id) {
		return TagKey.create(this.registryKey, ResourceLocation.parse(id));
	}

	@NotNull private ResourceKey<Item> getKey(String namespace, String path) {
		return ResourceKey.create(this.registryKey, ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	@Override
	protected void addTags(@NotNull HolderLookup.Provider arg) {

	}
}
