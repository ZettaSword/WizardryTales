package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class Locating extends Spell {

    public Locating() {
        super(WizardryTales.MODID, "locating", SpellActions.POINT_DOWN, false);
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        if(caster != null) {
            if(!world.isRemote) {
                Aterna.translate(caster, "spell.wizardry_tales:locating.comment");
                Aterna.messageChat(caster, "Dimension ID: [ " + caster.world.provider.getDimension() + " ]");
                Aterna.messageChat(caster, "X: [ " + (int) caster.posX + " ]");
                Aterna.messageChat(caster, "Y: [ " + (int) caster.posY + " ]");
                Aterna.messageChat(caster, "Z: [ " + (int) caster.posZ + " ]");
            }else{
                for(int i = 0; i<15;i++) {
                    double x = caster.posX + Solver.randFloat(-1.5f, 1.5f);
                    double y = caster.posY + caster.getEyeHeight() + Solver.randFloat(-1.5f, -1.25f);//+ Solver.randFloat(-1.5f, 0.5f);
                    double z = caster.posZ + Solver.randFloat(-1.5f, 1.5f);
                    ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0.8f, 1, 0.5f).spawn(world);
                    x = caster.posX + Solver.randFloat(-1.5f, 1.5f);
                    y = caster.posY + caster.getEyeHeight() + Solver.randFloat(-1.5f, 1.5f);
                    z = caster.posZ + Solver.randFloat(-1.5f, 1.5f);
                    ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(1f, 1f, 1f).spawn(world);
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
