package astramusfate.wizardry_tales.entity.construct;

import electroblob.wizardry.constants.Element;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public abstract class EntityRitual extends EntityMagicScaled {
    public boolean isEnded;
    public EntityRitual(World world) {
        super(world);
        setSize(3.0f*2, 0.2f);
        setLifetime(120);
        this.setNoGravity(true);
        this.noClip=true;
        this.isEnded = true;
    }

    public EntityRitual(World world, int lifetime) {
        super(world);
        setSize(3.0f*2, 0.2f);
        setLifetime(lifetime);
        this.setNoGravity(true);
        this.noClip=true;
        this.isEnded = true;
    }

    public EntityRitual(World world, float size, int lifetime) {
        super(world);
        setSize(size, 0.2f);
        setLifetime(lifetime);
        this.setNoGravity(true);
        this.noClip=true;
    }

    protected abstract Element getElement();
    public abstract String getLocation();

    public int getLife(){return this.ticksExisted;}

    @Override
    public boolean canRenderOnFire(){
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    protected abstract void tick();
    /** When ritual ends, it's being cast. **/
    protected abstract void ritualEnd();

    /** If true, everything is fine. **/
    public boolean isEnded(){
        return this.isEnded;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        tick();
    }

    @Override
    public void despawn() {
        if (isEnded()) ritualEnd();
        super.despawn();
    }

    public void setEnded(boolean set){this.isEnded=set;}

    /** Interrupts ritual. **/
    public void cancelRitual(){
        this.isEnded=true;
        despawn();
    }

    // Data Sync

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        nbt.setBoolean("isEnded", this.isEnded);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        this.isEnded=nbt.getBoolean("isEnded");
    }

    @Override
    public void writeSpawnData(ByteBuf data){
        super.writeSpawnData(data);
        data.writeBoolean(this.isEnded);
    }

    @Override
    public void readSpawnData(ByteBuf data){
        super.readSpawnData(data);
        this.isEnded=data.readBoolean();
    }

}