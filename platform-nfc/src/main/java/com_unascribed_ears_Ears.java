import static org.lwjgl.opengl.GL11.*;
import java.awt.image.BufferedImage;
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

public class com_unascribed_ears_Ears {

	private static final com_unascribed_ears_Ears INST = new com_unascribed_ears_Ears();
	
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	private ds render;
	private float brightness;
	private float tickDelta;
	
	public static void postRenderSpecials(ds renderPlayer, gs entity, float partialTicks) {
		INST.render(renderPlayer, entity, partialTicks);
	}
	
	public static void postDrawFirstPersonHand(ds renderPlayer, gs entity) {
		INST.renderRightArm(renderPlayer, entity, 0);
	}
	
	public static void preprocessSkin(rr subject, BufferedImage rawImg, BufferedImage img) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "preprocessSkin({}, {}, {})", subject, rawImg, img);
		EarsStorage.put(img, EarsStorage.Key.ALFALFA, EarsCommon.preprocessSkin(new AWTEarsImage(rawImg)));
	}
	
	private static boolean reenteringOpaque = false;
	
	public static boolean interceptSetAreaOpaque(rr rr, int x1, int y1, int x2, int y2) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "stripAlpha({}, {}, {}, {}, {}) reentering={}", rr, x1, y2, x2, y2, reenteringOpaque);
		if (reenteringOpaque) return false;
		if (x1 == 0 && y1 == 0 && x2 == 32 && y2 == 16) {
			try {
				reenteringOpaque = true;
				EarsCommon.carefullyStripAlpha((_x1, _y1, _x2, _y2) -> setAreaOpaque(rr, _x1, _y1, _x2, _y2), true);
			} finally {
				reenteringOpaque = false;
			}
		}
		return true;
	}
	
	private static final Method rr_setAreaOpaque;
	private static final Field ps_vertices;
	static {
		try {
			rr_setAreaOpaque = rr.class.getDeclaredMethod("setAreaOpaque", int.class, int.class, int.class, int.class);
			rr_setAreaOpaque.setAccessible(true);
			
			ps_vertices = ps.class.getDeclaredField("j");
			ps_vertices.setAccessible(true);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static void setAreaOpaque(rr rr, int x1, int y1, int x2, int y2) {
		try {
			rr_setAreaOpaque.invoke(rr, x1, y1, x2, y2);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	private static ib[] getVertices(ps ps) {
		try {
			return (ib[])ps_vertices.get(ps);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
	
	public static void checkSkin(String url, BufferedImage img) {
		EarsLog.debug(EarsLog.Tag.PLATFORM_INJECT, "checkSkin({}, {})", url, img);
		earsSkinFeatures.put(url, EarsFeatures.detect(new AWTEarsImage(img), EarsStorage.get(img, EarsStorage.Key.ALFALFA),
				data -> new AWTEarsImage(ImageIO.read(new ByteArrayInputStream(data)))));
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
			return false;
		}
		
		@Override
		protected EarsFeatures getEarsFeatures() {
			return earsSkinFeatures.containsKey(getSkinUrl()) ? earsSkinFeatures.get(getSkinUrl()) : EarsFeatures.DISABLED;
		}
		
		@Override
		protected void doBindSkin() {
			render.a(getSkinUrl(), peer.q_());
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
			ModelPlayer modelPlayer = (ModelPlayer)render.e;
			// this field order has never changed
			// a          b              c          d              e             f              g
			// bipedHead, bipedHeadwear, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg, bipedLeftLeg
			// thank god notch's proguard config doesn't randomize member order
			return d.map(BodyPart.HEAD, modelPlayer.a)
					.map(BodyPart.LEFT_ARM, modelPlayer.e)
					.map(BodyPart.LEFT_LEG, modelPlayer.g)
					.map(BodyPart.RIGHT_ARM, modelPlayer.d)
					.map(BodyPart.RIGHT_LEG, modelPlayer.f)
					.map(BodyPart.TORSO, modelPlayer.c);
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
			return peer.playerCapabilities.flying;
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
