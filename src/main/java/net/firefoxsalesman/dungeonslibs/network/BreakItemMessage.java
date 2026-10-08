package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.client.network.BreakItemClientHandler;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public class BreakItemMessage implements CustomPacketPayload {
	private final ItemStack stack;
	private final int entityID;

	public BreakItemMessage(int entityID, ItemStack stack) {
		this.stack = stack;
		this.entityID = entityID;
	}

	public static final CustomPacketPayload.Type<BreakItemMessage> TYPE =
			new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("dungeonslibs", "break_item"));

	public static final StreamCodec<RegistryFriendlyByteBuf, BreakItemMessage> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, p -> p.entityID,
			ItemStack.OPTIONAL_STREAM_CODEC, p -> p.stack,
			BreakItemMessage::new);

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static class BreakItemHandler {
		public static void handle(BreakItemMessage packet, IPayloadContext ctx) {
			if (packet != null) {
				ctx.enqueueWork(() -> {
					if (FMLEnvironment.dist == Dist.CLIENT) {
						BreakItemClientHandler.run(packet.entityID, packet.stack);
					}
				});
			}
		}
	}

}
