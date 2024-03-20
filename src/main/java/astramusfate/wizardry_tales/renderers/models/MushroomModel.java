package astramusfate.wizardry_tales.renderers.models;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityMushroom;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class MushroomModel extends AnimatedTickingGeoModel<EntityMushroom> {

	@Override
	public ResourceLocation getAnimationFileLocation(EntityMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "animations/mushroom.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(EntityMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "geo/mushroom.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(EntityMushroom entity) {
		return new ResourceLocation(WizardryTales.MODID, "textures/entity/mushroom/mushroom.png");
	}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setLivingAnimations(EntityMushroom entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}


}
