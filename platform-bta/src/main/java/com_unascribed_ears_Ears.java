import static org.lwjgl.opengl.GL11.*;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import javax.imageio.ImageIO;

import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.legacy.AWTEarsImage;
import com.unascribed.ears.common.legacy.ImmediateEarsRenderDelegate;
import com.unascribed.ears.common.render.EarsRenderDelegate.BodyPart;
import com.unascribed.ears.common.util.Decider;
import com.unascribed.ears.common.util.EarsStorage;
import com.unascribed.ears.legacy.LegacyHelper;

import net.minecraft.client.Minecraft;

public class com_unascribed_ears_Ears {

	private static final com_unascribed_ears_Ears INST = new com_unascribed_ears_Ears();
	
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	public static Minecraft game;
	
	private ds render;
	private float brightness;
	private float tickDelta;
	
	public static boolean forceBipedTextureHeight = false;
	
	public static ps slimLeftArm;
	public static ps slimRightArm;
	public static ps fatLeftArm;
	public static ps fatRightArm;
	
	public static void init(Minecraft minecraft) {
		game = minecraft;
		if (EarsLog.DEBUG) {
			EarsLog.debugva(EarsLog.Tag.PLATFORM, "Initialized - Better Than Adventure");
		}
	}
	
	public static void postRenderSpecials(ds renderPlayer, gs entity, float partialTicks) {
		INST.render(renderPlayer, entity, partialTicks);
	}
	
	public static void postDrawFirstPersonHand(ds renderPlayer) {
		INST.renderRightArm(renderPlayer, game.h, 0);
	}
	
