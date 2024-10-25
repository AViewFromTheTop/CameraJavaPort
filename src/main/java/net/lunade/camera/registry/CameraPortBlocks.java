package net.lunade.camera.registry;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.block.PrinterBlock;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CameraPortBlocks {

    public static final Block PRINTER = new PrinterBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.LOOM));

    public static void register() {
        Registry.register(BuiltInRegistries.BLOCK, CameraPortConstants.id("printer"), PRINTER);
        Registry.register(BuiltInRegistries.ITEM, CameraPortConstants.id("printer"), new BlockItem(PRINTER, new Item.Properties()));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register((entries) -> entries.addAfter(Items.LODESTONE, PRINTER));
    }
}
