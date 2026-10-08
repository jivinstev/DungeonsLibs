package net.firefoxsalesman.dungeonslibs.loot;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class LoottableModifier extends LootModifier {
	public static final Supplier<MapCodec<LoottableModifier>> CODEC = Suppliers
			.memoize(() -> RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
					.and(Codec.STRING.fieldOf("table").forGetter(m -> m.table))
					.apply(inst, LoottableModifier::new)));
	public final String table;

	public LoottableModifier(LootItemCondition[] conditonsIn, String table) {
		super(conditonsIn);
		this.table = table;
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec() {
		return CODEC.get();
	}

	@Override
	protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
			LootContext context) {
		context.getLevel().getServer().reloadableRegistries()
				.getLootTable(ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.parse(table)))
				.getRandomItemsRaw(context, generatedLoot::add);
		return generatedLoot;
	}
}
