package net.firefoxsalesman.dungeonslibs.capabilities.soulcaster;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.SOUL_GATHERING;
import com.Polarice3.Goety.utils.SEHelper;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.entities.SoulOrbEntity;
import net.firefoxsalesman.dungeonslibs.utils.ModHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class SoulEvents {

	@SubscribeEvent
	public static void onSoulSpawning(LivingDeathEvent event) {
		LivingEntity entityLiving = event.getEntity();
		Entity sourceEntity = event.getSource().getEntity();
		if (sourceEntity instanceof Player) {
			double soulAmount = ((Player) sourceEntity).getAttributeValue(SOUL_GATHERING);
			if (soulAmount > 0) {
				if (ModHelper.hasGoety()) {
					SEHelper.increaseSouls((Player) sourceEntity, (int) soulAmount);
				} else {
					entityLiving.level().addFreshEntity(new SoulOrbEntity((Player) sourceEntity,
							entityLiving.level(), entityLiving.getX(),
							entityLiving.getY() + 0.5D,
							entityLiving.getZ(), (float) soulAmount));
				}
			}
		}
	}
}
