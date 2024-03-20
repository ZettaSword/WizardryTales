package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class OreMarkPotion extends PotionMagicEffect {
    public OreMarkPotion() {
        super(false, Color.MAGENTA.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/magic_battle.png"));
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
