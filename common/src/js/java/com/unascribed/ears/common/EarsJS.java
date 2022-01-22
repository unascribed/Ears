package com.unascribed.ears.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
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

import com.unascribed.ears.api.Slice;
import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.common.render.AbstractDetachedEarsRenderDelegate;

/**
 * Entry point for the Manipulator to build quads.
 */
public class EarsJS {

	public static class EarsImageJS implements WritableEarsImage {
		private final ImageData img;
		private final DataView dv;

		public EarsImageJS(ImageData img) {
			this.img = img;
			this.dv = DataView.create(img.getData().getBuffer());
		}

		@Override
		public int getWidth() {
			return img.getWidth();
		}

		@Override
		public int getHeight() {
			return img.getHeight();
		}

		@Override
		public int getARGB(int x, int y) {
			int rgba = dv.getUint32(((y*64)+x)*4);
			int a = rgba & 0xFF;
			int argb = ((rgba >> 8)&0x00FFFFFF) | (a << 24);
			return argb;
		}

		@Override
		public void setARGB(int x, int y, int argb) {
			int rgba = argb<<8;
			rgba |= (argb>>24)&0xFF;
			dv.setUint32(((y*64)+x)*4, rgba);
		}

		@Override
		public WritableEarsImage copy() {
			throw new UnsupportedOperationException();
		}
	}

	@JSFunctor
	private interface NullFunctor extends JSObject {
		void invoke() throws Exception;
	}

	@JSFunctor
	private interface StringFunctor extends JSObject {
		String invoke() throws Exception;
	}
	
	/**
	 * Called "initCommon" in JavaScript.
	 */
	public static void main(String[] args) throws IOException {
		assignToWindow("commonVersion", JSString.valueOf(EarsVersion.COMMON));
		assignFuncToWindow("rebuildQuads", new NullFunctor() {
			@Override
			public void invoke() throws Exception {
				rebuildQuads();
			}
		});
		assignFuncToWindow("encodeAlfalfa", new NullFunctor() {
			@Override
			public void invoke() throws Exception {
				encodeAlfalfa();
			}
		});
	}
	
	public static void rebuildQuads() throws IOException {
		JSMapLike<JSString> magicPixels = JSObjects.create();
		JSMapLike<JSString> magicPixelValues = JSObjects.create();
		for (EarsFeaturesParserV0.MagicPixel mp : EarsFeaturesParserV0.MagicPixel.values()) {
			if (mp == EarsFeaturesParserV0.MagicPixel.UNKNOWN) continue;
			String name = mp.name().toLowerCase(Locale.ROOT);
			magicPixels.set(Long.toString((mp.rgb<<8|0xFF)&0xFFFFFFFFL), JSString.valueOf(name));
			magicPixelValues.set(name, JSString.valueOf("#"+Integer.toHexString(mp.rgb|0xFF000000).substring(2)));
		}
		assignToWindow("magicPixels", magicPixels);
		assignToWindow("magicPixelValues", magicPixelValues);
		HTMLCanvasElement canvas = (HTMLCanvasElement)Window.current().getDocument().getElementById("skin");
		final ImageData skin = ((CanvasRenderingContext2D)canvas.getContext("2d")).getImageData(0, 0, 64, 64);
		EarsImage img = new EarsImageJS(skin);
		EarsFeatures feat = EarsFeaturesParser.detect(img, Alfalfa.read(img), null);
		final JSArray<JSObject> objects = JSArray.create();
		EarsRenderer.render(feat, new AbstractDetachedEarsRenderDelegate() {
			
			private TexSource texture = TexSource.SKIN;
			private JSArray<JSObject> moves = JSArray.create();
			private JSArray<JSArray<JSObject>> movesStack = JSArray.create();
			private boolean emissive = false;

			@Override
			public void bind(TexSource src) {
				texture = src;
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
					push();
					translate(-grow.grow, -grow.grow, 0);
				}
				JSMapLike<JSObject> q = JSObjects.create();
				q.set("type", JSString.valueOf("quad"));
				q.set("moves", moves.slice(0));
				JSArray<JSArray<JSNumber>> uvs = JSArray.create();
				float[][] uvsArr = EarsCommon.calculateUVs(u, v, width, height, rot, back ? flip.flipHorizontally() : flip, texture);
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
				q.set("texture", JSString.valueOf(texture.lowerName()));
				q.set("emissive", JSBoolean.valueOf(emissive));
				if (grow.grow > 0) {
					pop();
				}
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
			public boolean isJacketEnabled() {
				return getJacketState();
			}
			
			@Override
			public boolean isSlim() {
				return getSlimState();
			}

			@Override
			public void setEmissive(boolean emissive) {
				this.emissive = emissive;
			}
			
		});
		assignToWindow("renderObjects", objects);
		JSMapLike<JSObject> alfalfaData = JSObjects.create();
		alfalfaData.set("version", JSNumber.valueOf(feat.alfalfa.version));
		for (Map.Entry<String, Slice> en : feat.alfalfa.data.entrySet()) {
			alfalfaData.set(en.getKey(), JSString.valueOf(toBinString(en.getValue().toByteArray())));
		}
		assignToWindow("alfalfaData", alfalfaData);
	}
	
