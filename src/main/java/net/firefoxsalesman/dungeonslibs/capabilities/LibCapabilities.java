package net.firefoxsalesman.dungeonslibs.capabilities;

import net.neoforged.bus.api.IEventBus;

import net.firefoxsalesman.dungeonslibs.capabilities.artifact.AttacherArtifactUsage;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.AttacherBuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.AttacherEliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile.AttacherEnchantedProjectile;
import net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.AttacherFollower;
import net.firefoxsalesman.dungeonslibs.capabilities.playerrewards.AttacherPlayerRewards;
import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.AttacherSoulCaster;
import net.firefoxsalesman.dungeonslibs.capabilities.timers.AttacherTimers;
import net.firefoxsalesman.dungeonslibs.capabilities.builtinenchantments.BuiltInEnchantments;
import net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile.EnchantedProjectile;
import net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.Follower;
import net.firefoxsalesman.dungeonslibs.capabilities.minionmaster.Leader;
import net.firefoxsalesman.dungeonslibs.capabilities.timers.Timers;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.capabilities.ItemCapability;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class LibCapabilities {

	public static final String MODID = "dungeonslibs";

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister
			.create(NeoForgeRegistries.ATTACHMENT_TYPES, MODID);

	public static final ItemCapability<BuiltInEnchantments, Void> BUILT_IN_ENCHANTMENTS_CAPABILITY = ItemCapability
			.createVoid(ResourceLocation.fromNamespaceAndPath(MODID, "built_in_enchantments"),
					BuiltInEnchantments.class);

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Leader>> LEADER_ATTACHMENT = ATTACHMENTS
			.register("master", () -> AttachmentType.builder(() -> new Leader())
					.serialize(nbtSerializer(() -> new Leader())).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Follower>> FOLLOWER_ATTACHMENT = ATTACHMENTS
			.register("follower_state", () -> AttachmentType.builder(() -> new Follower()).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Timers>> TIMERS_ATTACHMENT = ATTACHMENTS
			.register("lib_timers", () -> AttachmentType.builder(() -> new Timers())
					.serialize(nbtSerializer(() -> new Timers())).build());

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<EnchantedProjectile>> ENCHANTED_PROJECTILE_ATTACHMENT = ATTACHMENTS
			.register("lib_enchanted_projectile",
					() -> AttachmentType.builder(() -> new EnchantedProjectile())
							.serialize(new IAttachmentSerializer<CompoundTag, EnchantedProjectile>() {
								@Override
								public EnchantedProjectile read(IAttachmentHolder holder, CompoundTag tag,
										HolderLookup.Provider provider) {
									EnchantedProjectile projectile = new EnchantedProjectile();
									projectile.deserializeNBT(provider, tag);
									return projectile;
								}

								@Override
								public CompoundTag write(EnchantedProjectile attachment,
										HolderLookup.Provider provider) {
									return attachment.serializeNBT(provider);
								}
							}).build());

	private static <T extends INBTSerializable<CompoundTag>> IAttachmentSerializer<CompoundTag, T> nbtSerializer(
			Supplier<T> factory) {
		return new IAttachmentSerializer<CompoundTag, T>() {
			@Override
			public T read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
				T value = factory.get();
				value.deserializeNBT(provider, tag);
				return value;
			}

			@Override
			public CompoundTag write(T attachment, HolderLookup.Provider provider) {
				return attachment.serializeNBT(provider);
			}
		};
	}

	/** Registers every attachment type DeferredRegister on the mod event bus. */
	public static void setupCapabilities(IEventBus modEventBus) {
		ATTACHMENTS.register(modEventBus);
		modEventBus.addListener(AttacherBuiltInEnchantments::register);
		AttacherTimers.ATTACHMENT_TYPES.register(modEventBus);
		AttacherArtifactUsage.ATTACHMENT_TYPES.register(modEventBus);
		AttacherFollower.ATTACHMENT_TYPES.register(modEventBus);
		AttacherSoulCaster.ATTACHMENT_TYPES.register(modEventBus);
		AttacherEnchantedProjectile.ATTACHMENT_TYPES.register(modEventBus);
		AttacherPlayerRewards.ATTACHMENT_TYPES.register(modEventBus);
		AttacherEliteMob.ATTACHMENT_TYPES.register(modEventBus);
	}
}
