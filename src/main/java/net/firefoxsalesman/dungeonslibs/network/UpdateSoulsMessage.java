package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.client.network.UpdateSoulsClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class UpdateSoulsMessage implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UpdateSoulsMessage> TYPE = new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "update_souls"));
	public static final StreamCodec<FriendlyByteBuf, UpdateSoulsMessage> STREAM_CODEC = StreamCodec.ofMember(UpdateSoulsMessage::toBytes, UpdateSoulsMessage::new);

	private final float newAmount;

	public UpdateSoulsMessage(float souls) {
		this.newAmount = souls;
	}

	public UpdateSoulsMessage(FriendlyByteBuf buf) {
		this.newAmount = buf.readFloat();
	}

	public void toBytes(FriendlyByteBuf buf) {
		buf.writeFloat(this.newAmount);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(UpdateSoulsMessage packet, FriendlyByteBuf buf) {
		buf.writeFloat(packet.newAmount);
	}

	public static UpdateSoulsMessage decode(FriendlyByteBuf buf) {
		return new UpdateSoulsMessage(buf.readFloat());
	}

	public static class UpdateSoulsHandler {
		public static void handle(UpdateSoulsMessage packet, IPayloadContext ctx) {
			if (packet != null) {
				ctx.enqueueWork(() -> {
					if (FMLEnvironment.dist == Dist.CLIENT) {
						UpdateSoulsClientHandler.run(packet.newAmount);
					}
				});
			}
		}
	}
}
