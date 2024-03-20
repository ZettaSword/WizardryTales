package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Selena;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.classes.ISmartAnimatable;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.construct.EntityRedGas;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import software.bernie.geckolib3.core.AnimationState;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.IAnimationTickable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class EntityBigMushroom extends EntityMob implements ISmartAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private boolean spawnOnDeath;

    public EntityBigMushroom(World worldIn) {
        super(worldIn);
        this.setSize(1.8F, 2.2F);
        this.spawnOnDeath = false;
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn) {
        return super.getHurtSound(damageSourceIn);
    }

    // NO MOTION

    @Override
    public void applyEntityCollision(@Nonnull Entity entityIn) {
        //super.applyEntityCollision(entityIn);  We do nothing
    }

    @Override
    public void knockBack(@Nonnull Entity entityIn, float strength, double xRatio, double zRatio) {
        //Does nothing now!
    }

    // NO MOTION

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if(potioneffectIn.getPotion() == MobEffects.POISON) return false;
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public float getEyeHeight() {
        return 1.0f;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 14.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI()
    {
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(75.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(12.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(0.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (Solver.doEvery(this.ticksExisted, 5)) {
            if(!isAngry()) {
                List<EntityLivingBase> players = Selena.getAround(world,
                        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(),
                        this.getPosition(), EntityLivingBase.class, e ->
                                (e instanceof EntityPlayer && ((EntityPlayer)e).isCreative()) || e.isInvisible() || e == this);
                players.removeIf(e -> e instanceof EntityMushroom);

                if(!players.isEmpty()){
                    EntityLivingBase nearest = (EntityLivingBase) Selena.findNearest(this.getPositionVector(), players);
                    if (nearest != null) {
                        this.setRevengeTarget(nearest);
                        attackEntityAsMob(nearest);
                    }
                }
                return;
            }

            if (!world.isRemote && isAngry()) {
                BlockPos blockpos = (new BlockPos(EntityBigMushroom.this)).add(1, 2, 1);

                List<EntityLivingBase> players = Selena.getAround(world,
                        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).getAttributeValue(),
                        this.getPosition(), EntityLivingBase.class, e ->
                                (e instanceof EntityPlayer && ((EntityPlayer)e).isCreative()) || e.isInvisible() || e == this);

                players.removeIf(e -> e instanceof EntityMushroom &&
                        (e != this.getAttackTarget() || e != this.getRevengeTarget()));

                if(players.size() > 0) {
                    EntityLivingBase nearest = players.size() == 1 ? players.get(0) :
                            (EntityLivingBase) Selena.findNearest(this.getPositionVector(), players);

                    if (nearest == null) return;

                    if(nearest.getDistance(this) <= 6) {
                        EntityRedGas entitySpawn = new EntityRedGas(EntityBigMushroom.this.world);
                        entitySpawn.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
                        entitySpawn.setModifiers(new SpellModifiers());
                        entitySpawn.setLifetime(Solver.duration(5));
                        this.world.spawnEntity(entitySpawn);
                    }else{
                        List<EntityMushroom> mushrooms = Selena.getAround(world,
                                20,
                                this.getPosition(), EntityMushroom.class);
                        if(mushrooms.size() < 5) {
                            EntityMushroom mushroom = new EntityMushroom(EntityBigMushroom.this.world);
                            mushroom.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
                            mushroom.setAttackTarget(nearest);
                            mushroom.setRevengeTarget(nearest);
                            this.world.spawnEntity(mushroom);
                        }
                    }
                }


            }
        }
    }

    /// DEATH STUFF

    @Override
    protected void onDeathUpdate() {
        // Do nothing here!
    }

    @Override
    public int getTimeToDie() {
        return 40;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (getHealth() <= 0.0F) {
            if (getAnimationController(this.getUniqueID(), "controller_idle") != null) {
                AnimationController death = getAnimationController(this.getUniqueID(), "controller_idle");
                if(death.getAnimationState() == AnimationState.Stopped) {
                    onDeathTick();
                }
            }else {
                onDeathTick();
            }
        }
    }

    private void onDeathTick() {
        ++this.deathTime;

        if (this.deathTime == getTimeToDie()) {
            if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }

            this.setDead();

            for (int k = 0; k < 20; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
            }
        }
    }

    // DEATH STUFF

    public boolean isAngry(){
        return this.getAttackTarget() != null || this.getRevengeTarget() != null;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if(source == DamageSource.IN_WALL) return false; // Due to his big size, he has some problems with walls
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if(EntityUtils.isLiving(entityIn)) {
            boolean isLowHealth = ((EntityLivingBase) entityIn).getHealth()
                    <= ((EntityLivingBase) entityIn).getMaxHealth() * 0.7f;
            if (Solver.chance(50 + (isLowHealth ? 30 : 0))) {
                Alchemy.applyPotion(((EntityLivingBase) entityIn), Solver.duration(3), 0, MobEffects.POISON);
            }
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean getCanSpawnHere(){
        if (super.getCanSpawnHere()){
            BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
            Biome biome = this.world.getBiome(blockpos);
            return Arrays.stream(Tales.toResourceLocations(Tales.entities.big_mushroomBiomeWhitelist))
                    .anyMatch(e -> biome.getRegistryName() == e);
        }
        return false;
    }

    //////////////////////////////////////////// ANIMATIONS! ///////////////////////////////////////////////

    private <E extends IAnimatable> PlayState predicateIdles(AnimationEvent<E> event) {
        if (!isAngry() && (this.getHealth() < this.getMaxHealth()) && this.getHealth() > 0 ) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle_angry", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }else if (this.getHealth() <= 0) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        }else if (isIdle(event.getController())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState predicateGasAttack(AnimationEvent<E> event) {
        if (isAngry() && (this.getHealth() < this.getMaxHealth()) && this.getHealth() > 0 ) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("gas_attack", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        else return PlayState.STOP;
    }



    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller_idle", 10, this::predicateIdles));
        data.addAnimationController(new AnimationController<>(this, "controller_gas", 20, this::predicateGasAttack));
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }

    @Override
    public int tickTimer() {
        return ticksExisted;
    }

    @Override
    public void tick() {
        super.onUpdate();
    }
}
