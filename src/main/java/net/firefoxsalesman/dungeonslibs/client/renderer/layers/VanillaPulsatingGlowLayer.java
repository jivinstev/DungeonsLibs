package net.firefoxsalesman.dungeonslibs.client.renderer.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.firefoxsalesman.dungeonslibs.client.ConvenientModel;
import net.firefoxsalesman.dungeonslibs.client.KeyframeEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

public class VanillaPulsatingGlowLayer<T extends Entity & KeyframeEntity, M extends ConvenientModel<T>>
		extends GenericEyeLayer<T, M> {
	private float pulseSpeed;
	private float pulseAmount;
	private float minimumPulseAmount;

	public VanillaPulsatingGlowLayer(RenderLayerParent<T, M> parent, ResourceLocation path, float pulseSpeed,
			float pulseAmount, float minimumPulseAmount) {
		super(parent, path);
		this.pulseSpeed = pulseSpeed;
		this.pulseAmount = pulseAmount;
		this.minimumPulseAmount = minimumPulseAmount;
	}

	@Override
	public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity,
			float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks,
			float pNetHeadYaw, float pHeadPitch) {
		float glow = Math.max(minimumPulseAmount, Mth.cos(pLivingEntity.tickCount * pulseSpeed) * pulseAmount);
		VertexConsumer vertexconsumer = pBuffer.getBuffer(this.renderType());
		int channel = Mth.clamp((int) (glow * 255.0F), 0, 255);
		this.getParentModel().renderToBuffer(pMatrixStack, vertexconsumer, 15728640, OverlayTexture.NO_OVERLAY,
				FastColor.ARGB32.color(255, channel, channel, channel));
	}
}
