package net.firefoxsalesman.dungeonslibs.summon.goals;

import net.minecraft.core.registries.BuiltInRegistries;

import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.firefoxsalesman.dungeonslibs.entities.LibEntityTypes;
import net.firefoxsalesman.dungeonslibs.entities.SummonSpotEntity;
import net.firefoxsalesman.dungeonslibs.client.AnimationTimer;
import net.firefoxsalesman.dungeonslibs.summon.SummonHelper;
import net.firefoxsalesman.dungeonslibs.utils.PositionUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.phys.HitResult;

/**
 * A scaffold for building various summoning related goals.
 */
public abstract class AbstractSummonGoal<T extends Mob> extends Goal {
	int mobSummonRange;
	int closeMobSummonRange;
	int summonType;
	byte entityEventState;
	List<String> summonList;
	EntityType<?> backupEntityType;
	Optional<SoundEvent> summonPrepSound;
	Optional<SoundEvent> summonSound;
	public T mob;
	@Nullable
	public LivingEntity target;

	public int nextUseTime;

	public AbstractSummonGoal(T mob, int mobSummonRange, int closeMobSummonRange, int entityEventState,
			List<? extends String> summonList, EntityType<?> backupEntityType,
			@Nullable SoundEvent summonPrepSound, @Nullable SoundEvent summonSound, int summonType) {
		this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP));
		this.mob = mob;
		this.target = mob.getTarget();
		this.closeMobSummonRange = closeMobSummonRange;
		this.mobSummonRange = mobSummonRange;
		this.entityEventState = (byte) entityEventState;
		this.summonList = (List<String>) summonList;
		this.backupEntityType = backupEntityType;
		this.summonPrepSound = Optional.ofNullable(summonPrepSound);
		this.summonSound = Optional.ofNullable(summonSound);
		this.summonType = summonType;
	}

	@Override
	public boolean isInterruptable() {
		return false;
	}

	public boolean requiresUpdateEveryTick() {
		return true;
	}

	@Override
	public boolean canUse() {
		target = mob.getTarget();
		return target != null && mob.tickCount >= this.nextUseTime && timer().animationsUseable()
				&& mob.hasLineOfSight(target);
	}

	@Override
	public boolean canContinueToUse() {
		return target != null && !timer().animationsUseable();
	}

	@Override
	public void start() {
		if (summonSound.isPresent())
			mob.playSound(summonSound.get(), 1.0F, 1.0F);
		timer().reset();
		mob.level().broadcastEntityEvent(mob, entityEventState);
	}

	@Override
	public void tick() {
		target = mob.getTarget();
		this.mob.getNavigation().stop();
		if (target != null && tickCondition())
			tickBody();

	}

	/**
	 * The summon work that is done during the tick method. Designed so that you can
	 * override it to run it in a loop.
	 */
	protected void tickBody() {
		SummonSpotEntity mobSummonSpot = LibEntityTypes.SUMMON_SPOT.get().create(mob.level());
		mobSummonSpot.mobSpawnRotation = mob.getRandom().nextInt(360);
		mobSummonSpot.setSummonType(summonType);
		BlockPos summonPos = mob.blockPosition().offset(
				-mobSummonRange + mob.getRandom().nextInt((mobSummonRange * 2) + 1), 0,
				-mobSummonRange + mob.getRandom().nextInt((mobSummonRange * 2) + 1));
		mobSummonSpot.moveTo(summonPos, 0.0F, 0.0F);

		// RELOCATES SUMMONED MOB CLOSER TO NECROMANCER IF SPAWNED IN A POSITION THAT
		// MAY HINDER ITS ABILITY TO JOIN IN THE BATTLE
		if (mobSummonSpot.isInWall() || !canSee(mobSummonSpot, target)) {
			summonPos = mob.blockPosition().offset(
					-closeMobSummonRange + mob.getRandom()
							.nextInt((closeMobSummonRange * 2) + 1),
					0, -closeMobSummonRange + mob.getRandom()
							.nextInt((closeMobSummonRange * 2) + 1));
		}

		// RELOCATES SUMMONED MOB TO NECROMANCER'S POSITION IF STILL IN A POSITION THAT
		// MAY HINDER ITS ABILITY TO JOIN IN THE BATTLE
		if (mobSummonSpot.isInWall() || !canSee(mobSummonSpot, target)) {
			summonPos = mob.blockPosition();
		}
		((ServerLevel) mob.level()).addFreshEntityWithPassengers(mobSummonSpot);
		PositionUtils.moveToCorrectHeight(mobSummonSpot);

		EntityType<?> entityType = getEntityType();

		Mob summonedMob = null;

		Entity entity = SummonHelper.summonEntity(mob, mobSummonSpot.blockPosition(),
				entityType);

		if (entity == null) {
			mobSummonSpot.remove(RemovalReason.DISCARDED);
			return;
		}

		if (entity instanceof Mob) {
			summonedMob = ((Mob) entity);
		}

		summonedMob.setTarget(target);
		summonedMob.finalizeSpawn(((ServerLevel) mob.level()),
				mob.level().getCurrentDifficultyAt(summonPos),
				MobSpawnType.MOB_SUMMONED, null);
		if (summonPrepSound.isPresent())
			mobSummonSpot.playSound(summonPrepSound.get(), 1.0F, 1.0F);
		if (mob.getTeam() != null) {
			Scoreboard scoreboard = mob.level().getScoreboard();
			scoreboard.addPlayerToTeam(summonedMob.getScoreboardName(),
					scoreboard.getPlayerTeam(mob.getTeam().getName()));
		}
		mobSummonSpot.summonedEntity = summonedMob;
	}

	/**
	 * {@return} an {@link EntityType} from the list of possible entity types at
	 * random.
	 * If we cannot do this, we will instead use the backup entity type.
	 */
	private EntityType<?> getEntityType() {
		EntityType<?> entityType = null;
		List<String> necromancerMobSummons = summonList;
		if (!necromancerMobSummons.isEmpty()) {
			Collections.shuffle(necromancerMobSummons);

			int randomIndex = mob.getRandom().nextInt(necromancerMobSummons.size());
			String randomMobID = necromancerMobSummons.get(randomIndex);
			entityType = BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.parse(randomMobID));
		}
		if (entityType == null) {
			entityType = backupEntityType;
		}
		return entityType;
	}

	@Override
	public void stop() {
		this.nextUseTime = mob.tickCount + (200 + mob.getRandom().nextInt(200));
	}

	private boolean canSee(Entity entitySeeing, Entity pEntity) {
		Vec3 vector3d = new Vec3(entitySeeing.getX(), entitySeeing.getEyeY(), entitySeeing.getZ());
		Vec3 vector3d1 = new Vec3(pEntity.getX(), pEntity.getEyeY(), pEntity.getZ());
		if (pEntity.level() != entitySeeing.level()
				|| vector3d1.distanceToSqr(vector3d) > 128.0D * 128.0D)
			return false; // Forge Backport MC-209819
		return entitySeeing.level()
				.clip(new ClipContext(vector3d, vector3d1, ClipContext.Block.COLLIDER,
						ClipContext.Fluid.NONE, entitySeeing))
				.getType() == HitResult.Type.MISS;
	}

	/**
	 * Does nothing by default, but it could be overridden to make the summoner
	 * stare
	 * at its target.
	 */
	protected void tickStareHook() {
	}

	/**
	 * {@return} true if we should run tickBody()
	 */
	protected abstract boolean tickCondition();

	/**
	 * {@return} the {@link AnimationTimer} associated with this goal.
	 */
	protected abstract AnimationTimer timer();
}
