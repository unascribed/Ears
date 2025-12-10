package com.unascribed.ears;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.EarsFeaturesHolder;
import com.unascribed.ears.common.EarsFeaturesStorage;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorHumanoidArmorLayer;
import com.unascribed.ears.mixin.AccessorLivingEntityRenderer;
import com.unascribed.ears.mixin.AccessorTextureManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.ModelPart.Cube;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HumanoidArmorLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.PlayerModelType;
import net.minecraft.world.item.ItemStack;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class EarsLayerRenderer extends RenderLayer<AvatarRenderState, PlayerModel> {

	private final AvatarRenderer<LocalPlayer> per;

	public EarsLayerRenderer(AvatarRenderer<LocalPlayer> per) {
		super(per);
        this.per = per;
        EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}

	@Override
	public void submit(PoseStack m, SubmitNodeCollector vertexConsumers, int light, AvatarRenderState entity, float limbAngle, float limbDistance) {
//		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
		delegate.render(m, vertexConsumers, entity, light, LivingEntityRenderer.getOverlayCoords(entity, 0));
	}

	public void renderLeftArm(PoseStack m, SubmitNodeCollector vertexConsumers, int light) {
        AvatarRenderState state = per.createRenderState(Minecraft.getInstance().player, 1.0f);
		delegate.render(m, vertexConsumers, state, light, LivingEntityRenderer.getOverlayCoords(state, 0), BodyPart.LEFT_ARM);
	}

	public void renderRightArm(PoseStack m, SubmitNodeCollector vertexConsumers, int light) {
        AvatarRenderState state = per.createRenderState(Minecraft.getInstance().player, 1.0f);
		delegate.render(m, vertexConsumers, state, light, LivingEntityRenderer.getOverlayCoords(state, 0), BodyPart.RIGHT_ARM);
	}

	public static EarsFeatures getEarsFeatures(AvatarRenderState peer) {
		Identifier skin = peer.skin.body().id();
		AbstractTexture tex = Minecraft.getInstance().getTextureManager().getTexture(skin);
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "getEarsFeatures(): skin={}, tex={}", skin, tex);
		if (tex instanceof EarsFeaturesHolder) {
			EarsFeatures feat = ((EarsFeaturesHolder)tex).getEarsFeatures();
			EarsFeaturesStorage.INSTANCE.put(peer.scoreText == null ? Minecraft.getInstance().name() : peer.scoreText.getString(), /*peer.getGameProfile().getId()*/null, feat);
			if (!peer.isInvisible) {
				return feat;
			}
		}
		return EarsFeatures.DISABLED;
	}

	// official Mojang mappings continue to baffle me. PoseStack????????????
	// MCP mappings were bad but they never managed to make me irrationally angry
	private final IndirectEarsRenderDelegate<PoseStack, SubmitNodeCollector, RenderType, AvatarRenderState, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {

		@Override
		protected Decider<BodyPart, ModelPart> decideModelPart(Decider<BodyPart, ModelPart> d) {
			PlayerModel model = getParentModel();
			return d.map(BodyPart.HEAD, model.head)
					.map(BodyPart.LEFT_ARM, model.leftArm)
					.map(BodyPart.LEFT_LEG, model.leftLeg)
					.map(BodyPart.RIGHT_ARM, model.rightArm)
					.map(BodyPart.RIGHT_LEG, model.rightLeg)
					.map(BodyPart.TORSO, model.body);
		}

		@Override
		protected void doAnchorTo(BodyPart part, ModelPart modelPart) {
			modelPart.translateAndRotate(matrices);
			Cube cuboid = modelPart.getRandomCube(NotRandom119.INSTANCE);
			matrices.scale(1/16f, 1/16f, 1/16f);
			matrices.translate(cuboid.minX, cuboid.maxY, cuboid.minZ);
		}

		@Override
		protected boolean isVisible(ModelPart modelPart) {
			return modelPart.visible;
		}

		@Override
		protected EarsFeatures getEarsFeatures() {
			return EarsLayerRenderer.getEarsFeatures(peer);
		}

		@Override
		public boolean isSlim() {
			return peer.skin.model() == PlayerModelType.SLIM;
		}

		@Override
		protected void pushMatrix() {
			matrices.pushPose();
		}

		@Override
		protected void popMatrix() {
			matrices.popPose();
		}

		@Override
		protected void doTranslate(float x, float y, float z) {
			matrices.translate(x, y, z);
		}

		@Override
		protected void doRotate(float ang, float x, float y, float z) {
			matrices.mulPose(new AxisAngle4f(ang*Mth.DEG_TO_RAD, x, y, z).get(new Quaternionf()));
		}

		@Override
		protected void doScale(float x, float y, float z) {
			matrices.scale(x, y, z);
		}

		@Override
		protected void doUploadAux(TexSource src, byte[] pngData) {
			Identifier skin = peer.skin.body().id();
			Identifier id = Identifier.tryBuild(skin.getNamespace(), src.addSuffix(skin.getPath()));
			TextureManager textureManager = Minecraft.getInstance().getTextureManager();
			if (pngData != null && !((AccessorTextureManager) textureManager).ears$getTextures().containsKey(id)) {
				try {
					textureManager.register(id, new DynamicTexture(src::toString, NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					e.printStackTrace();
//					Minecraft.getInstance().getTextureManager().register(id, MissingTextureAtlasSprite.getTexture());
				}
			}
		}

		private float armorR = 1;
		private float armorG = 1;
		private float armorB = 1;
		private float armorA = 1;

		private HumanoidArmorLayer<?, ?, ?> afr;

		// Fabric API compat
		private final List<MethodHandle> entityCaptures = Lists.newArrayList();
		private final List<MethodHandle> slotCaptures = Lists.newArrayList();

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void doBindBuiltin(TexSource src) {
			commitQuads();
			if (src.isGlint()) {
				armorR = armorG = armorB = armorA = 1;
				vc = RenderTypes.armorEntityGlint();
			} else if (canBind(src)) {
				EquipmentSlot slot = getSlot(src);
				ItemStack equipment = getEquippedStack(peer, slot);
				AccessorHumanoidArmorLayer aafr = (AccessorHumanoidArmorLayer)afr;

				if (equipment.get(DataComponents.DYED_COLOR) != null) {
					int c = equipment.get(DataComponents.DYED_COLOR).rgb();
					armorR = (c >> 16 & 255) / 255.0F;
					armorG = (c >> 8 & 255) / 255.0F;
					armorB = (c & 255) / 255.0F;
					armorA = 1;
				}

				try {
					setCaptures(peer, slot);
					aafr.ears$renderArmorPiece(matrices, vcp, equipment, slot, 0, peer);
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
			if (equipment.isEmpty() || !(equipment.getComponents().has(DataComponents.EQUIPPABLE))) return false;
			if (afr == null) {
				for (RenderLayer<?, ?> fr : ((AccessorLivingEntityRenderer)per).ears$getLayers()) {
					if (fr instanceof HumanoidArmorLayer<?,?,?>) {
						afr = (HumanoidArmorLayer<?, ?, ?>)fr;
						for (Field f : HumanoidArmorLayer.class.getDeclaredFields()) {
							try {
								f.setAccessible(true);
								if (Modifier.isStatic(f.getModifiers())) continue;
								if (EquipmentSlot.class == f.getType()) {
									slotCaptures.add(MethodHandles.lookup().unreflectSetter(f));
								} else if (HumanoidRenderState.class.isAssignableFrom(f.getType())) {
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
				AccessorHumanoidArmorLayer aafr = (AccessorHumanoidArmorLayer)afr;
				HumanoidModel<?> bmodel = (HumanoidModel<?>) aafr.ears$getModelSet().chest();
				HumanoidModel<?> lmodel = (HumanoidModel<?>) aafr.ears$getModelSet().legs();

				try {
					setCaptures(peer, slot);
					HumanoidModel<?> model = aafr.ears$getArmor(peer, slot);
					setCaptures(null, null);
					if (model != bmodel && model != lmodel) {
						// custom armor model
						return false;
					}
					return glint ? equipment.hasFoil() : true;
				} catch (Throwable t) {
					EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Exception while attempting to retrieve armor model", t);
					return false;
				}
			}
			return false;
		}

		private void setCaptures(HumanoidRenderState entity, EquipmentSlot slot) {
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

		private ItemStack getEquippedStack(AvatarRenderState peer, EquipmentSlot slot) {
			return switch(slot) {
				case HEAD -> peer.headEquipment;
				case CHEST -> peer.chestEquipment;
				case LEGS -> peer.legsEquipment;
				case FEET -> peer.feetEquipment;
				default -> null;
			};
		}

		private final PoseStack.Pose IDENTITY3 = new PoseStack().last();

		@Override
		protected void addVertex(float x, float y, int z, float r, float g, float b, float a, float u, float v, float nX, float nY, float nZ) {
            r *= armorR;
            g *= armorG;
            b *= armorB;
            a *= armorA;

            final PoseStack.Pose snapshot = matrices.last().copy();
            final Matrix4f positionMatrix = snapshot.pose();
            final PoseStack.Pose normalMatrix = emissive ? IDENTITY3 : snapshot;

            final int packedLight = emissive ? LightTexture.pack(15, 15) : light;
            final int packedOverlay = overlay;

            final PoseStack frozenStack = new PoseStack();
            frozenStack.last().pose().set(positionMatrix);
            frozenStack.last().normal().set(snapshot.normal());

            final float fr = r, fg = g, fb = b, fa = a;

            vcp.submitCustomGeometry(frozenStack, vc, (entry, consumer) -> {
                consumer.addVertex(positionMatrix, x, y, z)
                        .setColor(fr, fg, fb, fa)
                        .setUv(u, v)
                        .setOverlay(packedOverlay)
                        .setLight(packedLight)
                        .setNormal(normalMatrix, nX, nY, nZ);
            });
		}

		@Override
		protected void commitQuads() {
			// not implemented
		}

		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// TODO port this to core profile (no)
		}

		@Override
		protected RenderType getVertexConsumer(TexSource src) {
			armorR = armorG = armorB = armorA = 1;
			Identifier id = peer.skin.body().id();
			if (src != TexSource.SKIN) {
				id = Identifier.tryBuild(id.getNamespace(), src.addSuffix(id.getPath()));
			}
			return RenderTypes.itemEntityTranslucentCull(id);
		}

		@Override
		public float getTime() {
			return peer.ageInTicks;
		}

		@Override
		public boolean isFlying() {
			return ((EarsPlayerRenderState)peer).ears$isFlying();
		}

		@Override
		public boolean isGliding() {
			return peer.isFallFlying;
		}

		@Override
		public boolean isJacketEnabled() {
			return peer.showJacket;
		}

		@Override
		public boolean isWearingBoots() {
			return peer.feetEquipment.getComponents().has(DataComponents.EQUIPPABLE);
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.chestEquipment.getComponents().has(DataComponents.EQUIPPABLE) && !isWearingElytra();
		}

		@Override
		public boolean isWearingElytra() {
			return peer.chestEquipment.getComponents().has(DataComponents.GLIDER);
		}

		@Override
		public float getHorizontalSpeed() {
			return ((EarsPlayerRenderState)peer).ears$getHorizontalSpeed();
		}

		@Override
		public float getLimbSwing() {
			return peer.attackTime;
		}

		@Override
		public float getStride() {
			return ((EarsPlayerRenderState)peer).ears$getStride();
		}

		@Override
		public float getBodyYaw() {
			return peer.bodyRot;
		}

		@Override
		public double getCapeX() {
			return ((EarsPlayerRenderState)peer).ears$getCapeX();
		}

		@Override
		public double getCapeY() {
			return ((EarsPlayerRenderState)peer).ears$getCapeY();
		}

		@Override
		public double getCapeZ() {
			return ((EarsPlayerRenderState)peer).ears$getCapeZ();
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
	};
}
