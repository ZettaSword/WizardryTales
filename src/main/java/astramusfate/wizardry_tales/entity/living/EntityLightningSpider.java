package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.ai.EntityAIAttackSpellSmart;
import com.google.common.collect.Lists;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber
public class EntityLightningSpider extends EntitySpider implements ISpellCaster {

    public EntityLightningSpider(World worldIn) {
        super(worldIn);
        this.tasks.addTask(0, new EntityAIAttackSpellSmart<>(this,
                1.0, 5,14.0f,
                30, 50));
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        List<Spell> spells = Lists.newArrayList();
        spells.add(Spells.lightning_disc);
        if(this.getHealth() <= this.getMaxHealth() * 0.6f) spells.add(Spells.paralysis);
        return spells;
    }

    @SubscribeEvent
    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        // We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
        if(event.getEntityLiving() instanceof EntityLightningSpider && !event.isSpawner()){
            if(!ArrayUtils.contains(Tales.entities.mobSpawnDimensions, event.getWorld().provider.getDimension()))
                event.setResult(Event.Result.DENY);
        }
    }
}
