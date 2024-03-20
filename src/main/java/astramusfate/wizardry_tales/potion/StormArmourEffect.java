package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Tenebria;
import astramusfate.wizardry_tales.api.Solver;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.UUID;

public class StormArmourEffect extends PotionMagicEffect {
    public static final UUID id = UUID.fromString("4e9401d2-5f66-47b0-a6b9-f2ef6352d190");


    public StormArmourEffect() {
        super(false,
                Color.BLUE.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/storm_armour.png"));
        this.setPotionName("potion." + WizardryTales.MODID + ":storm_armour");

        // vanilla attributes
        this.registerPotionAttributeModifier(Tenebria.MOVEMENT_SPEED, id.toString(), -0.1D, Solver.MULTIPLY);
        this.registerPotionAttributeModifier(Tenebria.MAX_HEALTH, id.toString(), 2.0D, Solver.ADD);
        this.registerPotionAttributeModifier(Tenebria.ARMOR, id.toString(), 4.0D, Solver.ADD);
        this.registerPotionAttributeModifier(Tenebria.KNOCKBACK_RESISTANCE, id.toString(), 1.0D, Solver.ADD);
        this.registerPotionAttributeModifier(Tenebria.ATTACK_DAMAGE, id.toString(), 2.0D, Solver.ADD);

        this.registerPotionAttributeModifier(Tenebria.ATTACK_SPEED, id.toString(), -0.25D, Solver.MULTIPLY);
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return false;
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {}

}
