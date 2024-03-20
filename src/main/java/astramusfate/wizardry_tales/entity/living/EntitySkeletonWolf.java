package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.ai.EntityAIAttackSpellSmart;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.List;

@Mod.EventBusSubscriber
public class EntitySkeletonWolf extends EntityWolf implements ISpellCaster {
    public EntitySkeletonWolf(World worldIn) {
        super(worldIn);
        this.tasks.addTask(3, new EntityAIAttackSpellSmart<>(this,
                1.0, 5,14.0f,
                30, 50));
    }

    @Override
    public boolean isPotionApplicable(PotionEffect potioneffectIn) {
        if(potioneffectIn.getPotion() == MobEffects.WITHER) return false;
        return super.isPotionApplicable(potioneffectIn);
    }

    @Override
    public EntityWolf createChild(@Nonnull EntityAgeable ageable) {
        return null;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        this.targetTasks.removeTask(new EntityAINearestAttackableTarget<>(this, AbstractSkeleton.class, false));
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        List<Spell> spells = Lists.newArrayList();
        spells.add(Spells.darkness_orb);
        if(this.getHealth() <= this.getMaxHealth() * 0.7f) spells.add(Spells.banish);
        return spells;
    }

    @SubscribeEvent
    public static void onCheckSpawnEvent(LivingSpawnEvent.CheckSpawn event){
        // We have no way of checking if it's a spawner in getCanSpawnHere() so this has to be done here instead
        if(event.getEntityLiving() instanceof EntitySkeletonWolf && !event.isSpawner()){
            if(!ArrayUtils.contains(Tales.entities.mobSpawnDimensions, event.getWorld().provider.getDimension()))
                event.setResult(Event.Result.DENY);
        }
    }

    @Override
    public boolean isTamed() {
        return super.isTamed() || this.getOwner() != null;
    }

    @Override
    public boolean processInteract(EntityPlayer player, @Nonnull EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        if (!itemstack.isEmpty()){
            if (itemstack.getItem() == TalesItems.infinite_summon){
                return true;
            }
        }
        if (this.isTamed())
        {
            if (!itemstack.isEmpty())
            {
                if (itemstack.getItem() == Items.BONE)
                {

                    if (this.getHealth() < 20.0F)
                    {
                        if (!player.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }

                        this.heal(4);
                        return true;
                    }
                }
                else if (itemstack.getItem() == Items.DYE)
                {
                    EnumDyeColor enumdyecolor = EnumDyeColor.byDyeDamage(itemstack.getMetadata());

                    if (enumdyecolor != this.getCollarColor())
                    {
                        this.setCollarColor(enumdyecolor);

                        if (!player.capabilities.isCreativeMode)
                        {
                            itemstack.shrink(1);
                        }

                        return true;
                    }
                }
            }

            if (this.isOwner(player) && !this.world.isRemote && !this.isBreedingItem(itemstack))
            {
                this.aiSit.setSitting(!this.isSitting());
                this.isJumping = false;
                this.navigator.clearPath();
                this.setAttackTarget(null);
            }
        }
        else if (itemstack.getItem() == Items.BONE && !this.isAngry())
        {
            if (!player.capabilities.isCreativeMode)
            {
                itemstack.shrink(1);
            }

            if (!this.world.isRemote)
            {
                if (this.rand.nextInt(3) == 0 && !net.minecraftforge.event.ForgeEventFactory.onAnimalTame(this, player))
                {
                    this.setTamedBy(player);
                    this.navigator.clearPath();
                    this.setAttackTarget(null);
                    this.aiSit.setSitting(true);
                    this.setHealth(20.0F);
                    this.playTameEffect(true);
                    this.world.setEntityState(this, (byte)7);
                }
                else
                {
                    this.playTameEffect(false);
                    this.world.setEntityState(this, (byte)6);
                }
            }

            return true;
        }


        return false;
    }

    @Override
    public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
        if(source == DamageSource.DROWN) return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    protected void playStepSound(@Nonnull BlockPos pos, @Nonnull Block blockIn)
    {
        this.playSound(SoundEvents.ENTITY_SKELETON_STEP, 0.15F, 1.0F);
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource damageSourceIn)
    {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound()
    {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Override
    protected SoundEvent getAmbientSound()
    {
        return SoundEvents.ENTITY_SKELETON_AMBIENT;
    }
}
