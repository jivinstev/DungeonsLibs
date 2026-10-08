package net.firefoxsalesman.dungeonslibs.capabilities.enchantedprojectile;

import java.util.Optional;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttacherEnchantedProjectile {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final DeferredHolder<AttachmentType<?>, AttachmentType<EnchantedProjectile>> ENCHANTED_PROJECTILE = ATTACHMENT_TYPES.register("enchanted_projectile",
			() -> AttachmentType.builder(EnchantedProjectile::new)
					.serialize(new IAttachmentSerializer<CompoundTag, EnchantedProjectile>() {
						@Override
						public EnchantedProjectile read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							EnchantedProjectile data = new EnchantedProjectile();
							data.deserializeNBT(provider, tag);
							return data;
						}

						@Override
						public CompoundTag write(EnchantedProjectile data, HolderLookup.Provider provider) {
							return data.serializeNBT(provider);
						}
					})
					.build());

	/** Attachments attach lazily, so the Projectile gate lives here instead of in an attach event. */
	public static Optional<EnchantedProjectile> get(Entity entity) {
		return entity instanceof Projectile ? Optional.of(entity.getData(ENCHANTED_PROJECTILE.get())) : Optional.empty();
	}
}
