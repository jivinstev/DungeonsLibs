package net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile;

import net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities;
import net.minecraft.world.entity.Entity;

public class EnchantedProjectileHelper {

	public static EnchantedProjectile getEnchantedProjectileCapability(Entity entity) {
		return entity.getData(LibCapabilities.ENCHANTED_PROJECTILE_ATTACHMENT);
	}
}
