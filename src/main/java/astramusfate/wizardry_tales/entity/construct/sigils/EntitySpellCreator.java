package astramusfate.wizardry_tales.entity.construct.sigils;

import astramusfate.wizardry_tales.entity.construct.sigils.chanting.EntityCircleWords;
import astramusfate.wizardry_tales.events.SpellCreation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.List;

public class EntitySpellCreator extends EntityCircleWords {

    public EntitySpellCreator(World world){
        super(world);
        lifetime = 1;
    }

    public EntitySpellCreator(World world, List<String> words, String location) {
        super(world, words);
        setLocation(location);
        lifetime = 1;
    }

    @Override
    public void despawn() {
        super.despawn();
        if(getCaster() != null && getCaster() instanceof EntityPlayer){
            SpellCreation.createSpell(words,
                    this, getCaster(), !world.isRemote);
        }
    }
}
