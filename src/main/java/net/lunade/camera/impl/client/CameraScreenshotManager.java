package net.lunade.camera.impl.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.Window;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Consumer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.CameraPortMain;
import net.lunade.camera.config.CameraPortConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class CameraScreenshotManager {
	private static final String PLAYER_UUID = Minecraft.getInstance().getGameProfile().getId().toString();

	public static boolean wasGuiHidden = false;
	public static boolean possessingCamera = false;
	public static boolean isCameraHandheld = false;
	@Nullable
	public static Entity previousCameraEntity = null;

	public static void executeScreenshot(@Nullable Entity entity, boolean handheld) {
		Minecraft client = Minecraft.getInstance();
		isCameraHandheld = handheld;
		previousCameraEntity = client.getCameraEntity();
		if (entity != null) client.setCameraEntity(entity);

		wasGuiHidden = client.options.hideGui;
		client.options.hideGui = true;
		possessingCamera = true;

		if (client.level != null) {
			Entity camEntity = client.getCameraEntity();
			if (camEntity != null) client.level.playLocalSound(client.player, CameraPortMain.CAMERA_SNAP, SoundSource.PLAYERS, 0.5F, 1F);
		}

		grabCameraScreenshot(client.gameDirectory, 256, 256);
		isCameraHandheld = false;

		if (client.level != null) {
			Entity camEntity = client.getCameraEntity();
			if (!isCameraHandheld) {
				if (camEntity != null) {
					int smokeCount = client.level.getRandom().nextInt(1, 5);
					for (int i = 0; i < smokeCount; i++) {
						client.level.addParticle(ParticleTypes.LARGE_SMOKE, camEntity.getX(), camEntity.getEyeY(), camEntity.getZ(), 0D, 0.15D, 0D);
					}
				}
			}
		}

		if (previousCameraEntity != null) {
			client.setCameraEntity(previousCameraEntity);
			previousCameraEntity = null;
		}

		client.options.hideGui = wasGuiHidden;
		possessingCamera = false;
	}

	public static void grabCameraScreenshot(File gameDirectory, int width, int height) {
		Minecraft minecraft = Minecraft.getInstance();
		Window window = minecraft.getWindow();
		int prevWidth = window.getWidth();
		int prevHeight = window.getHeight();
		RenderTarget renderTarget = minecraft.getMainRenderTarget();
		GameRenderer gameRenderer = minecraft.gameRenderer;
		gameRenderer.setRenderBlockOutline(false);

		try {
			gameRenderer.setPanoramicMode(true);
			window.setWidth(width);
			window.setHeight(height);
			renderTarget.resize(width, height);
			minecraft.gameRenderer.renderLevel(DeltaTracker.ONE);

			try {
				Thread.sleep(10L);
			} catch (InterruptedException ignored) {
			}

			grab(gameDirectory, renderTarget, (text) -> minecraft.execute(() -> minecraft.gui.getChat().addMessage(text)));
		} catch (Exception ignored) {
		} finally {
			gameRenderer.setRenderBlockOutline(true);
			window.setWidth(prevWidth);
			window.setHeight(prevHeight);
			renderTarget.resize(prevWidth, prevHeight);
			gameRenderer.setPanoramicMode(false);
		}
	}

	private static void grab(@NotNull File gameDirectory, RenderTarget renderTarget, Consumer<Component> messageReceiver) {
		Screenshot.takeScreenshot(renderTarget, 4, nativeImage -> {
			File photographPath = gameDirectory.toPath()
				.resolve("photographs")
				.resolve(".local")
				.toFile();
			photographPath.mkdirs();

			Optional<Path> iconPath = Optional.empty();
			Minecraft minecraft = Minecraft.getInstance();
			if (CameraPortConfig.get().useLatestPhotoAsWorldIcon && minecraft.isLocalServer()) {
				IntegratedServer integratedServer = minecraft.getSingleplayerServer();
				if (integratedServer != null) {
					iconPath = minecraft.getSingleplayerServer().getWorldScreenshotFile();
					iconPath.ifPresent(path -> path.toFile().mkdirs());
				}
			}

			File photographFile = getPhotographFile(photographPath);
			Optional<Path> finalIconPath = iconPath;

			Util.ioPool().execute(() -> {
				try {
					nativeImage.writeToFile(photographFile);
					finalIconPath.ifPresent(path -> copyPhotographToFileWithSize(nativeImage, path, 64, 64));

					Component component = Component.literal(photographFile.getName())
						.withStyle(ChatFormatting.UNDERLINE)
						.withStyle(style -> style.withClickEvent(new ClickEvent.OpenFile(photographFile.getAbsoluteFile())));

					messageReceiver.accept(Component.translatable("screenshot.success", component));
				} catch (Exception e) {
					CameraPortConstants.warn("Couldn't save screenshot " + e, true);
					messageReceiver.accept(Component.translatable("screenshot.failure", e.getMessage()));
				} finally {
					nativeImage.close();
				}
			});
		});
	}

	private static void copyPhotographToFileWithSize(@NotNull NativeImage nativeImage, Path path, int width, int height) {
		int i = nativeImage.getWidth();
		int j = nativeImage.getHeight();
		int k = 0;
		int l = 0;
		if (i > j) {
			k = (i - j) / 2;
			i = j;
		} else {
			l = (j - i) / 2;
			j = i;
		}

		try (NativeImage nativeImage2 = new NativeImage(64, 64, false)) {
			nativeImage.resizeSubRectTo(k, l, i, j, nativeImage2);
			nativeImage2.writeToFile(path);
		} catch (IOException e) {
			CameraPortConstants.LOGGER.warn("Couldn't save photograph to world icon", e);
		} finally {
			nativeImage.close();
		}
	}

	public static @NotNull File getPhotographFile(File directory) {
		String fileName = PLAYER_UUID + "_" + System.currentTimeMillis();
		int fileIndex = 1;

		while (true) {
			File file = new File(directory, fileName + (fileIndex == 1 ? "" : "_" + fileIndex) + ".png");
			if (!file.exists()) return file;
			++fileIndex;
		}
	}

}
