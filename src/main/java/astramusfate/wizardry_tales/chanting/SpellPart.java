package astramusfate.wizardry_tales.chanting;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.data.chanting.SpellParams;
import astramusfate.wizardry_tales.events.SpellCreationHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class SpellPart extends SpellCreationHelper implements Lexicon {

    public static final String WORD = "word";
    public static final String NEXT = "next";
    public static final String NEXT2 = "next2";
    public static final String NEXT3 = "next3";
    public static final String NEXT4 = "next4";
    public static final String PREVIOUS = "previous";

    protected SpellPart(boolean isAction){if (!isAction) Chanting.params.add(this);}

    public void useParam(@Nullable EntityLivingBase caster, SpellParams mods){
        if (mods.world == null || mods.target == null) return;
        // Spell parameter!
        action(mods.world, caster, mods.target, mods);
    }

    public void useAction(@Nullable EntityLivingBase caster, SpellParams mods){
        if (mods.world == null || mods.target == null) return;
        // Spell action!
        action(caster, mods);
    }

    protected void action(@Nullable EntityLivingBase caster, SpellParams mods){
        // Do nothing
    }

    protected void action(World world, @Nullable EntityLivingBase caster, Entity target, SpellParams mods) {
        // Do nothing
    }

    public boolean isAlive(Entity target){
        return target instanceof EntityLivingBase;
    }

    public boolean inv(boolean bool){return Solver.invert(bool);}
}
