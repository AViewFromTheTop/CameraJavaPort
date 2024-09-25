package net.lunade.camera.item;

import net.lunade.camera.CameraConstants;
import net.lunade.camera.component.tooltip.PictureTooltipComponent;
import net.lunade.camera.entity.Photograph;
import net.lunade.camera.registry.CameraEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

public class PictureItem extends Item {
    private final EntityType<? extends HangingEntity> type;

    public PictureItem(Properties settings) {
        super(settings);
        this.type = CameraEntityTypes.PHOTOGRAPH;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos blockPos = context.getClickedPos();
        Direction direction = context.getClickedFace();
        BlockPos blockPos2 = blockPos.relative(direction);
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if(player != null && !this.mayPlace(player, direction, itemStack, blockPos2))
            return InteractionResult.FAIL;
        else {
            Level level = context.getLevel();
            Photograph hangingEntity = new Photograph(level,blockPos2, direction);
            CustomData customData = itemStack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
            if(!customData.isEmpty())
                EntityType.updateCustomEntityTag(level, player, hangingEntity, customData);
            if(hangingEntity.survives()) {
                if(!level.isClientSide) {
                    hangingEntity.playPlacementSound();
                    level.gameEvent(player, GameEvent.ENTITY_PLACE, hangingEntity.position());
                    level.addFreshEntity(hangingEntity);
                }
                itemStack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else return InteractionResult.CONSUME;
        }
    }

    protected boolean mayPlace(Player player, Direction side, ItemStack stack, BlockPos pos) {
        return !side.getAxis().isVertical() && player.mayUseItemAt(pos, side, stack);
    }

    public static @Nullable ResourceLocation getPhoto(ItemStack stack) {
        final var a = stack.getOrDefault(DataComponents.ENTITY_DATA, CustomData.EMPTY);
        AtomicReference<ResourceLocation> re = new AtomicReference<>();
        if(!a.isEmpty()) {
            a.read(Photograph.ID_MAP_CODEC).result().ifPresentOrElse(value -> re.set(CameraConstants.id("photographs/" + value)), () -> {});
        }
        return re.get();
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        final var a = getPhoto(stack);
        if(a != null) return Optional.of(new PictureTooltipComponent(a));
        return Optional.empty();
    }
}
