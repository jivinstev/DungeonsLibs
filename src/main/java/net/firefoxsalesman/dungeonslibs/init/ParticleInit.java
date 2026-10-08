package net.firefoxsalesman.dungeonslibs.init;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

import static net.firefoxsalesman.dungeonslibs.DungeonsLibs.MOD_ID;

public class ParticleInit {
	private static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister
			.create(BuiltInRegistries.PARTICLE_TYPE, MOD_ID);

	public static final Supplier<SimpleParticleType> SNOWFLAKE = makeParticle("snowflake");

	public static void register(IEventBus eventBus) {
		PARTICLES.register(eventBus);
	}

	private static Supplier<SimpleParticleType> makeParticle(String name) {
		return PARTICLES.register(name, () -> new SimpleParticleType(true));
	}
}
