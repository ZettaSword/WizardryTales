package astramusfate.wizardry_tales.entity.ai;

import astramusfate.wizardry_tales.api.classes.IEntitySit;
import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISummonSit extends EntityAIBase
{
    ISummonedCreature summon;
    IEntitySit sitting;
    private final EntityLiving creature;

    public EntityAISummonSit(EntityLiving entityIn)
    {
        this.creature = entityIn;
        this.summon = (ISummonedCreature) entityIn;
        this.sitting = (IEntitySit) entityIn;
        this.setMutexBits(5);
    }

    public boolean shouldExecute()
    {
        if (this.summon.getCaster() == null)
        {
            return false;
        }
        else if (this.creature.isInWater())
        {
            return false;
        }
        else if (!this.creature.onGround)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.summon.getCaster();

            if (entitylivingbase == null)
            {
                return true;
            }
            else
            {
                return (!(this.creature.getDistanceSq(entitylivingbase) < 144.0D) || entitylivingbase.getRevengeTarget() == null) && this.sitting.isSitting();
            }
        }
    }

    public void startExecuting()
    {
        this.creature.getNavigator().clearPath();
        this.sitting.setSitting(true);
    }

    public void resetTask() {
        this.sitting.setSitting(false);
    }
}