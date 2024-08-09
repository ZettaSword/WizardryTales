package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.data.EventsBase;
import de.teamlapen.vampirism.util.Helper;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VampirismEvents extends EventsBase {

    @SubscribeEvent
    public static void onSpellcastRace(SpellCastEvent.Pre event){
        if(event.getCaster() instanceof EntityPlayer && Helper.isVampire(event.getCaster())) {
            SpellModifiers mods = event.getModifiers();
            if (event.getSpell().getElement() == Element.NECROMANCY || event.getSpell().getElement() == Element.ICE) {
                mods.set(SpellModifiers.POTENCY, mods.get(SpellModifiers.POTENCY) * 1.15f, false);
                mods.set(SpellModifiers.CHARGEUP, mods.get(SpellModifiers.CHARGEUP) * 0.9f, false);
            }
        }
    }
}
