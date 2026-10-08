package net.firefoxsalesman.dungeonslibs.attribute;

import net.minecraft.core.registries.BuiltInRegistries;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import static net.firefoxsalesman.dungeonslibs.DungeonsLibs.MOD_ID;

public class AttributeRegistry {

	private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(
			BuiltInRegistries.ATTRIBUTE,
			MOD_ID);

	/**
	 * @deprecated To be renamed FOLLOWER_COST_LIMIT in 1.20.0
	 */
	@Deprecated
	public static final DeferredHolder<Attribute, Attribute> SUMMON_CAP = ATTRIBUTES.register("summon_cap",
			() -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".summon_cap",
					0.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> FOLLOWER_COST_LIMIT = ATTRIBUTES.register("follower_cost_limit",
			() -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".follower_cost_limit",
					24.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> SOUL_GATHERING = ATTRIBUTES.register("soul_gathering",
			() -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".soul_gathering",
					0.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> SOUL_CAP = ATTRIBUTES.register("soul_cap",
			() -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".soul_cap",
					300.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> LIFE_STEAL = ATTRIBUTES.register("life_steal",
			() -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".life_steal",
					1.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> RANGED_DAMAGE_MULTIPLIER = ATTRIBUTES
			.register("ranged_damage_multiplier", () -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".ranged_damage_multiplier",
					1.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> MAGIC_DAMAGE_MULTIPLIER = ATTRIBUTES
			.register("magic_damage_multiplier", () -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".magic_damage_multiplier",
					0.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static final DeferredHolder<Attribute, Attribute> ARTIFACT_COOLDOWN_MULTIPLIER = ATTRIBUTES
			.register("artifact_cooldown_multiplier", () -> new RangedAttribute(
					"attribute.name.generic." + MOD_ID + ".artifact_cooldown_multiplier",
					1.0D,
					0.0D,
					1024.0D)
					.setSyncable(true));

	public static void register(IEventBus bus) {
		ATTRIBUTES.register(bus);
	}
}
