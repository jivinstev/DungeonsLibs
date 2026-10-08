package net.firefoxsalesman.dungeonslibs.client.renderer.gearconfig;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class ArmorGearClientExtensions implements IClientItemExtensions {
	private GeoArmorRenderer<?> renderer;

	@Override
	public HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack,
			EquipmentSlot armorSlot, HumanoidModel<?> _default) {
		// TODO: If your armour isn't rendering this is probably the issue
		if (renderer == null) {
			renderer = new ArmorGearRenderer<>();
		}
		renderer.prepForRender(entityLiving, itemStack, armorSlot, _default);
		return (HumanoidModel<?>) renderer;
	}
}
