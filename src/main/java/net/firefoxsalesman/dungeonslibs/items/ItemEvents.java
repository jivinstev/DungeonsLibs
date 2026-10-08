package net.firefoxsalesman.dungeonslibs.items;

import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.LIFE_STEAL;
import static net.firefoxsalesman.dungeonslibs.attribute.AttributeRegistry.MAGIC_DAMAGE_MULTIPLIER;

import java.util.UUID;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.utils.DamageSourceHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class ItemEvents {
	protected static final UUID BASE_ATTACK_DAMAGE_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");
	protected static final UUID BASE_ATTACK_SPEED_UUID = UUID.fromString("FA233E1C-4180-4865-B01B-BCCE9785ACA3");

	@SubscribeEvent
	public static void onMagicDamage(LivingDamageEvent.Pre event) {
		DamageSource source = event.getSource();
		if (source.getDirectEntity() != source.getEntity()
				&& DamageSourceHelper.isSource(source,
						event.getEntity().damageSources().magic())
				&&
				source.getEntity() instanceof LivingEntity) {

			float originalDamage = event.getNewDamage();

			LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
			AttributeInstance magicDamageMultiplierAttribute = attacker
					.getAttribute(MAGIC_DAMAGE_MULTIPLIER);
			double attributeModifier = magicDamageMultiplierAttribute != null
					? magicDamageMultiplierAttribute.getValue()
					: 1.0D;
			double additionalDamage = originalDamage * attributeModifier;

			if (additionalDamage > 0)
				event.setNewDamage(originalDamage + (float) additionalDamage);
		}
	}

	@SubscribeEvent
	public static void onEntityKilled(LivingDeathEvent event) {
		DamageSource source = event.getSource();
		if (source.getEntity() instanceof LivingEntity) {
			LivingEntity attacker = (LivingEntity) source.getEntity();
			AttributeInstance attribute = attacker.getAttribute(LIFE_STEAL);
			if (attribute != null) {
				double lifeStealAmount = attribute.getValue() - 1.0D;
				float victimMaxHealth = event.getEntity().getMaxHealth();
				if (attacker.getHealth() < attacker.getMaxHealth()) {
					attacker.heal(victimMaxHealth * (float) lifeStealAmount);
				}
			}
		}
	}
}
