package net.firefoxsalesman.dungeonslibs.integration.curios.client.message;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.network.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CuriosArtifactStopMessage implements CustomPacketPayload {

	public static final CustomPacketPayload.Type<CuriosArtifactStopMessage> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, "curios_artifact_stop"));
	public static final StreamCodec<FriendlyByteBuf, CuriosArtifactStopMessage> STREAM_CODEC =
			StreamCodec.of((buf, packet) -> packet.encode(buf), CuriosArtifactStopMessage::decode);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public CuriosArtifactStopMessage() {
	}

	public void encode(FriendlyByteBuf buf) {

	}

	public static CuriosArtifactStopMessage decode(FriendlyByteBuf buf) {
		return new CuriosArtifactStopMessage();
	}

	public void handle(IPayloadContext ctx) {
		ClientHandler.handleCuriosArtifactStopMessage(this, ctx);
	}

}
