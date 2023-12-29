package net.lunade.camera;

import net.lunade.camera.entity.CameraEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Screenshot;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.MobSpawnerLogic;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Objects;

public class CameraItem extends Item {

    public CameraItem(Item.Properties settings) {
        super(settings);
        this.cameraEntity = Main.CAMERA;
    }

    public static boolean canGo;
    public static boolean wasHidden;
    public EntityType<? extends CameraEntity> cameraEntity;

    @Override
    public void inventoryTick(ItemStack itemStack, Level world, Entity entity, int i, boolean bl) {
        if (entity instanceof Player player) {
            if (player.getCooldowns().isOnCooldown(this) && player.getCooldowns().getCooldownPercent(this, 0) == 0.9F) {
                world.playSound(null, entity.getX(), entity.getEyeY(), entity.getZ(), Main.CAMERA_SNAP, SoundCategory.PLAYERS, 0.5F, 1F);
                if (world.isClientSide && canGo) {
                Minecraft client = Minecraft.getInstance();
                Screenshot.grab(client, client.renderBuffers(), (text) -> client.execute(() -> client.gui.getChat().addMessage(text)));
                client.options.hudHidden = wasHidden;
                canGo = false;
                }
            }
        }
    }

    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (!(world instanceof ServerWorld)) {
            return ActionResult.SUCCESS;
        } else {
            ItemStack itemStack = context.getStack();
            BlockPos blockPos = context.getBlockPos();
            Direction direction = context.getSide();
            BlockState blockState = world.getBlockState(blockPos);
            if (blockState.isOf(Blocks.SPAWNER)) {
                BlockEntity blockEntity = world.getBlockEntity(blockPos);
                if (blockEntity instanceof MobSpawnerBlockEntity) {
                    MobSpawnerLogic mobSpawnerLogic = ((MobSpawnerBlockEntity)blockEntity).getLogic();
                    EntityType<?> entityType = this.cameraEntity;
                    mobSpawnerLogic.setEntityId(entityType);
                    blockEntity.markDirty();
                    world.updateListeners(blockPos, blockState, blockState, 3);
                    itemStack.decrement(1);
                    return ActionResult.CONSUME;
                }
            }
            BlockPos blockEntityPos = blockState.getCollisionShape(world, blockPos).isEmpty() ? blockPos : blockPos.offset(direction);
            boolean bl2 = !Objects.equals(blockPos, blockEntityPos) && direction == Direction.UP;
            EntityType<? extends CameraEntity> entityType = this.cameraEntity;
            CameraEntity entity = (CameraEntity) entityType.spawnFromItemStack((ServerWorld)world, itemStack, context.getPlayer(), blockEntityPos, SpawnReason.SPAWN_EGG, true, bl2);
            if (entity != null) {
                itemStack.decrement(1);
                entity.world.playSound(null, entity.getX(),entity.getY(),entity.getZ(), Main.CAMERA_PLACE, SoundCategory.NEUTRAL, 0.5F, (entity.getRandom().nextFloat() * 0.25F) + 0.75F);
                world.emitGameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockPos);
            }
            return ActionResult.CONSUME;
        }
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        BlockHitResult hitResult = raycast(world, user, RaycastContext.FluidHandling.SOURCE_ONLY);
        if (hitResult.getType() != HitResult.Type.BLOCK) {
            if (!user.getItemCooldownManager().isCoolingDown(this)) {
                user.getItemCooldownManager().set(this, 10);
                if (world.isClient) {
                    canGo = true;
                    MinecraftClient client = MinecraftClient.getInstance();
                    wasHidden = client.options.hudHidden;
                    client.options.hudHidden = true;
                }
                return TypedActionResult.success(itemStack);
            } else return TypedActionResult.fail(itemStack);
        } else if (!(world instanceof ServerWorld)) {
            return TypedActionResult.success(itemStack);
        } else {
            BlockPos blockPos = hitResult.getBlockPos();
            if (!(world.getBlockState(blockPos).getBlock() instanceof FluidBlock)) {
                return TypedActionResult.pass(itemStack);
            } else if (world.canPlayerModifyAt(user, blockPos) && user.canPlaceOn(blockPos, hitResult.getSide(), itemStack)) {
                EntityType<? extends CameraEntity> entityType = this.cameraEntity;
                if (entityType.spawnFromItemStack((ServerWorld)world, itemStack, user, blockPos, SpawnReason.SPAWN_EGG, false, false) == null) {
                    return TypedActionResult.pass(itemStack);
                } else {
                    if (!user.getAbilities().creativeMode) {
                        itemStack.decrement(1);
                    }
                    user.incrementStat(Stats.USED.getOrCreateStat(this));
                    world.emitGameEvent(user, GameEvent.ENTITY_PLACE, user.getPos());
                    return TypedActionResult.consume(itemStack);
                }
            } else {
                return TypedActionResult.fail(itemStack);
            }
        }
    }

}
