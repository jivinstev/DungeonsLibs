package net.firefoxsalesman.dungeonslibs.integration.curios.client.message;

import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactUseContext;
import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.ICurioStacksHandler;

import java.util.Optional;

public class CuriosArtifactStartMessage implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<CuriosArtifactStartMessage> TYPE = new CustomPacketPayload.Type<>(
			ResourceLocation.fromNamespaceAndPath(DungeonsLibs.MOD_ID, "curios_artifact_start"));
	public static final StreamCodec<FriendlyByteBuf, CuriosArtifactStartMessage> STREAM_CODEC = StreamCodec
			.of((buf, packet) -> encode(packet, buf), CuriosArtifactStartMessage::decode);

	private final int slot;
	private final BlockHitResult hitResult;

	public CuriosArtifactStartMessage(int slot, BlockHitResult hitResult) {
		this.slot = slot;
		this.hitResult = hitResult;
	}

	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}

	public static void encode(CuriosArtifactStartMessage packet, FriendlyByteBuf buf) {
		buf.writeInt(packet.slot);
		buf.writeBlockHitResult(packet.hitResult);
	}

	public static CuriosArtifactStartMessage decode(FriendlyByteBuf buf) {
		return new CuriosArtifactStartMessage(buf.readInt(), buf.readBlockHitResult());
	}

	public static class CuriosArtifactHandler {
		public static void handle(CuriosArtifactStartMessage packet, IPayloadContext ctx) {
			if (packet != null) {
				ctx.enqueueWork(() -> {
					ServerPlayer player = ctx.player() instanceof ServerPlayer serverPlayer ? serverPlayer : null;
					if (player != null) {
						CuriosApi.getCuriosHelper().getCuriosHandler(player)
								.ifPresent(iCuriosItemHandler -> {
									Optional<ICurioStacksHandler> artifactStackHandler = iCuriosItemHandler
											.getStacksHandler("artifact");
									if (artifactStackHandler.isPresent()) {
										ItemStack artifact = artifactStackHandler
												.get().getStacks()
												.getStackInSlot(packet.slot);
										if (!artifact.isEmpty() && artifact
												.getItem() instanceof ArtifactItem) {
											ArtifactUseContext iuc = new ArtifactUseContext(
													player.level(),
													player,
													artifact,
													packet.hitResult);
											((ArtifactItem) artifact
													.getItem())
													.activateArtifact(
															iuc);
										}
									}
								});
					}

				});
			}
		}
	}
}
