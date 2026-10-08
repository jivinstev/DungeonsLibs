package net.firefoxsalesman.dungeonslibs.items;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfigRegistry.ARTIFACT_GEAR_CONFIGS;
import static net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGearConfigRegistry.ARMOR_GEAR_CONFIGS;
import static net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterials.ARMOR_MATERIALS;
import static net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfigRegistry.BOW_GEAR_CONFIGS;
import static net.firefoxsalesman.dungeonslibs.items.gearconfig.CrossbowGearConfigRegistry.CROSSBOW_GEAR_CONFIGS;
import static net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry.MELEE_GEAR_CONFIGS;
import static net.firefoxsalesman.dungeonslibs.items.materials.weapon.WeaponMaterials.WEAPON_MATERIALS;
import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GearConfigReloadListener implements ResourceManagerReloadListener {

	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(WEAPON_MATERIALS);
		event.addListener(ARMOR_MATERIALS);
		event.addListener(MELEE_GEAR_CONFIGS);
		event.addListener(ARMOR_GEAR_CONFIGS);
		event.addListener(BOW_GEAR_CONFIGS);
		event.addListener(CROSSBOW_GEAR_CONFIGS);
		event.addListener(ARTIFACT_GEAR_CONFIGS);
		event.addListener(new GearConfigReloadListener());
	}

	@Override
	public void onResourceManagerReload(ResourceManager resourceManager) {
		reloadAllItems();
	}

	public static void reloadAllItems() {
		ITEM.entrySet().stream().filter(
				registryKeyItemEntry -> registryKeyItemEntry.getValue() instanceof IReloadableGear)
				.map(registryKeyItemEntry -> (IReloadableGear) registryKeyItemEntry.getValue())
				.forEach(IReloadableGear::reload);
	}
}
