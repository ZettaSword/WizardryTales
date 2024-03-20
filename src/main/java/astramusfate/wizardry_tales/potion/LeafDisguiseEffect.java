package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;

import java.util.UUID;

public class LeafDisguiseEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("4e9401d2-5f66-47b0-a6b9-f2ef6352d190");


    public LeafDisguiseEffect() {
        super(true,
                0x006400, new ResourceLocation(WizardryTales.MODID, "textures/potions/entangled.png"));
        this.setPotionName("potion." + WizardryTales.MODID + ":leaf_disguise");

        // vanilla attributes
        this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), -0.15D, Constants.AttributeModifierOperation.MULTIPLY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return Solver.doEvery(duration, Solver.asTicks(5));
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if(entity.isSneaking()) Alchemy.applyPotionHide(entity, Solver.asTicks(5), 0, MobEffects.INVISIBILITY);
    }

}
