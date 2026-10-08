package net.firefoxsalesman.dungeonslibs.items.materials.armor;

import net.firefoxsalesman.dungeonslibs.data.util.DefaultsCodecJsonDataManager;
import net.firefoxsalesman.dungeonslibs.network.materials.ArmorMaterialSyncPacket;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Collection;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.world.item.ArmorMaterials.*;

public class DungeonsArmorMaterials {

	public static final DefaultsCodecJsonDataManager<DungeonsArmorMaterial> ARMOR_MATERIALS = new DefaultsCodecJsonDataManager<>(
			"material/armor", DungeonsArmorMaterial.CODEC);
	public static final Map<ArmorMaterial, ArmorMaterialBaseType> baseArmorMaterials = new HashMap<>();

	public static void setupVanillaMaterials() {
		addDefaultArmorMaterial(LEATHER, ArmorMaterialBaseType.LEATHER,
				ResourceLocation.parse("minecraft:leather"));
		addDefaultArmorMaterial(CHAIN, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:chainmail"));
		addDefaultArmorMaterial(IRON, ArmorMaterialBaseType.METAL, ResourceLocation.parse("minecraft:iron"));
		addDefaultArmorMaterial(GOLD, ArmorMaterialBaseType.METAL, ResourceLocation.parse("minecraft:gold"));
		addDefaultArmorMaterial(DIAMOND, ArmorMaterialBaseType.GEM, ResourceLocation.parse("minecraft:diamond"));
		addDefaultArmorMaterial(TURTLE, ArmorMaterialBaseType.LEATHER,
				ResourceLocation.parse("minecraft:turtle"));
		addDefaultArmorMaterial(NETHERITE, ArmorMaterialBaseType.METAL,
				ResourceLocation.parse("minecraft:netherite"));
	}

	public static void addDefaultArmorMaterial(Holder<ArmorMaterial> material, ArmorMaterialBaseType baseType,
			ResourceLocation resourceLocation) {
		ArmorMaterial m = material.value();
		List<Integer> amounts = List.of(m.defense().getOrDefault(ArmorItem.Type.BOOTS, 0),
				m.defense().getOrDefault(ArmorItem.Type.LEGGINGS, 0),
				m.defense().getOrDefault(ArmorItem.Type.CHESTPLATE, 0),
				m.defense().getOrDefault(ArmorItem.Type.HELMET, 0));
		ResourceLocation repair = BuiltInRegistries.ITEM
				.getKey(m.repairIngredient().get().getItems()[0].getItem());
		ARMOR_MATERIALS.addDefault(resourceLocation,
				DungeonsArmorMaterial.create(resourceLocation.getPath(), 0, amounts, m.enchantmentValue(),
						repair, m.equipSound().value(), m.toughness(), m.knockbackResistance(),
						baseType));
		baseArmorMaterials.put(m, baseType);
	}

	public static DungeonsArmorMaterial getArmorMaterial(ResourceLocation resourceLocation) {
		return ARMOR_MATERIALS.getData().getOrDefault(resourceLocation,
				ARMOR_MATERIALS.getData().get(ResourceLocation.parse("minecraft:iron")));
	}

	public static boolean ArmorMaterialExists(ResourceLocation resourceLocation) {
		return ARMOR_MATERIALS.getData().containsKey(resourceLocation);
	}

	public static Collection<ResourceLocation> armorMaterialsKeys() {
		return ARMOR_MATERIALS.getData().keySet();
	}

	public static Collection<DungeonsArmorMaterial> getArmorMaterials(ArmorMaterialBaseType baseType) {
		return ARMOR_MATERIALS.getData().values().stream()
				.filter(material -> material.getBaseType() == baseType).collect(Collectors.toList());
	}

	public static ArmorMaterialSyncPacket toPacket(Map<ResourceLocation, DungeonsArmorMaterial> map) {
		return new ArmorMaterialSyncPacket(map);
	}

	public static void subscribe() {
		NeoForge.EVENT_BUS
				.addListener((OnDatapackSyncEvent event) -> {
					ArmorMaterialSyncPacket packet = toPacket(ARMOR_MATERIALS.getData());
					event.getRelevantPlayers().forEach(
							player -> PacketDistributor
									.sendToPlayer(player, packet));
				});
	}
}
