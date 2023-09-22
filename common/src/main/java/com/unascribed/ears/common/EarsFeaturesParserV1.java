package com.unascribed.ears.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.unascribed.ears.api.features.EarsFeatures;
import com.unascribed.ears.api.features.EarsFeatures.EarAnchor;
import com.unascribed.ears.api.features.EarsFeatures.EarMode;
import com.unascribed.ears.api.features.EarsFeatures.TailMode;
import com.unascribed.ears.api.features.EarsFeatures.WingMode;
import com.unascribed.ears.common.debug.EarsLog;
import com.unascribed.ears.common.util.BitInputStream;

public class EarsFeaturesParserV1 {

	public static final int MAGIC = 0xEA2501; // EARS01
	
	public static EarsFeatures.Builder parse(EarsImage img) {
		ByteArrayOutputStream data = new ByteArrayOutputStream(((4*4)-1)*3);
		for (int y = 0; y < 4; y++) {
			for (int x = 0; x < 4; x++) {
				if (x == 0 && y == 0) continue;
				int c = img.getARGB(x, 32+y);
				data.write((c>>16)&0xFF);
				data.write((c>>8)&0xFF);
				data.write(c&0xFF);
			}
		}
		return parse(new ByteArrayInputStream(data.toByteArray()));
	}
	
	public static EarsFeatures.Builder parse(InputStream in) {
		BitInputStream bis = null;
		try {
			bis = new BitInputStream(in);
			
			// currently, version means nothing. in the future it will indicate additional
			// data that has been added to the end of the format (earlier data mustn't change
			// format!)
			
			// budget: ((4*4)-1)*3 bytes (360 bits)
			int ver = bis.read(8);
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Found v1.{} (Binary) data.", ver);
			
			int ears = bis.read(6);
			// 6 bits has a range of 0-63
			// this means we can have up to 20 ear modes, since we're using "base-3" encoding
			// we're stuck with 3 anchors forever, though
			EarMode earMode;
			EarAnchor earAnchor;
			if (ears == 0) {
				earMode = EarMode.NONE;
				earAnchor = EarAnchor.CENTER;
			} else {
				earMode = byOrdinalOr(EarMode.class, ((ears-1)/3)+1, EarMode.NONE);
				earAnchor = byOrdinalOr(EarAnchor.class, (ears-1)%3, EarAnchor.CENTER);
			}
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Ears 6yte: {} (mode={} anchor={})", ears, earMode, earAnchor);
			
			boolean claws = bis.readBoolean();
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Claws bit: {}", claws);
			boolean horn = bis.readBoolean();
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Horn bit: {}", horn);
		
			int tailI = bis.read(3);
			// 3 bits has a range of 0-7 - if we run out, a value of 7 can mean "read elsewhere"
			
			TailMode tailMode = byOrdinalOr(TailMode.class, tailI, TailMode.NONE);
			int tailSegments = 0;
			float tailBend0 = 0;
			float tailBend1 = 0;
			float tailBend2 = 0;
			float tailBend3 = 0;
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Tail 3yte: {} ({})", tailI, tailMode);
			if (tailMode != TailMode.NONE) {
				tailSegments = bis.read(2)+1;
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Tail segments: {}", tailSegments);
				tailBend0 = bis.readSAMUnit(6)*90;
				tailBend1 = tailSegments > 1 ? bis.readSAMUnit(6)*90 : 0;
				tailBend2 = tailSegments > 2 ? bis.readSAMUnit(6)*90 : 0;
				tailBend3 = tailSegments > 3 ? bis.readSAMUnit(6)*90 : 0;
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Tail bends: {} {} {} {}", tailBend0, tailBend1, tailBend2, tailBend3);
			}
			
			int snoutOffset = 0;
			int snoutWidth = bis.read(3); // 0-7; valid snout widths are 1-7, so this is perfect as we can use 0 to mean "none"
			int snoutHeight = 0;
			int snoutDepth = 0;
			if (snoutWidth > 0) {
				snoutHeight = bis.read(2)+1; // 1-4; perfect
				snoutDepth = bis.read(3)+1; // 1-8; perfect (the limit used to be 6, but why not 8)
				snoutOffset = bis.read(3); // 0-7, but we have to cap it based on height
				if (snoutOffset > 8-snoutHeight) snoutOffset = 8-snoutHeight;
			}
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Snout: {}x{}x{}+0,{}", snoutWidth, snoutHeight, snoutDepth, snoutOffset);
			
			float chestSize = bis.readUnit(5);
			if (chestSize > 0) {
				EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Chest: {}%", (int)(chestSize*100));
			}
			
			int wingI = bis.read(3);
			// 3 bits has a range of 0-7
			WingMode wingMode = byOrdinalOr(WingMode.class, wingI, WingMode.NONE);
			boolean animateWings = wingMode == WingMode.NONE ? false : bis.readBoolean();
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Wing 3yte: {} (mode={} + animated={})", wingI, wingMode, animateWings);
			
			boolean capeEnabled = bis.readBoolean();
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Cape: {}", capeEnabled);
			
			boolean emissive = bis.readBoolean();
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Emissive: {}", emissive);
			
			return EarsFeatures.builder()
					.earMode(earMode)
					.earAnchor(earAnchor)
					.claws(claws)
					.horn(horn)
					.tailMode(tailMode)
					.tailSegments(tailSegments)
					.tailBends(tailBend0, tailBend1, tailBend2, tailBend3)
					.snoutOffset(snoutOffset)
					.snoutWidth(snoutWidth)
					.snoutHeight(snoutHeight)
					.snoutDepth(snoutDepth)
					.chestSize(chestSize)
					.wingMode(wingMode)
					.animateWings(animateWings)
					.capeEnabled(capeEnabled)
					.emissive(emissive);
		} catch (IOException e) {
			EarsLog.debug(EarsLog.Tag.COMMON_FEATURES, "detect(...): Error while parsing v1 (Binary) data. Disabling", e);
			return null;
		} finally {
			try {
				if (bis != null) bis.close();
			} catch (IOException e) {}
		}
	}

	private static <E extends Enum<E>> E byOrdinalOr(Class<E> clazz, int ordinal, E def) {
		if (ordinal < 0) return def;
		E[] cnst = clazz.getEnumConstants();
		if (ordinal >= cnst.length) return def;
		return cnst[ordinal];
	}

}
