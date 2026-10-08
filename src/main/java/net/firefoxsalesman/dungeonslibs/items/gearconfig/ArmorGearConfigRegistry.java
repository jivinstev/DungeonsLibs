package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import java.util.Map;

import net.firefoxsalesman.dungeonslibs.data.util.CodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.ArmorGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.minecraft.resources.ResourceLocation;

public class ArmorGearConfigRegistry {
	public static final ResourceLocation GEAR_CONFIG_BUILTIN_RESOURCELOCATION = ResourceLocationHelper
			.modLoc("gear_config");

	public static final CodecJsonDataManager<ArmorGearConfig> ARMOR_GEAR_CONFIGS = new CodecJsonDataManager<>(
			"gearconfig/armor", ArmorGearConfig.CODEC);

	public static ArmorGearConfig getConfig(ResourceLocation resourceLocation) {
		return ARMOR_GEAR_CONFIGS.getData().getOrDefault(resourceLocation, ArmorGearConfig.DEFAULT);
	}

	public static boolean gearConfigExists(ResourceLocation resourceLocation) {
		return ARMOR_GEAR_CONFIGS.getData().containsKey(resourceLocation);
	}

	public static ArmorGearConfigSyncPacket toPacket(Map<ResourceLocation, ArmorGearConfig> map) {
		return new ArmorGearConfigSyncPacket(map);
	}

	public static void subscribe() {
		ARMOR_GEAR_CONFIGS.subscribeAsSyncable(ArmorGearConfigRegistry::toPacket);
	}
}
