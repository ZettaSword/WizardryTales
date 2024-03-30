package astramusfate.wizardry_tales.entity.ai;

import astramusfate.wizardry_tales.api.Tenebria;
import electroblob.wizardry.util.AllyDesignationSystem;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIFriendHurtByTarget extends EntityAITarget
{
    EntityCreature creature;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAIFriendHurtByTarget(EntityCreature theDefendingSummon)
    {
        super(theDefendingSummon, false);
        this.creature = theDefendingSummon;
        this.setMutexBits(1);
    }

    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = Tenebria.getUniqueIdOwner(this.creature);

        if (entitylivingbase == null)
        {
            return false;
        }
        else
        {
            this.attacker = entitylivingbase.getRevengeTarget();
            int i = entitylivingbase.getRevengeTimer();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, false)
                    && AllyDesignationSystem.isValidTarget(entitylivingbase,
                    this.attacker);
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = Tenebria.getUniqueIdOwner(this.creature);

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getRevengeTimer();
        }

        super.startExecuting();
    }
}