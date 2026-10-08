package net.firefoxsalesman.dungeonslibs.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.firefoxsalesman.dungeonslibs.capabilities.soulcaster.SoulCasterHelper;
import net.firefoxsalesman.dungeonslibs.config.DungeonsLibrariesConfig;
import net.firefoxsalesman.dungeonslibs.utils.ModHelper;

import static net.firefoxsalesman.dungeonslibs.utils.ResourceLocationHelper.modLoc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.GameType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.LayeredDraw;

/**
 * Borrowed from Goety. Many thanks to Polarice
 */
public class SoulEnergyGui {
	public static final LayeredDraw.Layer OVERLAY = SoulEnergyGui::drawHUD;
	private static final Minecraft minecraft = Minecraft.getInstance();

	public static boolean shouldDisplayBar() {
		return SoulCasterHelper.hasSouls(minecraft.player) && (minecraft.gameMode != null
				&& minecraft.gameMode.getPlayerMode() != GameType.SPECTATOR)
				&& !ModHelper.hasGoety();
	}

	public static Font getFont() {
		return minecraft.font;
	}

	public static void drawHUD(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
		float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
		int screenWidth = guiGraphics.guiWidth();
		int screenHeight = guiGraphics.guiHeight();
		if (!shouldDisplayBar()) {
			return;
		}

		int soulEnergy = (int) SoulCasterHelper.getSouls(minecraft.player);
		int soulEnergyTotal = (int) SoulCasterHelper.getSoulCap(minecraft.player);
		int i = (screenWidth / 2) + 125 + DungeonsLibrariesConfig.SOUL_BAR_HORIZONTAL_OFFSET.get();
		int energylength = (int) (60 * (soulEnergy / (double) soulEnergyTotal));
		int maxenergy = 71;

		int height = screenHeight - 5 + DungeonsLibrariesConfig.SOUL_BAR_VERTICAL_OFFSET.get();

		int offset = (int) ((minecraft.player.tickCount + partialTicks) % 234);

		guiGraphics.blit(modLoc("textures/gui/soul_energy.png"), i, height - 9, 0,
				0, maxenergy, 9, 128, 90);
		RenderSystem.setShaderTexture(0,
				modLoc("textures/gui/soul_energy_bar.png"));
		guiGraphics.blit(modLoc("textures/gui/soul_energy_bar.png"), i + 9, height - 7,
				offset, 0,
				energylength, 5, 128, 5);
	}
}
