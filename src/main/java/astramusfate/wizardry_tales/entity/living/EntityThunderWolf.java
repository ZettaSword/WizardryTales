package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.ai.EntityAIAttackSpellSmart;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
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
public class EntityThunderWolf extends EntityWolf implements ISpellCaster {

    public EntityThunderWolf(World worldIn) {
        super(worldIn);
        this.tasks.addTask(3, new EntityAIAttackSpellSmart<>(this,
                1.0, 5,14.0f,
                20, 50));
        this.setScale(1.25f);
    }


    @Override
    public EntityWolf createChild(@Nonnull EntityAgeable ageable) {
        EntityThunderWolf entitywolf = new EntityThunderWolf(this.world);
        UUID uuid = this.getOwnerId();
        if (uuid != null)
        {
            entitywolf.setOwnerId(uuid);
            entitywolf.setTamed(true);
        }

        return entitywolf;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if(entityIn instanceof EntityLivingBase && !Alchemy.hasPotion((EntityLivingBase) entityIn,
                WizardryPotions.paralysis)){
            if (!world.isRemote && Solver.chance(25))
            Alchemy.applyPotion((EntityLivingBase) entityIn,
                    Solver.asTicks(2), 0, WizardryPotions.paralysis);
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        List<Spell> spells = Lists.newArrayList();
        spells.add(Spells.lightning_sigil);
        if(this.getHealth() <= this.getMaxHealth() * 0.7f) spells.add(Spells.lightning_arrow);
        return spells;
    }

    @SubscribeEvent
    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        // We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
        if(event.getEntityLiving() instanceof EntityThunderWolf && !event.isSpawner()){
            if(!ArrayUtils.contains(Tales.entities.mobSpawnDimensions, event.getWorld().provider.getDimension()))
                event.setResult(Event.Result.DENY);
        }
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
}
