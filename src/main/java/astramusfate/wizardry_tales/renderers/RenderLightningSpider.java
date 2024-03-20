package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityLightningSpider;
import astramusfate.wizardry_tales.renderers.layers.LayerSpiderEyes;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSpider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@SideOnly(Side.CLIENT)
public class RenderLightningSpider extends RenderSpider<EntityLightningSpider> {
    private static final ResourceLocation SPIDER_TEXTURES = new ResourceLocation(WizardryTales.MODID,
            "textures/entity/spider/lightning_spider.png");

    public RenderLightningSpider(RenderManager renderManagerIn) {
        super(renderManagerIn);
    }

    protected void preRenderCallback(EntityLightningSpider entitylivingbaseIn, float partialTickTime)
    {
        GlStateManager.scale(1F, 1F, 1F);
    }


    protected ResourceLocation getEntityTexture(EntityLightningSpider entity)
    {
        return SPIDER_TEXTURES;
    }
}
