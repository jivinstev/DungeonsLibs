package net.firefoxsalesman.dungeonslibs.capabilities.playerrewards;

import java.util.function.Supplier;

import net.firefoxsalesman.dungeonslibs.capabilities.playerrewards.PlayerRewards;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class AttacherPlayerRewards {

	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "dungeonslibs");

	public static final Supplier<AttachmentType<PlayerRewards>> PLAYER_REWARDS = ATTACHMENT_TYPES.register("player_rewards",
			() -> AttachmentType.builder(PlayerRewards::new)
					.serialize(new IAttachmentSerializer<CompoundTag, PlayerRewards>() {
						@Override
						public PlayerRewards read(IAttachmentHolder holder, CompoundTag tag, HolderLookup.Provider provider) {
							PlayerRewards rewards = new PlayerRewards();
							rewards.deserializeNBT(provider, tag);
							return rewards;
						}

						@Override
						public CompoundTag write(PlayerRewards rewards, HolderLookup.Provider provider) {
							return rewards.serializeNBT(provider);
						}
					})
					.copyOnDeath()
					.build());
}
