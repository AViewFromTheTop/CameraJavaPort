package net.lunade.camera.impl.client;

import com.mojang.datafixers.util.Pair;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.frozenblock.lib.texture.client.api.ServerTextureDownloader;
import net.lunade.camera.CameraPortConstants;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class PhotographLoader {
	private static final ResourceLocation FALLBACK = CameraPortConstants.id("textures/photographs/empty.png");
	private static final ArrayList<Pair<ResourceLocation, Date>> LOCAL_PHOTOGRAPHS = new ArrayList<>();

	public static boolean hasAnyLocalPhotographs() {
		return !LOCAL_PHOTOGRAPHS.isEmpty();
	}

	public static ResourceLocation getPhotograph(int index) {
		return LOCAL_PHOTOGRAPHS.get(index).getFirst();
	}

	public static @NotNull ResourceLocation getAndLoadPhotograph(String photographName, boolean local) {
		return getAndLoadPhotograph(getPhotographLocation(photographName), local);
	}

	public static @NotNull ResourceLocation getAndLoadPhotograph(@NotNull ResourceLocation photographLocation, boolean local) {
		String filename = photographLocation.getPath().replace("photographs/", "");
		if (!filename.endsWith(".png")) filename += ".png";

		ResourceLocation downloaderLocation = ServerTextureDownloader.getOrLoadServerTexture(
			photographLocation,
			"photographs",
			filename,
			FALLBACK
		);
		if (local) return photographLocation;
		return downloaderLocation;
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
		final File file = FabricLoader.getInstance().getGameDir().resolve("photographs").resolve(ServerTextureDownloader.LOCAL_TEXTURE_SOURCE).toFile();
		File[] fileList = file.listFiles();
		if (fileList == null) return 0;
		Stream<File> fileStream = Arrays.stream(fileList)
			.filter(File::isFile)
			.filter(file1 -> file1.getName().endsWith(".png"));
		LOCAL_PHOTOGRAPHS.clear();

		ArrayList<Pair<ResourceLocation, Date>> localPhotographs = new ArrayList<>();
		for (String name : fileStream.map(File::getName).toList()) {
			String strippedFileName = name.replace(".png", "");
			parseDate(strippedFileName).ifPresent(date -> {
				localPhotographs.add(Pair.of(PhotographLoader.getAndLoadPhotograph(strippedFileName, true), date));
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
		} catch (Exception ignored) {
		}
		return Optional.empty();
	}
}
