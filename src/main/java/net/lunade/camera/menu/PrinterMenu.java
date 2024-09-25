package net.lunade.camera.menu;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraConstants;
import net.lunade.camera.CameraPortMain;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.entity.Photograph;
import net.lunade.camera.impl.client.PhotographLoader;
import net.lunade.camera.item.PhotographItem;
import net.lunade.camera.registry.CameraBlocks;
import net.lunade.camera.registry.CameraItems;
import net.lunade.camera.registry.CameraMenuTypes;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.NotNull;

public class PrinterMenu extends AbstractContainerMenu {
    private final ContainerLevelAccess access;
    private final DataSlot pictureSlotsSize = DataSlot.standalone();
    private ItemStack input = ItemStack.EMPTY;
    private String temp;
    long lastSoundTime;
    final Slot inputSlot;
    final Slot resultSlot;
    Runnable slotUpdateListener = () -> {};
    public final Container container = new SimpleContainer(1) {
        @Override
        public void setChanged() {
            super.setChanged();
            PrinterMenu.this.slotsChanged(this);
            PrinterMenu.this.slotUpdateListener.run();
        }
    };
    final ResultContainer resultContainer = new ResultContainer();

    public PrinterMenu(int id, Inventory playerInventory) {
        this(id, playerInventory, ContainerLevelAccess.NULL);
    }

    public PrinterMenu(int id, Inventory playerInventory, ContainerLevelAccess context) {
        super(CameraMenuTypes.PRINTER, id);

        this.access = context;
        this.inputSlot = addSlot(
                new Slot(this.container, 0, 8, 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return stack.is(Items.PAPER);
                    }
                }
        );
        this.resultSlot = addSlot(
                new Slot(this.resultContainer, 1, 152, 18) {

                    @Override
                    public boolean mayPlace(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public void onTake(Player player, ItemStack stack) {
                        stack.onCraftedBy(player.level(), player, stack.getCount());
                        ItemStack itemStack = PrinterMenu.this.inputSlot.remove(1);
                        if (!itemStack.isEmpty()) {
                            PrinterMenu.this.setupResultSlot();
                        }

                        PrinterMenu.this.access.execute((level, blockPos) -> {
                            long l = level.getGameTime();
                            if (PrinterMenu.this.lastSoundTime != l) {
                                level.playSound(null, blockPos, CameraPortMain.CAMERA_SNAP, SoundSource.BLOCKS, 1F, 1F);
                                PrinterMenu.this.lastSoundTime = l;
                            }
                        });
                        PhotographLoader.onReceiveItem(PrinterMenu.this.temp, player);
                        super.onTake(player, stack);
                    }
                });
        
        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 198));
        }

        this.addDataSlot(this.pictureSlotsSize);
    }

    public boolean hasInputItem() {
        return this.inputSlot.hasItem() && pictureSlotsSize.get() != 0;
    }

    public ItemStack getInputItem() {
        return this.inputSlot.getItem();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, CameraBlocks.PRINTER);
    }

    @Override
    public void slotsChanged(Container inventory) {
        ItemStack itemstack = this.inputSlot.getItem();
        if (!itemstack.is(this.input.getItem())) {
            this.input = itemstack.copy();
            this.resultSlot.set(ItemStack.EMPTY);
        }
    }

    void setupResultSlot() {
        if (pictureSlotsSize.get() != 0 && this.inputSlot.getItem().is(Items.PAPER)) {
            ItemStack stack = new ItemStack(CameraItems.PHOTOGRAPH);
            String photographName = this.temp.replace("photographs/", "");
            stack.set(
                    CameraItems.PHOTO_COMPONENT,
                    new PhotographComponent(CameraConstants.id("photographs/" + photographName))
            );

            final CompoundTag tag = new CompoundTag();
            tag.putString(Photograph.PICTURE_NAME_KEY, photographName);
            tag.putString("id", "photograph");
            stack.set(
                    DataComponents.ENTITY_DATA,
                    CustomData.of(tag)
            );
            this.resultSlot.set(stack);
        } else {
            this.resultSlot.set(ItemStack.EMPTY);
        }
        this.broadcastChanges();
    }

    public void registerUpdateListener(Runnable listener) {
        this.slotUpdateListener = listener;
    }

    @Override
    public boolean canTakeItemForPickAll(ItemStack stack, @NotNull Slot slot) {
        return slot.container != this.resultContainer && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int fromIndex) {
        Slot slot = this.slots.get(fromIndex);
        ItemStack itemStack = ItemStack.EMPTY;
        if(slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            Item item = itemStack1.getItem();
            itemStack = itemStack1.copy();
            if (fromIndex == 1) {
                item.onCraftedBy(itemStack1, player.level(), player);
                if (!this.moveItemStackTo(itemStack1, 2, 38, false)) return ItemStack.EMPTY;
                slot.onQuickCraft(itemStack1, itemStack);
            } else if (fromIndex == 0) {
                if (!this.moveItemStackTo(itemStack1, 2, 38, true)) return ItemStack.EMPTY;
            } else if (itemStack1.getItem() instanceof PhotographItem) {
                if (!this.moveItemStackTo(itemStack1, 0, 1, false)) return ItemStack.EMPTY;
            } else if(fromIndex >= 2 && fromIndex < 29) {
                if (!this.moveItemStackTo(itemStack1, 29, 38, false)) return ItemStack.EMPTY;
            } else if(fromIndex >= 29 && fromIndex < 38 && !this.moveItemStackTo(itemStack1, 2, 29, false)) return ItemStack.EMPTY;

            if(itemStack1.isEmpty())
                slot.set(ItemStack.EMPTY);

            slot.setChanged();
            if(itemStack1.getCount() == itemStack.getCount())
                return ItemStack.EMPTY;

            slot.onTake(player, itemStack1);
            this.broadcastChanges();
            this.setupResultSlot();
        }
        return itemStack;
    }

    @Override
    public void removed(Player player) {
        super.removed(player);
        this.resultContainer.removeItemNoUpdate(1);
        this.access.execute((level, pos) -> this.clearContainer(player, this.container));
    }

    public void setupData(int size, String id) {
        this.pictureSlotsSize.set(size);
        this.temp = id;
        this.setupResultSlot();
    }

    @Environment(EnvType.CLIENT)
    public void onClient(String selected) {
        this.temp = selected;
    }
}
