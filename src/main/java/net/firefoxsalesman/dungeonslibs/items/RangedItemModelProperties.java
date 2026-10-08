package net.firefoxsalesman.dungeonslibs.items;

import net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.*;
import java.util.function.Supplier;

import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.utils.RangedAttackHelper.getCrossbowChargeTime;

public class RangedItemModelProperties {

	private static final ResourceLocation PULL_PROPERTY = ResourceLocation.parse("pull");
	private static final ResourceLocation PULLING_PROPERTY = ResourceLocation.parse("pulling");
	private static final ResourceLocation CHARGED_PROPERTY = ResourceLocation.parse("charged");

	public static void init() {
		Map<Item, Map<ResourceLocation, ItemPropertyFunction>> itemModelsProperties = ItemProperties.PROPERTIES;

		Map<ResourceLocation, ItemPropertyFunction> bowModelProperties = itemModelsProperties.get(Items.BOW);
		bowModelProperties.put(PULL_PROPERTY,
				RangedItemModelProperties::getBowPullProperty);
		bowModelProperties.put(PULLING_PROPERTY,
				RangedItemModelProperties::getBowPullingProperty);

		Map<ResourceLocation, ItemPropertyFunction> crossbowModelProperties = itemModelsProperties
				.get(Items.CROSSBOW);
		if (crossbowModelProperties != null) {
			crossbowModelProperties.put(PULL_PROPERTY,
					RangedItemModelProperties::getCrossbowPullProperty);
			crossbowModelProperties.put(PULLING_PROPERTY,
					RangedItemModelProperties::getCrossbowPullingProperty);
			crossbowModelProperties.put(CHARGED_PROPERTY,
					RangedItemModelProperties::getCrossbowChargedProperty);
		}
	}

	public static void addRangedModelProperties(Supplier<Item> itemRegistryObject) {
		if (itemRegistryObject.get() instanceof BowItem) {
			addBowModelProperties(itemRegistryObject);
		} else if (itemRegistryObject.get() instanceof CrossbowItem) {
			addCrossbowModelProperties(itemRegistryObject);
		}
	}

	public static void addBowModelProperties(Supplier<Item> itemRegistryObject) {
		ItemProperties.register(itemRegistryObject.get(), PULL_PROPERTY,
				RangedItemModelProperties::getBowPullProperty);
		ItemProperties.register(itemRegistryObject.get(), PULLING_PROPERTY,
				RangedItemModelProperties::getBowPullingProperty);
	}

	public static void addCrossbowModelProperties(Supplier<Item> itemRegistryObject) {
		ItemProperties.register(itemRegistryObject.get(), PULL_PROPERTY,
				RangedItemModelProperties::getCrossbowPullProperty);
		ItemProperties.register(itemRegistryObject.get(), PULLING_PROPERTY,
				RangedItemModelProperties::getCrossbowPullingProperty);
		ItemProperties.register(itemRegistryObject.get(), CHARGED_PROPERTY,
				RangedItemModelProperties::getCrossbowChargedProperty);
	}

	private static float getCrossbowPullProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		if (livingEntity == null || CrossbowItem.isCharged(stack)) {
			return 0.0F;
		} else
			return (stack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
					/ getCrossbowChargeTime(livingEntity, stack);
	}

	private static float getCrossbowPullingProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		return livingEntity != null && livingEntity.isUsingItem()
				&& livingEntity.getUseItem() == stack && !CrossbowItem.isCharged(stack)
						? 1.0F
						: 0.0F;
	}

	private static float getCrossbowChargedProperty(ItemStack stack, ClientLevel clientWorld,
			LivingEntity livingEntity, int i) {
		return livingEntity != null && CrossbowItem.isCharged(stack) ? 1.0F : 0.0F;
	}

	private static float getBowPullProperty(ItemStack stack, ClientLevel clientWorld, LivingEntity livingEntity,
			int i) {
		if (livingEntity == null || livingEntity.getUseItem() != stack) {
			return 0.0F;
		} else {
			return (stack.getUseDuration(livingEntity) - livingEntity.getUseItemRemainingTicks())
					/ RangedAttackHelper.getBowChargeTime(livingEntity, livingEntity.getUseItem());
		}
	}

	private static float getBowPullingProperty(ItemStack stack, ClientLevel clientWorld, LivingEntity livingEntity,
			int i) {
		return livingEntity != null && livingEntity.isUsingItem()
				&& livingEntity.getUseItem() == stack
						? 1.0F
						: 0.0F;
	}
}
