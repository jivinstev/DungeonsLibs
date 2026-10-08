package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGear;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Mob.class)
public abstract class MobMixin {

	@Shadow
	public abstract boolean canReplaceEqualItem(ItemStack pCandidate, ItemStack pExisting);

	private static double dungeonslibraries_swordDamage(ItemStack stack) {
		double total = 0;
		for (ItemAttributeModifiers.Entry entry : stack
				.getOrDefault(DataComponents.ATTRIBUTE_MODIFIERS, ItemAttributeModifiers.EMPTY).modifiers()) {
			if (entry.attribute().equals(Attributes.ATTACK_DAMAGE)) {
				total += entry.modifier().amount();
			}
		}
		return total;
	}

	@Inject(method = "Lnet/minecraft/world/entity/Mob;canReplaceCurrentItem(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Z", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
	private void dungeonslibraries_canReplaceCurrentItem(ItemStack pCandidate, ItemStack pExisting,
			CallbackInfoReturnable<Boolean> cir) {
		if (pCandidate.getItem() instanceof SwordItem) {
			if (!(pExisting.getItem() instanceof SwordItem)
					&& !(pExisting.getItem() instanceof MeleeGear)) {
				cir.setReturnValue(true);
			} else if (pExisting.getItem() instanceof MeleeGear) {
				double swordDamage = dungeonslibraries_swordDamage(pCandidate);
				double gearDamage = dungeonslibraries_swordDamage(pExisting);
				if (swordDamage != gearDamage) {
					cir.setReturnValue(swordDamage > gearDamage);
				} else {
					cir.setReturnValue(canReplaceEqualItem(pCandidate, pExisting));
				}
			}
		} else if (pCandidate.getItem() instanceof MeleeGear) {
			if (!(pExisting.getItem() instanceof SwordItem)
					&& !(pExisting.getItem() instanceof MeleeGear)) {
				cir.setReturnValue(true);
			} else if (pExisting.getItem() instanceof SwordItem) {
				double swordDamage = dungeonslibraries_swordDamage(pExisting);
				double gearDamage = dungeonslibraries_swordDamage(pCandidate);
				if (swordDamage != gearDamage) {
					cir.setReturnValue(swordDamage > gearDamage);
				} else {
					cir.setReturnValue(canReplaceEqualItem(pCandidate, pExisting));
				}
			}
		}
	}

}
