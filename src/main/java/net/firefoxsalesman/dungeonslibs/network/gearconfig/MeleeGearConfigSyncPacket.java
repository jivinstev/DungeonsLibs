package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfig;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.MeleeGearConfigRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.HashMap;
import java.util.Map;

import static net.firefoxsalesman.dungeonslibs.items.GearConfigReloadListener.reloadAllItems;

public class MeleeGearConfigSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<MeleeGearConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, "melee_gear_config_sync"));
	public static final StreamCodec<FriendlyByteBuf, MeleeGearConfigSyncPacket> STREAM_CODEC = StreamCodec.of(
			(buffer, packet) -> packet.encode(buffer), MeleeGearConfigSyncPacket::decode);

	private static final Codec<Map<ResourceLocation, MeleeGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, MeleeGearConfig.CODEC);

	public final Map<ResourceLocation, MeleeGearConfig> data;

	public MeleeGearConfigSyncPacket(Map<ResourceLocation, MeleeGearConfig> data) {
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, data).result()
				.orElse(new CompoundTag())));
	}

	public static MeleeGearConfigSyncPacket decode(FriendlyByteBuf buffer) {
		return new MeleeGearConfigSyncPacket(
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
		MeleeGearConfigRegistry.MELEE_GEAR_CONFIGS.setData(data);
		reloadAllItems();
	}
}
