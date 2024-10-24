package net.lunade.camera.registry;

import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.menu.PrinterMenu;
import net.lunade.camera.screen.PrinterScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

public class CameraMenuTypes {
    public static final MenuType<PrinterMenu> PRINTER = new MenuType<>(PrinterMenu::new, FeatureFlags.DEFAULT_FLAGS);
    public static void register() {
        Registry.register(BuiltInRegistries.MENU, CameraPortConstants.id("printer"), PRINTER);
        MenuScreens.register(PRINTER, PrinterScreen::new);
    }
}
