package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class AttacherBuiltInEnchantments {

	static boolean appliesTo(ItemStack stack) {
		return stack.isEnchantable() && stack.getMaxStackSize() == 1;
	}

	public static void register(final RegisterCapabilitiesEvent event) {
		ICapabilityProvider<ItemStack, Void, BuiltInEnchantments> provider = (stack, ctx) -> appliesTo(stack)
				? new BuiltInEnchantments(stack)
				: null;
		for (Item item : BuiltInRegistries.ITEM) {
			event.registerItem(LibCapabilities.BUILT_IN_ENCHANTMENTS_CAPABILITY, provider, item);
		}
	}
}
