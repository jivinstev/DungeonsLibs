package net.firefoxsalesman.dungeonslibs.utils;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile.EnchantedProjectile;
import net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile.EnchantedProjectileHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.core.Holder;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class ArrowHelper {

	@SubscribeEvent
	public static void onArrowJoinWorld(EntityJoinLevelEvent event) {
		if (event.getEntity() instanceof AbstractArrow) {
			AbstractArrow arrowEntity = (AbstractArrow) event.getEntity();
			// if(arrowEntity.getTags().contains("BonusProjectile") ||
			// arrowEntity.getTags().contains("ChainReactionProjectile")) return;
			Entity shooterEntity = arrowEntity.getOwner();
			if (shooterEntity instanceof LivingEntity) {
				LivingEntity shooter = (LivingEntity) shooterEntity;
				ItemStack mainhandStack = shooter.getMainHandItem();
				ItemStack offhandStack = shooter.getOffhandItem();
				// This should guarantee the arrow came from the correct itemstack
				if (mainhandStack.getItem() instanceof BowItem
						|| mainhandStack.getItem() instanceof CrossbowItem) {
					handleRangedEnchantments(arrowEntity, shooter, mainhandStack);
				} else if (offhandStack.getItem() instanceof BowItem
						|| offhandStack.getItem() instanceof CrossbowItem) {
					handleRangedEnchantments(arrowEntity, shooter, offhandStack);
				}
			}
		}
	}

	private static void handleRangedEnchantments(AbstractArrow arrowEntity, LivingEntity shooter, ItemStack stack) {
		addEnchantmentTagsToArrow(stack, arrowEntity);
	}

	public static boolean hasEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return enchantment != null && EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack) > 0;
	}

	public static boolean hasEnchantment(LivingEntity entity, Holder<Enchantment> enchantment) {
		return enchantment != null && EnchantmentHelper.getEnchantmentLevel(enchantment, entity) > 0;
	}

	public static boolean shooterIsLiving(AbstractArrow arrowEntity) {
		return arrowEntity.getOwner() != null && arrowEntity.getOwner() instanceof LivingEntity;
	}

	public static boolean arrowHitLivingEntity(HitResult hitResult) {
		if (hitResult instanceof EntityHitResult) {
			EntityHitResult entityHitResult = (EntityHitResult) hitResult;
			return entityHitResult.getEntity() instanceof LivingEntity;
		} else {
			return false;
		}
	}

	public static boolean arrowHitMob(HitResult hitResult) {
		if (hitResult instanceof EntityHitResult entityHitResult) {
			return entityHitResult.getEntity() instanceof Mob;
		} else {
			return false;
		}
	}

	public static void addEnchantmentTagsToArrow(ItemStack itemStack, Projectile projectileEntity) {
		EnchantedProjectile cap = EnchantedProjectileHelper.getEnchantedProjectileCapability(projectileEntity);
		cap.setEnchantments(itemStack);
	}

	public static int enchantmentTagToLevel(Projectile projectileEntity, Holder<Enchantment> enchantment) {
		EnchantedProjectile cap = EnchantedProjectileHelper.getEnchantedProjectileCapability(projectileEntity);
		return cap.getEnchantmentLevel(enchantment);
	}

	public static boolean wasHitByArrow(DamageSource source) {
		return source.getDirectEntity() != source.getEntity() && source.getDirectEntity() instanceof AbstractArrow;
	}
}
