package net.firefoxsalesman.dungeonslibs.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CrossbowItem.class)
public interface CrossbowItemInvoker {

	@Accessor
	boolean getStartSoundPlayed();

	@Accessor
	boolean getMidLoadSoundPlayed();

	@Accessor
	void setStartSoundPlayed(boolean b);

	@Accessor
	void setMidLoadSoundPlayed(boolean b);

	@Invoker
	static boolean callTryLoadProjectiles(LivingEntity livingEntity, ItemStack stack) {
		throw new RuntimeException("Invoker failed to mixin");
	}

	@Invoker("getChargingSounds")
	CrossbowItem.ChargingSounds callGetChargingSounds(ItemStack stack);
}
