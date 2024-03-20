package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.entity.living.EntityEnvenomedBlade;
import astramusfate.wizardry_tales.entity.living.EntityMushroom;
import astramusfate.wizardry_tales.renderers.models.EnvenomedBladeModel;
import astramusfate.wizardry_tales.renderers.models.MushroomModel;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import javax.annotation.Nonnull;

public class RenderEnvenomedBlade extends GeoEntityRenderer<EntityEnvenomedBlade> {

	public RenderEnvenomedBlade(RenderManager renderManager) {
		super(renderManager, new EnvenomedBladeModel());
	}

	@Override
	public void doRender(@Nonnull EntityEnvenomedBlade entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	public void renderEarly(EntityEnvenomedBlade animatable, float ticks, float red, float green, float blue, float partialTicks) {
		Arcanist.scale(0.8f);
		Arcanist.rotate(180, 0, 1, 0);
	}
}
