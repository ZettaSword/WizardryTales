package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.entity.living.EntityTenebria;
import astramusfate.wizardry_tales.renderers.models.TenebriaModel;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nonnull;

public class RenderTenebria extends GeoEntityRenderer<EntityTenebria> {

	public RenderTenebria(RenderManager renderManager) {
		super(renderManager, new TenebriaModel());
	}

	@Override
	public void doRender(@Nonnull EntityTenebria entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderEarly(EntityTenebria animatable, float ticks, float red, float green, float blue, float partialTicks) {
		//Arcanist.scale(1.0f);
		//Arcanist.rotate(180, 0, 1, 0);
	}
}
