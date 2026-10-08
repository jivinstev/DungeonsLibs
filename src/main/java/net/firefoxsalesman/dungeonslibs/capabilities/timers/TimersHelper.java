package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities;
import net.minecraft.world.entity.Entity;

public class TimersHelper {

	public static Timers getTimersCapability(Entity entity) {
		return entity.getData(LibCapabilities.TIMERS_ATTACHMENT);
	}
}
