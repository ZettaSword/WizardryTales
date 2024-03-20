package astramusfate.wizardry_tales.entity.ai;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.api.classes.IEntityGrowable;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.ISummonedCreature;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class EntityAICasterHealIfHurt extends EntityAITarget
{
    EntityCreature creature;
    ISummonedCreature summon;
    EntityLivingBase attacker;
    private int timeBeforeHeal;

    public EntityAICasterHealIfHurt(ISummonedCreature summon, EntityCreature theDefendingSummon)
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

                if(this.timeBeforeHeal > 0) this.timeBeforeHeal--;
                return this.timeBeforeHeal <= 0 && this.isSuitableTarget(this.attacker, false) && this.summon.isValidTarget(this.attacker);
            }
        }
    }

    public void startExecuting()
    {
        this.taskOwner.setAttackTarget(this.attacker);
        EntityLivingBase caster = this.summon.getCaster();

        boolean grown = true;
        if(this.creature instanceof IEntityGrowable){
            grown=((IEntityGrowable)this.creature).isGrown();
        }

        if (caster != null && caster.getHealth() < caster.getMaxHealth() && grown)
        {
            this.timeBeforeHeal = Solver.asTicks(10);
            caster.heal(4.0f);
            Wizard.castParticles(caster.world, Element.EARTH, caster.getPositionVector(), 16);
        }

        super.startExecuting();
    }
}