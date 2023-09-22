package com.unascribed.ears;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;

import org.joml.AxisAngle4f;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorArmorFeatureRenderer;
import com.unascribed.ears.mixin.AccessorLivingEntityRenderer;
import com.unascribed.ears.mixin.AccessorPlayerEntityModel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.PlayerModelPart;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.texture.MissingSprite;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EarsFeatureRenderer extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {
	
	private final PlayerEntityRenderer per;
	
	public EarsFeatureRenderer(PlayerEntityRenderer per) {
		super(per);
		this.per = per;
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, AbstractClientPlayerEntity entity) {
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, VertexConsumerProvider, VertexConsumer, AbstractClientPlayerEntity, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {
		
		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerEntityModel<AbstractClientPlayerEntity> model = getContextModel();
			return d.map(BodyPart.HEAD, model.head)
					.map(BodyPart.LEFT_ARM, model.leftArm)
					.map(BodyPart.LEFT_LEG, model.leftLeg)
					.map(BodyPart.RIGHT_ARM, model.rightArm)
					.map(BodyPart.RIGHT_LEG, model.rightLeg)
					.map(BodyPart.TORSO, model.body);
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.rotate(matrices);
			Cuboid cuboid = modelPart.getRandomCuboid(NotRandom1193.INSTANCE);
			matrices.scale(1/16f, 1/16f, 1/16f);
			matrices.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
		}
		
		@Override
		protected boolean isVisible(ModelPart modelPart) {
			return modelPart.visible;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsMod.getEarsFeatures(peer);
		}

		@Override
		public boolean isSlim() {
			return ((AccessorPlayerEntityModel)getContextModel()).ears$isThinArms();
		}

		@Override
		protected void pushMatrix() {
			matrices.push();
		}

		@Override
		protected void popMatrix() {
			matrices.pop();
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			matrices.translate(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			matrices.multiply(new AxisAngle4f(ang*MathHelper.RADIANS_PER_DEGREE, x, y, z).get(new Quaternionf()));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadAux(TexSource src, byte[] pngData) {
			Identifier skin = peer.getSkinTextures().texture();
			Identifier id = new Identifier(skin.getNamespace(), src.addSuffix(skin.getPath()));
			if (pngData != null && MinecraftClient.getInstance().getTextureManager().getOrDefault(id, null) == null) {
				try {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, new NativeImageBackedTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					MinecraftClient.getInstance().getTextureManager().registerTexture(id, MissingSprite.getMissingSpriteTexture());
				}
			}
		}
		
		private float armorR = 1;
		private float armorG = 1;
		private float armorB = 1;
		private float armorA = 1;
		
		private ArmorFeatureRenderer<?, ?, ?> afr;
		private final ModelPart blank = new ModelPart(Collections.emptyList(), Collections.emptyMap());
		private final ModelPart dummyRoot = new ModelPart(Collections.emptyList(), ImmutableMap.<String, ModelPart>builder()
				.put(EntityModelPartNames.HEAD, blank)
				.put(EntityModelPartNames.HAT, blank)
				.put(EntityModelPartNames.BODY, blank)
				.put(EntityModelPartNames.LEFT_ARM, blank)
				.put(EntityModelPartNames.RIGHT_ARM, blank)
				.put(EntityModelPartNames.LEFT_LEG, blank)
				.put(EntityModelPartNames.RIGHT_LEG, blank)
				.build());
		private final BipedEntityModel<?> dummyModel = new BipedEntityModel<PlayerEntity>(dummyRoot) {
			@Override
			public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
				// used to capture the VertexConsumer that has the correct RenderLayer
				vc = vertices;
			}
		};
		// Fabric API compat
		private final List<MethodHandle> entityCaptures = Lists.newArrayList();
		private final List<MethodHandle> slotCaptures = Lists.newArrayList();
		
		@Override
		protected void doBindBuiltin(TexSource src) {
			commitQuads();
			if (src.isGlint()) {
				armorR = armorG = armorB = armorA = 1;
				vc = vcp.getBuffer(RenderLayer.getArmorEntityGlint());
			} else if (canBind(src)) {
				EquipmentSlot slot = getSlot(src);
				ItemStack equipment = peer.getEquippedStack(slot);
				ArmorItem ai = (ArmorItem)equipment.getItem();
				AccessorArmorFeatureRenderer aafr = (AccessorArmorFeatureRenderer)afr;
				if (ai instanceof DyeableArmorItem) {
					int c = ((DyeableArmorItem)ai).getColor(equipment);
					armorR = (c >> 16 & 255) / 255.0F;
					armorG = (c >> 8 & 255) / 255.0F;
					armorB = (c & 255) / 255.0F;
					armorA = 1;
				}
				try {
					setCaptures(peer, slot);
					aafr.ears$renderArmorParts(matrices, vcp, 0, ai, dummyModel, aafr.ears$usesSecondLayer(slot), 1, 1, 1, null);
					setCaptures(null, null);
				} catch (Throwable t) {
					if (skipRendering == 0) skipRendering = 1;
					EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Exception while attempting to retrieve armor texture", t);
				}
			}
		}
		
		@Override
		public boolean canBind(TexSource tex) {
			boolean glint = tex.isGlint();
			if (glint) tex = tex.getParent();
			EquipmentSlot slot = getSlot(tex);
			if (slot == null) return super.canBind(tex);
			ItemStack equipment = peer.getEquippedStack(slot);
			if (equipment.isEmpty() || !(equipment.getItem() instanceof ArmorItem)) return false;
			if (afr == null) {
				for (FeatureRenderer<?, ?> fr : ((AccessorLivingEntityRenderer)per).ears$getFeatures()) {
					if (fr instanceof ArmorFeatureRenderer) {
						afr = (ArmorFeatureRenderer<?, ?, ?>)fr;
						for (Field f : ArmorFeatureRenderer.class.getDeclaredFields()) {
							try {
								f.setAccessible(true);
								if (Modifier.isStatic(f.getModifiers())) continue;
								if (EquipmentSlot.class == f.getType()) {
									slotCaptures.add(MethodHandles.lookup().unreflectSetter(f));
								} else if (LivingEntity.class.isAssignableFrom(f.getType())) {
									entityCaptures.add(MethodHandles.lookup().unreflectSetter(f));
								}
							} catch (Throwable t) {
								EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Exception while attempting to scan for captures", t);
							}
						}
						break;
					}
				}
			}
			if (afr != null) {
				AccessorArmorFeatureRenderer aafr = (AccessorArmorFeatureRenderer)afr;
				BipedEntityModel<?> bmodel = aafr.ears$getBodyModel();
				BipedEntityModel<?> lmodel = aafr.ears$getLeggingsModel();

				try {
					setCaptures(peer, slot);
					BipedEntityModel<?> model = aafr.ears$getArmor(slot);
					setCaptures(null, null);
					if (model != bmodel && model != lmodel) {
						// custom armor model
						return false;
					}
					return glint ? equipment.hasGlint() : true;
				} catch (Throwable t) {
					EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Exception while attempting to retrieve armor model", t);
					return false;
				}
			}
			return false;
		}

		private void setCaptures(LivingEntity entity, EquipmentSlot slot) {
			for (MethodHandle mh : entityCaptures) {
				try {
					mh.invoke(afr, entity);
				} catch (Throwable t) {}
			}
			for (MethodHandle mh : slotCaptures) {
				try {
					mh.invoke(afr, slot);
				} catch (Throwable t) {}
			}
		}

		private EquipmentSlot getSlot(TexSource tex) {
			return Decider.<TexSource, EquipmentSlot>begin(tex)
					.map(TexSource.HELMET, EquipmentSlot.HEAD)
					.map(TexSource.CHESTPLATE, EquipmentSlot.CHEST)
					.map(TexSource.LEGGINGS, EquipmentSlot.LEGS)
					.map(TexSource.BOOTS, EquipmentSlot.FEET)
					.orElse(null);
		}

		private final Matrix3f IDENTITY3 = new Matrix3f();
		
		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			r *= armorR;
			g *= armorG;
			b *= armorB;
			a *= armorA;
			Matrix4f mm = matrices.peek().getPositionMatrix();
			Matrix3f mn = emissive ? IDENTITY3 : matrices.peek().getNormalMatrix();
			vc.vertex(mm, x, y, z).color(r, g, b, a).texture(u, v).overlay(overlay).light(emissive ? LightmapTextureManager.pack(15, 15) : light).normal(mn, nX, nY, nZ).next();
		}
		
		@Override
		protected void commitQuads() {
			if (vcp instanceof VertexConsumerProvider.Immediate) {
				((VertexConsumerProvider.Immediate)vcp).drawCurrentLayer();
			}
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// TODO port this to core profile (nah)
		}

		@Override
		protected VertexConsumer getVertexConsumer(TexSource src) {
			armorR = armorG = armorB = armorA = 1;
			Identifier id = peer.getSkinTextures().texture();
			if (src != TexSource.SKIN) {
				id = new Identifier(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return vcp.getBuffer(RenderLayer.getEntityTranslucentCull(id));
		}

		@Override
		public float getTime() {
			return peer.age+MinecraftClient.getInstance().getTickDelta();
		}

		@Override
		public boolean isFlying() {
			return peer.getAbilities().flying;
		}

		@Override
		public boolean isGliding() {
			return peer.isFallFlying();
		}

		@Override
		public boolean isJacketEnabled() {
			return peer.isPartVisible(PlayerModelPart.JACKET);
		}

		@Override
		public boolean isWearingBoots() {
			return peer.getEquippedStack(EquipmentSlot.FEET).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			return peer.getEquippedStack(EquipmentSlot.CHEST).getItem() instanceof ElytraItem;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.prevHorizontalSpeed, peer.horizontalSpeed, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getLimbSwing() {
			return peer.limbAnimator.getSpeed(MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.prevStrideDistance, peer.strideDistance, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.prevBodyYaw, peer.bodyYaw, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.prevCapeX, peer.capeX, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.prevCapeY, peer.capeY, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.prevCapeZ, peer.capeZ, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.prevX, peer.getPos().x, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.prevY, peer.getPos().y, MinecraftClient.getInstance().getTickDelta());
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.prevZ, peer.getPos().z, MinecraftClient.getInstance().getTickDelta());
		}
	};
}
