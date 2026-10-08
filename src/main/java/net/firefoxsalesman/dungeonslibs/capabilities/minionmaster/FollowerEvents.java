package net.firefoxsalesman.dungeonslibs.capabilities.minionmaster;

import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.utils.AbilityHelper;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingChangeTargetEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.List;
import java.util.stream.Collectors;

import static net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.FollowerLeaderHelper.getFollowerCapability;
import static net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.FollowerLeaderHelper.getLeaderCapability;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class FollowerEvents {
	@SubscribeEvent
	public static void onSetAttackTarget(LivingChangeTargetEvent event) {
		LivingEntity target = event.getNewAboutToBeSetTarget();
		LivingEntity attacker = event.getEntity();
		if (attacker instanceof Mob && target instanceof Mob) {
			if (AbilityHelper.isAlly(attacker, target)) {
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDropsEvent(LivingDropsEvent event) {
		Follower cap = getFollowerCapability(event.getEntity());
		if (cap.isFollower()) {
			event.setCanceled(true);
		}
	}

	@SubscribeEvent
	public static void onLivingEntityTick(EntityTickEvent.Pre event) {
	    if (!(event.getEntity() instanceof LivingEntity living)) return;
		LivingEntity entityLiving = living;
		if (entityLiving.level().isClientSide)
			return;
		Follower cap = getFollowerCapability(entityLiving);
		if (cap.isFollower()) {
			if (cap.isTemporary()) {
				if (cap.getFollowerDuration() > 0) {
					cap.setFollowerDuration(cap.getFollowerDuration() - 1);
				} else {
					if (cap.revertsOnExpiration()) {
						FollowerLeaderHelper.removeFollower(entityLiving);
					} else {
						entityLiving.remove(Entity.RemovalReason.KILLED);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void reAddFollowerGoals(EntityJoinLevelEvent event) {
		Entity entity = event.getEntity();
		if (!event.getLevel().isClientSide() && entity instanceof LivingEntity livingEntity) {
			Leader leaderCapability = getLeaderCapability(entity);
			if (entity instanceof Mob mob) {
				FollowerLeaderHelper.addFollowerGoals(mob);
			}
			List<Entity> minions = leaderCapability.getAllMinions();
			for (Entity minion : minions) {
				if (minion instanceof Mob) {
					Follower minionCapability = getFollowerCapability(minion);
					minionCapability.setLeader(livingEntity);
					FollowerLeaderHelper.addFollowerGoals((Mob) minion);
				}
			}
		}
	}

	// Avoids a situation where your minion died but onSummonableDeath didn't fire
	// in time,
	// making you unable to summon any more of that entity
	@SubscribeEvent
	public static void checkSummonedMobIsDead(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (player.level().isClientSide())
			return;
		if (!player.isAlive())
			return;
		Leader leaderCap = getLeaderCapability(player);
		updateAliveList(leaderCap);
	}

	@SubscribeEvent
	public static void onFollowerDeath(LivingDeathEvent event) {
		if (!event.getEntity().level().isClientSide() && FollowerLeaderHelper.isFollower(event.getEntity())) {
			LivingEntity livingEntity = event.getEntity();
			Follower FollowerCapability = getFollowerCapability(livingEntity);
			LivingEntity summoner = FollowerCapability.getLeader();
			if (summoner != null) {
				Leader leaderCapability = getLeaderCapability(summoner);
				updateAliveList(leaderCapability);
			}
		}
	}

	private static void updateAliveList(Leader leader) {
		List<Entity> aliveSummons = leader.getSummonedMobs().stream()
				.filter(entity -> entity != null && entity.isAlive()).collect(Collectors.toList());
		leader.setSummonedMobs(aliveSummons);
		List<Entity> aliveMinions = leader.getOtherMinions().stream()
				.filter(entity -> entity != null && entity.isAlive()).collect(Collectors.toList());
		leader.setOtherMinions(aliveMinions);
	}

}
