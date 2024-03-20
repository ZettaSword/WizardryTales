package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.Lists;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber
public class EntitySpellWitch extends EntityWitch implements ISpellCaster {
    public EntitySpellWitch(World worldIn) {
        super(worldIn);
        this.tasks.removeTask(new EntityAIAttackRanged(this, 1.0D, 60, 10.0F));
        //this.tasks.addTask(1, new EntityAIAttackSpell<>(this, 1.0, 14.0f,
        //        30, 70));
    }

    @Override
    protected void initEntityAI()
    {
        super.initEntityAI();
        this.tasks.addTask(4, new EntityAIAvoidEntity<>(this, EntityPlayer.class, 8f,1.0,1.5));
        this.tasks.addTask(1, new EntityAIAttackSpell<>(this, 1.0, 14.0f,
                30, 70));
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        List<Spell> spells = Lists.newArrayList();
        if(this.getHealth() > this.getMaxHealth() * 0.7f) {
            spells.add(Spells.summon_skeleton);
            spells.add(Spells.summon_zombie);
        }else {
            spells.add(Spells.summon_blaze);
            spells.add(Spells.fireball);
            spells.add(Spells.decay);
            spells.add(Spells.banish);
            spells.add(Spells.slime);
            spells.add(Spells.cobwebs);
        }
        return spells;
    }

    @SubscribeEvent
    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        // We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
        if(event.getEntityLiving() instanceof EntitySpellWitch && !event.isSpawner()){
            if(!ArrayUtils.contains(Tales.entities.mobSpawnDimensions, event.getWorld().provider.getDimension()))
                event.setResult(Event.Result.DENY);
        }
    }
}
