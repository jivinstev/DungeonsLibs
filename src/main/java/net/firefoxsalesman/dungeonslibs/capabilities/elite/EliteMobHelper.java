package net.firefoxsalesman.dungeonslibs.capabilities.elite;

import net.minecraft.world.entity.Entity;

public class EliteMobHelper {

	public static EliteMob getEliteMobCapability(Entity entity) {
		return AttacherEliteMob.get(entity).orElse(new EliteMob());
	}
}
