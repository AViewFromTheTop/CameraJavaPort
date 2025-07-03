package net.lunade.camera.registry;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.block.PrinterBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DoubleHighBlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.TallFlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CameraPortBlocks {

	public static final PrinterBlock PRINTER = register("printer",
		PrinterBlock::new,
		BlockBehaviour.Properties.ofFullCopy(Blocks.LOOM)
	);

	public static void register() {
		ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PRINTER));
	}

	private static <T extends Block> T registerWithoutItem(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		ResourceLocation id = CameraPortConstants.id(path);
		return doRegister(id, makeBlock(block, properties, id));
	}

	private static <T extends Block> T register(String path, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties properties) {
		T registered = registerWithoutItem(path, block, properties);
		registerBlockItem(registered);
		return registered;
	}

	private static <T extends Block> T doRegister(ResourceLocation id, T block) {
		if (BuiltInRegistries.BLOCK.getOptional(id).isEmpty()) {
			return Registry.register(BuiltInRegistries.BLOCK, id, block);
		}
		throw new IllegalArgumentException("Block with id " + id + " is already in the block registry.");
	}

	private static <T extends Block> T makeBlock(Function<BlockBehaviour.Properties, T> function, BlockBehaviour.Properties properties, ResourceLocation id) {
		return function.apply(properties.setId(ResourceKey.create(Registries.BLOCK, id)));
	}

	private static void registerBlockItem(Block block) {
		BiFunction<Block, Item.Properties, Item> itemSupplier = BlockItem::new;
		if (block instanceof DoorBlock || block instanceof TallFlowerBlock) itemSupplier = DoubleHighBlockItem::new;
		Items.registerBlock(block, itemSupplier);
	}
}
