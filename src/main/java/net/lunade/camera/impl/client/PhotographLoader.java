package net.lunade.camera.impl.client;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.image_transfer.client.ServerTexture;
import net.lunade.camera.CameraPortConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.*;

@Environment(EnvType.CLIENT)
public class PhotographLoader {
    private static final TextureManager TEXTURE_MANAGER = Minecraft.getInstance().getTextureManager();
    private static final ArrayList<ResourceLocation> LOADED_TEXTURES = new ArrayList<>();
    private static final ArrayList<Pair<ResourceLocation, Date>> LOCAL_PHOTOGRAPHS = new ArrayList<>();

    public static boolean hasAnyLocalPhotographs() {
        return !LOCAL_PHOTOGRAPHS.isEmpty();
    }

    public static ResourceLocation getPhotograph(int index) {
        return LOCAL_PHOTOGRAPHS.get(index).getFirst();
    }

    public static @NotNull ResourceLocation getAndLoadPhotograph(String photographName) {
        return getAndLoadPhotograph(getPhotographLocation(photographName));
    }

    public static @NotNull ResourceLocation getAndLoadPhotograph(ResourceLocation photographLocation) {
        if (!LOADED_TEXTURES.contains(photographLocation)) {
            final var serverTexture = new ServerTexture(
                    "photographs",
                    photographLocation.getPath().replace("photographs/", "") + ".png",
                    CameraPortConstants.id("textures/photographs/empty.png"),
                    () -> {}
            );
            TEXTURE_MANAGER.register(photographLocation, serverTexture);
            LOADED_TEXTURES.add(photographLocation);
        }
        return photographLocation;
    }

    public static int getSize() {
        return LOCAL_PHOTOGRAPHS.size();
    }

    public static @Nullable ResourceLocation getInfiniteLocalPhotograph(int index) {
        if (LOCAL_PHOTOGRAPHS.isEmpty()) return null;
        int size = LOCAL_PHOTOGRAPHS.size();
        int adjustedIndex = ((index % size) + size) % size;
        return getPhotograph(adjustedIndex);
    }

    private static @NotNull ResourceLocation getPhotographLocation(@NotNull String name) {
        return CameraPortConstants.id("photographs/" + name);
    }

    public static int loadLocalPhotographs() {
        final File file = FabricLoader.getInstance().getGameDir().resolve("photographs").resolve(ServerTexture.LOCAL_TEXTURE_SOURCE).toFile();
        File[] fileList = file.listFiles();
        if (fileList == null) return 0;
        final var fileStream = Arrays.stream(fileList)
                .filter(File::isFile)
                .filter(file1 -> file1.getName().endsWith(".png"));
        LOCAL_PHOTOGRAPHS.clear();

        ArrayList<Pair<ResourceLocation, Date>> localPhotographs = new ArrayList<>();
        for (String name : fileStream.map(File::getName).toList()) {
            String strippedFileName = name.replace(".png", "").replace(ServerTexture.LOCAL_TEXTURE_SOURCE, "");
            parseDate(strippedFileName).ifPresent(date -> {
                localPhotographs.add(Pair.of(PhotographLoader.getAndLoadPhotograph(strippedFileName), date));
            });
        }

        localPhotographs.stream().sorted(Comparator.comparing(Pair::getSecond)).forEach(LOCAL_PHOTOGRAPHS::add);
        return LOCAL_PHOTOGRAPHS.size();
    }

    public static Optional<Date> parseDate(@NotNull String fileName) {
        int lastIndex = fileName.lastIndexOf(".png");
        lastIndex = lastIndex == -1 ? fileName.length() : lastIndex;
        String strippedFileName = fileName.substring(Math.max(fileName.lastIndexOf("/"), 0), lastIndex);
        try {
            int firstUnderScoreIndex = strippedFileName.indexOf("_");
            int lastUnderscoreIndex = strippedFileName.lastIndexOf("_");
            lastUnderscoreIndex = lastUnderscoreIndex == -1 || lastUnderscoreIndex == firstUnderScoreIndex
                    ? strippedFileName.length() : lastUnderscoreIndex;

            String unixString = strippedFileName.substring(firstUnderScoreIndex + 1, lastUnderscoreIndex);
            long unixTime = Long.parseLong(unixString);
            return Optional.of(new Date(unixTime));
        } catch (Exception ignored) {}
        return Optional.empty();
    }
}
