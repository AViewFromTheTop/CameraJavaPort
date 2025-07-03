package net.lunade.camera.item;

import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PortfolioItem extends Item {
	public PortfolioItem(Item.Properties properties) {
		super(properties);
	}

	@Override
	public @NotNull InteractionResult use(@NotNull Level world, @NotNull Player user, InteractionHand hand) {
		ItemStack itemStack = user.getItemInHand(hand);
		user.openItemGui(itemStack, hand);
		user.awardStat(Stats.ITEM_USED.get(this));
		return InteractionResult.SUCCESS;
	}
}
