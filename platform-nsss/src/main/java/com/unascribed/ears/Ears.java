package com.unascribed.ears;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;
import com.unascribed.ears.common.util.EarsStorage;
import com.unascribed.ears.legacy.LegacyHelper;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.entity.EntityPlayer;
import com.mojang.minecraft.entity.model.ModelBiped;
import com.mojang.minecraft.entity.model.ModelRenderer;
import com.mojang.minecraft.entity.render.RenderLiving;
import com.mojang.minecraft.entity.render.RenderPlayer;
import com.mojang.minecraft.render.ImageBufferDownload;
import com.mojang.minecraft.render.PositionTexureVertex;
import com.mojang.minecraft.render.TexturedQuad;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsCommon.StripAlphaMethod;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.EarsFeatures;

public class Ears {
	
	public static Minecraft game;
	
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	public static boolean forceTextureHeight = false;
	
	private static LayerEars layer;
	
	public static ModelBiped myModel;
	
	public static ModelRenderer slimLeftArm;
	public static ModelRenderer slimRightArm;
	
	public static ModelRenderer fatLeftArm;
	public static ModelRenderer fatRightArm;
	
	public static void init(Minecraft minecraft) {
		game = minecraft;
		if (EarsLog.DEBUG) {
			EarsLog.debugva("Platform", "Initialized - Not So Seecret Saturday");
		}
		layer = new LayerEars();
	}
	
	public static void amendTexturedQuad(TexturedQuad subject, PositionTexureVertex[] apositiontexurevertex, int i, int j, int k, int l) {
		EarsLog.debug("Platform:Inject", "amendTexturedQuad({}, ..., {}, {}, {}, {}); forceTexturedHeight={}", subject, i, j, k, l, forceTextureHeight);
		if (forceTextureHeight) {
			float f = 0.0015625F;
			float f1 = 0.003125F;
			apositiontexurevertex[0] = apositiontexurevertex[0].func_1115_a(k / 64.0F - f, j / 64.0F + f1);
			apositiontexurevertex[1] = apositiontexurevertex[1].func_1115_a(i / 64.0F + f, j / 64.0F + f1);
			apositiontexurevertex[2] = apositiontexurevertex[2].func_1115_a(i / 64.0F + f, l / 64.0F - f1);
			apositiontexurevertex[3] = apositiontexurevertex[3].func_1115_a(k / 64.0F - f, l / 64.0F - f1);
		}
	}

