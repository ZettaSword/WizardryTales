package astramusfate.wizardry_tales.renderers.models;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityBigMushroom;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class BigMushroomModel extends AnimatedTickingGeoModel<EntityBigMushroom> {

	@Override
	public ResourceLocation getAnimationFileLocation(EntityBigMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "animations/big_mushroom.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(EntityBigMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "geo/big_mushroom.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(EntityBigMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "textures/entity/mushroom/big_mushroom.png");
	}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setLivingAnimations(EntityBigMushroom entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}


}
