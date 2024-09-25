package net.lunade.camera.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.image_transfer.FileTransferPacket;
import net.frozenblock.lib.image_transfer.client.ServerTexture;
import net.lunade.camera.CameraConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.stream.Stream;

@Environment(EnvType.CLIENT)
public class PhotographLoader {
    private static final TextureManager TEXTURE_MANAGER = Minecraft.getInstance().getTextureManager();
    private static final ArrayList<ResourceLocation> LOADED_TEXTURES = new ArrayList<>();
    private static final ArrayList<ResourceLocation> LOADING_TEXTURES = new ArrayList<>();

    public static ResourceLocation get(int index) {
        return LOADING_TEXTURES.get(index);
    }

    public static @NotNull ResourceLocation get(String pictureName) {
        final var location = getPhotographLocation(pictureName);
        if (!LOADED_TEXTURES.contains(location)) {
            final var serverTexture = new ServerTexture(
                    "photographs",
                    pictureName + ".png",
                    CameraConstants.id("photographs/empty"),
                    () -> {}
            );
            TEXTURE_MANAGER.register(location, serverTexture);
            LOADED_TEXTURES.add(location);
        }
        return location;
    }

    public static int getSize() {
        return LOADING_TEXTURES.size();
    }

    public static @Nullable ResourceLocation getInfinite(int index) {
        if (LOADING_TEXTURES.isEmpty()) return null;
        int size = LOADING_TEXTURES.size();
        int adjustedIndex = ((index % size) + size) % size;
        return get(adjustedIndex);
    }

    private static @NotNull ResourceLocation getPhotographLocation(@NotNull String name) {
        return CameraConstants.id("photographs/" + name);
    }

    public static int load() {
        final File file = FabricLoader.getInstance().getGameDir().resolve("photographs").toFile();
        final var list = file.listFiles();
        LOADING_TEXTURES.clear();
        if (list != null) {
            for (String name : Stream.of(list).map(File::getName).toList()) {
                name = name.replace(".png", "");
                LOADING_TEXTURES.add(PhotographLoader.get(name));
            }
        } else {
            //TODO: Something if the photograph folder does not exist
        }
        return LOADING_TEXTURES.size();
    }

    /**
     * Invoked when a picture item is created through the GUI. You might want to check if you're on Client
     * and then send the texture packet
     * @param imageId The image is a string, I know, but that's how it's stored in the menu
     * */
    public static void onReceiveItem(String imageId, Player player) {
        FileTransferPacket.createRequest("photgraphs/", imageId);
    }
}
