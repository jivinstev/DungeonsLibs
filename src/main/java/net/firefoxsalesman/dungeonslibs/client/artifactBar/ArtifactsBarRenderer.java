package net.firefoxsalesman.dungeonslibs.client.artifactBar;

import static net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper.modLoc;

import java.util.List;
import java.util.Optional;
import com.mojang.blaze3d.systems.RenderSystem;

import net.firefoxsalesman.dungeonslibs.client.CuriosKeyBindings;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.items.artifacts.ArtifactItem;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.LayeredDraw;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.type.inventory.IDynamicStackHandler;

/**
 * Borrowed from Goety. Many thanks to Polarice
 */
public class ArtifactsBarRenderer {
	public static final LayeredDraw.Layer OVERLAY = ArtifactsBarRenderer::drawHUD;
	private static final Minecraft minecraft = Minecraft.getInstance();
	private static final ResourceLocation ARTIFACT_BAR_RESOURCE = modLoc("textures/gui/artifact_bar.png");

	private static Optional<IDynamicStackHandler> getStackHandler() {
		if (minecraft.getCameraEntity() instanceof Player renderPlayer)
			return CuriosApi.getCuriosHelper().getCuriosHandler(renderPlayer)
					.map(iCuriosItemHandler -> iCuriosItemHandler.getStacksHandler("artifact")
							.map(artifactStackHandler -> artifactStackHandler
									.getStacks())
							.get());
		return Optional.empty();
	}

	/**
	 * Return true if an artifact is equipped
	 */
	private static boolean artifactEquipped() {
		Optional<Boolean> result = getStackHandler().map(stacks -> {
			int slots = stacks.getSlots();
			for (int slot = 0; slot < slots; slot++) {
				ItemStack artifact = stacks.getStackInSlot(slot);
				if (!artifact.isEmpty() && artifact.getItem() instanceof ArtifactItem) {
					return true;
				}
			}
			return false;
		});
		if (result.isPresent())
			return result.get();
		return false;
	}

	private static boolean shouldDisplayBar() {
		return artifactEquipped() && minecraft.gameMode != null
				&& minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR;
	}

	private static Font getFont() {
		return minecraft.font;
	}

	public static void drawHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		int screenWidth = guiGraphics.guiWidth();
		int screenHeight = guiGraphics.guiHeight();
		if (!shouldDisplayBar())
			return;

		getStackHandler().ifPresent(stacks -> {
			int x = (screenWidth / 2) - 200
					+ DungeonsLibrariesConfig.ARTIFACT_BAR_HORIZONTAL_OFFSET.get();
			int y = screenHeight - 21
					+ DungeonsLibrariesConfig.ARTIFACT_BAR_VERTICAL_OFFSET.get();

			RenderSystem.setShaderTexture(0, ARTIFACT_BAR_RESOURCE);
			// i & height - 16 are equivalent to the x & y found in the old renderer
			guiGraphics.blit(ARTIFACT_BAR_RESOURCE, x, y, 0,
					0, 62, 22, 62, 22);
			int i = 0;
			for (KeyMapping key : List.of(CuriosKeyBindings.activateArtifact1,
					CuriosKeyBindings.activateArtifact2,
					CuriosKeyBindings.activateArtifact3)) {
				ItemStack artifact = stacks.getStackInSlot(i);
				if (!artifact.isEmpty() && artifact.getItem() instanceof ArtifactItem) {
					guiGraphics.renderItem(artifact, x + (i * 20) + 3, y + 3);
					guiGraphics.renderItemDecorations(getFont(), artifact, x + (i * 20) + 3, y + 3);
				}
				String keyName = key.getKey().getDisplayName().getString();
				guiGraphics.drawString(getFont(), keyName, x + 19 + (i * 20) - getFont().width(keyName),
						y + 3, 0xFFFFFF, true);
				i++;
			}
		});
	}
}
