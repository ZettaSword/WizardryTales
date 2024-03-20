package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.world.World;

public class ConjureIceHalberd extends SpellConjuration {
    public ConjureIceHalberd() {
        super(WizardryTales.MODID, "conjure_ice_halberd", TalesItems.ice_halberd);
        addProperties(DAMAGE);
    }

    @Override
    protected void spawnParticles(World world, EntityLivingBase caster, SpellModifiers modifiers){

        for(int i=0; i<10; i++){
            double x = caster.posX + world.rand.nextDouble() * 2 - 1;
            double y = caster.posY + caster.getEyeHeight() - 0.5 + world.rand.nextDouble();
            double z = caster.posZ + world.rand.nextDouble() * 2 - 1;
            ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(x, y, z).spawn(world);
        }
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
