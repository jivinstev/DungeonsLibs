package net.firefoxsalesman.dungeonslibs.integration.curios.client;

import static net.firefoxsalesman.dungeonslibs.client.CuriosKeyBindings.activateArtifact1;
import static net.firefoxsalesman.dungeonslibs.client.CuriosKeyBindings.activateArtifact2;
import static net.firefoxsalesman.dungeonslibs.client.CuriosKeyBindings.activateArtifact3;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class CuriosClientIntegration {
	@SubscribeEvent
	public static void setupCuriosKeybindings(RegisterKeyMappingsEvent event) {
		activateArtifact1.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
		event.register(activateArtifact1);
		activateArtifact2.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
		event.register(activateArtifact2);
		activateArtifact3.setKeyConflictContext(KeyConflictContext.UNIVERSAL);
		event.register(activateArtifact3);
	}
}
