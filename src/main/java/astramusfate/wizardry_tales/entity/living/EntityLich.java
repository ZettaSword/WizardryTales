package astramusfate.wizardry_tales.entity.living;

import electroblob.wizardry.Wizardry;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntityLich extends EntityLiving implements IEntityOwnable {
    private UUID casterUUID;
    public EntityLich(World p_i1741_1_) {
        super(p_i1741_1_);
    }

    @Override
    protected SoundEvent getHurtSound(@Nonnull DamageSource p_184601_1_) {
        return SoundEvents.ENTITY_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_SKELETON_DEATH;
    }

    @Nullable
    @Override
    public UUID getOwnerId(){ return casterUUID; }

    @Nullable
    @Override
    public Entity getOwner() {
        if(this instanceof Entity){ // Bit of a cheat but it saves having yet another method just to get the world

            Entity entity = EntityUtils.getEntityByUUID(this.world, getOwnerId());

            if(entity != null && !(entity instanceof EntityLivingBase)){ // Should never happen
                Wizardry.logger.warn("{} has a non-living owner!", this);
                return null;
            }

            return entity;

        }else{
            return null;
        }
    }

    public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }

    @Override
    public void readEntityFromNBT(@Nonnull NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        this.setOwnerId(nbt.getUniqueId("ownerUUID"));
    }

    @Override
    public void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        if(this.getOwner() != null){
            nbt.setUniqueId("ownerUUID", this.getOwner().getUniqueID());
        }
    }

    @Nonnull
    @Override
    public EnumHandSide getPrimaryHand() {
        return getOwner() instanceof EntityPlayer ? ((EntityPlayer) getOwner()).getPrimaryHand() : super.getPrimaryHand();
    }
}
