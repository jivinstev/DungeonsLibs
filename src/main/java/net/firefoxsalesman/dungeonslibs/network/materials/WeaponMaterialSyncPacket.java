package net.firefoxsalesman.dungeonslibs.network.materials;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.items.materials.weapon.DungeonsWeaponMaterial;
import net.firefoxsalesman.dungeonslibs.items.materials.weapon.WeaponMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Tier;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

import java.util.stream.Collectors;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public class WeaponMaterialSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<WeaponMaterialSyncPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath("dungeonslibs", "weapon_material_sync"));
	public static final StreamCodec<FriendlyByteBuf, WeaponMaterialSyncPacket> STREAM_CODEC = StreamCodec.of(
			(buf, packet) -> packet.encode(buf), WeaponMaterialSyncPacket::decode);

	private static final Codec<Map<ResourceLocation, Tier>> MAPPER = Codec.unboundedMap(ResourceLocation.CODEC,
			DungeonsWeaponMaterial.CODEC);

	public final Map<ResourceLocation, Tier> data;

	public WeaponMaterialSyncPacket(Map<ResourceLocation, Tier> data) {
		this.data = data.entrySet().stream().filter(entry -> entry.getValue() instanceof DungeonsWeaponMaterial)
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, data).result()
				.orElse(new CompoundTag())));
	}

	public static WeaponMaterialSyncPacket decode(FriendlyByteBuf buffer) {
		return new WeaponMaterialSyncPacket(
				MAPPER.parse(NbtOps.INSTANCE, buffer.readNbt()).result().orElse(new HashMap<>()));
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(this::handlePacketOnMainThread);
	}

	private void handlePacketOnMainThread() {
		WeaponMaterials.WEAPON_MATERIALS.setData(data);
		reloadAllItems();
	}
}
