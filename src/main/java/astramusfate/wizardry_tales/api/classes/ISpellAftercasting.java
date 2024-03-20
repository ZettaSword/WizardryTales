package astramusfate.wizardry_tales.api.classes;

import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

/** This interface is required to understand if the spell can Aftercast.
 * <br><br/> What is Aftercast? It's when spell actually casts only when it's casting is finished
 * <br><br/> For obvious reasons this class is used only for Continuous spells, take that in mind please!
 * <br><br/> And for obvious reasons it can't work for Entities, well, yet **/
public interface ISpellAftercasting {

    boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers);
}
