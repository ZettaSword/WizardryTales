package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class MantisAgilityEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("cf65cb40-36c4-405e-b3af-64bbfc5e7d01");


    public MantisAgilityEffect() {
        super(false,
                0x006400, new ResourceLocation(WizardryTales.MODID, "textures/potions/mantis_agility.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":mantis_agility");

            // vanilla attributes
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, id.toString(), 0.25D, Constants.AttributeModifierOperation.MULTIPLY);
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), 0.1D, Constants.AttributeModifierOperation.MULTIPLY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
