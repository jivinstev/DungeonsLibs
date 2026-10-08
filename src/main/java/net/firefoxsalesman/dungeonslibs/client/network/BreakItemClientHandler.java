package net.firefoxsalesman.dungeonslibs.client.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BreakItemClientHandler {
	public static void run(int entityID, ItemStack stack) {
		ClientLevel world = Minecraft.getInstance().level;
		Entity target = null;
		if (world != null)
			target = world.getEntity(entityID);
		if (target instanceof LivingEntity livingEntity) {
			livingEntity.breakItem(stack);
		}
	}
}
