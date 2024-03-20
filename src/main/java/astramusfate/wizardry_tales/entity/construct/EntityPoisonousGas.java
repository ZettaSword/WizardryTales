package astramusfate.wizardry_tales.entity.construct;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import java.util.List;

public class EntityPoisonousGas extends EntitySmartConstruct {
    public EntityPoisonousGas(World world){
        super(world);
        setSize(2.0f * 2, 2);
    }

    public EntityPoisonousGas(World world, SpellModifiers spellModifiers) {
        super(world, spellModifiers);
        setSize(2.0f * 2, 2);
    }

    public void onUpdate() {

        super.onUpdate();

        if (!this.world.isRemote) {
            List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(2.0D, this.posX, this.posY,
                    this.posZ, this.world);

            for(EntityLivingBase target : targets){

                if(this.isValidTarget(target)){

                    if(this.getCaster() != null){
                        if(Solver.doEvery(this, 2) && !MagicDamage.isEntityImmune(MagicDamage.DamageType.POISON, target)) {
                            EntityUtils.attackEntityWithoutKnockback(target,
                                    MagicDamage.causeIndirectMagicDamage(this, getCaster(), MagicDamage.DamageType.POISON),
                                    1 * modifiers.get(Sage.POTENCY));
                        }
                    }else{
                        EntityUtils.attackEntityWithoutKnockback(target, DamageSource.MAGIC,
                                1 * modifiers.get(Sage.POTENCY));
                    }

                    Alchemy.applyPotion(target,
                            (int) (TalesSpells.blooming.getProperty(Spell.EFFECT_DURATION).floatValue()
                                    * 0.5f * modifiers.get(Sage.DURATION)), 0, MobEffects.POISON);
                }

            }
        }else{
            for(int i=0; i<3; i++){
                ParticleBuilder.create(ParticleBuilder.Type.CLOUD).pos(this.posX + Solver.range(2.0),
                        this.posY + Solver.range(2.0), this.posZ + Solver.range(2.0))
                        .clr(0x587246).shaded(true).spawn(world);
            }
        }
    }
}
