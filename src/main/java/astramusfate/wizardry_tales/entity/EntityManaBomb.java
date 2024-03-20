package astramusfate.wizardry_tales.entity;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.entity.projectile.EntityBomb;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityManaBomb  extends EntityBomb {

    public EntityManaBomb(World world){
        super(world);
    }

    @Override
    public int getLifetime(){
        return -1;
    }

    @Override
    protected void onImpact(@Nonnull RayTraceResult rayTrace){

        // Particle effect
        if(world.isRemote){

            //ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(this.getPositionVector()).scale(5 * blastMultiplier).clr(0, 0, 0).spawn(world);

            //this.world.spawnParticle(EnumParticleTypes.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 0, 0, 0);

            for(int i = 0; i < 60 * blastMultiplier; i++){

                //float brightness = rand.nextFloat() * 0.1f + 0.1f;
                ParticleBuilder.create(ParticleBuilder.Type.CLOUD, rand, posX, posY, posZ, 2*blastMultiplier, false)
                        .clr(0xFFE34C).time(40 + this.rand.nextInt(12)).shaded(true).spawn(world);

                //brightness = rand.nextFloat() * 0.3f;
                ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC, rand, posX, posY, posZ, 2*blastMultiplier, false)
                        .clr(0xFFE34C).spawn(world);
            }
        }

        if(!this.world.isRemote){

            this.playSound(WizardrySounds.ENTITY_SMOKE_BOMB_SMASH, 1.5F, rand.nextFloat() * 0.4F + 0.6F);
            this.playSound(WizardrySounds.ENTITY_SMOKE_BOMB_SMOKE, 1.2F, 1.0f);

            double range = TalesSpells.mana_bomb.getProperty(Spell.BLAST_RADIUS).floatValue() * blastMultiplier;

            List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(range, this.posX, this.posY,
                    this.posZ, this.world);

            int duration = TalesSpells.mana_bomb.getProperty(Spell.EFFECT_DURATION).intValue();

            for(EntityLivingBase target : targets){
                if(target != this.getThrower()){
                    int type = Solver.randInt(0, 4);
                    if(type == 0) {
                        target.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, duration, 0));
                    }else if(type == 1){
                        target.setFire((int) Solver.asSeconds(duration));
                        target.addPotionEffect(new PotionEffect(TalesEffects.burning_disease, duration, 0));
                    }else if(type == 2){
                        target.addPotionEffect(new PotionEffect(WizardryPotions.frost, duration, 0));
                    }else if(type == 3){
                        target.addPotionEffect(new PotionEffect(TalesEffects.entangled, duration, 0));
                    }else if(type == 4){
                        target.addPotionEffect(new PotionEffect(TalesEffects.teleportation_curse, duration*5, 0));
                    }
                }
            }

            this.setDead();
        }
    }
}