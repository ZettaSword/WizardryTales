package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderWolf;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

//@SideOnly(Side.CLIENT)
public class RenderSkeletonWolf extends RenderWolf {

	private static final ResourceLocation texture = new ResourceLocation(WizardryTales.MODID,
			"textures/entity/wolf/skeleton_wolf.png");

//	private static final int GHOST_COPIES = 3;
//	private static final float DECONVERGENCE = 0.8f;

	public RenderSkeletonWolf(RenderManager renderManager){
		super(renderManager);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityWolf entity){
		return texture;
	}

	@Override
	protected void preRenderCallback(EntityWolf entity, float partialTickTime){
		super.preRenderCallback(entity, partialTickTime);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Override
	public void doRender(EntityWolf entity, double x, double y, double z, float entityYaw, float partialTicks){
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}
}
