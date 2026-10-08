package net.firefoxsalesman.dungeonslibs.network.materials;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterial;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterials;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public class ArmorMaterialSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ArmorMaterialSyncPacket> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_material_sync"));
	public static final StreamCodec<FriendlyByteBuf, ArmorMaterialSyncPacket> STREAM_CODEC = StreamCodec.ofMember(ArmorMaterialSyncPacket::toBytes, ArmorMaterialSyncPacket::new);

	private static final Codec<Map<ResourceLocation, DungeonsArmorMaterial>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, DungeonsArmorMaterial.CODEC);

	public final Map<ResourceLocation, DungeonsArmorMaterial> data;

	public ArmorMaterialSyncPacket(FriendlyByteBuf buffer) {
		this(MAPPER.parse(NbtOps.INSTANCE, buffer.readNbt()).result().orElse(new HashMap<>()));
	}

	public ArmorMaterialSyncPacket(Map<ResourceLocation, DungeonsArmorMaterial> data) {
		this.data = data.entrySet().stream()
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
	}

	public void toBytes(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, data).result()
				.orElse(new CompoundTag())));
	}

	public static ArmorMaterialSyncPacket decode(FriendlyByteBuf buffer) {
		return new ArmorMaterialSyncPacket(
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
		DungeonsArmorMaterials.ARMOR_MATERIALS.setData(data);
		reloadAllItems();
	}
}
