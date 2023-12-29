package net.lunade.camera.mixin;

import net.lunade.camera.Main;
import net.minecraft.block.Blocks;
import net.minecraft.block.JukeboxBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.JukeboxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.JukeboxBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;

import static net.minecraft.block.JukeboxBlock.HAS_RECORD;
import static net.minecraft.world.level.block.JukeboxBlock.HAS_RECORD;

@Mixin(Entity.class)
public class EntityMixin {

    @Inject(at = @At("HEAD"), method = "changeDimension")
    public void moveToWorld(ServerLevel serverLevel, CallbackInfoReturnable<Entity> info) {
        Entity entity = Entity.class.cast(this);
        if (entity instanceof ItemEntity item) {
            ArrayList<BlockPos> poses = jukeboxSphere(entity.blockPosition(), 16, serverLevel);
            if (item.getItem().is(Main.CAMERA_ITEM) && !poses.isEmpty()) {
                int count = item.getItem().getCount();
                item.setStack(new ItemStack(Main.DISC_CAMERA_ITEM));
                item.getStack().setCount(count);
                item.world.createExplosion(entity, entity.getX(), entity.getY(), entity.getZ(),0, Explosion.DestructionType.NONE);
                for (BlockPos pos : poses) {
                    if (entity.world.getBlockState(pos).getBlock() instanceof JukeboxBlock && !entity.world.isClient) {
                        BlockEntity blockEntity = entity.world.getBlockEntity(pos);
                        if (blockEntity instanceof JukeboxBlockEntity jukeboxBlockEntity) {
                            ItemStack itemStack = jukeboxBlockEntity.getRecord();
                            if (!itemStack.isEmpty()) {
                                entity.world.syncWorldEvent(1010, pos, 0);
                                jukeboxBlockEntity.clear();
                                double d = (double)(entity.world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                                double e = (double)(entity.world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                                double g = (double)(entity.world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                                ItemStack itemStack2 = itemStack.copy();
                                ItemEntity itemEntity = new ItemEntity(entity.world, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + g, itemStack2);
                                itemEntity.setToDefaultPickupDelay();
                                entity.world.spawnEntity(itemEntity);
                                entity.world.setBlockState(pos, Blocks.JUKEBOX.getDefaultState());
                            }
                        }
                    }
                }
            }
        }
    }

    private static ArrayList<BlockPos> jukeboxSphere(BlockPos pos, int radius, Level world) {
        ArrayList<BlockPos> blocks = new ArrayList<>();
        int bx = pos.getX();
        int by = pos.getY();
        int bz = pos.getZ();
        for(int x = bx - radius; x <= bx + radius; x++) {
            for(int y = by - radius; y <= by + radius; y++) {
                for(int z = bz - radius; z <= bz + radius; z++) {
                    double distance = ((bx-x) * (bx-x) + ((bz-z) * (bz-z)) + ((by-y) * (by-y)));
                    if(distance < radius * radius) {
                        BlockPos l = new BlockPos(x, y, z);
                        if (world.getBlockState(l).getBlock() instanceof JukeboxBlock) {
                            if (world.getBlockState(l).getValue(HAS_RECORD)) {blocks.add(l);}
                        }
                    }
                }
            }
        } return blocks;
    }

}
