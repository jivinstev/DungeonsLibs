package net.firefoxsalesman.dungeonslibs.capabilities.minionmaster;

import java.util.Optional;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttacherFollower {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Follower>> FOLLOWER =
			ATTACHMENT_TYPES.register("minion", () -> AttachmentType.builder(() -> new Follower())
					.serialize(new IAttachmentSerializer<CompoundTag, Follower>() {
						@Override
						public Follower read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							Follower follower = new Follower();
							follower.deserializeNBT(provider, tag);
							return follower;
						}

						@Override
						public CompoundTag write(Follower follower, HolderLookup.Provider provider) {
							return follower.serializeNBT(provider);
						}
					})
					.build());

	// only living entities carry the follower data
	public static Optional<Follower> get(Entity entity) {
		return entity instanceof LivingEntity ? Optional.of(entity.getData(FOLLOWER)) : Optional.empty();
	}
}
