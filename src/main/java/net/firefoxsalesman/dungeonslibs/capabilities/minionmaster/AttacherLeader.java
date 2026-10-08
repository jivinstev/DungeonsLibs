package net.firefoxsalesman.dungeonslibs.capabilities.minionmaster;

import java.util.Optional;

import net.firefoxsalesman.dungeonslibs.capabilities.LibCapabilities;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

public class AttacherLeader {

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Leader>> LEADER = LibCapabilities.ATTACHMENTS.register("master", () -> AttachmentType.builder(() -> new Leader()).serialize(new LeaderSerializer()).build());

	private static final class LeaderSerializer implements IAttachmentSerializer<CompoundTag, Leader> {

		@Override
		public Leader read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
			Leader leader = new Leader();
			leader.deserializeNBT(provider, tag);
			return leader;
		}

		@Override
		public CompoundTag write(Leader attachment, HolderLookup.Provider provider) {
			return attachment.serializeNBT(provider);
		}
	}

	// only living entities carry the leader data
	public static Optional<Leader> get(Entity entity) {
		return entity instanceof LivingEntity ? Optional.of(entity.getData(LEADER.get())) : Optional.empty();
	}
}
