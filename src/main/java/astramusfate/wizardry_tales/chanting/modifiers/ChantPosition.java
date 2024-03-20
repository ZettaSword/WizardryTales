package astramusfate.wizardry_tales.chanting.modifiers;

import astramusfate.wizardry_tales.chanting.SpellPart;
import astramusfate.wizardry_tales.data.chanting.SpellParams;
import astramusfate.wizardry_tales.events.SpellCreation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;
import java.util.HashMap;

public class ChantPosition extends SpellPart {

    public ChantPosition() {
        super(false);
    }

    public void action(@Nullable EntityLivingBase caster, SpellParams mods, HashMap<String, String> words) {
        if (inv(findIn(mods.keys.get(WORD), "position pos"))) return;
        int x;
        int y;
        int z;
        if(!findIn(words.get(NEXT), ignore)) {
            x = getInteger(words.get(NEXT), 0);
            y = getInteger(words.get(NEXT2), 0);
            z = getInteger(words.get(NEXT3), 0);
        }else{
            x = getInteger(words.get(NEXT2), 0);
            y = getInteger(words.get(NEXT3), 0);
            z = getInteger(words.get(NEXT4), 0);
        }

        boolean hasChanges = Math.abs(x) + Math.abs(y) + Math.abs(z) > 0;

        if (findIn(words.get(PREVIOUS), "add") && hasChanges){
            mods.change = mods.change == null ? new Vec3d(x, y, z) : mods.change.addVector(x, y, z);
        }

        if (findIn(words.get(PREVIOUS), "set")){
            boolean custom = false;
            if (findIn(words.get(NEXT), "caster") && caster != null){
                mods.pos = caster.getPositionVector();
                custom = true;
            }

            if (findIn(words.get(NEXT), "target") && mods.target != null){
                mods.pos = mods.target.getPositionVector();
                custom = true;
            }

            if (findIn(words.get(NEXT), "focal") && mods.focal != null){
                mods.pos = mods.focal.getPositionVector();
                custom = true;
            }

            if (!custom && hasChanges)
                mods.pos = new Vec3d(x,y,z);
        }
    }
}
