package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class BuiltInEnchantmentLevelEvents {

	@SubscribeEvent
	public static void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(event.getStack());
		cap.getAllBuiltInEnchantmentInstances().forEach(instance -> instance.enchantment.unwrapKey()
				.ifPresent(key -> {
					if (event.isTargetting(key)) {
						event.getEnchantments().set(instance.enchantment,
								event.getEnchantments().getLevel(instance.enchantment)
										+ instance.level);
					}
				}));
	}
}
