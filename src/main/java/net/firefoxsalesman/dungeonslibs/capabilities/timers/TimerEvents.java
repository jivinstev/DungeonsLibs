package net.firefoxsalesman.dungeonslibs.capabilities.timers;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class TimerEvents {

	@SubscribeEvent
	public static void onLivingUpdate(EntityTickEvent.Pre event) {
	    if (!(event.getEntity() instanceof LivingEntity living)) return;
		Timers timersCapability = TimersHelper.getTimersCapability(living);
		timersCapability.tickTimers();
	}

	@SubscribeEvent
	public static void onPlayerUpdate(PlayerTickEvent.Pre event) {
		Timers timersCapability = TimersHelper.getTimersCapability(event.getEntity());
		if (!event.getEntity().isSpectator()
				&& !event.getEntity().level().isClientSide()) {
			timersCapability.tickTimers();
		}
	}
}
