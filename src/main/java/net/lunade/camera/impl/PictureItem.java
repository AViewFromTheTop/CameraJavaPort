package net.lunade.camera.impl;

import net.lunade.camera.registry.RegisterItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PictureItem extends Item {
    public PictureItem(Properties settings) {
        super(settings);
    }

    public static @Nullable ResourceLocation getPhoto(ItemStack stack) {
        final var a = stack.get(RegisterItems.PHOTO_COMPONENT);
        if(a == null) return null;
        return ResourceLocation.parse(a);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag config) {
        super.appendHoverText(stack, context, tooltip, config);
        final var a = getPhoto(stack);
        //TODO: Render photo in the tooltip
        if(a != null) tooltip.add(Component.translatable("item.camera_port.picture_tooltip", a.toString()));
    }
}
