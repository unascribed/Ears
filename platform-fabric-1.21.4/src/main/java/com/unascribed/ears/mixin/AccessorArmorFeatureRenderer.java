package com.unascribed.ears.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;

@Mixin(ArmorFeatureRenderer.class)
public interface AccessorArmorFeatureRenderer<S extends BipedEntityRenderState, M extends BipedEntityModel<S>, A extends BipedEntityModel<S>> {

	@Accessor("innerModel")
	BipedEntityModel<?> ears$getLeggingsModel();
	@Accessor("outerModel")
	BipedEntityModel<?> ears$getBodyModel();
	
	@Invoker("getModel")
	BipedEntityModel<?> ears$getArmor(S state, EquipmentSlot slot);
	@Invoker("usesInnerModel")
	boolean ears$usesSecondLayer(EquipmentSlot slot);

	@Invoker("renderArmor")
	void ears$renderArmor(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, A armorModel);
	
}
