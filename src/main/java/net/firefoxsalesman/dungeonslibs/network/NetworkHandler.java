package net.firefoxsalesman.dungeonslibs.network;

import net.firefoxsalesman.dungeonslibs.integration.curios.client.message.CuriosArtifactStartMessage;
import net.firefoxsalesman.dungeonslibs.integration.curios.client.message.CuriosArtifactStopMessage;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.ArmorGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.ArtifactGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.BowGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.CrossbowGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.gearconfig.MeleeGearConfigSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.materials.ArmorMaterialSyncPacket;
import net.firefoxsalesman.dungeonslibs.network.materials.WeaponMaterialSyncPacket;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {

	public NetworkHandler() {
	}

	/** Call with the mod event bus. */
	public static void init(IEventBus modBus) {
		modBus.addListener(NetworkHandler::register);
	}

	public static void register(RegisterPayloadHandlersEvent event) {
		PayloadRegistrar r = event.registrar("1");
		r.playToClient(UpdateSoulsMessage.TYPE, UpdateSoulsMessage.STREAM_CODEC,
				UpdateSoulsMessage.UpdateSoulsHandler::handle);
		r.playToClient(ArmorGearConfigSyncPacket.TYPE, ArmorGearConfigSyncPacket.STREAM_CODEC,
				ArmorGearConfigSyncPacket::handle);
		r.playToClient(MeleeGearConfigSyncPacket.TYPE, MeleeGearConfigSyncPacket.STREAM_CODEC,
				MeleeGearConfigSyncPacket::handle);
		r.playToClient(BowGearConfigSyncPacket.TYPE, BowGearConfigSyncPacket.STREAM_CODEC,
				BowGearConfigSyncPacket::handle);
		r.playToClient(CrossbowGearConfigSyncPacket.TYPE, CrossbowGearConfigSyncPacket.STREAM_CODEC,
				CrossbowGearConfigSyncPacket::handle);
		r.playToClient(ArmorMaterialSyncPacket.TYPE, ArmorMaterialSyncPacket.STREAM_CODEC,
				ArmorMaterialSyncPacket::handle);
		r.playToClient(WeaponMaterialSyncPacket.TYPE, WeaponMaterialSyncPacket.STREAM_CODEC,
				WeaponMaterialSyncPacket::handle);
		r.playBidirectional(CuriosArtifactStartMessage.TYPE, CuriosArtifactStartMessage.STREAM_CODEC,
				CuriosArtifactStartMessage.CuriosArtifactHandler::handle);
		r.playBidirectional(CuriosArtifactStopMessage.TYPE, CuriosArtifactStopMessage.STREAM_CODEC,
				CuriosArtifactStopMessage::handle);
		r.playToClient(EliteMobMessage.TYPE, EliteMobMessage.STREAM_CODEC, EliteMobMessage::handle);
		r.playToClient(BreakItemMessage.TYPE, BreakItemMessage.STREAM_CODEC,
				BreakItemMessage.BreakItemHandler::handle);
		r.playToServer(SwitchHandMessage.TYPE, SwitchHandMessage.STREAM_CODEC, SwitchHandMessage::handle);
		r.playToClient(ArtifactGearConfigSyncPacket.TYPE, ArtifactGearConfigSyncPacket.STREAM_CODEC,
				ArtifactGearConfigSyncPacket::handle);
	}
}
