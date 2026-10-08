package net.firefoxsalesman.dungeonslibs.client.network;

import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCaster;
import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCasterHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class UpdateSoulsClientHandler {
	public static void run(float newAmount) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null) {
			SoulCaster soulCasterCap = SoulCasterHelper.getSoulCasterCapability(player);
			soulCasterCap.setSouls(newAmount, player);
		}
	}
}
