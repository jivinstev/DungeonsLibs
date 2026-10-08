package net.firefoxsalesman.dungeonslibs.capabilities.playerrewards;

import net.minecraft.world.entity.player.Player;

import static net.firefoxsalesman.dungeonslibs.capabilities.playerrewards.AttacherPlayerRewards.PLAYER_REWARDS;

public class PlayerRewardsHelper {

	public static PlayerRewards getPlayerRewardsCapability(Player playerEntity) {
		return playerEntity.getData(PLAYER_REWARDS);
	}

}
