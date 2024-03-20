package astramusfate.wizardry_tales.entity.construct;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.entity.living.EntityBigMushroom;
import astramusfate.wizardry_tales.entity.living.EntityMushroom;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.world.World;

import java.util.List;

public class EntityRedGas extends EntitySmartConstruct {
    public EntityRedGas(World world){
        super(world);
        setSize(2.0f * 2, 2);
    }

    public EntityRedGas(World world, SpellModifiers spellModifiers) {
        super(world, spellModifiers);
        setSize(2.0f * 2, 2);
    }



    public void onUpdate() {

        super.onUpdate();

        if (!this.world.isRemote) {
            List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(2.0D * modifiers.get(Sage.BLAST), this.posX, this.posY,
                    this.posZ, this.world);

            for(EntityLivingBase target : targets){

                if(!(target instanceof EntityMushroom) && !(target instanceof EntityBigMushroom)){

                    Alchemy.applyPotion(target,
                            (int) (Solver.duration(5) * modifiers.get(Sage.DURATION)), 0, MobEffects.POISON);

                    if(Solver.doEvery(this, 2)) {
                        EntityUtils.attackEntityWithoutKnockback(target,
                                MagicDamage.causeIndirectMagicDamage(this, this, MagicDamage.DamageType.POISON),
                                1 * modifiers.get(Sage.POTENCY));
                    }


                }

            }
        }else{
            for(int i=0; i<3; i++){
                ParticleBuilder.create(ParticleBuilder.Type.CLOUD).pos(this.posX + Solver.range(2.0),
                        this.posY + Solver.range(2.0D), this.posZ + Solver.range(2.0))
                        .clr(0x871105).shaded(true).spawn(world);
            }
        }
    }
}
