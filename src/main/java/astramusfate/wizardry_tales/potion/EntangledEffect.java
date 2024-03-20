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

public class EntangledEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("da0bd8d3-7ecc-465b-b0a0-72981727a202");


    public EntangledEffect() {
        super(true,
                0x006400, new ResourceLocation(WizardryTales.MODID, "textures/potions/entangled.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":entangled");

            // vanilla attributes
        this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, id.toString(), -0.2D, Constants.AttributeModifierOperation.MULTIPLY);
            this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), -0.4D, Constants.AttributeModifierOperation.MULTIPLY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if(entity.isBurning()){
            entity.removePotionEffect(this);
            if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, entity)) {
                entity.attackEntityFrom(MagicDamage.causeDirectMagicDamage(null, MagicDamage.DamageType.FIRE),
                        TalesSpells.entangle.getProperty("damage_if_burning").floatValue());
            }
            //entity.setFire(entity.setFire(););
        }
    }
}
