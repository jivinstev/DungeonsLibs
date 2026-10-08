package net.firefoxsalesman.dungeonslibs.items.gearconfig;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import java.util.function.Supplier;

public class ArmorSet {
	private final ResourceLocation registryName;
	private final Supplier<Item> head;
	private final Supplier<Item> chest;
	private final Supplier<Item> legs;
	private final Supplier<Item> feet;

	public ArmorSet(ResourceLocation registryName, Supplier<Item> head, Supplier<Item> chest,
			Supplier<Item> legs, Supplier<Item> feet) {
		this.registryName = registryName;
		this.head = head;
		this.chest = chest;
		this.legs = legs;
		this.feet = feet;
	}

	public ResourceLocation getRegistryName() {
		return registryName;
	}

	public Supplier<Item> getHead() {
		return head;
	}

	public Supplier<Item> getChest() {
		return chest;
	}

	public Supplier<Item> getLegs() {
		return legs;
	}

	public Supplier<Item> getFeet() {
		return feet;
	}
}
