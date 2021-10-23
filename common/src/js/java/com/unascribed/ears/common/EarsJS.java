package com.unascribed.ears.common;

import java.io.IOException;
import java.util.Locale;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.canvas.CanvasRenderingContext2D;
import org.teavm.jso.canvas.ImageData;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSMapLike;
import org.teavm.jso.core.JSNumber;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.typedarrays.DataView;

import com.unascribed.ears.common.EarsFeatures.Alfalfa;
import com.unascribed.ears.common.EarsFeatures.MagicPixel;
import com.unascribed.ears.common.render.EarsRenderDelegate;

/**
 * Entry point for the Manipulator to build quads.
 */
public class EarsJS {
	
	/**
	 * Called "rebuildQuads" in JavaScript.
	 */
	public static void main(String[] args) throws IOException {
		JSMapLike<JSString> magicPixels = JSObjects.create();
		JSMapLike<JSString> magicPixelValues = JSObjects.create();
		for (MagicPixel mp : MagicPixel.values()) {
			if (mp == MagicPixel.UNKNOWN) continue;
			String name = mp.name().toLowerCase(Locale.ROOT);
			magicPixels.set(Long.toString((mp.rgb<<8|0xFF)&0xFFFFFFFFL), JSString.valueOf(name));
			magicPixelValues.set(name, JSString.valueOf("#"+Integer.toHexString(mp.rgb|0xFF000000).substring(2)));
		}
		assignToWindow("magicPixels", magicPixels);
		assignToWindow("magicPixelValues", magicPixelValues);
		HTMLCanvasElement canvas = (HTMLCanvasElement)Window.current().getDocument().getElementById("skin");
		final ImageData skin = ((CanvasRenderingContext2D)canvas.getContext("2d")).getImageData(0, 0, 64, 64);
		final DataView dv = DataView.create(skin.getData().getBuffer());
		EarsImage img = new EarsImage() {
			
			@Override
			public int getWidth() {
				return skin.getWidth();
			}
			
			@Override
			public int getHeight() {
				return skin.getHeight();
			}
			
			@Override
			public int getARGB(int x, int y) {
				int rgba = dv.getUint32(((y*64)+x)*4);
				int a = rgba & 0xFF;
				int argb = ((rgba >> 8)&0x00FFFFFF) | (a << 24);
				return argb;
			}
		};
		EarsFeatures feat = EarsFeatures.detect(img, Alfalfa.read(img));
		final JSArray<JSObject> objects = JSArray.create();
		EarsCommon.render(feat, new EarsRenderDelegate() {
			
			private JSArray<JSObject> moves = JSArray.create();
			private JSArray<JSArray<JSObject>> movesStack = JSArray.create();
			
			@Override
			public void setUp() {}

			@Override
			public void tearDown() {}

			@Override
			public void bind(TexSource tex) {
				// TODO
			}

			@Override
			public void scale(float x, float y, float z) {
				JSMapLike<JSObject> qm = JSObjects.create();
				qm.set("type", JSString.valueOf("scale"));
				qm.set("x", JSNumber.valueOf(x));
				qm.set("y", JSNumber.valueOf(y));
				qm.set("z", JSNumber.valueOf(z));
				moves.push(qm);
				
			}
			@Override
			public void translate(float x, float y, float z) {
				JSMapLike<JSObject> qm = JSObjects.create();
				qm.set("type", JSString.valueOf("translate"));
				qm.set("x", JSNumber.valueOf(x));
				qm.set("y", JSNumber.valueOf(y));
				qm.set("z", JSNumber.valueOf(z));
				moves.push(qm);
			}
			
			@Override
			public void rotate(float ang, float x, float y, float z) {
				JSMapLike<JSObject> qm = JSObjects.create();
				qm.set("type", JSString.valueOf("rotate"));
				qm.set("ang", JSNumber.valueOf(ang));
				qm.set("x", JSNumber.valueOf(x));
				qm.set("y", JSNumber.valueOf(y));
				qm.set("z", JSNumber.valueOf(z));
				moves.push(qm);
			}
			
			@Override
			public void renderFront(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
				renderQuad(u, v, width, height, rot, flip, grow, false);
			}
			
			@Override
			public void renderBack(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
				renderQuad(u, v, width, height, rot, flip, grow, true);
			}
			
			@Override
			public void renderDebugDot(float r, float g, float b, float a) {
				JSMapLike<JSObject> p = JSObjects.create();
				p.set("type", JSString.valueOf("point"));
				p.set("moves", moves);
				p.set("color", JSNumber.valueOf(((int)(a*255)<<24)|((int)(r*255)<<16)|((int)(g*255)<<8)|((int)(b*255))));
				objects.push(p);
			}
			
			private void renderQuad(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow, boolean back) {
				float w = width;
				float h = height;
				if (grow.grow > 0) {
					w += grow.grow*2;
					h += grow.grow*2;
					translate(-grow.grow, -grow.grow, 0);
				}
				JSMapLike<JSObject> q = JSObjects.create();
				q.set("type", JSString.valueOf("quad"));
				q.set("moves", moves.slice(0));
				JSArray<JSArray<JSNumber>> uvs = JSArray.create();
				float[][] uvsArr = EarsCommon.calculateUVs(u, v, width, height, rot, back ? flip.flipHorizontally() : flip, TexSource.SKIN);
				for (float[] arr : uvsArr) {
					JSArray<JSNumber> jarr = JSArray.create();
					for (int i = 0; i < arr.length; i++) {
						jarr.push(JSNumber.valueOf(arr[i]));
					}
					uvs.push(jarr);
				}
				q.set("uvs", uvs);
				q.set("width", JSNumber.valueOf(w));
				q.set("height", JSNumber.valueOf(h));
				q.set("back", JSBoolean.valueOf(back));
				objects.push(q);
			}
			
			@Override
			public void push() {
				movesStack.push(moves);
				moves = moves.slice(0); // clone
			}
			
			@Override
			public void pop() {
				moves = movesStack.pop();
			}
			
			@Override
			public void anchorTo(BodyPart part) {
				JSMapLike<JSObject> qm = JSObjects.create();
				qm.set("type", JSString.valueOf("anchor"));
				qm.set("part", JSString.valueOf(part.name().toLowerCase(Locale.ROOT)));
				moves.push(qm);
			}

			@Override
			public void renderDoubleSided(int u, int v, int width, int height, TexRotation rot, TexFlip flip, QuadGrow grow) {
				renderFront(u, v, width, height, rot, flip, grow);
				renderBack(u, v, width, height, rot, flip.flipHorizontally(), grow);
			}

			@Override
			public float getTime() {
				return 0;
			}

			@Override
			public boolean isFlying() {
				return false;
			}
		}, 0, getSlimState());
		assignToWindow("renderObjects", objects);
	}
	
	@JSBody(params={"name", "obj"}, script="window[name] = obj;")
	private static native void assignToWindow(String name, JSObject obj);
	
	@JSBody(script="return !!document.getElementById(\"slim-enabled\").checked;")
	private static native boolean getSlimState();
	
}
