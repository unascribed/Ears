package com.unascribed.ears.mixin;

import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EquipmentModelData;
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

	@Accessor("field_61804")
    EquipmentModelData<A> ears$getModelData();
	
	@Invoker("getModel")
	BipedEntityModel<?> ears$getArmor(S state, EquipmentSlot slot);
	@Invoker("usesInnerModel")
	boolean ears$usesSecondLayer(EquipmentSlot slot);

	@Invoker("renderArmor")
	void ears$renderArmor(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, S bipedEntityRenderState);
	
}
