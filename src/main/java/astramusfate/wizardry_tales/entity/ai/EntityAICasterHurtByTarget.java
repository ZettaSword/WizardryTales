package astramusfate.wizardry_tales.entity.ai;

import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAICasterHurtByTarget extends EntityAITarget
{
    EntityCreature creature;
    ISummonedCreature summon;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAICasterHurtByTarget(ISummonedCreature summon, EntityCreature theDefendingSummon)
    {
        super(theDefendingSummon, false);
        this.creature = theDefendingSummon;
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
                this.attacker = entitylivingbase.getRevengeTarget();
                int i = entitylivingbase.getRevengeTimer();
                return i != this.timestamp && this.isSuitableTarget(this.attacker, false) && this.summon.isValidTarget(this.attacker);
            }
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = this.summon.getCaster();

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getRevengeTimer();
        }

        super.startExecuting();
    }
}