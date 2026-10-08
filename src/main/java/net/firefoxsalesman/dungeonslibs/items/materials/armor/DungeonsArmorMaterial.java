package net.firefoxsalesman.dungeonslibs.items.materials.armor;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem.Type;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static net.minecraft.core.registries.BuiltInRegistries.ITEM;

public class DungeonsArmorMaterial {

	public static DungeonsArmorMaterial create(String name, int durability, List<Integer> damageReductionAmounts,
			int enchantability, ResourceLocation repairItem, SoundEvent equipSound, float toughness,
			float knockbackResistance, ArmorMaterialBaseType baseType) {
		return new DungeonsArmorMaterial(name, durability, damageReductionAmounts, enchantability, repairItem,
				equipSound, toughness, knockbackResistance, baseType);
	}

	public static final Codec<DungeonsArmorMaterial> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.STRING.fieldOf("name").forGetter(DungeonsArmorMaterial::getName),
			Codec.INT.fieldOf("durability").forGetter(m -> m.durability),
			Codec.INT.listOf().fieldOf("damage_reduction_amounts").forGetter(m -> m.damageReductionAmounts),
			Codec.INT.fieldOf("enchantability").forGetter(DungeonsArmorMaterial::getEnchantmentValue),
			ResourceLocation.CODEC.fieldOf("repair_item").forGetter(m -> m.repairItemResourceLocation),
			BuiltInRegistries.SOUND_EVENT.byNameCodec().fieldOf("equip_sound")
					.forGetter(DungeonsArmorMaterial::getEquipSound),
			Codec.FLOAT.fieldOf("toughness").forGetter(DungeonsArmorMaterial::getToughness),
			Codec.FLOAT.fieldOf("knockback_resistance")
					.forGetter(DungeonsArmorMaterial::getKnockbackResistance),
			ArmorMaterialBaseType.CODEC.fieldOf("base_type").forGetter(m -> m.baseType))
			.apply(instance, DungeonsArmorMaterial::new));

	// Armor order: boots, leggings, chestplate, helmet
	private static final int[] BASE_DURABILITY_ARRAY = new int[] { 13, 15, 16, 11 };
	private final String name;
	private final SoundEvent equipSound;
	private final int durability;
	private final int enchantability;
	private final ResourceLocation repairItemResourceLocation;
	private final LazyLoadedValue<Ingredient> repairItem;
	private final List<Integer> damageReductionAmounts;
	private final float toughness;
	private final float knockbackResistance;
	private final ArmorMaterialBaseType baseType;

	private DungeonsArmorMaterial(String name, int durability, List<Integer> damageReductionAmounts,
			int enchantability, ResourceLocation repairItemResourceLocation, SoundEvent equipSound,
			float toughness, float knockbackResistance, ArmorMaterialBaseType baseType) {
		this.name = name;
		this.equipSound = equipSound;
		this.durability = durability;
		this.enchantability = enchantability;
		this.repairItemResourceLocation = repairItemResourceLocation;
		if (ITEM.containsKey(repairItemResourceLocation)) {
			Item item = ITEM.get(repairItemResourceLocation);
			repairItem = new LazyLoadedValue<>(() -> Ingredient.of(item));
		} else {
			repairItem = new LazyLoadedValue<>(() -> Ingredient.of(Items.IRON_INGOT));
		}
		this.damageReductionAmounts = damageReductionAmounts;
		this.toughness = toughness;
		this.knockbackResistance = knockbackResistance;
		this.baseType = baseType;
	}

	public int getEnchantmentValue() {
		return enchantability;
	}

	public String getName() {
		return name;
	}

	public Ingredient getRepairIngredient() {
		return repairItem.get();
	}

	public SoundEvent getEquipSound() {
		return equipSound;
	}

	public float getToughness() {
		return toughness;
	}

	// getKnockbackResistance
	public float getKnockbackResistance() {
		return knockbackResistance;
	}

	public ArmorMaterialBaseType getBaseType() {
		return baseType;
	}

	public int getDurabilityForType(Type pType) {
		return BASE_DURABILITY_ARRAY[pType.getSlot().getIndex()] * durability;
	}

	public int getDefenseForType(Type pType) {
		return damageReductionAmounts.get(pType.getSlot().getIndex());
	}

	/** Builds the vanilla 1.21.1 ArmorMaterial record from this data. */
	public ArmorMaterial toArmorMaterial() {
		Map<Type, Integer> defense = new EnumMap<>(Type.class);
		for (Type type : Type.values()) {
			if (type == Type.BODY) {
				continue;
			}
			int index = type.getSlot().getIndex();
			if (index < damageReductionAmounts.size()) {
				defense.put(type, damageReductionAmounts.get(index));
			}
		}
		ResourceLocation assetName = name.contains(":") ? ResourceLocation.parse(name)
				: ResourceLocation.withDefaultNamespace(name);
		return new ArmorMaterial(defense, enchantability, Holder.direct(equipSound), repairItem::get,
				List.of(new ArmorMaterial.Layer(assetName)), toughness, knockbackResistance);
	}
}
