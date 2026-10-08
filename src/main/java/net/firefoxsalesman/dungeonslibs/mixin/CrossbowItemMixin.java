package net.firefoxsalesman.dungeonslibs.mixin;

import net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public abstract class CrossbowItemMixin {
	@Redirect(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/CrossbowItem;getShootingPower(Lnet/minecraft/world/item/component/ChargedProjectiles;)F"))
	private float libraries_use_getShootingPower(ChargedProjectiles projectiles, Level level, Player player,
			InteractionHand hand) {
		return RangedAttackHelper.getCrossbowArrowVelocity(player, player.getItemInHand(hand));
	}

	@Inject(at = @At("RETURN"), method = "getChargeDuration", cancellable = true)
	private static void getChargeDuration(ItemStack stack, LivingEntity shooter, CallbackInfoReturnable<Integer> cir) {
		cir.setReturnValue((int) RangedAttackHelper.getVanillaCrossbowChargeTime(shooter, stack));
	}

	@Inject(method = "createProjectile", at = @At(value = "RETURN"))
	private void handleGetArrow(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo,
			boolean isCrit, CallbackInfoReturnable<Projectile> cir) {
		if (!(cir.getReturnValue() instanceof AbstractArrow returnValue)) {
			return;
		}
		RangedAttackHelper.multiplyRangedDamage(shooter, returnValue);

		// Power, Punch and Flame: in 1.21.1 these are enchantment effects (damage, knockback,
		// projectile_spawned) keyed on the arrow's firing weapon, which ArrowItem#createArrow sets to the
		// crossbow -- so vanilla now applies them to crossbow arrows exactly as the author's code did here.
	}
}
