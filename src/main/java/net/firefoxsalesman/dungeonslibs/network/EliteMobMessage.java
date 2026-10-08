package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.network.client.ClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class EliteMobMessage implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<EliteMobMessage> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "elite_mob"));
	public static final StreamCodec<FriendlyByteBuf, EliteMobMessage> STREAM_CODEC =
			StreamCodec.of((buf, msg) -> msg.encode(buf), EliteMobMessage::decode);

	private final int entityId;
	private final boolean isElite;
	private final ResourceLocation texture;

	public EliteMobMessage(int entityId, boolean isElite, ResourceLocation texture) {
		this.entityId = entityId;
		this.isElite = isElite;
		this.texture = texture;
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(this.entityId);
		buffer.writeBoolean(isElite);
		buffer.writeResourceLocation(texture);
	}

	public static EliteMobMessage decode(FriendlyByteBuf buffer) {
		int entityId = buffer.readInt();
		boolean isElite = buffer.readBoolean();
		ResourceLocation texture = buffer.readResourceLocation();

		return new EliteMobMessage(entityId, isElite, texture);
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ClientHandler.handleEliteMobMessage(this, ctx);
	}

	public int getEntityId() {
		return entityId;
	}

	public boolean isElite() {
		return isElite;
	}

	public ResourceLocation getTexture() {
		return texture;
	}
}