	public static void encodeAlfalfa() throws IOException {
		JSObject alfalfaData = retrieveFromWindow("alfalfaData");
		Map<String, Slice> data = new HashMap<String, Slice>();
		JSArray<JSArray<JSObject>> entries = entries(alfalfaData);
		for (int i = 0; i < entries.getLength(); i++) {
			String key = ((JSString)entries.get(i).get(0)).stringValue();
			if (key.equals("version")) continue;
			JSString s = entries.get(i).get(1).cast();
			data.put(key, new Slice(fromBinString(s)));
		}
		JSMapLike<JSObject> alfalfaDataM = alfalfaData.cast();
		JSNumber version = alfalfaDataM.get("version").cast();
		AlfalfaData a = new AlfalfaData(version.intValue(), data);
		HTMLCanvasElement canvas = (HTMLCanvasElement)Window.current().getDocument().getElementById("skin");
		final ImageData skin = ((CanvasRenderingContext2D)canvas.getContext("2d")).getImageData(0, 0, 64, 64);
		WritableEarsImage img = new EarsImageJS(skin);
		Alfalfa.write(a, img);
		((CanvasRenderingContext2D)canvas.getContext("2d")).putImageData(skin, 0, 0, 0, 0, 64, 64);
	}
	
	private static byte[] fromBinString(JSString val) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		for (int j = 0; j < val.getLength(); j++) {
			baos.write(val.charCodeAt(j));
		}
		return baos.toByteArray();
	}
	
	private static String toBinString(byte[] bys) {
		StringBuilder sb = new StringBuilder(bys.length);
		for (int i = 0; i < bys.length; i++) {
			sb.append((char)(bys[i]&0xFF));
		}
		return sb.toString();
	}

	@JSBody(params={"name", "obj"}, script="window[name] = obj;")
	private static native void assignToWindow(String name, JSObject obj);
	@JSBody(params={"name", "obj"}, script="window[name] = obj;")
	private static native void assignFuncToWindow(String name, NullFunctor obj);
	@JSBody(params={"name", "obj"}, script="window[name] = obj;")
	private static native void assignFuncToWindow(String name, StringFunctor obj);
	@JSBody(params={"name"}, script="return window[name];")
	private static native JSObject retrieveFromWindow(String name);
	@JSBody(params={"obj"}, script="return Object.entries(obj);")
	private static native JSArray<JSArray<JSObject>> entries(JSObject obj);
	
	@JSBody(script="return !!document.getElementById(\"slim-enabled\").checked;")
	private static native boolean getSlimState();
	@JSBody(script="return !!document.getElementById(\"torso2-enabled\").checked;")
	private static native boolean getJacketState();
	
}
