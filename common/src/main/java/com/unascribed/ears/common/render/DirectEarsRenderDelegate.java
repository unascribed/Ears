package com.unascribed.ears.common.render;

import com.unascribed.ears.common.EarsCommon;

public abstract class DirectEarsRenderDelegate<TPeer, TModelPart> extends AbstractEarsRenderDelegate<TPeer, TModelPart> {

	public void render(TPeer peer, float limbDistance) {
		render(peer, limbDistance, null);
	}
	
	public void render(TPeer peer, float limbDistance, BodyPart permittedBodyPart) {
		this.peer = peer;
		this.permittedBodyPart = permittedBodyPart;
		this.feat = getEarsFeatures();
		EarsCommon.render(this.feat, this, limbDistance, isSlim());
	}
	
}
