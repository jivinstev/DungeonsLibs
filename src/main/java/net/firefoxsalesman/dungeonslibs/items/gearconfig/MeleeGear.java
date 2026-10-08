package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.core.registries.BuiltInRegistries;

import net.firefoxsalesman.dungeonslibs.items.interfaces.IComboWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IMeleeWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.mixin.ItemMaxDamage;
import net.firefoxsalesman.dungeonslibs.mixin.TieredItemAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.enchantment.Enchantment;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class MeleeGear extends TieredItem
		implements IMeleeWeapon, IComboWeapon, IReloadableGear, IUniqueGear {

	private ItemAttributeModifiers defaultModifiers;
	private MeleeGearConfig meleeGearConfig;
	private float attackDamage;

	public MeleeGear(Item.Properties properties) {
		super(Tiers.WOOD, properties);
		reload();
	}

	@Override
	public void reload() {
		meleeGearConfig = MeleeGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		((TieredItemAccessor) this).setTier(meleeGearConfig.getWeaponMaterial());
		ItemMaxDamage.setMaxDamage(this, getTier().getUses());
		ItemMaxDamage.setRarity(this, meleeGearConfig.getRarity());
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		meleeGearConfig.getAttributes().forEach(attributeModifier -> {
			Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeModifier.getAttributeResourceLocation());
			if (attribute.isPresent()) {
				if (ATTACK_DAMAGE.equals(attribute.get())) {
					attackDamage = (float) attributeModifier.getAmount()
							+ getTier().getAttackDamageBonus();
				}
				builder.add(attribute.get(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "weapon_modifier"),
						attributeModifier.getAmount(), attributeModifier.getOperation()), EquipmentSlotGroup.MAINHAND);
			}
		});
		defaultModifiers = builder.build();
	}

	public MeleeGearConfig getGearConfig() {
		return meleeGearConfig;
	}

	@Override
	public int getComboLength(ItemStack stack, LivingEntity attacker) {
		return getGearConfig().getComboLength();
	}

	@Override
	public boolean isUnique() {
		return meleeGearConfig.isUnique();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return defaultModifiers;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return getGearConfig().isDisablesShield();
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
		return true;
	}

	public float getDamage() {
		return attackDamage;
	}

	public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
		return !pPlayer.isCreative();
	}

	@Override
	public boolean mineBlock(ItemStack itemStack, Level level, BlockState blockState, BlockPos blockPos,
			LivingEntity livingEntity) {
		if (blockState.getDestroySpeed(level, blockPos) != 0.0F) {
			itemStack.hurtAndBreak(1, livingEntity, EquipmentSlot.MAINHAND);
		}

		return true;
	}

	@Override
	public boolean isCorrectToolForDrops(ItemStack stack, BlockState p_150897_1_) {
		return p_150897_1_.is(Blocks.COBWEB) || p_150897_1_.is(BlockTags.LEAVES);
	}

	@Override
	public float getDestroySpeed(ItemStack itemStack, BlockState pBlockState) {
		if (pBlockState.is(Blocks.COBWEB) || pBlockState.is(BlockTags.LEAVES)) {
			return 15.0F;
		} else {
			// Material material = pBlockState.getMaterial();
			// return material != Material.PLANT && material != Material.REPLACEABLE_PLANT
			// && !pBlockState.is(BlockTags.LEAVES) && material != Material.VEGETABLE ? 1.0F
			// : 1.5F;

			MapColor m = pBlockState.getBlock().defaultMapColor();
			PushReaction p = pBlockState.getPistonPushReaction();
			return !(m.equals(MapColor.PLANT) && p.equals(PushReaction.DESTROY)) ? 1.0F : 1.5F;
		}
	}

	@Override
	public boolean supportsEnchantment(ItemStack stack, Holder<Enchantment> enchantment) {
		return super.supportsEnchantment(stack, enchantment)
				|| enchantment.value().definition().supportedItems().contains(stack.getItemHolder());
	}
}
