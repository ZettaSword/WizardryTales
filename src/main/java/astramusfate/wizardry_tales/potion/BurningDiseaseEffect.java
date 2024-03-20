package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

public class BurningDiseaseEffect extends PotionMagicEffect {


    public BurningDiseaseEffect() {
        super(true,
                Color.RED.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/burning_disease.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":burning_disease");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
            //entity.setFire(entity.setFire(););
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected void drawIcon(int x, int y, PotionEffect effect, net.minecraft.client.Minecraft mc){
        Arcanist.push();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        mc.renderEngine.bindTexture(new ResourceLocation(WizardryTales.MODID,
                "textures/potions/burning_disease.png"));
        electroblob.wizardry.client.DrawingUtils.drawTexturedRect(x, y, 0, 0, 18, 18, 18, 18);
        Arcanist.pop();
    }
}
