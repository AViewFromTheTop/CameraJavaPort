package net.lunade.camera.registry;

import net.lunade.camera.screen.PrinterScreen;
import net.minecraft.client.gui.screens.MenuScreens;

public class CameraPortScreens {

    public static void init() {
        MenuScreens.register(CameraPortMenuTypes.PRINTER, PrinterScreen::new);
    }
}
