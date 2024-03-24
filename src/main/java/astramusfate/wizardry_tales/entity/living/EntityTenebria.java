package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.construct.EntityRedGas;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
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

public class EntityTenebria extends EntityMob implements IAnimatable, IAnimationTickable {
    private final AnimationFactory factory = new AnimationFactory(this);
    private boolean spawnOnDeath;
    private int hurtTime;

    public EntityTenebria(World worldIn) {
        super(worldIn);
        this.setSize(0.6F, 1.8F);
        this.spawnOnDeath = false;
        this.hurtTime = 0;
        this.setAlwaysRenderNameTag(true);
        this.setCustomNameTag("Tenebria");
    }

    @Override
    public boolean isPotionApplicable(@Nonnull PotionEffect potioneffectIn) {
        return false;
    }

    @Override
    public float getEyeHeight() {
        return 1.0f;
    }

    @Override
    protected void initEntityAI()
    {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.75D, false));
        this.tasks.addTask(4, new EntityAIWander(this, 1.75D));
        this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 14.0F));
        this.tasks.addTask(8, new EntityAILookIdle(this));
        this.applyEntityAI();
    }

    protected void applyEntityAI()
    {
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10000);
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(35.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(10.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(20.0D);
    }

    private <E extends IAnimatable> PlayState predicateIdle(AnimationEvent<E> event) {
        if (event.getController().getCurrentAnimation() == null) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("idle", ILoopType.EDefaultLoopTypes.LOOP));
        }
        return PlayState.CONTINUE;
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
    public void registerControllers(AnimationData data) {
        data.addAnimationController(new AnimationController<>(this, "controller_idle", 0, this::predicateIdle));
        data.addAnimationController(new AnimationController<>(this, "controller_walk", 10, this::predicateWalk));
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

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if(this.hurtTime > 0) this.hurtTime--;

        if (getHealth() <= 9990) {
            if (!spawnOnDeath) {
                if (!world.isRemote) {
                    for(int i = 0; i < 10; i++) {
                        BlockPos blockpos = (new BlockPos(EntityTenebria.this)).add(1, 2, 1);
                        EntityBat entitySpawn = new EntityBat(EntityTenebria.this.world);
                        entitySpawn.moveToBlockPosAndAngles(blockpos, 0.0F, 0.0F);
                        this.world.spawnEntity(entitySpawn);
                    }
                }
                if (world.isRemote){
                    for(int i = 0; i < 20; i++){
                        ParticleBuilder.create(ParticleBuilder.Type.DUST)
                                .pos(this.posX + (this.rand.nextDouble() - 0.5) * this.width, this.posY
                                        + this.rand.nextDouble() * this.height, this.posZ + (this.rand.nextDouble() - 0.5) * this.width)
                                .time(40)
                                .clr(0.2f, 1.0f, 0.8f)
                                .shaded(true)
                                .spawn(world);
                    }
                }
                this.setDead();
                spawnOnDeath = true;
            }
        }
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        this.hurtTime = Solver.asTicks(5);
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
        if(EntityUtils.isLiving(entityIn)) {
            boolean isLowHealth = ((EntityLivingBase) entityIn).getHealth()
                    <= ((EntityLivingBase) entityIn).getMaxHealth() * 0.2f;
            if (isLowHealth) {
                Alchemy.applyPotion(((EntityLivingBase) entityIn), Solver.duration(3), 0, MobEffects.NAUSEA);
                return false;
            }
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean getCanSpawnHere(){
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setBoolean("spawn_on_death", spawnOnDeath);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound p_70020_1_) {
        super.readEntityFromNBT(p_70020_1_);
        this.spawnOnDeath = p_70020_1_.getBoolean("spawn_on_death");
    }
}
