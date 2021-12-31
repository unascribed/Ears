

import static org.lwjgl.opengl.GL11.*;

public class com_unascribed_ears_ModelRendererTrans extends ps {

	public com_unascribed_ears_ModelRendererTrans(int xTexOffset, int yTexOffset) {
		super(xTexOffset, yTexOffset);
	}

	@Override
	public void a(float f) {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		super.a(f);
		glDisable(GL_BLEND);
	}

}
