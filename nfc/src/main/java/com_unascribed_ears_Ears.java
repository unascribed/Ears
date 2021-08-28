import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;

import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.WeakHashMap;

import com.unascribed.ears.common.AWTEarsImage;
import com.unascribed.ears.common.EarsCommon;
import com.unascribed.ears.common.EarsFeatures;
import com.unascribed.ears.common.EarsRenderDelegate;
import com.unascribed.ears.common.debug.EarsLog;

public class com_unascribed_ears_Ears implements EarsRenderDelegate {

	private static final com_unascribed_ears_Ears INST = new com_unascribed_ears_Ears();
	
	public static final Map<String, EarsFeatures> earsSkinFeatures = new WeakHashMap<>();
	
	private ds render;
	private float brightness;
	private int skipRendering;
	private int stackDepth;
	private BodyPart permittedBodyPart;
	
	public static void postRenderSpecials(ds renderPlayer, gs entity, float partialTicks) {
		INST.render(renderPlayer, entity, partialTicks);
	}
	
	public static void postDrawFirstPersonHand(ds renderPlayer, gs entity) {
		INST.renderRightArm(renderPlayer, entity, 0);
	}
	
	private static boolean reenteringOpaque = false;
	
	public static boolean interceptSetAreaOpaque(rr rr, int x1, int y1, int x2, int y2) {
		EarsLog.debug("Platform:Inject", "stripAlpha({}, {}, {}, {}, {}) reentering={}", rr, x1, y2, x2, y2, reenteringOpaque);
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
		EarsLog.debug("Platform:Inject", "checkSkin({}, {})", url, img);
		earsSkinFeatures.put(url, EarsFeatures.detect(new AWTEarsImage(img)));
	}
	
	public void render(ds render, gs entity, float partialTicks) {
		EarsLog.debug("Platform:Renderer", "render({}, {}, {})", entity, 0, partialTicks);
		String skin = entity.bA;
		EarsLog.debug("Platform:Renderer", "render(...): skin={}", skin);
		if (earsSkinFeatures.containsKey(skin)) {
			if (!render.a(skin, null)) return; // bind the skin texture
			EarsLog.debug("Platform:Renderer", "render(...): Checks passed");
			this.render = render;
			this.brightness = entity.a(partialTicks);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = null;
			glEnable(GL_CULL_FACE);
			glEnable(GL_RESCALE_NORMAL);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(com_unascribed_ears_Ears.earsSkinFeatures.get(skin), this, 0, false);
			glDisable(GL_BLEND);
			glDisable(GL_RESCALE_NORMAL);
			glDisable(GL_CULL_FACE);
			this.render = null;
		}
	}
	
	public void renderRightArm(ds render, gs entity, float partialTicks) {
		String skin = entity.bA;
		EarsLog.debug("Platform:Renderer", "renderRightArm(...): skin={}", skin);
		if (com_unascribed_ears_Ears.earsSkinFeatures.containsKey(skin)) {
			EarsLog.debug("Platform:Renderer", "renderRightArm(...): Checks passed");
			this.render = render;
			this.brightness = entity.a(partialTicks);
			this.skipRendering = 0;
			this.stackDepth = 0;
			this.permittedBodyPart = BodyPart.RIGHT_ARM;
			glEnable(GL_CULL_FACE);
			glEnable(GL_RESCALE_NORMAL);
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			EarsCommon.render(com_unascribed_ears_Ears.earsSkinFeatures.get(skin), this, 0, false);
			glDisable(GL_BLEND);
			glDisable(GL_RESCALE_NORMAL);
			glDisable(GL_CULL_FACE);
			this.render = null;
		}
	}

	@Override
	public void push() {
		stackDepth++;
		glPushMatrix();
		if (skipRendering > 0) skipRendering++;
	}

	@Override
	public void pop() {
		if (stackDepth <= 0) {
			new Exception("STACK UNDERFLOW").printStackTrace();
			return;
		}
		stackDepth--;
		glPopMatrix();
		if (skipRendering > 0) skipRendering--;
	}

