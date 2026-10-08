package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGearConfigRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public class ArmorGearConfigSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ArmorGearConfigSyncPacket> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "armor_gear_config_sync"));
	public static final StreamCodec<FriendlyByteBuf, ArmorGearConfigSyncPacket> STREAM_CODEC =
			StreamCodec.ofMember(ArmorGearConfigSyncPacket::encode, ArmorGearConfigSyncPacket::decode);

	private static final Codec<Map<ResourceLocation, ArmorGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, ArmorGearConfig.CODEC);

	public final Map<ResourceLocation, ArmorGearConfig> data;

	public ArmorGearConfigSyncPacket(Map<ResourceLocation, ArmorGearConfig> data) {
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, data).result()
				.orElse(new CompoundTag())));
	}

	public static ArmorGearConfigSyncPacket decode(FriendlyByteBuf buffer) {
		return new ArmorGearConfigSyncPacket(
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
		ArmorGearConfigRegistry.ARMOR_GEAR_CONFIGS.setData(data);
		reloadAllItems();
	}
}
