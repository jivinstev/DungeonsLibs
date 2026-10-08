package net.firefoxsalesman.dungeonslibs.entities;

import net.minecraft.core.registries.BuiltInRegistries;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import static net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper.modLoc;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import java.util.function.Supplier;

public final class LibEntityTypes {

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
			.create(BuiltInRegistries.ENTITY_TYPE, DungeonsLibs.MOD_ID);

	public static final Supplier<EntityType<SoulOrbEntity>> SOUL_ORB = ENTITY_TYPES.register("soul_orb",
			() -> EntityType.Builder.<SoulOrbEntity>of(SoulOrbEntity::new, MobCategory.MISC)
					.sized(0.5F, 0.5F).clientTrackingRange(6).updateInterval(20)
					.build(modLoc("soul_orb").toString()));
	public static final Supplier<EntityType<SummonSpotEntity>> SUMMON_SPOT = ENTITY_TYPES.register(
			"summon_spot",
			() -> EntityType.Builder.<SummonSpotEntity>of(SummonSpotEntity::new, MobCategory.MISC)
					.fireImmune()
					.sized(1.0F, 2.0F)
					.clientTrackingRange(10)
					.build(modLoc("summon_spot").toString()));

	public static void register(IEventBus eventBus) {
		ENTITY_TYPES.register(eventBus);
	}
}
