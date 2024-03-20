package astramusfate.wizardry_tales.renderers.layers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.registry.TalesEffects;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class LayerTangled extends TalesLayerTiledOverlay<EntityLivingBase> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryTales.MODID,
            "textures/layers/vine.png");

    public LayerTangled(RenderLivingBase<?> renderer) {
        super(renderer);
    }

    public boolean shouldRender(EntityLivingBase entity, float partialTicks) {
        return !entity.isInvisible() && entity.isPotionActive(TalesEffects.entangled);
    }

    public ResourceLocation getTexture(EntityLivingBase entity, float partialTicks) {
        return TEXTURE;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        // GlStateManager.enableLighting();
        Arcanist.shade(entity);
        super.doRenderLayer(entity, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
        GlStateManager.disableBlend();
    }



    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}