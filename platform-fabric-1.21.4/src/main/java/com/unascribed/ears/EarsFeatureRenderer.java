package com.unascribed.ears;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorArmorFeatureRenderer;
import com.unascribed.ears.mixin.AccessorLivingEntityRenderer;
import com.unascribed.ears.mixin.AccessorTextureManager;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelPartNames;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class EarsFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	
	private final PlayerEntityRenderer per;
	
	public EarsFeatureRenderer(PlayerEntityRenderer per) {
		super(per);
		this.per = per;
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(MatrixStack m, VertexConsumerProvider vertexConsumers, int light, PlayerEntityRenderState entity, float limbAngle, float limbDistance) {
		//EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance);
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light) {
		@SuppressWarnings("resource")
		PlayerEntityRenderState state = per.getAndUpdateRenderState(MinecraftClient.getInstance().player, 1.0f);
		delegate.render(m, vertexConsumers, state, light, LivingEntityRenderer.getOverlay(state, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, VertexConsumerProvider vertexConsumers, int light) {
		@SuppressWarnings("resource")
		PlayerEntityRenderState state = per.getAndUpdateRenderState(MinecraftClient.getInstance().player, 1.0f);
		delegate.render(m, vertexConsumers, state, light, LivingEntityRenderer.getOverlay(state, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, VertexConsumerProvider, VertexConsumer, PlayerEntityRenderState, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {
		
		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerEntityModel model = getContextModel();
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
			return peer.skinTextures.model() == SkinTextures.Model.SLIM;
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
			Identifier skin = peer.skinTextures.texture();
			Identifier id = Identifier.tryParse(skin.getNamespace(), src.addSuffix(skin.getPath()));
			TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
			if (pngData != null && !((AccessorTextureManager) textureManager).ears$getTextures().containsKey(id)) {
				try {
					textureManager.registerTexture(id, new NativeImageBackedTexture(NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					//textureManager.registerTexture(id, MissingSprite.getMissingSpriteTexture());
				}
			}
		}
		
		private float armorR = 1;
		private float armorG = 1;
		private float armorB = 1;
		private float armorA = 1;
		
		private ArmorFeatureRenderer<?, ?, ?> afr;
		private final ModelPart blank = new ModelPart(Collections.emptyList(), Collections.emptyMap());
		private final ModelPart blankHead = new ModelPart(Collections.emptyList(), Map.of(EntityModelPartNames.HAT, blank));
		private final ModelPart dummyRoot = new ModelPart(
				List.of(new ModelPart.Cuboid(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, false, 0, 0, Set.of()) {
					@Override
					public void renderCuboid(MatrixStack.Entry entry, VertexConsumer vertices, int light, int overlay, int color) {
						// used to capture the VertexConsumer that has the correct RenderLayer
						vc = vertices;
					}
				}), 
				ImmutableMap.<String, ModelPart>builder()
				.put(EntityModelPartNames.HEAD, blankHead)
				.put(EntityModelPartNames.BODY, blank)
				.put(EntityModelPartNames.LEFT_ARM, blank)
				.put(EntityModelPartNames.RIGHT_ARM, blank)
				.put(EntityModelPartNames.LEFT_LEG, blank)
				.put(EntityModelPartNames.RIGHT_LEG, blank)
				.build());
		private final BipedEntityModel<?> dummyModel = new BipedEntityModel<PlayerEntityRenderState>(dummyRoot);
		
		// Fabric API compat
		private final List<MethodHandle> entityCaptures = Lists.newArrayList();
		private final List<MethodHandle> slotCaptures = Lists.newArrayList();
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void doBindBuiltin(TexSource src) {
			commitQuads();
			if (src.isGlint()) {
				armorR = armorG = armorB = armorA = 1;
				vc = vcp.getBuffer(RenderLayer.getArmorEntityGlint());
			} else if (canBind(src)) {
				EquipmentSlot slot = getSlot(src);
				ItemStack equipment = getEquippedStack(peer, slot);
				AccessorArmorFeatureRenderer aafr = (AccessorArmorFeatureRenderer)afr;
				if (equipment.get(DataComponentTypes.DYED_COLOR) != null) {
					int c = equipment.get(DataComponentTypes.DYED_COLOR).rgb();
					armorR = (c >> 16 & 255) / 255.0F;
					armorG = (c >> 8 & 255) / 255.0F;
					armorB = (c & 255) / 255.0F;
					armorA = 1;
				}
				try {
					setCaptures(peer, slot);
					aafr.ears$renderArmor(matrices, vcp, equipment, slot, 0, dummyModel);
					setCaptures(null, null);
				} catch (Throwable t) {
					if (skipRendering == 0) skipRendering = 1;
					EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Exception while attempting to retrieve armor texture", t);
				}
			}
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public boolean canBind(TexSource tex) {
			boolean glint = tex.isGlint();
			if (glint) tex = tex.getParent();
			EquipmentSlot slot = getSlot(tex);
			if (slot == null) return super.canBind(tex);
			ItemStack equipment = getEquippedStack(peer, slot);
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
								} else if (BipedEntityRenderState.class.isAssignableFrom(f.getType())) {
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
					BipedEntityModel<?> model = aafr.ears$getArmor(peer, slot);
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

		private void setCaptures(BipedEntityRenderState entity, EquipmentSlot slot) {
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
		
		private ItemStack getEquippedStack(PlayerEntityRenderState peer, EquipmentSlot slot) {
			return switch(slot) {
			case HEAD -> peer.equippedHeadStack;
			case CHEST -> peer.equippedChestStack;
			case LEGS -> peer.equippedLegsStack;
			case FEET -> peer.equippedFeetStack;
			default -> null;
			};
		}

		private final MatrixStack.Entry IDENTITY3 = new MatrixStack().peek();
		
		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
			r *= armorR;
			g *= armorG;
			b *= armorB;
			a *= armorA;
			Matrix4f mm = matrices.peek().getPositionMatrix();
			var mn = emissive ? IDENTITY3 : matrices.peek();
			vc.vertex(mm, x, y, z).color(r, g, b, a).texture(u, v).overlay(overlay).light(emissive ? LightmapTextureManager.pack(15, 15) : light).normal(mn, nX, nY, nZ);
		}
		
		@Override
		protected void commitQuads() {
			if (vcp instanceof VertexConsumerProvider.Immediate immediate) {
				immediate.drawCurrentLayer();
			}
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// TODO port this to core profile (nah)
		}

		@Override
		protected VertexConsumer getVertexConsumer(TexSource src) {
			armorR = armorG = armorB = armorA = 1;
			Identifier id = peer.skinTextures.texture();
			if (src != TexSource.SKIN) {
				id = Identifier.tryParse(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return vcp.getBuffer(RenderLayer.getItemEntityTranslucentCull(id));
		}

		@Override
		public float getTime() {
			return peer.age;
		}

		@Override
		public boolean isFlying() {
			return peer.applyFlyingRotation;
		}

		@Override
		public boolean isGliding() {
			return peer.isGliding;
		}

		@Override
		public boolean isJacketEnabled() {
			return peer.jacketVisible;
		}

		@Override
		public boolean isWearingBoots() {
			return peer.equippedFeetStack.getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.equippedChestStack.getItem() instanceof ArmorItem;
		}

		@Override
		public boolean isWearingElytra() {
			//TODO: this is not correct, should check for EQUIPPABLE that has actual textures under assetId for elytra
			return peer.equippedChestStack.getComponents().contains(DataComponentTypes.GLIDER);
		}

		@Override
		public float getLimbSwing() {
			return peer.handSwingProgress;
		}

		@Override
		public float getBodyYaw() {
			return peer.bodyYaw;
		}

		@Override
		public double getCapeX() {
			return peer.field_53536;
		}

		@Override
		public double getCapeY() {
			return peer.field_53537;
		}

		@Override
		public double getCapeZ() {
			return peer.field_53538;
		}

		@Override
		public double getX() {
			return peer.x;
		}

		@Override
		public double getY() {
			return peer.y;
		}

		@Override
		public double getZ() {
			return peer.z;
		}

		@Override
		public float getHorizontalSpeed() {
			// TODO: unimplemented, not used by modified common code
			return 0;
		}

		@Override
		public float getStride() {
			// TODO: unimplemented, not used by modified common code
			return 0;
		}
	};
}
