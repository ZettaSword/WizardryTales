package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.entity.living.EntityMushroom;
import astramusfate.wizardry_tales.renderers.models.MushroomModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nonnull;

public class RenderMushroom extends GeoEntityRenderer<EntityMushroom> {

	public RenderMushroom(RenderManager renderManager) {
		super(renderManager, new MushroomModel());
	}

	@Override
	public void doRender(@Nonnull EntityMushroom entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderEarly(EntityMushroom animatable, float ticks, float red, float green, float blue, float partialTicks) {
		Arcanist.scale(1.0f);
	}
}
