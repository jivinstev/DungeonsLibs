package net.firefoxsalesman.dungeonslibs.summon;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.data.util.CodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class SummonConfigRegistry {
	public static final ResourceLocation SUMMON_RESOURCELOCATION = ResourceLocationHelper.modLoc("summon");

	public static final CodecJsonDataManager<SummonConfig> SUMMON_CONFIGS = new CodecJsonDataManager<>("summon",
			SummonConfig.CODEC);

	public static SummonConfig getConfig(ResourceLocation resourceLocation) {
		return SUMMON_CONFIGS.getData().getOrDefault(resourceLocation, SummonConfig.DEFAULT);
	}

	public static boolean gearConfigExists(ResourceLocation resourceLocation) {
		return SUMMON_CONFIGS.getData().containsKey(resourceLocation);
	}

	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(SUMMON_CONFIGS);
	}
}
