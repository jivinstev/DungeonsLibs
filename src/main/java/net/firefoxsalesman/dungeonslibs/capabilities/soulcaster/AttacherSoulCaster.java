package net.firefoxsalesman.dungeonslibs.capabilities.soulcaster;

import java.util.Optional;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttacherSoulCaster {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<SoulCaster>> SOUL_CASTER =
			ATTACHMENT_TYPES.register("soul_caster", () -> AttachmentType.builder(holder -> new SoulCaster())
					.serialize(new IAttachmentSerializer<CompoundTag, SoulCaster>() {
						@Override
						public SoulCaster read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							SoulCaster caster = new SoulCaster();
							caster.deserializeNBT(provider, tag);
							return caster;
						}

						@Override
						public CompoundTag write(SoulCaster attachment, HolderLookup.Provider provider) {
							return attachment.serializeNBT(provider);
						}
					})
					.build());

	// only living entities carry the soul caster
	public static Optional<SoulCaster> get(Object holder) {
		if (holder instanceof LivingEntity living) {
			return Optional.of(living.getData(SOUL_CASTER.get()));
		}
		return Optional.empty();
	}
}
