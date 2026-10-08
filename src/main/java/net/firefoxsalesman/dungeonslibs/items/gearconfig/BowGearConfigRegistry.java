package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.firefoxsalesman.dungeonslibs.data.util.CodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.BowGearConfigSyncPacket;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class BowGearConfigRegistry {

	public static final CodecJsonDataManager<BowGearConfig> BOW_GEAR_CONFIGS = new CodecJsonDataManager<>(
			"gearconfig/bow", BowGearConfig.CODEC);

	public static BowGearConfig getConfig(ResourceLocation resourceLocation) {
		return BOW_GEAR_CONFIGS.getData().getOrDefault(resourceLocation, BowGearConfig.DEFAULT);
	}

	public static boolean gearConfigExists(ResourceLocation resourceLocation) {
		return BOW_GEAR_CONFIGS.getData().containsKey(resourceLocation);
	}

	public static BowGearConfigSyncPacket toPacket(Map<ResourceLocation, BowGearConfig> map) {
		return new BowGearConfigSyncPacket(map);
	}

	public static void subscribe() {
		BOW_GEAR_CONFIGS.subscribeAsSyncable(BowGearConfigRegistry::toPacket);
	}
}
