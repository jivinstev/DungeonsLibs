package net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments;

import net.minecraft.world.item.ItemStack;

import static net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities.BUILT_IN_ENCHANTMENTS_CAPABILITY;

public class BuiltInEnchantmentsHelper {

	public static BuiltInEnchantments getBuiltInEnchantmentsCapability(ItemStack itemStack) {
		BuiltInEnchantments cap = itemStack.getCapability(BUILT_IN_ENCHANTMENTS_CAPABILITY);
		return cap != null ? cap : new BuiltInEnchantments(itemStack);
	}

}
