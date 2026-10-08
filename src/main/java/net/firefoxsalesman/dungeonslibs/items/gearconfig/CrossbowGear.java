package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;

import net.firefoxsalesman.dungeonslibs.event.CrossbowEvent;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IRangedWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.mixin.CrossbowItemInvoker;
import net.firefoxsalesman.dungeonslibs.utils.ItemMaxDamage;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.firefoxsalesman.dungeonslibs.ModHolders;
import net.neoforged.neoforge.common.NeoForge;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class CrossbowGear extends CrossbowItem implements IRangedWeapon, IReloadableGear, IUniqueGear {
	private ItemAttributeModifiers defaultModifiers;
	private int configuredDurability;
	private BowGearConfig crossbowGearConfig;

	public CrossbowGear(Properties builder) {
		super(builder.durability(384));
		reload();
	}

	@Override
	public void reload() {
		crossbowGearConfig = CrossbowGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		int index = 0;
		for (var attributeModifier : crossbowGearConfig.getAttributes()) {
			Holder<Attribute> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeModifier.getAttributeResourceLocation()).orElse(null);
			if (attribute != null) {
				ResourceLocation modifierId;
				if (ATTACK_DAMAGE.equals(attribute)) {
					modifierId = Item.BASE_ATTACK_DAMAGE_ID;
				} else if (ATTACK_SPEED.equals(attribute)) {
					modifierId = Item.BASE_ATTACK_SPEED_ID;
				} else {
					modifierId = ResourceLocation.fromNamespaceAndPath("dungeonslibs", "crossbow." + index);
				}
				builder.add(attribute, new AttributeModifier(modifierId,
						attributeModifier.getAmount(), attributeModifier.getOperation()), EquipmentSlotGroup.MAINHAND);
			}
			index++;
		}
		defaultModifiers = builder.build();
		configuredDurability = crossbowGearConfig.getDurability();
		ItemMaxDamage.setRarity(this, crossbowGearConfig.getRarity());
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return defaultModifiers;
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return configuredDurability;
	}

	public float getDefaultChargeTime() {
		return crossbowGearConfig.getDefaultChargeTime();
	}

	@Override
	public void onUseTick(Level world, LivingEntity livingEntity, ItemStack stack, int timeLeft) {
		if (!world.isClientSide) {
			int quickChargeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.QUICK_CHARGE),
					stack);

			CrossbowItemInvoker crossbowItemInvoker = (CrossbowItemInvoker) this;
			CrossbowItem.ChargingSounds chargingSounds = crossbowItemInvoker.callGetChargingSounds(stack);
			SoundEvent quickChargeSoundEvent = chargingSounds.start().map(Holder::value).orElse(null);
			Holder<SoundEvent> loadingMiddleSoundEvent = chargingSounds.mid().orElse(null);
			float chargeTime = (float) (stack.getUseDuration(livingEntity) - timeLeft)
					/ getCrossbowChargeTime(livingEntity, stack);
			if (chargeTime < 0.2F) {
				crossbowItemInvoker.setStartSoundPlayed(false);
				crossbowItemInvoker.setMidLoadSoundPlayed(false);
			}

			if (chargeTime >= 0.2F && !crossbowItemInvoker.getStartSoundPlayed() && chargeTime < 1.0F) {
				crossbowItemInvoker.setStartSoundPlayed(true);
				world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
						quickChargeSoundEvent, SoundSource.PLAYERS, 0.5F, 1.0F);
			}

			if (chargeTime >= 0.5F && loadingMiddleSoundEvent != null
					&& !crossbowItemInvoker.getMidLoadSoundPlayed() && chargeTime < 1.0F) {
				crossbowItemInvoker.setMidLoadSoundPlayed(true);
				world.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
						loadingMiddleSoundEvent, SoundSource.PLAYERS, 0.5F, 1.0F);
			}
		}

	}

	@Override
	public void releaseUsing(ItemStack stack, Level worldIn, LivingEntity livingEntity, int timeLeft) {
		float chargeTime = getCrossbowChargeTime(livingEntity, stack) + 3 - timeLeft;
		float getCharge = getCrossbowCharge(livingEntity, chargeTime, stack);
		// Call to CrossbowItem.tryLoadProjectiles must be in-line as it modifies NBT
		// without the previous checks
		// Do not refactor as a variable preceding this if statement
		if (getCharge >= 1.0F && !isCharged(stack)
				&& CrossbowItemInvoker.callTryLoadProjectiles(livingEntity, stack)) {
			// Charged state is set by tryLoadProjectiles via the CHARGED_PROJECTILES component in 1.21
			SoundSource soundSource = livingEntity instanceof Player ? SoundSource.PLAYERS
					: SoundSource.HOSTILE;
			worldIn.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),
					SoundEvents.CROSSBOW_LOADING_END, soundSource, 1.0F,
					1.0F / (livingEntity.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
		}

	}

	public float getCrossbowCharge(LivingEntity livingEntity, float useTime, ItemStack stack) {
		float crossbowChargeTime = getCrossbowChargeTime(livingEntity, stack);
		float charge = useTime / crossbowChargeTime;
		if (charge > 1.0F) {
			charge = 1.0F;
		}

		return charge;
	}

	public float getCrossbowChargeTime(@Nullable LivingEntity livingEntity, ItemStack stack) {
		int quickChargeLevel = EnchantmentHelper.getItemEnchantmentLevel(ModHolders.enchantment(Enchantments.QUICK_CHARGE), stack);
		float minTime = 1;
		CrossbowEvent.ChargeTime event = new CrossbowEvent.ChargeTime(livingEntity, stack,
				getDefaultChargeTime());
		NeoForge.EVENT_BUS.post(event);
		return Math.max(event.getChargeTime() - 5 * quickChargeLevel, minTime);
	}

	@Override
	public int getUseDuration(ItemStack stack, LivingEntity livingEntity) {
		return (int) getCrossbowChargeTime(null, stack) + 3;
	}

	@Override
	public boolean useOnRelease(ItemStack stack) {
		return true;
	}

	@Override
	public boolean isUnique() {
		return crossbowGearConfig.isUnique();
	}

	public BowGearConfig getGearConfig() {
		return crossbowGearConfig;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}
}
