package com.unascribed.ears;

import com.google.common.collect.Lists;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.render.IndirectEarsRenderDelegate;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.mixin.AccessorArmorFeatureRenderer;
import com.unascribed.ears.mixin.AccessorLivingEntityRenderer;
import com.unascribed.ears.mixin.AccessorTextureManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPart.Cuboid;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.MovingBlockRenderState;
import net.minecraft.client.render.command.ModelCommandRenderer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.command.RenderCommandQueue;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.client.render.entity.state.EntityHitboxAndView;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.render.model.BlockStateModel;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerSkinType;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

public class EarsFeatureRenderer extends FeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {
	
	private final PlayerEntityRenderer<ClientPlayerEntity> per;
	
	public EarsFeatureRenderer(PlayerEntityRenderer<ClientPlayerEntity> per) {
		super(per);
		this.per = per;
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "Constructed");
	}
	
	@Override
	public void render(MatrixStack m, OrderedRenderCommandQueue queue, int light, PlayerEntityRenderState entity, float limbAngle, float limbDistance) {
		//EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {}, {}, {})", m, vertexConsumers, light, entity, limbAngle, limbDistance);
		delegate.render(m, queue, entity, light, LivingEntityRenderer.getOverlay(entity, 0));
	}
	
	public void renderLeftArm(MatrixStack m, OrderedRenderCommandQueue queue, int light) {
		@SuppressWarnings("resource")
		PlayerEntityRenderState state = per.getAndUpdateRenderState(MinecraftClient.getInstance().player, 1.0f);
		delegate.render(m, queue, state, light, LivingEntityRenderer.getOverlay(state, 0), BodyPart.LEFT_ARM);
	}
	
	public void renderRightArm(MatrixStack m, OrderedRenderCommandQueue queue, int light) {
		@SuppressWarnings("resource")
		PlayerEntityRenderState state = per.getAndUpdateRenderState(MinecraftClient.getInstance().player, 1.0f);
		delegate.render(m, queue, state, light, LivingEntityRenderer.getOverlay(state, 0), BodyPart.RIGHT_ARM);
	}

	private final IndirectEarsRenderDelegate<MatrixStack, OrderedRenderCommandQueue, RenderLayer, PlayerEntityRenderState, ModelPart> delegate = new IndirectEarsRenderDelegate<>() {

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
            modelPart.applyTransform(matrices);
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
			return peer.skinTextures.model() == PlayerSkinType.SLIM;
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
			Identifier skin = peer.skinTextures.body().id();
			Identifier id = Identifier.tryParse(skin.getNamespace(), src.addSuffix(skin.getPath()));
			TextureManager textureManager = MinecraftClient.getInstance().getTextureManager();
			if (pngData != null && !((AccessorTextureManager) textureManager).ears$getTextures().containsKey(id)) {
				try {
					textureManager.registerTexture(id, new NativeImageBackedTexture(src::toString, NativeImage.read(toNativeBuffer(pngData))));
				} catch (IOException e) {
					e.printStackTrace();
					//textureManager.registerTexture(id, MissingSprite.getMissingSpriteTexture());
				}
			}
		}
		
		private float armorR = 1;
		private float armorG = 1;
		private float armorB = 1;
		private float armorA = 1;
		
		private ArmorFeatureRenderer<?, ?, ?> afr;
		
		// Fabric API compat
		private final List<MethodHandle> entityCaptures = Lists.newArrayList();
		private final List<MethodHandle> slotCaptures = Lists.newArrayList();
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected void doBindBuiltin(TexSource src) {
			commitQuads();
			if (src.isGlint()) {
				armorR = armorG = armorB = armorA = 1;
				vc = RenderLayer.getArmorEntityGlint();
			} else if (canBind(src)) {
				EquipmentSlot slot = getSlot(src);
				ItemStack equipment = getEquippedStack(peer, slot);
				AccessorArmorFeatureRenderer aafr = (AccessorArmorFeatureRenderer) afr;
				if (equipment.get(DataComponentTypes.DYED_COLOR) != null) {
					int c = equipment.get(DataComponentTypes.DYED_COLOR).rgb();
					armorR = (c >> 16 & 255) / 255.0F;
					armorG = (c >> 8 & 255) / 255.0F;
					armorB = (c & 255) / 255.0F;
					armorA = 1;
				}
				try {
					setCaptures(peer, slot);
					aafr.ears$renderArmor(matrices, new OrderedRenderCommandQueue() {
						@Override
						public void submitDebugHitbox(MatrixStack matrices, EntityRenderState renderState, EntityHitboxAndView debugHitbox) {

						}

						@Override
						public void submitShadowPieces(MatrixStack matrices, float shadowRadius, List<EntityRenderState.ShadowPiece> shadowPieces) {

						}

						@Override
						public void submitLabel(MatrixStack matrices, @Nullable Vec3d nameLabelPos, int y, Text label, boolean notSneaking, int light, double squaredDistanceToCamera, CameraRenderState cameraState) {

						}

						@Override
						public void submitText(MatrixStack matrices, float x, float y, OrderedText text, boolean dropShadow, TextRenderer.TextLayerType layerType, int light, int color, int backgroundColor, int outlineColor) {

						}

						@Override
						public void submitFire(MatrixStack matrices, EntityRenderState renderState, Quaternionf rotation) {

						}

						@Override
						public void submitLeash(MatrixStack matrices, EntityRenderState.LeashData leashData) {

						}

						@Override
						public <S> void submitModel(Model<? super S> model, S state, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, int tintedColor, @Nullable Sprite sprite, int outlineColor, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay) {
							vc = renderLayer;
						}

						@Override
						public void submitModelPart(ModelPart part, MatrixStack matrices, RenderLayer renderLayer, int light, int overlay, @Nullable Sprite sprite, boolean sheeted, boolean hasGlint, int tintedColor, @Nullable ModelCommandRenderer.CrumblingOverlayCommand crumblingOverlay, int i) {

						}

						@Override
						public void submitBlock(MatrixStack matrices, BlockState state, int light, int overlay, int outlineColor) {

						}

						@Override
						public void submitMovingBlock(MatrixStack matrices, MovingBlockRenderState state) {

						}

						@Override
						public void submitBlockStateModel(MatrixStack matrices, RenderLayer renderLayer, BlockStateModel model, float r, float g, float b, int light, int overlay, int outlineColor) {

						}

						@Override
						public void submitItem(MatrixStack matrices, ItemDisplayContext displayContext, int light, int overlay, int outlineColors, int[] tintLayers, List<BakedQuad> quads, RenderLayer renderLayer, ItemRenderState.Glint glintType) {

						}

						@Override
						public void submitCustom(MatrixStack matrices, RenderLayer renderLayer, Custom customRenderer) {

						}

						@Override
						public void submitCustom(LayeredCustom customRenderer) {

						}

						@Override
						public RenderCommandQueue getBatchingQueue(int order) {
							return this;
						}
					}, equipment, slot, 0, peer);
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
			if (equipment.isEmpty() || !(equipment.getComponents().contains(DataComponentTypes.EQUIPPABLE))) return false;
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
				BipedEntityModel<?> bmodel = (BipedEntityModel<?>) aafr.ears$getModelData().chest();
				BipedEntityModel<?> lmodel = (BipedEntityModel<?>) aafr.ears$getModelData().legs();

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

            final MatrixStack.Entry snapshot = matrices.peek().copy();
            final Matrix4f positionMatrix = snapshot.getPositionMatrix();
            final MatrixStack.Entry normalMatrix = emissive ? IDENTITY3 : snapshot;

            final int packedLight = emissive ? LightmapTextureManager.pack(15, 15) : light;
            final int packedOverlay = overlay;

            final MatrixStack frozenStack = new MatrixStack();
            frozenStack.peek().getPositionMatrix().set(positionMatrix);
            frozenStack.peek().getNormalMatrix().set(snapshot.getNormalMatrix());

            final float fr = r, fg = g, fb = b, fa = a;

            vcp.submitCustom(frozenStack, vc, (entry, consumer) -> {
                consumer.vertex(positionMatrix, x, y, z)
                        .color(fr, fg, fb, fa)
                        .texture(u, v)
                        .overlay(packedOverlay)
                        .light(packedLight)
                        .normal(normalMatrix, nX, nY, nZ);
            });
		}
		
		@Override
		protected void commitQuads() {
            // not implemented
		}
		
		@Override
		protected void doRenderDebugDot(float r, float g, float b, float a) {
			// not implemented on post-1.17 versions
		}

        @Override
        protected RenderLayer getVertexConsumer(TexSource src) {
            armorR = armorG = armorB = armorA = 1;
            Identifier id = peer.skinTextures.body().id();
            if (src != TexSource.SKIN) {
                id = Identifier.tryParse(id.getNamespace(), src.addSuffix(id.getPath()));
            }
            return RenderLayer.getItemEntityTranslucentCull(id);
        }

		@Override
		public float getTime() {
			return peer.age;
		}

		@Override
		public boolean isFlying() {
			return ((EarsPlayerRenderState)peer).ears$isFlying();
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
			return peer.equippedFeetStack.getComponents().contains(DataComponentTypes.EQUIPPABLE);
		}

		@Override
		public boolean isWearingChestplate() {
			return peer.equippedChestStack.getComponents().contains(DataComponentTypes.EQUIPPABLE) && !isWearingElytra();
		}

		@Override
		public boolean isWearingElytra() {
			//TODO: this is not correct, should check for EQUIPPABLE that has actual textures under assetId for elytra
			return peer.equippedChestStack.getComponents().contains(DataComponentTypes.GLIDER);
		}

		@Override
		public float getLimbSwing() {
			return peer.limbSwingAmplitude;
		}

		@Override
		public float getBodyYaw() {
			return peer.bodyYaw;
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

		@Override
		public float getHorizontalSpeed() {
			return ((EarsPlayerRenderState)peer).ears$getHorizontalSpeed();
		}

		@Override
		public float getStride() {
			return ((EarsPlayerRenderState)peer).ears$getStride();
		}
	};
}
