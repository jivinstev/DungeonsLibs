package net.firefoxsalesman.dungeonslibs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import static net.firefoxsalesman.dungeonslibs.DungeonsLibs.MOD_ID;

import net.firefoxsalesman.dungeonslibs.client.particles.SnowflakeParticle;
import net.firefoxsalesman.dungeonslibs.init.ParticleInit;

@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ParticleEventHandler {

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onParticleFactory(RegisterParticleProvidersEvent event) {
		register(ParticleInit.SNOWFLAKE.get(), SnowflakeParticle.Factory::new);
	}

	private static void register(ParticleType<SimpleParticleType> particleType,
			ParticleEngine.SpriteParticleRegistration<SimpleParticleType> factory) {
		Minecraft.getInstance().particleEngine.register(particleType, factory);
	}
}
