package net.lunade.camera.config.gui;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.config.CameraPortConfig;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class CameraPortMainConfigGui {

	public static Screen buildScreen(@NotNull Screen parent) {
		var configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(CameraPortConstants.text("component.title"));
		configBuilder.setSavingRunnable(CameraPortConfig.INSTANCE::save);

		ConfigEntryBuilder entryBuilder = configBuilder.entryBuilder();

		var block = configBuilder.getOrCreateCategory(CameraPortConstants.text("config"));
		CameraPortConfigGui.setupEntries(block, entryBuilder);
		return configBuilder.build();
	}
}
