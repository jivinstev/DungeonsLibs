package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.enchanting.GetEnchantmentLevelEvent;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class BuiltInEnchantmentEvents {

	@SubscribeEvent
	public static void onGetEnchantmentLevel(GetEnchantmentLevelEvent event) {
		ItemStack stack = event.getStack();
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(stack);
		cap.getAllBuiltInEnchantmentInstances().forEach(instance -> {
			Holder<Enchantment> holder = instance.enchantment;
			if (holder.unwrapKey().filter(event::isTargetting).isPresent()) {
				event.getEnchantments().set(holder, event.getEnchantments().getLevel(holder) + instance.level);
			}
		});
	}
}
