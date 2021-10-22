package com.unascribed.ears.common.render;

import com.unascribed.ears.common.EarsCommon;

public abstract class IndirectEarsRenderDelegate<TMatrixStack, TVertexConsumerProvider, TVertexConsumer, TPeer, TModelPart> extends AbstractEarsRenderDelegate<TPeer, TModelPart> {

	protected TMatrixStack matrices;
	protected TVertexConsumerProvider vcp;
	protected TVertexConsumer vc;
	protected int light, overlay;
	
	public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, float limbDistance, int light, int overlay) {
		render(matrices, vertexConsumers, peer, limbDistance, light, overlay, null);
	}
	
	public void render(TMatrixStack matrices, TVertexConsumerProvider vertexConsumers, TPeer peer, float limbDistance, int light, int overlay, BodyPart permittedBodyPart) {
		this.matrices = matrices;
		this.vcp = vertexConsumers;
		this.peer = peer;
		this.permittedBodyPart = permittedBodyPart;
		this.feat = getEarsFeatures();
		this.vc = getVertexConsumer(TexSource.SKIN);
		this.light = light;
		this.overlay = overlay;
		EarsCommon.render(this.feat, this, limbDistance, isSlim());
		matrices = null;
		vertexConsumers = null;
		vc = null;
	}
	
	@Override
	protected final void setUpRenderState() {}
	
	@Override
	protected final void tearDownRenderState() {}
	
	@Override
	protected final void doBindSkin() {
		this.vc = getVertexConsumer(TexSource.SKIN);
	}
	
	protected abstract TVertexConsumer getVertexConsumer(TexSource src);

	@Override
	protected final void doBindSub(TexSource src, byte[] pngData) {
		doUploadSub(src, pngData);
		this.vc = getVertexConsumer(src);
	}

	protected abstract void doUploadSub(TexSource src, byte[] pngData);
	
	@Override
	protected void beginQuad() {}
	
	@Override
	protected void drawQuad() {}
	
}
