package net.firefoxsalesman.dungeonslibs.network;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class BuiltInEnchantmentsMessage {
	private final int entityId;
	private final ResourceLocation resourceLocation;
	private final List<EnchantmentInstance> enchantmentInstanceList;

	public BuiltInEnchantmentsMessage(int entityId, ResourceLocation resourceLocation,
			List<EnchantmentInstance> enchantmentInstanceList) {
		this.entityId = entityId;
		this.resourceLocation = resourceLocation;
		this.enchantmentInstanceList = enchantmentInstanceList;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
		buffer.writeResourceLocation(resourceLocation);
		buffer.writeVarInt(enchantmentInstanceList.size());
		this.enchantmentInstanceList.forEach(enchantmentInstance -> {
			buffer.writeResourceLocation(enchantmentInstance.enchantment.unwrapKey().orElseThrow().location());
			buffer.writeInt(enchantmentInstance.level);
		});
	}

	public static BuiltInEnchantmentsMessage decode(RegistryFriendlyByteBuf buffer) {
		int entityId = buffer.readInt();
		ResourceLocation resourceLocation = buffer.readResourceLocation();
		List<EnchantmentInstance> enchantmentInstance = new ArrayList<>();
		int length = buffer.readVarInt();
		HolderLookup.RegistryLookup<Enchantment> lookup = buffer.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		for (int x = 0; x < length; x++) {
			ResourceKey<Enchantment> key = ResourceKey.create(Registries.ENCHANTMENT, buffer.readResourceLocation());
			Holder<Enchantment> holder = lookup.getOrThrow(key);
			enchantmentInstance.add(new EnchantmentInstance(holder, buffer.readInt()));
		}

		return new BuiltInEnchantmentsMessage(entityId, resourceLocation, enchantmentInstance);
	}

	public static boolean onPacketReceived(BuiltInEnchantmentsMessage message,
			IPayloadContext context) {
		/*
		 * NetworkEvent.Context context = contextSupplier.get();
		 * if (context.getDirection().getReceptionSide() == LogicalSide.CLIENT) {
		 * context.enqueueWork(() -> {
		 * Entity entity =
		 * Minecraft.getInstance().player.level.getEntity(message.entityId);
		 * if (entity instanceof LivingEntity) {
		 * getEnchantableCapabilityLazy(entity).ifPresent(iEnchantable -> {
		 * iEnchantable.clearAllEnchantments();
		 * message.enchantmentInstanceList.forEach(iEnchantable::addEnchantment);
		 * });
		 * }
		 * });
		 * }
		 */
		return true;
	}
}
