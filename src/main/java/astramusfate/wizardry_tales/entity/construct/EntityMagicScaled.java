package astramusfate.wizardry_tales.entity.construct;

import astramusfate.wizardry_tales.data.Tales;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntityMagicScaled extends EntityMagic {

    /** The size multiplier for this construct, usually determined by the blast modifier the spell was cast with. */
    protected float sizeMultiplier = (float) Tales.effects.circle_size;

    public EntityMagicScaled(World world){
        super(world);
    }

    public float getSizeMultiplier(){
        return sizeMultiplier;
    }

    public void setSizeMultiplier(float sizeMultiplier){
        this.sizeMultiplier = sizeMultiplier;
        setSize(shouldScaleWidth() ? width * sizeMultiplier : width, shouldScaleHeight() ? height * sizeMultiplier : height);
    }

    /** Returns true if the width of this entity's bounding box should be scaled by the size multiplier on creation. */
    protected boolean shouldScaleWidth(){
        return true;
    }

    /** Returns true if the height of this entity's bounding box should be scaled by the size multiplier on creation. */
    protected boolean shouldScaleHeight(){
        return true;
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        nbt.setFloat("sizeMultiplier", sizeMultiplier);

    }
    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        setSizeMultiplier(nbt.getFloat("sizeMultiplier"));
    }


    @Override
    public void writeSpawnData(ByteBuf data){
        super.writeSpawnData(data);
        data.writeFloat(sizeMultiplier);
    }
    @Override
    public void readSpawnData(ByteBuf data){
        super.readSpawnData(data);
        setSizeMultiplier(data.readFloat()); // Set the width correctly on the client side
    }
}
