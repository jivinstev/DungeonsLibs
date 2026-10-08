package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.BowGearConfigRegistry;
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

public class BowGearConfigSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<BowGearConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, "bow_gear_config_sync"));
	public static final StreamCodec<FriendlyByteBuf, BowGearConfigSyncPacket> STREAM_CODEC = StreamCodec
			.ofMember(BowGearConfigSyncPacket::encode, BowGearConfigSyncPacket::decode);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	private static final Codec<Map<ResourceLocation, BowGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, BowGearConfig.CODEC);

	public final Map<ResourceLocation, BowGearConfig> data;

	public BowGearConfigSyncPacket(Map<ResourceLocation, BowGearConfig> data) {
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, data).result()
				.orElse(new CompoundTag())));
	}

	public static BowGearConfigSyncPacket decode(FriendlyByteBuf buffer) {
		return new BowGearConfigSyncPacket(
				MAPPER.parse(NbtOps.INSTANCE, buffer.readNbt()).result().orElse(new HashMap<>()));
	}

	public void handle(IPayloadContext context) {
		context.enqueueWork(this::handlePacketOnMainThread);
	}

	private void handlePacketOnMainThread() {
		BowGearConfigRegistry.BOW_GEAR_CONFIGS.setData(data);
		reloadAllItems();
	}
}
