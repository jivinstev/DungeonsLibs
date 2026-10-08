package net.firefoxsalesman.dungeonslibs;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry;
import net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.entities.LibEntityTypes;
import net.firefoxsalesman.dungeonslibs.init.ParticleInit;
import net.firefoxsalesman.dungeonslibs.items.ItemTagWrappers;
import net.firefoxsalesman.dungeonslibs.network.NetworkHandler;
import net.firefoxsalesman.dungeonslibs.items.RangedItemModelProperties;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.CrossbowGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterials;
import net.firefoxsalesman.dungeonslibs.items.materials.weapon.WeaponMaterials;
import net.firefoxsalesman.dungeonslibs.loot.ModLootModifiers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DungeonsLibs.MOD_ID)
public class DungeonsLibs {
	// Define mod id in a common place for everything to reference
	public static final String MOD_ID = "dungeonslibs";
	// Directly reference a slf4j logger
	public static final Logger LOGGER = LogUtils.getLogger();

	public DungeonsLibs(IEventBus modEventBus, ModContainer modContainer) {
		modContainer.registerConfig(Type.COMMON, DungeonsLibrariesConfig.COMMON_SPEC,
				"dungeons-lib-common.toml");
		modEventBus.addListener(this::doClientStuff);

		ItemTagWrappers.init();
		AttributeRegistry.register(modEventBus);
		LibEntityTypes.register(modEventBus);
		DungeonsArmorMaterials.setupVanillaMaterials();
		WeaponMaterials.setupVanillaMaterials();

		ArmorGearConfigRegistry.subscribe();
		MeleeGearConfigRegistry.subscribe();
		BowGearConfigRegistry.subscribe();
		CrossbowGearConfigRegistry.subscribe();
		WeaponMaterials.subscribe();
		DungeonsArmorMaterials.subscribe();
		ArtifactGearConfigRegistry.subscribe();

		LibCapabilities.setupCapabilities(modEventBus);

		ModLootModifiers.register(modEventBus);
		ParticleInit.register(modEventBus);
		NetworkHandler.init(modEventBus);
	}

	// You can use SubscribeEvent and let the Event Bus discover methods to call
	@SubscribeEvent
	public void onServerStarting(ServerStartingEvent event) {
	}

	private void doClientStuff(final FMLClientSetupEvent event) {
		event.enqueueWork(RangedItemModelProperties::init);
	}

	// You can use EventBusSubscriber to automatically register all static methods
	// in the class annotated with @SubscribeEvent
	@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
	public static class ClientModEvents {
		@SubscribeEvent
		public static void onClientSetup(FMLClientSetupEvent event) {
		}
	}
}
