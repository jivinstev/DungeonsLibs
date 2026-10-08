package net.firefoxsalesman.dungeonslibs.combat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingEquipmentChangeEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class TwoHandedHandler {

	@SubscribeEvent
	public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
		if (!DungeonsLibrariesConfig.ENABLE_TWO_HANDED_WEAPON.get())
			return;
		MeleeGearConfig configTo = MeleeGearConfigRegistry
				.getConfig(BuiltInRegistries.ITEM.getKey(event.getTo().getItem()));
		if (configTo.isTwoHanded()) {
			if (event.getSlot().equals(EquipmentSlot.MAINHAND)) {
				ItemStack offhandItem = event.getEntity().getOffhandItem();
				if (!offhandItem.isEmpty()) {
					event.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
					event.getEntity().spawnAtLocation(offhandItem);
				}
			} else if (event.getSlot().equals(EquipmentSlot.OFFHAND)) {
				ItemStack mainhandItem = event.getEntity().getMainHandItem();
				event.getEntity().setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
				event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, event.getTo());
				if (!mainhandItem.isEmpty()) {
					event.getEntity().spawnAtLocation(mainhandItem);
				}
			}
		} else if (!event.getTo().isEmpty()) {
			ItemStack mainhandItem = event.getEntity().getMainHandItem();
			MeleeGearConfig configMainHand = MeleeGearConfigRegistry
					.getConfig(BuiltInRegistries.ITEM.getKey(mainhandItem.getItem()));
			if (configMainHand.isTwoHanded() && event.getSlot().equals(EquipmentSlot.OFFHAND)) {
				event.getEntity().setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
				event.getEntity().spawnAtLocation(mainhandItem);
			}
		}

	}

}
