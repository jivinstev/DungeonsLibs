package net.firefoxsalesman.dungeonslibs.utils;

import net.neoforged.fml.ModList;

public class ModHelper {
	/**
	 * @return true if the mod with modId is loaded
	 */
	public static boolean hasMod(String modId) {
		return ModList.get().isLoaded(modId);
	}

	/**
	 * @return true if Goety is loaded.
	 */
	public static boolean hasGoety() {
		return hasMod("goety");
	}
}
