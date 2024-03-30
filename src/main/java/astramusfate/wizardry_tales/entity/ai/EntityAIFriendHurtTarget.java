package astramusfate.wizardry_tales.entity.ai;

import astramusfate.wizardry_tales.api.Tenebria;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAIFriendHurtTarget extends EntityAITarget
{
    EntityCreature creature;
    EntityLivingBase attacker;
    private int timestamp;

    public EntityAIFriendHurtTarget(EntityCreature entityCreature)
    {
        super(entityCreature, false);
        this.creature = entityCreature;
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
            this.attacker = entitylivingbase.getLastAttackedEntity();
            int i = entitylivingbase.getLastAttackedEntityTime();
            return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase entitylivingbase = Tenebria.getUniqueIdOwner(this.creature);

        if (entitylivingbase != null)
        {
            this.timestamp = entitylivingbase.getLastAttackedEntityTime();
        }

        super.startExecuting();
    }
}