package com.unascribed.ears.common;

public interface WritableEarsImage extends EarsImage {

	void setARGB(int x, int y, int argb);
	
	WritableEarsImage copy();
	
}
