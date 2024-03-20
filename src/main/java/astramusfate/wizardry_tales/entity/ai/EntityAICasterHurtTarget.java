package astramusfate.wizardry_tales.entity.ai;

import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAICasterHurtTarget extends EntityAITarget
{
    ISummonedCreature summon;
    EntityCreature creature;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAICasterHurtTarget(ISummonedCreature summon, EntityCreature entityCreature)
    {
        super(entityCreature, false);
        this.creature = entityCreature;
        this.summon = summon;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        if (this.summon.getCaster() == null)
        {
            return false;
        }
        else
        {
            EntityLivingBase entitylivingbase = this.summon.getCaster();

            if (entitylivingbase == null)
            {
                return false;
            }
            else
            {
                this.attacker = entitylivingbase.getLastAttackedEntity();
                int i = entitylivingbase.getLastAttackedEntityTime();
                return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
            }
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = this.summon.getCaster();

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }

        super.startExecuting();
    }
}