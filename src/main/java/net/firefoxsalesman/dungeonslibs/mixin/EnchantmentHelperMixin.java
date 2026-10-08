package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantmentsHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

	@Inject(method = "getTagEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I", remap = false, at = @At("RETURN"), cancellable = true)
	private static void dungeonslibraries_getItemEnchantmentLevel(Holder<Enchantment> enchantment,
			ItemStack itemStack, CallbackInfoReturnable<Integer> cir) {
		Integer builtIn = getBuiltInEnchantmentLevel(itemStack, enchantment);
		if (builtIn > 0) {
			int level = cir.getReturnValueI();
			// Found on the stack: its level already includes the built-in one, otherwise add it
			cir.setReturnValue(level > 0 ? level - builtIn : builtIn);
		}
	}

	@NotNull
	private static Integer getBuiltInEnchantmentLevel(ItemStack itemStack, Holder<Enchantment> enchantment) {
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(itemStack);
		Integer reduce = cap.getAllBuiltInEnchantmentInstances().stream()
				.filter(enchantmentInstance -> enchantmentInstance.enchantment.value() == enchantment.value())
				.map(enchantmentInstance -> enchantmentInstance.level)
				.reduce(0, Integer::sum);
		return reduce;
	}

	@Redirect(method = "runIterationOnItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/enchantment/EnchantmentHelper$EnchantmentVisitor;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getAllEnchantments(Lnet/minecraft/core/HolderLookup$RegistryLookup;)Lnet/minecraft/world/item/enchantment/ItemEnchantments;", remap = false))
	private static ItemEnchantments dungeonslibraries_getAllEnchantments(ItemStack itemStack,
			HolderLookup.RegistryLookup<Enchantment> lookup) {
		ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(itemStack.getAllEnchantments(lookup));
		BuiltInEnchantments cap = BuiltInEnchantmentsHelper.getBuiltInEnchantmentsCapability(itemStack);
		cap.getAllBuiltInEnchantmentInstances().forEach(enchantmentInstance -> enchantments
				.set(enchantmentInstance.enchantment,
						enchantments.getLevel(enchantmentInstance.enchantment) + enchantmentInstance.level));
		return enchantments.toImmutable();
	}
}
