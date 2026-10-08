package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.data.util.CodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.CrossbowGearConfigSyncPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class CrossbowGearConfigRegistry {

	public static final CodecJsonDataManager<BowGearConfig> CROSSBOW_GEAR_CONFIGS = new CodecJsonDataManager<>(
			"gearconfig/crossbow", BowGearConfig.CODEC);

	public static BowGearConfig getConfig(ResourceLocation resourceLocation) {
		return CROSSBOW_GEAR_CONFIGS.getData().getOrDefault(resourceLocation, BowGearConfig.DEFAULT);
	}

	public static boolean gearConfigExists(ResourceLocation resourceLocation) {
		return CROSSBOW_GEAR_CONFIGS.getData().containsKey(resourceLocation);
	}

	public static CrossbowGearConfigSyncPacket toPacket(Map<ResourceLocation, BowGearConfig> map) {
		return new CrossbowGearConfigSyncPacket(map);
	}

	public static void subscribe() {
		CROSSBOW_GEAR_CONFIGS.subscribeAsSyncable(CrossbowGearConfigRegistry::toPacket);
	}
}
