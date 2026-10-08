package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = "dungeonslibs")
public class BuiltInEnchantmentsEvents {

	@SubscribeEvent
	public static void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
		ItemStack stack = event.getStack();
		if (stack.isEmpty()) {
			return;
		}
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(stack);
		for (EnchantmentInstance instance : cap.getAllBuiltInEnchantmentInstances()) {
			instance.enchantment.unwrapKey().ifPresent(key -> {
				if (event.isTargetting(key)) {
					event.getHolder(key).ifPresent(holder -> event.getEnchantments().set(holder,
							event.getEnchantments().getLevel(holder) + instance.level));
				}
			});
		}
	}
}
