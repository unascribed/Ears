package com.unascribed.ears.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.unascribed.ears.api.features.AlfalfaData;
import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.EarMode;
import com.unascribed.ears.api.features.EarsFeatures.TailMode;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.util.BitOutputStream;

public class EarsFeaturesWriterV1 {

	public static void write(EarsFeatures feat, OutputStream os) throws IOException {
		@SuppressWarnings("resource")
		BitOutputStream bos = new BitOutputStream(os);
		bos.write(8, 0); // version
		int ears;
		if (feat.earMode == EarMode.NONE) {
			ears = 0;
		} else {
			ears = ((feat.earMode.ordinal()-1)*3)+(feat.earAnchor.ordinal())+1;
		}
		bos.write(6, ears);
		bos.write(feat.claws);
		bos.write(feat.horn);
		bos.write(3, feat.tailMode.ordinal());
		if (feat.tailMode != TailMode.NONE) {
			bos.write(2, feat.tailSegments-1);
			bos.writeSAMUnit(6, feat.tailBend0/90);
			if (feat.tailSegments > 1) bos.writeSAMUnit(6, feat.tailBend1/90);
			if (feat.tailSegments > 2) bos.writeSAMUnit(6, feat.tailBend2/90);
			if (feat.tailSegments > 3) bos.writeSAMUnit(6, feat.tailBend3/90);
		}
		if (feat.snoutWidth > 0 && feat.snoutHeight > 0 && feat.snoutDepth > 0) {
			bos.write(3, feat.snoutWidth);
			bos.write(2, feat.snoutHeight-1);
			bos.write(3, feat.snoutDepth-1);
			bos.write(3, feat.snoutOffset);
		} else {
			bos.write(3, 0);
		}
		bos.writeUnit(5, feat.chestSize);
		bos.write(3, feat.wingMode.ordinal());
		if (feat.wingMode != WingMode.NONE) {
			bos.write(feat.animateWings);
		}
		bos.write(feat.capeEnabled);
		bos.write(feat.emissive);
		bos.flush();
	}
	
	public static void write(EarsFeatures feat, WritableEarsImage img) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		write(feat, baos);
		if (baos.size() > ((4*4)-1)*3) throw new IOException("Cannot write "+baos.size()+" bytes - only have room for 141");
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				int c;
				if (x == 0 && y == 0) {
					c = 0xFFEA2501;
				} else {
					c = 0xFF000000;
					c |= (readEOF0(bais)&0xFF) << 16;
					c |= (readEOF0(bais)&0xFF) << 8;
					c |= (readEOF0(bais)&0xFF);
				}
				img.setARGB(x, 32+y, c);
			}
		}
		if (feat.alfalfa != null) {
			Alfalfa.write(feat.alfalfa, img);
		} else {
			Alfalfa.write(AlfalfaData.NONE, img);
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		BufferedImage img = ImageIO.read(new File("binary-test-in.png"));
//		AWTEarsImage eimg = new AWTEarsImage(img);
//		EarsFeatures feat = EarsFeatures.detect(eimg, Alfalfa.read(eimg));
//		write(feat, eimg);
//		System.out.println("in: "+feat);
//		ImageIO.write(img, "PNG", new File("binary-test-out.png"));
//		EarsFeatures feat2 = EarsFeatures.detect(eimg, Alfalfa.read(eimg));
//		System.out.println("out: "+feat2);
//		for (Field f : EarsFeatures.class.getDeclaredFields()) {
//			if (Modifier.isStatic(f.getModifiers())) continue;
//			f.setAccessible(true);
//			System.out.println(f.getName()+": "+f.get(feat)+" -> "+f.get(feat2));
//		}
//	}
	
	private static int readEOF0(InputStream in) throws IOException {
		int i = in.read();
		if (i == -1) return 0;
		return i;
	}
	
}
