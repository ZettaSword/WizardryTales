package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;

//@SideOnly(Side.CLIENT)
public class RenderThunderWolf extends RenderWolf {

	private static final ResourceLocation texture = new ResourceLocation(WizardryTales.MODID,
			"textures/entity/wolf/thunder_wolf.png");

//	private static final int GHOST_COPIES = 3;
//	private static final float DECONVERGENCE = 0.8f;

	public RenderThunderWolf(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWolf entity){
		return texture;
	}

	@Override
	protected void preRenderCallback(EntityWolf entity, float partialTickTime){
		Arcanist.scale(1.25f);
		super.preRenderCallback(entity, partialTickTime);
	}

	@Override
	public void doRender(EntityWolf entity, double x, double y, double z, float entityYaw, float partialTicks){
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}