package net.lunade.camera.mixin;

import net.lunade.camera.component.tooltip.PictureTooltipComponent;
import net.lunade.camera.component.tooltip.client.ClientPictureTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientTooltipComponent.class)
public interface ClientTooltipComponentMixin {

    @Inject(
            at = @At("HEAD"),
            method = "create(Lnet/minecraft/world/inventory/tooltip/TooltipComponent;)Lnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipComponent;",
            cancellable = true
    )
    private static void cameraPort$create(TooltipComponent data, CallbackInfoReturnable<ClientTooltipComponent> info) {
        if (data instanceof PictureTooltipComponent tooltip) info.setReturnValue(new ClientPictureTooltipComponent(tooltip));
    }
}
