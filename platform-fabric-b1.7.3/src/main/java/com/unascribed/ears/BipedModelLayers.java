package com.unascribed.ears;

import net.minecraft.client.model.ModelPart;

public interface BipedModelLayers {
	ModelPart getLeftSleeve();
	void setLeftSleeve(ModelPart part);
	ModelPart getRightSleeve();
	void setRightSleeve(ModelPart part);
	ModelPart getLeftPantLeg();
	void setLeftPantLeg(ModelPart part);
	ModelPart getRightPantLeg();
	void setRightPantLeg(ModelPart part);
	ModelPart getJacket();
	void setJacket(ModelPart part);
}
