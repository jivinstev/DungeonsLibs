package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;

import net.firefoxsalesman.dungeonslibs.items.interfaces.IRangedWeapon;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.firefoxsalesman.dungeonslibs.mixin.ItemMaxDamage;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Optional;

import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE;
import static net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_SPEED;

public class BowGear extends BowItem implements IRangedWeapon, IReloadableGear, IUniqueGear {

	private ItemAttributeModifiers defaultModifiers;
	private int configuredDurability;
	private BowGearConfig bowGearConfig;

	public BowGear(Properties builder) {
		super(builder.durability(384));
		reload();
	}

	@Override
	public void reload() {
		bowGearConfig = BowGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		bowGearConfig.getAttributes().forEach(attributeModifier -> {
			ResourceLocation attributeId = attributeModifier.getAttributeResourceLocation();
			Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeId);
			if (attribute.isPresent()) {
				ResourceLocation id;
				if (ATTACK_DAMAGE.equals(attribute.get())) {
					id = BASE_ATTACK_DAMAGE_ID;
				} else if (ATTACK_SPEED.equals(attribute.get())) {
					id = BASE_ATTACK_SPEED_ID;
				} else {
					id = ResourceLocation.fromNamespaceAndPath("dungeonslibs",
							"weapon." + attributeId.getNamespace() + "." + attributeId.getPath());
				}
				builder.add(attribute.get(), new AttributeModifier(id,
						attributeModifier.getAmount(), attributeModifier.getOperation()), EquipmentSlotGroup.MAINHAND);
			}
		});
		defaultModifiers = builder.build();
		configuredDurability = bowGearConfig.getDurability();
		ItemMaxDamage.setRarity(this, bowGearConfig.getRarity());
	}

	public float getDefaultChargeTime() {
		return bowGearConfig.getDefaultChargeTime();
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers(ItemStack stack) {
		return defaultModifiers;
	}

	@Override
	public ItemAttributeModifiers getDefaultAttributeModifiers() {
		return defaultModifiers;
	}

	@Override
	public int getMaxDamage(ItemStack stack) {
		return configuredDurability;
	}

	@Override
	public boolean isUnique() {
		return bowGearConfig.isUnique();
	}

	public BowGearConfig getGearConfig() {
		return bowGearConfig;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext world, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, world, list, flag);
		DescriptionHelper.addFullDescription(list, stack);
	}
}
