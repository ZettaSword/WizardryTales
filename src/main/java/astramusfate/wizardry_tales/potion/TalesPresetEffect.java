package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/** Used for effects that are custom-ly handled in others EventBuses **/
public class TalesPresetEffect extends PotionMagicEffect {

    public TalesPresetEffect(String name, String iconName, int color) {
        super(true,
                color, new ResourceLocation(WizardryTales.MODID,
                        "textures/potions/" + iconName + ".png"));
        this.setPotionName("potion." + WizardryTales.MODID + ":" + name);

        applyAttributeModifiers();
    }

    public void applyAttributeModifiers(){}

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {}
}
