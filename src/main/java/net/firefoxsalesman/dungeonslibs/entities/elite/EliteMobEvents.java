package net.firefoxsalesman.dungeonslibs.entities.elite;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import com.google.common.collect.ImmutableMultimap;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMobHelper;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.network.EliteMobMessage;
import net.minecraft.client.model.EntityModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.chunk.LevelChunk;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.event.RenderLivingEvent;
import net.neoforged.neoforge.event.entity.EntityEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.living.LivingConversionEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

import static java.util.UUID.randomUUID;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID)
public class EliteMobEvents {
	public static final float SIZE_ADJUSTMENT = 1.1F;

	@SubscribeEvent
	public static void onEntityJoin(EntityJoinLevelEvent event) {
		Level level = event.getLevel();
		if (!level.isClientSide() && event.getEntity() instanceof LivingEntity entity
				&& DungeonsLibrariesConfig.ENABLE_ELITE_MOBS.get()) {
			makeEliteChance(level, entity);
		}
	}

	public static void makeEliteChance(Level level, LivingEntity entity) {
		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		EliteMobConfig config = EliteMobConfigRegistry.getRandomConfig(
				BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), entity.getRandom());
		if (!cap.hasSpawned() && config != null) {
			LevelChunk chunk = level.getChunkSource().getChunkNow(entity.blockPosition().getX() >> 4,
					entity.blockPosition().getZ() >> 4);
			if (chunk != null && chunk.getPersistedStatus().isOrAfter(ChunkStatus.FULL)
					&& entity.getRandom()
							.nextFloat() < DungeonsLibrariesConfig.ELITE_MOBS_BASE_CHANCE
									.get()
									* level.getCurrentDifficultyAt(
											entity.blockPosition())
											.getSpecialMultiplier()) {
				makeElite(entity, config);
			}
		}
		cap.setHasSpawned(true);
	}

	public static void makeElite(LivingEntity entity) {
		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		EliteMobConfig config = EliteMobConfigRegistry.getRandomConfig(
				BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()), entity.getRandom());
		if (!cap.hasSpawned() && config != null) {
			makeElite(entity, config);
		}
		cap.setHasSpawned(true);
	}

	private static void makeElite(LivingEntity entity, EliteMobConfig config) {
		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		setItemSlot(entity, EquipmentSlot.HEAD, config.getHeadItem());
		setItemSlot(entity, EquipmentSlot.CHEST, config.getChestItem());
		setItemSlot(entity, EquipmentSlot.LEGS, config.getLegsItem());
		setItemSlot(entity, EquipmentSlot.FEET, config.getFeetItem());
		setItemSlot(entity, EquipmentSlot.MAINHAND, config.getHandItem());
		setItemSlot(entity, EquipmentSlot.OFFHAND, config.getOffhandItem());
		ImmutableMultimap.Builder<Holder<Attribute>, AttributeModifier> builder = ImmutableMultimap.builder();
		config.getAttributes().forEach(attributeModifier -> {
			BuiltInRegistries.ATTRIBUTE.getHolder(attributeModifier.getAttributeResourceLocation()).ifPresent(attribute -> {
				builder.put(attribute, new AttributeModifier(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_modifier"),
						attributeModifier.getAmount(), attributeModifier.getOperation()));
			});
		});
		entity.getAttributes().addTransientAttributeModifiers(builder.build());
		cap.setElite(true);
		cap.setTexture(config.getTexture());
	}

	private static void setItemSlot(LivingEntity entity, EquipmentSlot slotType, ItemStack item) {
		if (!item.equals(ItemStack.EMPTY)) {
			entity.setItemSlot(slotType, item);
		}
	}

	@SubscribeEvent
	public static void onEntityEventSize(EntityEvent.Size event) {
		Entity entity = event.getEntity();

		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		if (cap.isElite()) {
			float totalWidth = event.getNewSize().width() * SIZE_ADJUSTMENT;
			float totalHeight = event.getNewSize().height() * SIZE_ADJUSTMENT;
			event.setNewSize(EntityDimensions.fixed(totalWidth, totalHeight)
					.withEyeHeight(event.getNewSize().eyeHeight() * SIZE_ADJUSTMENT));
		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderLivingEventPre(
			RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
		final LivingEntity entity = event.getEntity();
		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		if (cap.isElite()) {
			event.getPoseStack().pushPose();
			event.getPoseStack().scale(SIZE_ADJUSTMENT, SIZE_ADJUSTMENT, SIZE_ADJUSTMENT);

		}
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void onRenderLivingEventPost(
			RenderLivingEvent.Post<LivingEntity, EntityModel<LivingEntity>> event) {
		final LivingEntity entity = event.getEntity();
		EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
		if (cap.isElite()) {
			event.getPoseStack().popPose();
		}
	}

	@SubscribeEvent
	public static void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		Player player = event.getEntity();
		Entity target = event.getTarget();
		if (player instanceof ServerPlayer && target instanceof LivingEntity) {
			EliteMob cap = EliteMobHelper.getEliteMobCapability(event.getTarget());
			if (cap.isElite()) {
				PacketDistributor.sendToPlayer((ServerPlayer) player,
						new EliteMobMessage(target.getId(), cap.isElite(), cap.getTexture()));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingConvert(LivingConversionEvent.Post event) {
		EliteMob cap = EliteMobHelper.getEliteMobCapability(event.getEntity());
		EliteMob outcomeCap = EliteMobHelper.getEliteMobCapability(event.getOutcome());
		outcomeCap.setHasSpawned(true);
		if (cap.isElite()) {
			outcomeCap.setElite(true);
		}
	}
}
