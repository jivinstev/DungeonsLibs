package net.firefoxsalesman.dungeonslibs.init;

import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import static net.neoforged.api.distmarker.Dist.CLIENT;

import net.firefoxsalesman.dungeonslibs.DungeonsLibs;
import net.firefoxsalesman.dungeonslibs.client.renderer.SoulOrbRenderer;
import net.firefoxsalesman.dungeonslibs.client.renderer.SummonSpotRenderer;
import net.firefoxsalesman.dungeonslibs.entities.LibEntityTypes;

@EventBusSubscriber(modid = DungeonsLibs.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = CLIENT)
public class ClientEventBusSubscriber {

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(LibEntityTypes.SOUL_ORB.get(), SoulOrbRenderer::new);
		event.registerEntityRenderer(LibEntityTypes.SUMMON_SPOT.get(), SummonSpotRenderer::new);
	}

	// @SubscribeEvent
	// public static void registerArmorRenderers(final
	// EntityRenderersEvent.AddLayers event) {
	// GeoArmorRenderer.registerArmorRenderer(ArmorGear.class,
	// ArmorGearRenderer::new);
	// }
}
