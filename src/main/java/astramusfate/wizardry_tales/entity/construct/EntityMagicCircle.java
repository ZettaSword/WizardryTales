package astramusfate.wizardry_tales.entity.construct;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;

public class EntityMagicCircle extends EntityMagicScaled {

    /** The location for this circle, to have texture */
    protected String location = "u_magic";

    public EntityMagicCircle(World world){
        super(world);
        this.noClip=false;
    }

    public void chooseSize() {
        setSize(this.width, 0.2f);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return false;
    }

    public String getLocation(){
        return location;
    }

    public void setLocation(String location){
        this.location = location;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        setLocation(nbt.getString("location"));
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        nbt.setString("location", location);
    }

    @Override
    public void readSpawnData(ByteBuf data){
        super.readSpawnData(data);
        setLocation(ByteBufUtils.readUTF8String(data)); // Set the width correctly on the client side
    }

    @Override
    public void writeSpawnData(ByteBuf data){
        super.writeSpawnData(data);
        ByteBufUtils.writeUTF8String(data, location);
    }
}
