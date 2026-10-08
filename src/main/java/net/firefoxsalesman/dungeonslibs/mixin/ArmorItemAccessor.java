package net.firefoxsalesman.dungeonslibs.mixin;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ArmorMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ArmorItem.class)
public interface ArmorItemAccessor {

	@Accessor
	@Mutable
	void setMaterial(Holder<ArmorMaterial> armorMaterial);
}
