package net.firefoxsalesman.dungeonslibs.entities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.Animation.LoopType;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class SummonSpotEntity extends Entity implements GeoEntity {

	private static final EntityDataAccessor<Integer> SUMMON_TYPE = SynchedEntityData.defineId(
			SummonSpotEntity.class,
			EntityDataSerializers.INT);

	public int lifeTime = 0;

	public Entity summonedEntity = null;

	AnimatableInstanceCache factory = GeckoLibUtil.createInstanceCache(this);

	public int mobSpawnRotation;

	public SummonSpotEntity(Level worldIn) {
		super(LibEntityTypes.SUMMON_SPOT.get(), worldIn);
	}

	public SummonSpotEntity(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Override
	public void baseTick() {
		super.baseTick();

		lifeTime++;

		if (!level().isClientSide && lifeTime == getSummonTime() && summonedEntity != null) {
			summonedEntity.moveTo(blockPosition(), 0.0F, 0.0F);
			summonedEntity.setYBodyRot(random.nextInt(360));
			((ServerLevel) level()).addFreshEntityWithPassengers(summonedEntity);
		}

		if (!level().isClientSide && lifeTime >= getDespawnTime()) {
			remove(RemovalReason.DISCARDED);
		}
	}

	@Override
	protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
		builder.define(SUMMON_TYPE, 0);
	}

	@Override
	protected void readAdditionalSaveData(CompoundTag p_70037_1_) {
		setSummonType(p_70037_1_.getInt("SummonType"));
	}

	@Override
	protected void addAdditionalSaveData(CompoundTag p_213281_1_) {
		p_213281_1_.putInt("SummonType", getSummonType());
	}

	public int getSummonType() {
		return Mth.clamp(entityData.get(SUMMON_TYPE), 0, 3);
	}

	public void setSummonType(int attached) {
		entityData.set(SUMMON_TYPE, attached);
	}

	public int getDespawnTime() {
		if (getSummonType() == 0) {
			return 18;
		} else if (getSummonType() == 1) {
			return 18;
		} else if (getSummonType() == 2) {
			return 18;
		} else if (getSummonType() == 3) {
			return 18;
		} else {
			return 2;
		}
	}

	public int getSummonTime() {
		if (getSummonType() == 0) {
			return 10;
		} else if (getSummonType() == 1) {
			return 10;
		} else if (getSummonType() == 2) {
			return 10;
		} else if (getSummonType() == 3) {
			return 10;
		} else {
			return 1;
		}
	}

	@Override
	public void registerControllers(ControllerRegistrar controllers) {
		controllers.add(new AnimationController<GeoAnimatable>(this, "controller", 1, this::predicate));
	}

	private <P extends GeoAnimatable> PlayState predicate(AnimationState<P> event) {
		if (getSummonType() == 0) {
			event.getController().setAnimation(
					RawAnimation.begin().then("illusioner_summon_spot_summon", LoopType.LOOP));
		} else if (getSummonType() == 1) {
			event.getController().setAnimation(
					RawAnimation.begin().then("wildfire_summon_spot_summon", LoopType.LOOP));
		} else if (getSummonType() == 2) {
			event.getController().setAnimation(
					RawAnimation.begin().then("illusioner_summon_spot_summon", LoopType.LOOP));
		} else if (getSummonType() == 3) {
			event.getController().setAnimation(
					RawAnimation.begin().then("illusioner_summon_spot_summon", LoopType.LOOP));
		} else {

		}
		return PlayState.CONTINUE;
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return factory;
	}

}
