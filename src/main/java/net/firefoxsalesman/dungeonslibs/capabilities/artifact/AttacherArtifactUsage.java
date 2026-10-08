package net.firefoxsalesman.dungeonslibs.capabilities.artifact;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.Optional;

public class AttacherArtifactUsage {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	private static final IAttachmentSerializer<CompoundTag, ArtifactUsage> SERIALIZER = new IAttachmentSerializer<>() {
		@Override
		public ArtifactUsage read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
			ArtifactUsage usage = new ArtifactUsage();
			usage.deserializeNBT(provider, tag);
			return usage;
		}

		@Override
		public CompoundTag write(ArtifactUsage usage, HolderLookup.Provider provider) {
			return usage.serializeNBT(provider);
		}
	};

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<ArtifactUsage>> ARTIFACT_USAGE =
			ATTACHMENT_TYPES.register("artifact_usage",
					() -> AttachmentType.builder(() -> new ArtifactUsage())
							.serialize(SERIALIZER)
							.build());

	// only players carry artifact usage; other holders yield empty
	public static Optional<ArtifactUsage> get(Entity entity) {
		if (entity instanceof Player) {
			return Optional.of(entity.getData(ARTIFACT_USAGE.get()));
		}
		return Optional.empty();
	}
}
