package net.lunade.camera.config;

import java.nio.file.Path;
import net.frozenblock.lib.config.api.instance.Config;
import net.frozenblock.lib.config.api.instance.json.JsonConfig;
import net.frozenblock.lib.config.api.instance.json.JsonType;
import net.frozenblock.lib.config.api.registry.ConfigRegistry;
import net.frozenblock.lib.config.api.sync.SyncBehavior;
import net.frozenblock.lib.config.api.sync.annotation.EntrySyncData;
import net.lunade.camera.CameraPortConstants;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CameraPortConfig {
	public static final Config<CameraPortConfig> INSTANCE = ConfigRegistry.register(
		new JsonConfig<>(
			CameraPortConstants.MOD_ID,
			CameraPortConfig.class,
			configPath(true),
			JsonType.JSON5,
			null,
			null
		) {
			@Override
			public void onSave() throws Exception {
				super.onSave();
				this.onSync(null);
			}

			@Override
			public void onSync(CameraPortConfig syncInstance) {
				var config = this.config();
			}
		}
	);

	@EntrySyncData(value = "useLatestPhotoAsWorldIcon", behavior = SyncBehavior.UNSYNCABLE)
	public boolean useLatestPhotoAsWorldIcon = true;

	public static CameraPortConfig get() {
		return get(false);
	}

	public static CameraPortConfig get(boolean real) {
		if (real)
			return INSTANCE.instance();
		return INSTANCE.config();
	}

	public static CameraPortConfig getWithSync() {
		return INSTANCE.configWithSync();
	}

	@Contract(pure = true)
	private static @NotNull Path configPath(boolean json5) {
		return Path.of("./config/" + CameraPortConstants.MOD_ID + "." + (json5 ? "json5" : "json"));
	}
}
