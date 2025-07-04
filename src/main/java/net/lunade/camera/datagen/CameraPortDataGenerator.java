package net.lunade.camera.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.frozenblock.lib.feature_flag.api.FeatureFlagApi;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.datagen.loot.CameraPortBlockLootProvider;
import net.lunade.camera.datagen.model.CameraPortModelProvider;
import net.lunade.camera.datagen.recipe.CameraPortRecipeProvider;
import net.lunade.camera.datagen.tag.CameraPortBlockTagProvider;
import net.lunade.camera.datagen.tag.CameraPortEntityTagProvider;
import net.lunade.camera.datagen.tag.CameraPortItemTagProvider;
import net.minecraft.core.RegistrySetBuilder;
import org.jetbrains.annotations.NotNull;

public final class CameraPortDataGenerator implements DataGeneratorEntrypoint {

	@Override
	public void onInitializeDataGenerator(@NotNull FabricDataGenerator dataGenerator) {
		FeatureFlagApi.rebuild();
		final FabricDataGenerator.Pack pack = dataGenerator.createPack();

		// ASSETS

		pack.addProvider(CameraPortModelProvider::new);

		// DATA

		pack.addProvider(CameraPortBlockLootProvider::new);
		pack.addProvider(CameraPortBlockTagProvider::new);
		pack.addProvider(CameraPortItemTagProvider::new);
		pack.addProvider(CameraPortEntityTagProvider::new);
		pack.addProvider(CameraPortRecipeProvider::new);
	}

	@Override
	public void buildRegistry(@NotNull RegistrySetBuilder registryBuilder) {
	}

	@Override
	public @NotNull String getEffectiveModId() {
		return CameraPortConstants.MOD_ID;
	}
}
