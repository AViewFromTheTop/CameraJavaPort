package net.lunade.camera.impl.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.image_transfer.FileTransferPacket;
import net.frozenblock.lib.image_transfer.client.ServerTexture;
import net.lunade.camera.CameraConstants;
import net.minecraft.FileUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

@Environment(EnvType.CLIENT)
public class PhotographLoader {
    private static final TextureManager TEXTURE_MANAGER = Minecraft.getInstance().getTextureManager();
    private static final ArrayList<ResourceLocation> LOADED_TEXTURES = new ArrayList<>();
    private static final ArrayList<ResourceLocation> LOCAL_PHOTOGRAPHS = new ArrayList<>();

    public static boolean hasAnyLocalPhotographs() {
        return !LOCAL_PHOTOGRAPHS.isEmpty();
    }

    public static ResourceLocation getLocalPhotograph(int index) {
        return LOCAL_PHOTOGRAPHS.get(index);
    }

    public static @NotNull ResourceLocation getLocalPhotograph(String pictureName) {
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
        return LOCAL_PHOTOGRAPHS.size();
    }

    public static @Nullable ResourceLocation getInfiniteLocalPhotograph(int index) {
        if (LOCAL_PHOTOGRAPHS.isEmpty()) return null;
        int size = LOCAL_PHOTOGRAPHS.size();
        int adjustedIndex = ((index % size) + size) % size;
        return getLocalPhotograph(adjustedIndex);
    }

    private static @NotNull ResourceLocation getPhotographLocation(@NotNull String name) {
        return CameraConstants.id("photographs/" + name);
    }

    public static int loadLocalPhotographs() {
        final File file = FabricLoader.getInstance().getGameDir().resolve("photographs").resolve(".local").toFile();
        File[] fileList = file.listFiles();
        if (fileList == null) return 0;
        final var fileStream = Arrays.stream(fileList)
                .filter(File::isFile)
                .filter(file1 -> file1.getName().endsWith(".png"));
        LOCAL_PHOTOGRAPHS.clear();
        for (String name : fileStream.map(File::getName).toList()) {
            name = name.replace(".png", "");
            LOCAL_PHOTOGRAPHS.add(PhotographLoader.getLocalPhotograph(name));
        }
        return LOCAL_PHOTOGRAPHS.size();
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
