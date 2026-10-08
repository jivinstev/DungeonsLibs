package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import java.util.Map;

import net.firefoxsalesman.dungeonslibs.data.util.CodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.MeleeGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;

public class MeleeGearConfigRegistry {
	public static final ResourceLocation GEAR_CONFIG_BUILTIN_RESOURCELOCATION = ResourceLocationHelper
			.modLoc("gear_config");

	public static final CodecJsonDataManager<MeleeGearConfig> MELEE_GEAR_CONFIGS = new CodecJsonDataManager<>(
			"gearconfig/melee", MeleeGearConfig.CODEC);

	public static MeleeGearConfig getConfig(ResourceLocation resourceLocation) {
		return MELEE_GEAR_CONFIGS.getData().getOrDefault(resourceLocation, MeleeGearConfig.DEFAULT);
	}

	public static boolean gearConfigExists(ResourceLocation resourceLocation) {
		return MELEE_GEAR_CONFIGS.getData().containsKey(resourceLocation);
	}

	public static MeleeGearConfigSyncPacket toPacket(Map<ResourceLocation, MeleeGearConfig> map) {
		return new MeleeGearConfigSyncPacket(map);
	}

	public static void subscribe() {
		MELEE_GEAR_CONFIGS.subscribeAsSyncable(MeleeGearConfigRegistry::toPacket);
	}
}
