package net.firefoxsalesman.dungeonslibs.network.client;

import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsage;
import net.firefoxsalesman.dungeonslibs.capabilities.artifact.ArtifactUsageHelper;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMob;
import net.firefoxsalesman.dungeonslibs.capabilities.elite.EliteMobHelper;
import net.firefoxsalesman.dungeonslibs.integration.curios.client.message.CuriosArtifactStopMessage;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.firefoxsalesman.dungeonslibs.network.EliteMobMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.PacketFlow;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class ClientHandler {
	public static void handleCuriosArtifactStopMessage(CuriosArtifactStopMessage packet,
			IPayloadContext context) {
		if (packet != null) {
			if (context.flow() == PacketFlow.CLIENTBOUND) {
				context.enqueueWork(() -> {
					AbstractClientPlayer player = Minecraft.getInstance().player;
					if (player != null) {
						ArtifactUsage cap = ArtifactUsageHelper
								.getArtifactUsageCapability(player);
						ItemStack artifactStack = cap.getUsingArtifact();
						if (artifactStack != null && artifactStack
								.getItem() instanceof ArtifactItem artifactItem) {
							artifactItem.stopUsingArtifact(player);
							cap.stopUsingArtifact();
						}
					}
				});
			}
		}
	}

	public static void handleEliteMobMessage(EliteMobMessage message,
			IPayloadContext context) {
		if (context.flow() == PacketFlow.CLIENTBOUND) {
			context.enqueueWork(() -> {
				Entity entity = Minecraft.getInstance().player.level().getEntity(message.getEntityId());
				if (entity instanceof LivingEntity) {
					EliteMob cap = EliteMobHelper.getEliteMobCapability(entity);
					cap.setElite(message.isElite());
					cap.setTexture(message.getTexture());
					if (cap.isElite()) {
						entity.refreshDimensions();
					}
				}
			});
		}
	}
}
