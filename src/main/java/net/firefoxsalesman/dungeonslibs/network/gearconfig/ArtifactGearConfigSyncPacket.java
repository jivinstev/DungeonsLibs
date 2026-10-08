package net.firefoxsalesman.dungeonslibs.network.gearconfig;

import com.mojang.serialization.Codec;

import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfigRegistry;
import net.firefoxsalesman.dungeonslibs.items.artifacts.config.ArtifactGearConfig;
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

public class ArtifactGearConfigSyncPacket implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ArtifactGearConfigSyncPacket> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath("dungeonslibs", "artifact_gear_config_sync"));
	public static final StreamCodec<FriendlyByteBuf, ArtifactGearConfigSyncPacket> STREAM_CODEC = StreamCodec
			.of((buffer, packet) -> packet.encode(buffer), ArtifactGearConfigSyncPacket::decode);

	private static final Codec<Map<ResourceLocation, ArtifactGearConfig>> MAPPER = Codec
			.unboundedMap(ResourceLocation.CODEC, ArtifactGearConfig.CODEC);

	public final Map<ResourceLocation, ArtifactGearConfig> data;

	public ArtifactGearConfigSyncPacket(Map<ResourceLocation, ArtifactGearConfig> data) {
		this.data = data;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeNbt((CompoundTag) (MAPPER.encodeStart(NbtOps.INSTANCE, this.data).result()
				.orElse(new CompoundTag())));
	}

	public static ArtifactGearConfigSyncPacket decode(FriendlyByteBuf buffer) {
		return new ArtifactGearConfigSyncPacket(
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
		ArtifactGearConfigRegistry.ARTIFACT_GEAR_CONFIGS.setData(this.data);
		reloadAllItems();
	}
}
