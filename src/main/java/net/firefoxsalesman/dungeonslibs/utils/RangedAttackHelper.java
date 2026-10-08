package net.firefoxsalesman.dungeonslibs.utils;

import net.firefoxsalesman.dungeonslibs.event.BowEvent;
import net.firefoxsalesman.dungeonslibs.event.CrossbowEvent;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGear;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.CrossbowGear;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.Nullable;

import net.firefoxsalesman.dungeonslibs.ModHolders;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.RANGED_DAMAGE_MULTIPLIER;

public class RangedAttackHelper {

	public static float getBowArrowVelocity(LivingEntity livingEntity, ItemStack stack, int charge) {
		float bowChargeTime = RangedAttackHelper.getBowChargeTime(livingEntity, stack);
		if (bowChargeTime <= 0) {
			bowChargeTime = 1;
		}
		float arrowVelocity = (float) charge / bowChargeTime;
		arrowVelocity = (arrowVelocity * arrowVelocity + arrowVelocity * 2.0F) / 3.0F;
		float velocityLimit = 1.0F;
		BowEvent.Overcharge overchargeEvent = new BowEvent.Overcharge(livingEntity, stack, 0);
		NeoForge.EVENT_BUS.post(overchargeEvent);
		int overchargeLevel = overchargeEvent.getCharges();
		if (overchargeLevel > 0) {
			velocityLimit += overchargeLevel;
		}
		if (arrowVelocity > velocityLimit) {
			arrowVelocity = velocityLimit;
		}

		BowEvent.Velocity velocityEvent = new BowEvent.Velocity(livingEntity, stack, arrowVelocity);
		NeoForge.EVENT_BUS.post(velocityEvent);
		return velocityEvent.getVelocity();
	}

	public static float getBowChargeTime(LivingEntity livingEntity, ItemStack stack) {
		float defaultChargeTime = stack.getItem() instanceof BowGear
				? ((BowGear) stack.getItem()).getDefaultChargeTime()
				: 20.0F;
		int quickChargeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.QUICK_CHARGE), stack);
		float minTime = 1;
		BowEvent.ChargeTime event = new BowEvent.ChargeTime(livingEntity, stack, defaultChargeTime);
		NeoForge.EVENT_BUS.post(event);
		return Math.max(event.getChargeTime() - 5 * quickChargeLevel, minTime);
	}

	public static float getVanillaCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		int quickChargeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.QUICK_CHARGE), stack);
		float minTime = 1;
		CrossbowEvent.ChargeTime event = new CrossbowEvent.ChargeTime(livingEntity, stack, 25.0F);
		NeoForge.EVENT_BUS.post(event);
		return Math.max(event.getChargeTime() - 5 * quickChargeLevel, minTime);
	}

	public static float getCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		float chargeTime;
		if (stack.getItem() instanceof CrossbowGear crossbowGear) {
			chargeTime = crossbowGear.getCrossbowChargeTime(livingEntity, stack);
		} else {
			chargeTime = getVanillaCrossbowChargeTime(livingEntity, stack);
		}
		return chargeTime;
	}

	public static float getCrossbowArrowVelocity(@Nullable LivingEntity livingEntity, ItemStack stack) {
		float baseVelocity = 3.15F;
		if (stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).contains(Items.FIREWORK_ROCKET)) {
			baseVelocity = 1.6F;
		}
		CrossbowEvent.Velocity event = new CrossbowEvent.Velocity(livingEntity, stack, baseVelocity);
		NeoForge.EVENT_BUS.post(event);
		return event.getVelocity();
	}

	public static float getAngleForProjectileByIndex(int projectileIndex) {
		int indexScale = projectileIndex / 2;
		return 10.0F * (projectileIndex % 2 != 0 ? indexScale + 1 : indexScale * -1.0F);
	}

	public static void multiplyRangedDamage(LivingEntity shooter, AbstractArrow arrow) {
		AttributeInstance rangedDamageMultiplier = shooter.getAttribute(RANGED_DAMAGE_MULTIPLIER);
		if (rangedDamageMultiplier != null) {
			arrow.setBaseDamage(arrow.getBaseDamage() * (rangedDamageMultiplier.getValue()));
		}
	}

	public static void createBowArrow(ItemStack bowStack, Level world, Player player, ItemStack projectileStack,
			float powerForTime, int arrowIndex, boolean isInfiniteArrow) {
		ArrowItem arrowitem = (ArrowItem) (projectileStack.getItem() instanceof ArrowItem
				? projectileStack.getItem()
				: Items.ARROW);
		AbstractArrow arrow = arrowitem.createArrow(world, projectileStack, player, bowStack);
		if (bowStack.getItem() instanceof BowItem bowItem)
			arrow = bowItem.customArrow(arrow, projectileStack, bowStack);
		multiplyRangedDamage(player, arrow);
		arrow.shootFromRotation(player, player.getXRot(),
				player.getYRot() + getAngleForProjectileByIndex(arrowIndex), 0.0F, powerForTime * 3.0F,
				1.0F);

		if (powerForTime >= 1.0F) {
			arrow.setCritArrow(true);
		}
		int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.POWER), bowStack);
		if (powerLevel > 0) {
			arrow.setBaseDamage(arrow.getBaseDamage() + (double) powerLevel * 0.5D + 0.5D);
		}

		int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.PUNCH), bowStack);
		if (punchLevel > 0) {
			// Punch is now applied from the weapon passed to createArrow (bowStack); no direct setter in 1.21.1.
		}

		int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.FLAME), bowStack);
		if (flameLevel > 0) {
			arrow.igniteForSeconds(100);
		}

		int piercingLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.PIERCING), bowStack);
		if (piercingLevel > 0) {
			// Piercing is now applied from the weapon passed to createArrow (bowStack); setPierceLevel is private in 1.21.1.
		}

		bowStack.hurtAndBreak(1, player,
				player.getUsedItemHand() == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
		if (isInfiniteArrow
				|| player.getAbilities().instabuild && isSpecialArrow(projectileStack)
				|| arrowIndex > 0) {
			arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
		}
		world.addFreshEntity(arrow);
	}

	private static boolean isSpecialArrow(ItemStack projectileStack) {
		return projectileStack.is(Items.SPECTRAL_ARROW) || projectileStack.is(Items.TIPPED_ARROW);
	}
}
