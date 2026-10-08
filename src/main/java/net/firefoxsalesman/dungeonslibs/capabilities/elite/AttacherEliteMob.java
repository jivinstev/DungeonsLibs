package net.firefoxsalesman.dungeonslibs.capabilities.elite;

import java.util.Optional;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttacherEliteMob {

	// Must be registered on the mod event bus by the main mod class: ATTACHMENT_TYPES.register(modEventBus)
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<EliteMob>> ELITE_MOB =
			ATTACHMENT_TYPES.register("elite_mob", () -> AttachmentType.builder(() -> new EliteMob())
					.serialize(new IAttachmentSerializer<CompoundTag, EliteMob>() {
						@Override
						public EliteMob read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							EliteMob mob = new EliteMob();
							mob.deserializeNBT(tag);
							return mob;
						}

						@Override
						public CompoundTag write(EliteMob attachment, HolderLookup.Provider provider) {
							return attachment.serializeNBT();
						}
					})
					.build());

	// Only Mobs carry elite data; the gate that used to live in the attach handler goes here.
	public static Optional<EliteMob> get(Entity entity) {
		return entity instanceof Mob ? Optional.of(entity.getData(ELITE_MOB)) : Optional.empty();
	}
}
