package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.classes.ISmartAnimatable;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.registry.TalesLoot;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityWitherSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.storage.loot.LootTableList;
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
import javax.annotation.Nullable;
import java.util.Arrays;

public class EntityEnvenomedBlade extends AbstractSkeleton implements ISmartAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);

    public EntityEnvenomedBlade(World worldIn) {
        super(worldIn);
        this.setSize(0.7F, 2.4F);
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if(potioneffectIn.getPotion() == MobEffects.POISON) return false;
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.applyEntityAI();
    }

    protected void applyEntityAI()
    {
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
        this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }


    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(2.0D);
    }

    @Nullable
    protected ResourceLocation getLootTable()
    {
        return TalesLoot.ENVENOMED_BLADE;
    }

    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Nonnull
    protected SoundEvent getStepSound()
    {
        return SoundEvents.ENTITY_SKELETON_STEP;
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if (!super.attackEntityAsMob(entityIn))
        {
            return false;
        }
        else
        {
            if (entityIn instanceof EntityLivingBase)
            {
                ((EntityLivingBase)entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, Solver.asTicks(3)));
            }

            return true;
        }
    }

    @Override
    public boolean getCanSpawnHere(){
        if (super.getCanSpawnHere()){
            BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
            Biome biome = this.world.getBiome(blockpos);
            return Arrays.stream(Tales.toResourceLocations(Tales.entities.envenomed_bladeBiomeWhitelist))
                    .anyMatch(e -> biome.getRegistryName() == e);
        }
        return false;
    }

    @Override
    protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
        super.setEquipmentBasedOnDifficulty(difficulty);
        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
    }

    //////////////////////////////////////////// ANIMATIONS! ///////////////////////////////////////////////
    @Override
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller_idle", 0, this::predicateIdle));
        data.addAnimationController(new AnimationController<>(this, "controller_walk", 10, this::predicateWalk));
        data.addAnimationController(new AnimationController<>(this, "controller_attack", 5, this::predicateAttack));
    }

    private <E extends IAnimatable> PlayState predicateAttack(AnimationEvent<E> event) {
        if (this.isSwingingArms()) {
            event.getController().setAnimationSpeed(2.0D);
            event.getController().setAnimation(new AnimationBuilder().addAnimation("attack", ILoopType.EDefaultLoopTypes.PLAY_ONCE));
            return PlayState.CONTINUE;
        } else return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState predicateIdle(AnimationEvent<E> event) {
        if (this.getHealth() <= 0) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("death", ILoopType.EDefaultLoopTypes.HOLD_ON_LAST_FRAME));
            return PlayState.CONTINUE;
        } else if (isIdle(event.getController())) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
            return PlayState.CONTINUE;
        }

        return PlayState.STOP;
    }

    private <E extends IAnimatable> PlayState predicateWalk(AnimationEvent<E> event) {
        if (!(event.getLimbSwingAmount() > -0.15F && event.getLimbSwingAmount() < 0.15F)) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("walking", ILoopType.EDefaultLoopTypes.LOOP));
            event.getController().setAnimationSpeed(this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED)
                    .getAttributeValue() * 10.0);
            return PlayState.CONTINUE;
        }

        else return PlayState.STOP;
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

    /// DEATH STUFF

    @Override
    protected void onDeathUpdate() {
        // Do nothing here!
    }

    @Override
    public int getTimeToDie() {
        return 20;
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
        if (getAnimationController(this.getUniqueID(), "controller_attack") != null) {
            AnimationController attack = getAnimationController(this.getUniqueID(), "controller_attack");
            if(attack.getAnimationState() == AnimationState.Stopped) {
                this.setSwingingArms(false);
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

}
