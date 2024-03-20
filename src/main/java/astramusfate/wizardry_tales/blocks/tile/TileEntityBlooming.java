package astramusfate.wizardry_tales.blocks.tile;

import astramusfate.wizardry_tales.api.Solver;
import electroblob.wizardry.tileentity.TileEntityTimer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityBlooming extends TileEntityTimer {

    private int casterId;
    private UUID casterUUID;
    private double potencyMod = 1.0f;
    private double durationMod = 1.0f;

    public TileEntityBlooming(){
    }

    public TileEntityBlooming(World world){
        this.world = world;
    }

    public void setPotencyMod(double mod){ potencyMod =mod; }
    public void setDurationMod(double mod){ durationMod =mod; }
    public double getDurationMod(){return durationMod; }
    public double getPotencyMod(){return potencyMod;}

    @Override
    public void update() {
        //super.update(); NO DURATION
    }


    public void spawnParticleEffect(){
        if(this.world.isRemote){
            for(int i = 0; i < 15; i++){
                this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.pos.getX() + Solver.range(0.5f),
                        this.pos.getY() + Solver.range(0.5f), this.pos.getZ() + Solver.range(0.5f), 0, 0, 0);
            }
        }
    }

    @Nullable
    public EntityPlayer getCaster(){
     return world.getPlayerEntityByUUID(this.casterUUID) == null ? null : world.getPlayerEntityByUUID(this.casterUUID);
    }


    @Override
    public void readFromNBT(@Nonnull NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("caster_id")) {
            casterId = compound.getInteger("caster_id");
        }
        if (compound.hasKey("caster_uuid")) {
            casterUUID = compound.getUniqueId("caster_uuid");
            casterUUID = NBTUtil.getUUIDFromTag((NBTTagCompound) compound.getTag("caster_uuid"));
        }

        if(compound.hasKey("potencyMod")){ potencyMod = compound.getDouble("potencyMod");}
        if(compound.hasKey("durationMod")){ potencyMod = compound.getDouble("durationMod");}
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(@Nonnull NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("caster_id", casterId);
        if(casterUUID != null) compound.setTag("caster_uuid", NBTUtil.createUUIDTag(casterUUID));
        compound.setDouble("potencyMod", potencyMod);
        compound.setDouble("durationMod", durationMod);
        return compound;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(pos, 0, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(@Nonnull NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    public void setCaster(EntityPlayer caster) {
        casterId = caster.getEntityId();
        casterUUID = caster.getUniqueID();
    }
}
