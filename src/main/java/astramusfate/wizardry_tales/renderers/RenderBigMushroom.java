package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.entity.living.EntityBigMushroom;
import astramusfate.wizardry_tales.renderers.models.BigMushroomModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nonnull;

public class RenderBigMushroom extends GeoEntityRenderer<EntityBigMushroom> {

	public RenderBigMushroom(RenderManager renderManager) {
		super(renderManager, new BigMushroomModel());
	}

	@Override
	public void doRender(@Nonnull EntityBigMushroom entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderEarly(EntityBigMushroom animatable, float ticks, float red, float green, float blue, float partialTicks) {
		Arcanist.scale(1.5f);
	}

	// DEATH ANIMATION!

	@Override
	protected float getDeathMaxRotation(EntityBigMushroom entityLivingBaseIn) {
		return 0;
	}

	@Override
	protected boolean setDoRenderBrightness(EntityBigMushroom entityLivingBaseIn, float partialTicks) {
		if(entityLivingBaseIn.getHealth() > 0 && !entityLivingBaseIn.isDead) {
			return super.setDoRenderBrightness(entityLivingBaseIn, partialTicks);
		}
		return false;
	}
}
