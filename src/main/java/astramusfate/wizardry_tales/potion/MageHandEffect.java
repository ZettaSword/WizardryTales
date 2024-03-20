package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeModContainer;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class MageHandEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("c202b686-47d3-4b4a-b72e-e5acfeffb8ef");

    public MageHandEffect() {
        super(false,
                0x9c266e, new ResourceLocation(WizardryTales.MODID, "textures/potions/mage_hand.png"));
        this.setPotionName("potion." + WizardryTales.MODID + ":mage_hand");
        // vanilla attributes
        this.registerPotionAttributeModifier(EntityPlayer.REACH_DISTANCE, id.toString(), 2.0D, Solver.ADD);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
