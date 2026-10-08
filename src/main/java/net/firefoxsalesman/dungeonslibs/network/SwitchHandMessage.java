package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.combat.DualWieldHandler;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SwitchHandMessage() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<SwitchHandMessage> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, "switch_hand"));
	public static final StreamCodec<FriendlyByteBuf, SwitchHandMessage> STREAM_CODEC = StreamCodec
			.unit(new SwitchHandMessage());

	public SwitchHandMessage() {
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public void handle(IPayloadContext ctx) {
		ctx.enqueueWork(() -> {
			if (ctx.player() instanceof ServerPlayer player) {
				DualWieldHandler.switchHand(player);
			}
		});
	}
}
