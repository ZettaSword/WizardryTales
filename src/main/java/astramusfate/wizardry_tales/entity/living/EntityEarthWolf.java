package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.ai.EntityAIAttackSpellSmart;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Mod.EventBusSubscriber
public class EntityEarthWolf extends EntityWolf implements ISpellCaster {
    public EntityEarthWolf(World worldIn) {
        super(worldIn);
        this.tasks.addTask(3, new EntityAIAttackSpellSmart<>(this,
                1.0, 5,14.0f,
                20, 50));
    }

    @Override
    public EntityWolf createChild(@Nonnull EntityAgeable ageable) {
        EntityEarthWolf entitywolf = new EntityEarthWolf(this.world);
        UUID uuid = this.getOwnerId();

        if (uuid != null)
        {
            entitywolf.setOwnerId(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        List<Spell> spells = Lists.newArrayList();
        spells.add(Spells.dart);
        if(this.getHealth() <= this.getMaxHealth() * 0.7f) spells.add(Spells.whirlwind);
        return spells;
    }

    @Override
    public boolean processInteract(EntityPlayer player , EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty()){
            if (itemstack.getItem() == TalesItems.infinite_summon){
                return true;
            }
        }
        return super.processInteract(player, hand);
    }

    @SubscribeEvent
    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        // We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
        if(event.getEntityLiving() instanceof EntityEarthWolf && !event.isSpawner()){
            if(!ArrayUtils.contains(Tales.entities.mobSpawnDimensions, event.getWorld().provider.getDimension()))
                event.setResult(Event.Result.DENY);
        }
    }
}
