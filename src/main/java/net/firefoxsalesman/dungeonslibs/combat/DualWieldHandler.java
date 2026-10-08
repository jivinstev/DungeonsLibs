package net.firefoxsalesman.dungeonslibs.combat;

import net.minecraft.core.registries.BuiltInRegistries;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

public class DualWieldHandler {
	public static void switchHand(ServerPlayer player) {
		if (!DungeonsLibrariesConfig.ENABLE_DUAL_WIELDING.get())
			return;
		ItemStack mainHandItem = player.getMainHandItem();
		ItemStack offHandItem = player.getOffhandItem();
		MeleeGearConfig config = MeleeGearConfigRegistry
				.getConfig(BuiltInRegistries.ITEM.getKey(mainHandItem.getItem()));
		if (config.isLight() && !offHandItem.isEmpty() && offHandItem.getItem() instanceof TieredItem) {
			player.setItemInHand(InteractionHand.MAIN_HAND, player.getOffhandItem());
			player.setItemInHand(InteractionHand.OFF_HAND, mainHandItem);
		}
	}
}
