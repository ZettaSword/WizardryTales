package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.spells.TalesSpells;
import astramusfate.wizardry_tales.spells.list.TeleportationCurse;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Banish;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;
import java.util.UUID;

public class TeleportationCurseEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("6716ffce-65f9-4eaf-b107-0271445615e7");


    public TeleportationCurseEffect() {
        super(true,
                0x6716FF, new ResourceLocation(WizardryTales.MODID, "textures/potions/teleportation_curse.png"));
        this.setPotionName("potion." + WizardryTales.MODID + ":teleportation_curse");

        // vanilla attributes
        //this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), -0.15D, Constants.AttributeModifierOperation.MULTIPLY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }
}
