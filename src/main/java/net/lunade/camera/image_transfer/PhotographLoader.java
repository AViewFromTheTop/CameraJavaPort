package net.lunade.camera.image_transfer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.lunade.camera.CameraConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class PhotographLoader {

    private static final ArrayList<ResourceLocation> LOADED_TEXTURES = new ArrayList<>();
    private static final ArrayList<ResourceLocation> LOADING_PICTURES = new ArrayList<>();

    public static ResourceLocation get(int index) {
        return LOADING_PICTURES.get(index);
    }

    public static ResourceLocation get(String pictureName) {
        final var location = getPhotographLocation(pictureName);
        if(!LOADED_TEXTURES.contains(location)) {
            final var serverTexture = new ServerTexture(
                    "photographs",
                    pictureName + ".png",
                    CameraConstants.id("photographs/empty"),
                    () -> {}
            );
            Minecraft.getInstance().getTextureManager().register(location, serverTexture);
            LOADED_TEXTURES.add(location);
        }
        return location;
    }

    public static int getSize() {
        return LOADING_PICTURES.size();
    }

    public static ResourceLocation getInfinite(int index) {
        if(LOADING_PICTURES.isEmpty()) return null;
        int size = LOADING_PICTURES.size();
        int adjustedIndex = ((index % size) + size) % size;
        return get(adjustedIndex);
    }

    private static ResourceLocation getPhotographLocation(@NotNull String name) {
        return CameraConstants.id("photographs/" + name);
    }

    public static int load() {
        final File file = FabricLoader.getInstance().getGameDir().resolve("photographs").toFile();
        final var list = file.listFiles();
        LOADING_PICTURES.clear();
        if(list != null) {
            for (String name : Stream.of(list).map(File::getName).toList()) {
                name = name.replace(".png", "");
                LOADING_PICTURES.add(PhotographLoader.get(name));
            }
        } else {
            //TODO: Something if the photograph folder does not exist
        }
        return LOADING_PICTURES.size();
    }

    /**
     * Invoked when a picture item is created through the GUI. You might want to check if you're on Client
     * and then send the texture packet
     * @param imageId The image is a string, I know, but that's how it's stored in the menu
     * */
    public static void onReceiveItem(String imageId, Player player) {

    }
}
