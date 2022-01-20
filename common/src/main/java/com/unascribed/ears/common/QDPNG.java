package com.unascribed.ears.common;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import com.unascribed.ears.api.Slice;

/**
 * An extremely minimal PNG writer that puts zero effort into making small files.
 */
public class QDPNG {

	private static final Slice HEADER = Slice.parse("89[PNG\r\n]1A[\n]");
	private static final Slice IHDR = Slice.parse("[IHDR]");
	private static final Slice IDAT = Slice.parse("[IDAT]");
	private static final Slice IEND_COMPLETE = Slice.parse("00000000[IEND]AE426082");
	
	public static byte[] write(WritableEarsImage out) {
		return new QDPNG(out).write();
	}

	private final WritableEarsImage img;
	
	private final ByteArrayOutputStream root = new ByteArrayOutputStream();
	private final DataOutputStream dosRoot = new DataOutputStream(root);
	
	private final ByteArrayOutputStream tmp = new ByteArrayOutputStream();
	private final CheckedOutputStream crcTmp = new CheckedOutputStream(tmp, new CRC32());
	private final DataOutputStream dosTmp = new DataOutputStream(crcTmp);
	
	private DataOutputStream out = dosRoot;
	
	private QDPNG(WritableEarsImage img) {
		this.img = img;
	}
	
	private byte[] write() {
		try {
			root.reset();
			out = dosRoot;
			
			HEADER.writeTo(out);
			beginChunk(IHDR);
				out.writeInt(img.getWidth());
				out.writeInt(img.getHeight());
				out.writeByte(8); // 8bpc
				out.writeByte(6); // RGBA
				out.writeByte(0); // compression method, 0 is DEFLATE (no other methods defined)
				out.writeByte(0); // filter method, 0 is the only legal value
				out.writeByte(0); // interlace method, 0 is uninterlaced
			endChunk();
			beginChunk(IDAT);
				DeflaterOutputStream zout = new DeflaterOutputStream(crcTmp, new Deflater(2));
				for (int y = 0; y < img.getHeight(); y++) {
					zout.write(0); // no filtering
					for (int x = 0; x < img.getWidth(); x++) {
						int c = img.getARGB(x, y);
						zout.write((c >> 16) & 0xFF); // R
						zout.write((c >>  8) & 0xFF); // G
						zout.write((c >>  0) & 0xFF); // B
						zout.write((c >> 24) & 0xFF); // A
					}
				}
				zout.finish();
			endChunk();
			// we could use the normal chunk machinery, but IEND is always the same
			IEND_COMPLETE.writeTo(out);
			
			return root.toByteArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void beginChunk(Slice name) throws IOException {
		tmp.reset();
		crcTmp.getChecksum().reset();
		out = dosTmp;
		name.writeTo(out);
	}
	
	private void endChunk() throws IOException {
		out.flush();
		out = dosRoot;
		out.writeInt(tmp.size()-4);
		tmp.writeTo(out);
		out.writeInt((int)crcTmp.getChecksum().getValue());
	}

}
