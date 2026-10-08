package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import static java.util.UUID.randomUUID;
import static net.minecraft.world.item.ArmorMaterials.CHAIN;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.component.ItemAttributeModifiers;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import net.firefoxsalesman.dungeonslibs.client.renderer.gearconfig.ArmorGearClientExtensions;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IArmor;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IReloadableGear;
import net.firefoxsalesman.dungeonslibs.items.interfaces.IUniqueGear;
import net.firefoxsalesman.dungeonslibs.utils.DescriptionHelper;
import net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper;
import net.firefoxsalesman.dungeonslibs.mixin.ArmorItemAccessor;
import net.firefoxsalesman.dungeonslibs.utils.ItemMaxDamage;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;

import software.bernie.geckolib.util.GeckoLibUtil;

public class ArmorGear extends ArmorItem implements IReloadableGear, IArmor, IUniqueGear, GeoItem {
	private static final ResourceLocation DEFAULT_ARMOR_ANIMATIONS = ResourceLocationHelper
			.modLoc("animations/armor/armor_default.animation.json");
	private static final UUID[] ARMOR_MODIFIER_UUID_PER_SLOT = new UUID[] {
			UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"),
			UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"),
			UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"),
			UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150") };

	private ItemAttributeModifiers defaultModifiers;
	private int durability;
	private ArmorGearConfig armorGearConfig;
	private final ResourceLocation armorSet;
	private final ResourceLocation modelLocation;
	private final ResourceLocation textureLocation;
	private final ResourceLocation animationFileLocation;

	public ArmorGear(Type slotType, Properties properties, ResourceLocation armorSet,
			ResourceLocation modelLocation, ResourceLocation textureLocation,
			ResourceLocation animationFileLocation) {
		super(CHAIN, slotType, properties.durability(1));
		this.armorSet = armorSet;
		this.modelLocation = modelLocation;
		this.textureLocation = textureLocation;
		this.animationFileLocation = animationFileLocation;
		reload();
	}

	@Override
	public void reload() {
		armorGearConfig = ArmorGearConfigRegistry.getConfig(armorSet);
		if (armorGearConfig == ArmorGearConfig.DEFAULT) {
			armorGearConfig = ArmorGearConfigRegistry.getConfig(BuiltInRegistries.ITEM.getKey(this));
		}
		net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterial material = armorGearConfig
				.getArmorMaterial();
		java.util.Map<ArmorItem.Type, Integer> defenseMap = new java.util.EnumMap<>(ArmorItem.Type.class);
		for (ArmorItem.Type armorType : ArmorItem.Type.values()) {
			defenseMap.put(armorType, material.getDefenseForType(armorType));
		}
		ArmorMaterial base = CHAIN.value();
		ArmorMaterial vanillaMaterial = new ArmorMaterial(defenseMap, material.getEnchantmentValue(),
				Holder.direct(material.getEquipSound()), base.repairIngredient(), base.layers(),
				material.getToughness(), material.getKnockbackResistance());
		((ArmorItemAccessor) this).setMaterial(Holder.direct(vanillaMaterial));
		ItemMaxDamage.setRarity(this, armorGearConfig.getRarity());
		durability = getType().getDurability(15);
		float armorToughness = material.getToughness();
		float armorKnockbackResistance = material.getKnockbackResistance();
		ItemAttributeModifiers.Builder builder = ItemAttributeModifiers.builder();
		EquipmentSlotGroup slotGroup = EquipmentSlotGroup.bySlot(getType().getSlot());
		builder.add(Attributes.ARMOR, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_defense"),
				material.getDefenseForType(getType()), AttributeModifier.Operation.ADD_VALUE), slotGroup);
		builder.add(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_toughness"),
				armorToughness, AttributeModifier.Operation.ADD_VALUE), slotGroup);
		if (armorKnockbackResistance > 0) {
			builder.add(Attributes.KNOCKBACK_RESISTANCE,
					new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_knockback_resistance"),
							armorKnockbackResistance,
							AttributeModifier.Operation.ADD_VALUE), slotGroup);
		}
		int configIndex = 0;
		for (var attributeModifier : armorGearConfig.getAttributes()) {
			Optional<Holder.Reference<Attribute>> attribute = BuiltInRegistries.ATTRIBUTE.getHolder(attributeModifier.getAttributeResourceLocation());
			if (attribute.isPresent()) {
				builder.add(attribute.get(), new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_config." + configIndex),
						attributeModifier.getAmount(), attributeModifier.getOperation()), slotGroup);
			}
			configIndex++;
		}
		defaultModifiers = builder.build();
	}

	public ArmorGearConfig getGearConfig() {
		return armorGearConfig;
	}

	@Override
	public boolean isUnique() {
		return armorGearConfig.isUnique();
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
		return durability;
	}

	@Override
	public void appendHoverText(ItemStack stack, net.minecraft.world.item.Item.TooltipContext level, List<Component> list, TooltipFlag flag) {
		super.appendHoverText(stack, level, list, flag);
		if (armorSet != null) {
			DescriptionHelper.addLoreDescription(list, armorSet);
		} else {
			DescriptionHelper.addLoreDescription(list, BuiltInRegistries.ITEM.getKey(this));
		}
	}

	public ResourceLocation getArmorSet() {
		return armorSet;
	}

	protected AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 20, this::predicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return factory;
	}

	private <P extends GeoAnimatable> PlayState predicate(AnimationState<P> event) {
		event.getController().setAnimation(RawAnimation.begin().then("idle", LoopType.LOOP));
		return PlayState.CONTINUE;
	}

	public ResourceLocation getModelLocation() {
		return modelLocation;
	}

	public ResourceLocation getTextureLocation() {
		return textureLocation;
	}

	public ResourceLocation getAnimationFileLocation() {
		return animationFileLocation;
	}

	@Override
	public void initializeClient(Consumer<IClientItemExtensions> consumer) {
		super.initializeClient(consumer);
		consumer.accept(new ArmorGearClientExtensions());
	}
}
