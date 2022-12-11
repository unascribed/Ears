package com.unascribed.ears.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;

@Mixin(ArmorFeatureRenderer.class)
public interface AccessorArmorFeatureRenderer {

	@Accessor("innerModel")
	BipedEntityModel<?> ears$getLeggingsModel();
	@Accessor("outerModel")
	BipedEntityModel<?> ears$getBodyModel();
	
	@Invoker("getModel")
	BipedEntityModel<?> ears$getArmor(EquipmentSlot slot);
	@Invoker("usesInnerModel")
	boolean ears$usesSecondLayer(EquipmentSlot slot);

	@Invoker("renderArmorParts")
	void ears$renderArmorParts(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, ArmorItem armorItem, boolean bl, BipedEntityModel<?> bipedEntityModel, boolean bl2, float f, float g, float h, @Nullable String string);
	
}
