package net.lunade.camera;

import net.lunade.camera.entity.CameraEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class CameraItem extends SpawnEggItem {

    public CameraItem(EntityType<? extends CameraEntity> entityType, int i, int j, Properties properties) {
        super(entityType, i, j, properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = SpawnEggItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (blockHitResult.getType() != HitResult.Type.BLOCK) {
            if (!player.getCooldowns().isOnCooldown(this)) {
                player.getCooldowns().addCooldown(this, 10);
                if (level.isClientSide) {
                    if (ClientCameraManager.possessingCamera) {
                        return InteractionResultHolder.fail(itemStack);
                    }
                    ClientCameraManager.changeToCamera(null, true);
                }
                return InteractionResultHolder.success(itemStack);
            } else return InteractionResultHolder.fail(itemStack);
        }
        return super.use(level, player, interactionHand);
    }

}
