package net.lunade.camera.item;

import java.util.Optional;
import net.lunade.camera.component.PhotographComponent;
import net.lunade.camera.component.tooltip.PhotographTooltipComponent;
import net.lunade.camera.registry.CameraPortItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PhotographItem extends Item {

	public PhotographItem(Properties settings) {
		super(settings);
	}

	public static @Nullable ResourceLocation getPhotograph(@NotNull ItemStack stack) {
		PhotographComponent component = stack.get(CameraPortItems.PHOTO_COMPONENT);
		if (component != null) return component.location();
		return null;
	}

	@Override
	public @NotNull Optional<TooltipComponent> getTooltipImage(@NotNull ItemStack stack) {
		TooltipDisplay tooltipDisplay = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
		if (!tooltipDisplay.shows(CameraPortItems.PHOTO_COMPONENT)) return Optional.empty();

		ResourceLocation photographLocation = getPhotograph(stack);
		if (photographLocation != null) return Optional.of(new PhotographTooltipComponent(photographLocation));
		return Optional.empty();
	}
}
