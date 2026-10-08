package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class ToolGear extends MeleeGear {
	private static final TagKey<Enchantment> DIGGER_ENCHANTMENTS = TagKey.create(Registries.ENCHANTMENT, ResourceLocation.fromNamespaceAndPath("dungeonslibs", "digger"));
	private final TagKey<Block> blocks;

	public ToolGear(TagKey<Block> blocks, Properties properties) {
		super(properties);
		this.blocks = blocks;
	}

	@Override
	public float getDestroySpeed(ItemStack pStack, BlockState pState) {
		return pState.is(this.blocks) ? this.getTier().getSpeed() : 1.0F;
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.supportsEnchantment(stack, enchantment)
				|| enchantment.is(DIGGER_ENCHANTMENTS);
	}

	// FORGE START
	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
		return state.is(blocks) && !state.is(getTier().getIncorrectBlocksForDrops());
	}

}
