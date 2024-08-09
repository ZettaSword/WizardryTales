package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityVampire;
import astramusfate.wizardry_tales.renderers.models.ModelVampire;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

public class RenderVampire  extends RenderBiped<EntityVampire> {

    static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

    public RenderVampire(RenderManager renderManager){
        super(renderManager, new ModelVampire(0.0F), 0.5F);

        for(int i = 0; i < 6; i++){
            TEXTURES[i] = new ResourceLocation(WizardryTales.MODID, "textures/entity/vampire/vampire_" + i + ".png");
        }
        // Just using the default without overriding models, since the armour sets its own model anyway.
        this.addLayer(new LayerBipedArmor(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVampire vampire){
        return TEXTURES[vampire.textureIndex];
    }


}
