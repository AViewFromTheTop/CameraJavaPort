package net.lunade.camera.datagen.model;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.lunade.camera.registry.CameraPortBlocks;
import net.lunade.camera.registry.CameraPortItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TexturedModel;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class CameraPortModelProvider extends FabricModelProvider {

	public CameraPortModelProvider(FabricDataOutput output) {
		super(output);
	}

	@Override
	public void generateBlockStateModels(@NotNull BlockModelGenerators generator) {
		generator.createHorizontallyRotatedBlock(CameraPortBlocks.PRINTER, TexturedModel.ORIENTABLE);
	}

	@Override
	public void generateItemModels(@NotNull ItemModelGenerators generator) {
		generator.generateFlatItem(CameraPortItems.PHOTOGRAPH.asItem(), ModelTemplates.FLAT_ITEM);

		generator.declareCustomModelItem(CameraPortItems.CAMERA.asItem());
		generator.declareCustomModelItem(CameraPortItems.DISC_CAMERA.asItem());
	}
}
