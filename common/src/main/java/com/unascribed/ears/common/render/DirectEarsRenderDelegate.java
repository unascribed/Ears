package com.unascribed.ears.common.render;

import com.unascribed.ears.common.EarsCommon;

/**
 * Provides peer-only render methods on top of {@link AbstractEarsRenderDelegate}.
 */
public abstract class DirectEarsRenderDelegate<TPeer, TModelPart> extends AbstractEarsRenderDelegate<TPeer, TModelPart> {

	public void render(TPeer peer) {
		render(peer, null);
	}
	
	public void render(TPeer peer, BodyPart permittedBodyPart) {
		this.peer = peer;
		this.permittedBodyPart = permittedBodyPart;
		this.feat = getEarsFeatures();
		EarsCommon.render(this.feat, this);
	}
	
}
