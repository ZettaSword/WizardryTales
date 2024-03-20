package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityMidnightTrader;
import net.minecraft.client.model.ModelVillager;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderMidnightTrader extends RenderLiving<EntityMidnightTrader>
{
    private static final ResourceLocation MIDNIGHT_TEXTURES = new ResourceLocation(WizardryTales.MODID
            ,"textures/entity/midnight/midnight_trader.png");
    public RenderMidnightTrader(RenderManager renderManagerIn)
    {
        super(renderManagerIn, new ModelVillager(0.0F), 0.5F);
    }

    @Nonnull
    public ModelVillager getMainModel()
    {
        return (ModelVillager)super.getMainModel();
    }

    protected ResourceLocation getEntityTexture(EntityMidnightTrader entity)
    {
        return MIDNIGHT_TEXTURES;
    }

    protected void preRenderCallback(@Nonnull EntityMidnightTrader entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.9375F;
        this.shadowSize = 0.5F;

        GlStateManager.scale(f, f, f);
    }
}