	public static void preprocessSkin(rr subject, BufferedImage rawImg, BufferedImage img) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "preprocessSkin({}, {}, {})", subject, rawImg, img);
		EarsStorage.put(img, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new AWTEarsImage(rawImg)));
	}
	
	public static BufferedImage interceptParseUserSkin(final rr subject, BufferedImage image) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin({}, {})", subject, image);
		if (image == null) {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin(...): Image is null");
			return null;
		} else {
			setImageWidth(subject, 64);
			setImageHeight(subject, 64);
			BufferedImage newImg = new BufferedImage(64, 64, 2);
			Graphics g = newImg.getGraphics();
			g.drawImage(image, 0, 0, null);

			if (image.getHeight() == 32) {
				EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "parseUserSkin(...): Upgrading legacy skin");
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
			EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setAreaOpaque(subject, _x1, _y1, _x2, _y2), true);
			setAreaTransparent(subject, 32, 0, 64, 32);
			setAreaTransparent(subject, 0, 32, 16, 48);
			setAreaTransparent(subject, 16, 32, 40, 48);
			setAreaTransparent(subject, 40, 32, 56, 48);
			setAreaTransparent(subject, 0, 48, 16, 64);
			setAreaTransparent(subject, 48, 48, 64, 64);
			return newImg;
		}
	}
	
	private static final Method rr_setAreaOpaque;
	private static final Method rr_setAreaTransparent;
	private static final Field rr_imageHeight;
	private static final Field rr_imageWidth;
	private static final Field rr_imageData;
	private static final Field ps_vertices;
	private static final Field ds_modelBipedMain;
	static {
		try {
			rr_setAreaOpaque = rr.class.getDeclaredMethod("b", int.class, int.class, int.class, int.class);
			rr_setAreaOpaque.setAccessible(true);
			
			rr_setAreaTransparent = rr.class.getDeclaredMethod("a", int.class, int.class, int.class, int.class);
			rr_setAreaTransparent.setAccessible(true);
			
			rr_imageHeight = rr.class.getDeclaredField("c");
			rr_imageHeight.setAccessible(true);
			
			rr_imageWidth = rr.class.getDeclaredField("b");
			rr_imageWidth.setAccessible(true);
			
			rr_imageData = rr.class.getDeclaredField("a");
			rr_imageData.setAccessible(true);
			
			ps_vertices = ps.class.getDeclaredField("j");
			ps_vertices.setAccessible(true);
			
			ds_modelBipedMain = ds.class.getDeclaredField("a");
			ds_modelBipedMain.setAccessible(true);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static void setImageWidth(rr subject, int i) {
		try {
			rr_imageWidth.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageHeight(rr subject, int i) {
		try {
			rr_imageHeight.set(subject, i);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setImageData(rr subject, int[] data) {
		try {
			rr_imageData.set(subject, data);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaOpaque(rr subject, int x1, int y1, int x2, int y2) {
		try {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "stripAlpha({}, {}, {}, {}, {})", subject, x1, y1, x2, y2);
			rr_setAreaOpaque.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	private static void setAreaTransparent(rr subject, int x1, int y1, int x2, int y2) {
		try {
			rr_setAreaTransparent.invoke(subject, x1, y1, x2, y2);
		} catch (Throwable e) {
			if (e instanceof RuntimeException) throw (RuntimeException)e;
			throw new RuntimeException(e);
		}
	}
	
	private static ib[] getVertices(ps ps) {
		try {
			return (ib[])ps_vertices.get(ps);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static fh getModelBipedMain(ds ds) {
		try {
			return (fh)ds_modelBipedMain.get(ds);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static void setModelBipedMain(ds ds, fh fh) {
		try {
			ds_modelBipedMain.set(ds, fh);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public static void beforeRender(ds rp) {
		beforeRender(rp, game.h);
	}
	
	public static void beforeRender(ds rp, gs player) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT_RENDERER, "Before render rp={} player={}", rp, player);
		boolean slim = LegacyHelper.isSlimArms(player.l);
		fh modelBipedMain = getModelBipedMain(rp);
		if (slim) {
			modelBipedMain.e = slimLeftArm;
			modelBipedMain.d = slimRightArm;
		} else {
			modelBipedMain.e = fatLeftArm;
			modelBipedMain.d = fatRightArm;
		}
	}
	
	public static void checkSkin(String url, BufferedImage img) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "checkSkin({}, {})", url, img);
		earsSkinFeatures.put(url, EarsFeatures.detect(new AWTEarsImage(img), EarsStorage.get(img, EarsStorage.Key.ALFALFA),
				data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));
	}
	
	public static String amendSkinUrl(String url) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "Amend skin URL {}", url);
		if (url.startsWith("https://betacraft.pl/skin/") && url.endsWith(".png")) {
			final String username = url.substring(26, url.length()-4);
			return LegacyHelper.getSkinUrl(username);
		} else if (url.startsWith("http://s3.amazonaws.com/MinecraftSkins/") && url.endsWith(".png")) {
			final String username = url.substring(39, url.length()-4);
			return LegacyHelper.getSkinUrl(username);
		}
		return url;
	}
	
	private static boolean forceTextureHeight;
	private static fh myModel;
	
	public static void amendTexturedQuad(tz subject, ib[] apositiontexurevertex, int i, int j, int k, int l) {
		if (forceTextureHeight) {
			EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "amendTexturedQuad({}, ..., {}, {}, {}, {})", subject, i, j, k, l);
			float f = 0.0015625F;
			float f1 = 0.003125F;
			apositiontexurevertex[0] = apositiontexurevertex[0].a(k / 64.0F - f, j / 64.0F + f1);
			apositiontexurevertex[1] = apositiontexurevertex[1].a(i / 64.0F + f, j / 64.0F + f1);
			apositiontexurevertex[2] = apositiontexurevertex[2].a(i / 64.0F + f, l / 64.0F - f1);
			apositiontexurevertex[3] = apositiontexurevertex[3].a(k / 64.0F - f, l / 64.0F - f1);
		}
	}

	public static void amendPlayerRenderer(ds rp) throws IllegalArgumentException, IllegalAccessException {
		EarsLog.debug(EarsLog.Tag.PLATFORM, "Hacking 64x64 skin support into player model");
		forceTextureHeight = true;
		fh model = new fh(0, 0);
		myModel = model;
		
		Field mainModel = gv.class.getDeclaredFields()[0];
		mainModel.setAccessible(true);
		mainModel.set(rp, model);
		setModelBipedMain(rp, model);
		
		// translucent head layer
		model.b = new com_unascribed_ears_ModelRendererTrans(32, 0);
		model.b.a(-4.0F, -8.0F, -4.0F, 8, 8, 8, 0.5F);
		model.b.a(0.0F, 0.0F, 0.0F);
		
		// non-flipped left arm/leg
		model.e = new ps(32, 48);
		model.e.a(-1, -2, -2, 4, 12, 4, 0);
		model.e.a(5, 2, 0);
		
		model.g = new ps(16, 48);
		model.g.a(-2, 0, -2, 4, 12, 4, 0);
		model.g.a(1.9f, 12, 0);
		
		fatLeftArm = model.e;
		fatRightArm = model.d;
		
		// slim arms
		slimLeftArm = new ps(32, 48);
		slimLeftArm.a(-1, -2, -2, 3, 12, 4, 0);
		slimLeftArm.a(5, 2.5f, 0);
		
		slimRightArm = new ps(40, 16);
		slimRightArm.a(-2, -2, -2, 3, 12, 4, 0);
		slimRightArm.a(-5, 2.5f, 0);
		
		flipBottom(model.a);
		flipBottom(model.c);
		flipBottom(fatLeftArm);
		flipBottom(slimLeftArm);
		flipBottom(fatRightArm);
		flipBottom(slimRightArm);
		flipBottom(model.g);
		flipBottom(model.f);
		flipBottom(model.b);
		
		forceTextureHeight = false;
	}
	
	private static void flipBottom(ps box) throws IllegalArgumentException, IllegalAccessException, SecurityException {
		Field quadsF = ps.class.getDeclaredFields()[1];
		quadsF.setAccessible(true);
		tz[] quads = (tz[]) quadsF.get(box);
		ib[] verts = quads[3].a;
		float minV = verts[0].c;
		float maxV = verts[2].c;
		verts[0].c = maxV;
		verts[1].c = maxV;
		verts[2].c = minV;
		verts[3].c = minV;
	}
	
	public void render(ds render, gs entity, float partialTicks) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "render({}, {}, {})", entity, 0, partialTicks);
		this.render = render;
		this.brightness = entity.a(partialTicks);
		this.tickDelta = partialTicks;
		delegate.render(entity);
		this.render = null;
	}
	
	public void renderRightArm(ds render, gs entity, float partialTicks) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_RENDERER, "renderRightArm({}, {}, {})", render, entity, partialTicks);
		this.render = render;
		this.brightness = entity.a(partialTicks);
		this.tickDelta = partialTicks;
		delegate.render(entity, BodyPart.RIGHT_ARM);
		this.render = null;
	}
	
	private final ImmediateEarsRenderDelegate<gs, ps> delegate = new ImmediateEarsRenderDelegate<gs, ps>() {
		
		@Override
		protected boolean isVisible(ps modelPart) {
			return modelPart.h;
		}
		
		@Override
		public boolean isSlim() {
			return LegacyHelper.isSlimArms(peer.l);
		}
		
		@Override
		protected EarsFeatures getEarsFeatures() {
			return earsSkinFeatures.containsKey(getSkinUrl()) ? earsSkinFeatures.get(getSkinUrl()) : EarsFeatures.DISABLED;
		}
		
		@Override
		protected void doBindSkin() {
			try {
				render.a(getSkinUrl(), peer.q_());
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		protected void doAnchorTo(BodyPart part, ps modelPart) {
			modelPart.c(1/16f); // postRender (you can tell because it's the one that applies transforms without matrix isolation)
			glScalef(1/16f, 1/16f, 1/16f);
			ib vert = getVertices(modelPart)[3];
			glTranslatef((float)vert.a.a, (float)vert.a.b, (float)vert.a.c);
		}
		
		@Override
		protected Decider<BodyPart, ps> decideModelPart(Decider<BodyPart, ps> d) {
			// safe, cast is performed in the RenderPlayer (ds) constructor
			fh modelBiped = (fh)render.e;
			// this field order has never changed
			// a          b              c          d              e             f              g
			// bipedHead, bipedHeadwear, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg, bipedLeftLeg
			// thank god notch's proguard config doesn't randomize member order
			return d.map(BodyPart.HEAD, modelBiped.a)
					.map(BodyPart.LEFT_ARM, modelBiped.e)
					.map(BodyPart.LEFT_LEG, modelBiped.g)
					.map(BodyPart.RIGHT_ARM, modelBiped.d)
					.map(BodyPart.RIGHT_LEG, modelBiped.f)
					.map(BodyPart.TORSO, modelBiped.c);
		}
		
		@Override
		protected int uploadImage(BufferedImage img) {
			return th.a.e.a(img);
		}
		
		@Override
		protected String getSkinUrl() {
			return peer.bA;
		}
		
		@Override
		protected float getBrightness() {
			return brightness;
		}

		@Override
		public float getTime() {
			return ((sn)peer).bt+tickDelta;
		}

		@Override
		public boolean isFlying() {
			return peer.bq;
		}

		@Override
		public boolean isGliding() {
			return false;
		}

		@Override
		public boolean isJacketEnabled() {
			return true;
		}

		@Override
		public boolean isWearingBoots() {
			// c = ix = PlayerInventory
			iz feet = peer.c.b[0];
			// wa = ArmorItem
			return feet != null && feet.a() instanceof wa;
		}

		@Override
		public boolean isWearingChestplate() {
			iz chest = peer.c.b[2];
			return chest != null && chest.a() instanceof wa;
		}

		@Override
		public boolean isWearingElytra() {
			return false;
		}
		
		@Override
		public boolean needsSecondaryLayersDrawn() {
			return true;
		}

		@Override
		public float getHorizontalSpeed() {
			return EarsCommon.lerpDelta(peer.bi, peer.bj, tickDelta);
		}

		@Override
		public float getLimbSwing() {
			return EarsCommon.lerpDelta(peer.ak, peer.al, tickDelta);
		}

		@Override
		public float getStride() {
			return EarsCommon.lerpDelta(peer.h, peer.i, tickDelta);
		}

		@Override
		public float getBodyYaw() {
			return EarsCommon.lerpDelta(peer.I, peer.H, tickDelta);
		}

		@Override
		public double getCapeX() {
			return EarsCommon.lerpDelta(peer.o, peer.r, tickDelta);
		}

		@Override
		public double getCapeY() {
			return EarsCommon.lerpDelta(peer.p, peer.s, tickDelta);
		}

		@Override
		public double getCapeZ() {
			return EarsCommon.lerpDelta(peer.q, peer.t, tickDelta);
		}

		@Override
		public double getX() {
			return EarsCommon.lerpDelta(peer.aJ, peer.aM, tickDelta);
		}

		@Override
		public double getY() {
			return EarsCommon.lerpDelta(peer.aK, peer.aN, tickDelta);
		}

		@Override
		public double getZ() {
			return EarsCommon.lerpDelta(peer.aL, peer.aO, tickDelta);
		}
	};

}
