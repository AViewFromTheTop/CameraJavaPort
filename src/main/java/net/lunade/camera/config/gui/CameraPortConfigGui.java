/*
 * Copyright 2023-2024 FrozenBlock
 * This file is part of Wilder Wild.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <https://www.gnu.org/licenses/>.
 */

package net.lunade.camera.config.gui;

import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.clothconfig.FrozenClothConfig;
import net.lunade.camera.CameraPortConstants;
import net.lunade.camera.config.CameraPortConfig;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class CameraPortConfigGui {
	private CameraPortConfigGui() {
		throw new UnsupportedOperationException("CameraPortConfigGui contains only static declarations.");
	}

	public static void setupEntries(@NotNull ConfigCategory category, @NotNull ConfigEntryBuilder entryBuilder) {
		var config = CameraPortConfig.get(true);
		Class<? extends CameraPortConfig> clazz = config.getClass();
		Config<? extends CameraPortConfig> configInstance = CameraPortConfig.INSTANCE;
		var modifiedConfig = CameraPortConfig.getWithSync();
		var defaultConfig = CameraPortConfig.INSTANCE.defaultInstance();

		var useLatestPhotoAsWorldIcon = category.addEntry(
			FrozenClothConfig.syncedEntry(
				entryBuilder.startBooleanToggle(CameraPortConstants.text("use_as_world_icon"), modifiedConfig.useLatestPhotoAsWorldIcon)
					.setDefaultValue(defaultConfig.useLatestPhotoAsWorldIcon)
					.setSaveConsumer(newValue -> config.useLatestPhotoAsWorldIcon = newValue)
					.setTooltip(CameraPortConstants.tooltip("use_as_world_icon"))
					.build(),
				clazz,
				"useLatestPhotoAsWorldIcon",
				configInstance
			)
		);
	}
}
