package net.lunade.camera;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.ScreenshotRecorder;
import net.minecraft.entity.Entity;

@Environment(EnvType.CLIENT)
public class ClientCameraManager {

    public static void changeToCamera(Entity entity, int fov) {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setCameraEntity(entity);
        client.inGameHud.vignetteDarkness = 0.0f;
        client.options.hudHidden = true;
        client.options.getFov().setValue(fov);
    }

    public static void changeFromCamera(float prevVig, boolean prevHidden, int fov) {
        MinecraftClient client = MinecraftClient.getInstance();
        ScreenshotRecorder.saveScreenshot(client.runDirectory, client.getFramebuffer(), (text) -> client.execute(() -> client.inGameHud.getChatHud().addMessage(text)));
        client.setCameraEntity(client.player);
        client.inGameHud.vignetteDarkness = prevVig;
        client.options.hudHidden = prevHidden;
        client.options.getFov().setValue(fov);
    }

}
