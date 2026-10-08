package net.firefoxsalesman.dungeonslibs.capabilities.timers;

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
import java.util.Optional;

public class AttacherTimers {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<Timers>> TIMERS =
			ATTACHMENT_TYPES.register("timers", () -> AttachmentType.builder(Timers::new)
					.serialize(new IAttachmentSerializer<CompoundTag, Timers>() {
						@Override
						public Timers read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							Timers timers = new Timers();
							timers.deserializeNBT(provider, tag);
							return timers;
						}

						@Override
						public CompoundTag write(Timers timers, HolderLookup.Provider provider) {
							return timers.serializeNBT(provider);
						}
					})
					.build());

	// attachments attach lazily; gate to living entities at the call site
	public static Optional<Timers> get(Entity entity) {
		return entity instanceof LivingEntity ? Optional.of(entity.getData(TIMERS.get())) : Optional.empty();
	}
}
