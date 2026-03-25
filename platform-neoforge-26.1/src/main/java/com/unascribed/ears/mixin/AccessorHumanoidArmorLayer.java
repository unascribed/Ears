package com.unascribed.ears.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArmorModelSet;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HumanoidArmorLayer.class)
public interface AccessorHumanoidArmorLayer<S extends HumanoidRenderState, M extends HumanoidModel<S>, A extends HumanoidModel<S>> {

    @Accessor("modelSet")
    ArmorModelSet<A> ears$getModelSet();

    @Invoker("getArmorModel")
    HumanoidModel<?> ears$getArmor(S state, EquipmentSlot slot);
    @Invoker("usesInnerModel")
    boolean ears$usesInnerModel(EquipmentSlot slot);

    @Invoker("renderArmorPiece")
    void ears$renderArmorPiece(PoseStack matrices, SubmitNodeCollector vertexConsumers, ItemStack stack, EquipmentSlot slot, int light, S state);
}