	public static void amendPlayerRenderer(RenderPlayer rp) throws IllegalArgumentException, IllegalAccessException {
		EarsLog.debug("Platform", "Hacking 64x64 skin support into player model");
		forceTextureHeight = true;
		ModelBiped model = new ModelBiped(0, 0);
		myModel = model;
		
		Field mainModel = RenderLiving.class.getDeclaredFields()[0];
		mainModel.setAccessible(true);
		mainModel.set(rp, model);
		setModelBipedMain(rp, model);
		
		// translucent head layer
		model.bipedHeadwear = new ModelRendererTrans(32, 0);
		model.bipedHeadwear.func_923_a(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		model.bipedHeadwear.func_925_a(0.0F, 0.0F, 0.0F);
		
		// non-flipped left arm/leg
		model.bipedLeftArm = new ModelRenderer(32, 48);
		model.bipedLeftArm.func_923_a(-1, -2, -2, 4, 12, 4, 0);
		model.bipedLeftArm.func_925_a(5, 2, 0);
		
		model.bipedLeftLeg = new ModelRenderer(16, 48);
		model.bipedLeftLeg.func_923_a(-2, 0, -2, 4, 12, 4, 0);
		model.bipedLeftLeg.func_925_a(1.9f, 12, 0);
		
		fatLeftArm = model.bipedLeftArm;
		fatRightArm = model.bipedRightArm;
		
		// slim arms
		slimLeftArm = new ModelRenderer(32, 48);
		slimLeftArm.func_923_a(-1, -2, -2, 3, 12, 4, 0);
		slimLeftArm.func_925_a(5, 2.5f, 0);
		
		slimRightArm = new ModelRenderer(40, 16);
		slimRightArm.func_923_a(-2, -2, -2, 3, 12, 4, 0);
		slimRightArm.func_925_a(-5, 2.5f, 0);
		
		flipBottom(model.bipedHead);
		flipBottom(model.bipedBody);
		flipBottom(fatLeftArm);
		flipBottom(slimLeftArm);
		flipBottom(fatRightArm);
		flipBottom(slimRightArm);
		flipBottom(model.bipedLeftLeg);
		flipBottom(model.bipedRightLeg);
		flipBottom(model.bipedHeadwear);
		
		forceTextureHeight = false;
	}
	
	private static void flipBottom(ModelRenderer box) {
		TexturedQuad[] quads = getQuads(box);
		PositionTexureVertex[] verts = quads[3].field_1195_a;
		float minV = verts[0].field_1656_c;
		float maxV = verts[2].field_1656_c;
		verts[0].field_1656_c = maxV;
		verts[1].field_1656_c = maxV;
		verts[2].field_1656_c = minV;
		verts[3].field_1656_c = minV;
	}

	public static void renderSpecials(RenderPlayer render, EntityPlayer player, float f) {
		EarsLog.debug("Platform:Inject:Renderer", "renderSpecials player={}, partialTicks={}", player, f);
		layer.doRenderLayer(render, player,
				player.field_705_Q + (player.limbSwingAmount - player.field_705_Q) * f,
				f);
	}
	
	public static BufferedImage interceptParseUserSkin(final ImageBufferDownload subject, BufferedImage image) {
		EarsLog.debug("Platform:Inject", "parseUserSkin({}, {})", subject, image);
		if (image == null) {
			EarsLog.debug("Platform:Inject", "parseUserSkin(...): Image is null");
			return null;
		} else {
			setImageWidth(subject, 64);
			setImageHeight(subject, 64);
			BufferedImage newImg = new BufferedImage(64, 64, 2);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);

			if (image.getHeight() == 32) {
				EarsLog.debug("Platform:Inject", "parseUserSkin(...): Upgrading legacy skin");
				g.drawImage(newImg, 24, 48, 20, 52, 4, 16, 8, 20, null);
				g.drawImage(newImg, 28, 48, 24, 52, 8, 16, 12, 20, null);
				g.drawImage(newImg, 20, 52, 16, 64, 8, 20, 12, 32, null);
				g.drawImage(newImg, 24, 52, 20, 64, 4, 20, 8, 32, null);
				g.drawImage(newImg, 28, 52, 24, 64, 0, 20, 4, 32, null);
				g.drawImage(newImg, 32, 52, 28, 64, 12, 20, 16, 32, null);
				g.drawImage(newImg, 40, 48, 36, 52, 44, 16, 48, 20, null);
				g.drawImage(newImg, 44, 48, 40, 52, 48, 16, 52, 20, null);
				g.drawImage(newImg, 36, 52, 32, 64, 48, 20, 52, 32, null);
				g.drawImage(newImg, 40, 52, 36, 64, 44, 20, 48, 32, null);
				g.drawImage(newImg, 44, 52, 40, 64, 40, 20, 44, 32, null);
				g.drawImage(newImg, 48, 52, 44, 64, 52, 20, 56, 32, null);
			}
			
			g.dispose();
			
			EarsStorage.put(newImg, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new AWTEarsImage(newImg)));
			
			setImageData(subject, ((DataBufferInt) newImg.getRaster().getDataBuffer()).getData());
			EarsCommon.carefullyStripAlpha(new StripAlphaMethod() {
				@Override
				public void stripAlpha(int _x1, int _y1, int _x2, int _y2) {
					setAreaOpaque(subject, _x1, _y1, _x2, _y2);
				}
			}, true);
			setAreaTransparent(subject, 32, 0, 64, 32);
			setAreaTransparent(subject, 0, 32, 16, 48);
			setAreaTransparent(subject, 16, 32, 40, 48);
			setAreaTransparent(subject, 40, 32, 56, 48);
			setAreaTransparent(subject, 0, 48, 16, 64);
			setAreaTransparent(subject, 48, 48, 64, 64);
			return newImg;
		}
	}
	
	public static void checkSkin(Object tdi, BufferedImage img) {
		EarsLog.debug("Platform:Inject", "Process player skin");
		earsSkinFeatures.put(getLocation(tdi), EarsFeatures.detect(new AWTEarsImage(img), EarsStorage.get(img, EarsStorage.Key.ALFALFA)));
	}
	
	public static String amendSkinUrl(String username) {
		EarsLog.debug("Platform:Inject", "Amend skin URL {}", username);
		return LegacyHelper.getSkinUrl(username);
	}
	
	public static void beforeRender(RenderPlayer rp) {
		beforeRender(rp, game.thePlayer);
	}
	
	public static void beforeRender(RenderPlayer rp, EntityPlayer player) {
		EarsLog.debug("Platform:Inject:Renderer", "Before render rp={} player={}", rp, player);
		boolean slim = LegacyHelper.isSlimArms(player.playerName);
		ModelBiped modelBipedMain = getModelBipedMain(rp);
		if (slim) {
			modelBipedMain.bipedLeftArm = slimLeftArm;
			modelBipedMain.bipedRightArm = slimRightArm;
		} else {
			modelBipedMain.bipedLeftArm = fatLeftArm;
			modelBipedMain.bipedRightArm = fatRightArm;
		}
	}
	
	public static void renderFirstPersonArm(RenderPlayer rp) {
		layer.renderRightArm(rp, game.thePlayer);
	}
	
	private static final Method setAreaOpaque;
	private static final Method setAreaTransparent;
	private static final Field imageHeight;
	private static final Field imageWidth;
	private static final Field imageData;
	private static final Field location;
	private static final Field modelBipedMain;
	private static final Field vertices;
	private static final Field quads;
	static {
		try {
			setAreaOpaque = ImageBufferDownload.class.getDeclaredMethod("func_884_b", int.class, int.class, int.class, int.class);
			setAreaOpaque.setAccessible(true);
			
			setAreaTransparent = ImageBufferDownload.class.getDeclaredMethod("func_885_a", int.class, int.class, int.class, int.class);
			setAreaTransparent.setAccessible(true);
			
			imageHeight = ImageBufferDownload.class.getDeclaredField("field_1344_c");
			imageHeight.setAccessible(true);
			
			imageWidth = ImageBufferDownload.class.getDeclaredField("field_1342_b");
			imageWidth.setAccessible(true);
			
			imageData = ImageBufferDownload.class.getDeclaredField("field_1343_a");
			imageData.setAccessible(true);
			
			location = Class.forName("com.mojang.minecraft.util.ThreadDownloadImage").getDeclaredField("field_1216_a");
			location.setAccessible(true);
			
			modelBipedMain = RenderPlayer.class.getDeclaredField("field_209_f");
			modelBipedMain.setAccessible(true);
			
			vertices = ModelRenderer.class.getDeclaredField("field_1401_j");
			vertices.setAccessible(true);
			
			quads = ModelRenderer.class.getDeclaredField("field_1400_k");
			quads.setAccessible(true);
		} catch (Throwable t) {
			throw new Error(t);
		}
	}
	
	private static int getImageWidth(ImageBufferDownload subject) {
		try {
			return (int)imageWidth.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static int getImageHeight(ImageBufferDownload subject) {
		try {
			return (int)imageHeight.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static String getLocation(Object subject) {
		try {
			return (String)location.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	public static PositionTexureVertex[] getVertices(ModelRenderer subject) {
		try {
			return (PositionTexureVertex[])vertices.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	public static TexturedQuad[] getQuads(ModelRenderer subject) {
		try {
			return (TexturedQuad[])quads.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageWidth(ImageBufferDownload subject, int i) {
		try {
			imageWidth.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageHeight(ImageBufferDownload subject, int i) {
		try {
			imageHeight.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageData(ImageBufferDownload subject, int[] data) {
		try {
			imageData.set(subject, data);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setModelBipedMain(RenderPlayer subject, ModelBiped model) {
		try {
			modelBipedMain.set(subject, model);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	public static ModelBiped getModelBipedMain(RenderPlayer subject) {
		try {
			return (ModelBiped)modelBipedMain.get(subject);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaOpaque(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			EarsLog.debug("Platform:Inject", "stripAlpha({}, {}, {}, {}, {})", subject, x1, y1, x2, y2);
			setAreaOpaque.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaTransparent(ImageBufferDownload subject, int x1, int y1, int x2, int y2) {
		try {
			setAreaTransparent.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	
}
