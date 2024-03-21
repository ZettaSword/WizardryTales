package astramusfate.wizardry_tales.renderers.models;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityTenebria;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class TenebriaModel extends AnimatedTickingGeoModel<EntityTenebria> {

	@Override
	public ResourceLocation getAnimationFileLocation(EntityTenebria entity) {
		return new ResourceLocation(WizardryTales.MODID, "animations/tenebria.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(EntityTenebria entity) {
		return new ResourceLocation(WizardryTales.MODID, "geo/tenebria.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(EntityTenebria entity) {
		return new ResourceLocation(WizardryTales.MODID, "textures/entity/gods/tenebria.png");
	}

	@SuppressWarnings({ "rawtypes"})
	@Override
	public void setLivingAnimations(EntityTenebria entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}


}
