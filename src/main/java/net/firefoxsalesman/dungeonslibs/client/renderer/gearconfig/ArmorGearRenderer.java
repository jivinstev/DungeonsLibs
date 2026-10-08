package net.firefoxsalesman.dungeonslibs.client.renderer.gearconfig;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.firefoxsalesman.dungeonslibs.entities.SpawnArmoredMob;
import net.firefoxsalesman.dungeonslibs.items.gearconfig.ArmorGear;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.ArmorMaterialBaseType;
import net.firefoxsalesman.dungeonslibs.items.materials.armor.DungeonsArmorMaterial;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.cache.object.GeoCube;
import software.bernie.geckolib.renderer.GeoArmorRenderer;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.util.RenderUtil;

public class ArmorGearRenderer<T extends ArmorGear> extends GeoArmorRenderer<T> {
	public ArmorGearRenderer() {
		super(new ArmorGearModel<>());
	}

	public ArmorGearRenderer(ArmorGearModel<T> model) {
		super(model);
	}

	@Override
	public void renderRecursively(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType,
			MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick,
			int packedLight, int packedOverlay, int colour) {
		poseStack.pushPose();
		prepMatrixForBone(poseStack, bone);
		renderCubesOfBone(poseStack, bone, buffer, packedLight, packedOverlay, colour);
		// renderChildBones(bone, poseStack, buffer, packedLight, packedOverlay, red,
		// green, blue, alpha);
		renderChildBones(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick,
				packedLight, packedOverlay, colour);
		poseStack.popPose();
	}

	public void prepMatrixForBone(PoseStack stack, GeoBone bone) {
		RenderUtil.translateMatrixToBone(stack, bone);
		RenderUtil.translateToPivotPoint(stack, bone);
		EntityRenderer<? super LivingEntity> entityRenderer = Minecraft.getInstance()
				.getEntityRenderDispatcher().getRenderer(getCurrentEntity());
		if (!(entityRenderer instanceof GeoEntityRenderer) || !bone.getName().contains("armor")) {
			RenderUtil.rotateMatrixAroundBone(stack, bone);
		}
		RenderUtil.scaleMatrixForBone(stack, bone);
		Object material = getAnimatable().getMaterial().value();
		if (bone.getName().contains("Body") && material instanceof DungeonsArmorMaterial
				&& ((DungeonsArmorMaterial) material).getBaseType() == ArmorMaterialBaseType.CLOTH) {
			stack.scale(1.0F, 1.0F, 0.93F);
		}
		RenderUtil.translateAwayFromPivotPoint(stack, bone);
	}

	@Override
	public void renderCubesOfBone(PoseStack poseStack, GeoBone bone, VertexConsumer buffer, int packedLight, int packedOverlay, int colour) {
		if (bone.isHidden())
			return;

		for (GeoCube cube : bone.getCubes()) {
			if (!bone.isHidden()) {
				poseStack.pushPose();
				if (getCurrentEntity() instanceof SpawnArmoredMob
						&& ((SpawnArmoredMob) getCurrentEntity())
								.getArmorSet()
								.getRegistryName() == getAnimatable().getArmorSet()) {
					renderCube(poseStack, cube, buffer, packedLight,
							LivingEntityRenderer.getOverlayCoords(
									(LivingEntity) getCurrentEntity(), 0.0F), colour);
				} else {
					renderCube(poseStack, cube, buffer, packedLight, packedOverlay, colour);
				}
				poseStack.popPose();
			}
		}
	}

}
