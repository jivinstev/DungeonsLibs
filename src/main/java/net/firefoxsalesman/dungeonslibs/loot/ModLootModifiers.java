package net.firefoxsalesman.dungeonslibs.loot;

import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

import static net.firefoxsalesman.dungeonslibs.DungeonsLibs.MOD_ID;

import com.mojang.serialization.MapCodec;

public class ModLootModifiers {
	public static DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIER_SERIALIZERS = DeferredRegister
			.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, MOD_ID);
	public static final Supplier<MapCodec<? extends IGlobalLootModifier>> LOOTTABLE = LOOT_MODIFIER_SERIALIZERS
			.register("loottable", LoottableModifier.CODEC);

	public static void register(IEventBus eventBus) {
		LOOT_MODIFIER_SERIALIZERS.register(eventBus);
	}
}
