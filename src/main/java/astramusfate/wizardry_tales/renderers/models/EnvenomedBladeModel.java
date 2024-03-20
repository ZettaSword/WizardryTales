package astramusfate.wizardry_tales.renderers.models;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityEnvenomedBlade;
import net.minecraft.util.ResourceLocation;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.model.AnimatedTickingGeoModel;

public class EnvenomedBladeModel extends AnimatedTickingGeoModel<EntityEnvenomedBlade> {

	@Override
	public ResourceLocation getAnimationFileLocation(EntityEnvenomedBlade entity) {
		return new ResourceLocation(WizardryTales.MODID, "animations/envenomed_blade.animation.json");
	}

	@Override
	public ResourceLocation getModelLocation(EntityEnvenomedBlade entity) {
		return new ResourceLocation(WizardryTales.MODID, "geo/envenomed_blade.geo.json");
	}

	@Override
	public ResourceLocation getTextureLocation(EntityEnvenomedBlade entity) {
		return new ResourceLocation(WizardryTales.MODID, "textures/entity/skeleton/envenomed_blade.png");
	}

	@Override
	public void setLivingAnimations(EntityEnvenomedBlade entity, Integer uniqueID, AnimationEvent customPredicate) {
		super.setLivingAnimations(entity, uniqueID, customPredicate);
	}


}