	@Override
	public void anchorTo(BodyPart part) {
		if (permittedBodyPart != null && part != permittedBodyPart) {
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not permissible in this pass, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		// safe, cast is performed in the RenderPlayer (ds) constructor
		ModelPlayer modelPlayer = (ModelPlayer)render.e;
		// this field order has never changed
		// a          b              c          d              e             f              g
		// bipedHead, bipedHeadwear, bipedBody, bipedRightArm, bipedLeftArm, bipedRightLeg, bipedLeftLeg
		// thank god notch's proguard config doesn't randomize member order
		ps model;
		switch (part) {
			case HEAD:
				model = modelPlayer.a;
				break;
			case LEFT_ARM:
				model = modelPlayer.e;
				break;
			case LEFT_LEG:
				model = modelPlayer.g;
				break;
			case RIGHT_ARM:
				model = modelPlayer.d;
				break;
			case RIGHT_LEG:
				model = modelPlayer.f;
				break;
			case TORSO:
				model = modelPlayer.c;
				break;
			default: return;
		}
		if (!model.h) { // visible
			EarsLog.debug("Platform:Renderer:Delegate", "anchorTo(...): Part is not visible, skip rendering until pop");
			if (skipRendering == 0) {
				skipRendering = 1;
			}
			return;
		}
		model.c(1/16f); // postRender (you can tell because it's the one that applies transforms without matrix isolation)
		glScalef(1/16f, 1/16f, 1/16f);
		ib vert = getVertices(model)[3];
		glTranslatef((float)vert.a.a, (float)vert.a.b, (float)vert.a.c);
	}

	@Override
	public void translate(float x, float y, float z) {
		if (skipRendering > 0) return;
		glTranslatef(x, y, z);
	}

	@Override
	public void rotate(float ang, float x, float y, float z) {
		if (skipRendering > 0) return;
		glRotatef(ang, x, y, z);
	}

	@Override
	public void renderFront(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;

		// using tesselator isn't worth it when everything is obfuscated like this
		// so let's just do this the old-fashioned way
		
		// the JNI overhead will be a bit gnarly but we've got so much frametime to spare on these simplistic old versions
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip);
		float g = grow.grow;
		
		glColor3f(brightness, brightness, brightness);
		glBegin(GL_QUADS);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[0][0], uv[0][1]);
			glVertex3f(-g, h+g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[1][0], uv[1][1]);
			glVertex3f(w+g, h+g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[2][0], uv[2][1]);
			glVertex3f(w+g, -g, 0);
			glNormal3f(0, 0, -1);
			glTexCoord2f(uv[3][0], uv[3][1]);
			glVertex3f(-g, -g, 0);
		glEnd();
	}

	@Override
	public void renderBack(int u, int v, int w, int h, TexRotation rot, TexFlip flip, QuadGrow grow) {
		if (skipRendering > 0) return;
		
		float[][] uv = EarsCommon.calculateUVs(u, v, w, h, rot, flip.flipHorizontally());
		float g = grow.grow;
		
		glColor3f(brightness, brightness, brightness);
		glBegin(GL_QUADS);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[3][0], uv[3][1]);
			glVertex3f(-g, -g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[2][0], uv[2][1]);
			glVertex3f(w+g, -g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[1][0], uv[1][1]);
			glVertex3f(w+g, h+g, 0);
			glNormal3f(0, 0, 1);
			glTexCoord2f(uv[0][0], uv[0][1]);
			glVertex3f(-g, h+g, 0);
		glEnd();
	}

	@Override
	public void renderDebugDot(float r, float g, float b, float a) {
		if (skipRendering > 0) return;
		
		glPointSize(8);
		glDisable(GL_TEXTURE_2D);
		glBegin(GL_POINTS);
			glColor4f(r, g, b, a);
			glVertex3f(0, 0, 0);
		glEnd();
		glEnable(GL_TEXTURE_2D);
	}

}
