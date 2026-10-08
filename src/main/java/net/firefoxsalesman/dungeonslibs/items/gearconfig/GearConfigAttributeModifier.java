package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.core.registries.BuiltInRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class GearConfigAttributeModifier {

	private static DataResult<AttributeModifier.Operation> operationFromId(int id) {
		AttributeModifier.Operation[] operations = AttributeModifier.Operation.values();
		if (id < 0 || id >= operations.length) {
			return DataResult.error(() -> "Unknown attribute modifier operation: " + id);
		}
		return DataResult.success(operations[id]);
	}

	public static final Codec<AttributeModifier.Operation> ATTRIBUTE_MODIFIER_OPERATION_CODEC = Codec.INT
			.flatXmap(GearConfigAttributeModifier::operationFromId, d -> DataResult.success(d.ordinal()));

	public static final Codec<GearConfigAttributeModifier> CODEC = RecordCodecBuilder.create(instance -> instance
			.group(
					ResourceLocation.CODEC.fieldOf("attribute").forGetter(
							GearConfigAttributeModifier::getAttributeResourceLocation),
					Codec.DOUBLE.fieldOf("amount")
							.forGetter(GearConfigAttributeModifier::getAmount),
					ATTRIBUTE_MODIFIER_OPERATION_CODEC.fieldOf("operation")
							.forGetter(GearConfigAttributeModifier::getOperation))
			.apply(instance, GearConfigAttributeModifier::new));

	private final ResourceLocation attributeResourceLocation;
	private final double amount;
	private final AttributeModifier.Operation operation;

	public GearConfigAttributeModifier(ResourceLocation attributeResourceLocation, double amount,
			AttributeModifier.Operation operation) {
		this.attributeResourceLocation = attributeResourceLocation;
		this.amount = amount;
		this.operation = operation;
	}

	public ResourceLocation getAttributeResourceLocation() {
		return attributeResourceLocation;
	}

	public double getAmount() {
		return amount;
	}

	public AttributeModifier.Operation getOperation() {
		return operation;
	}

	public AttributeModifier toAttributeModifier(ResourceLocation id) {
		return new AttributeModifier(id, amount, operation);
	}

	public Attribute getAttribute() {
		return BuiltInRegistries.ATTRIBUTE.get(attributeResourceLocation);
	}
}
