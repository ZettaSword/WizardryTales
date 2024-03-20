package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.events.SpellCreation;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityCircleTeleportation extends EntityMagicCircle {

    protected Vec3d stored = new Vec3d(0, 0, 0);
    protected int cooldown = 20;
    public EntityCircleTeleportation(World world) {
        super(world);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (cooldown > 0) cooldown--;
        if(!this.world.isRemote){
            if (cooldown == 0) {
                List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width / 2, posX, posY, posZ, world);

                for (EntityLivingBase target : targets) {
                    if (target.timeUntilPortal > 0){continue;}
                    double distance = this.getPositionVector().distanceTo(this.getStoredPosition());
                    if ((getCaster() != null && (target == getCaster() || AllyDesignationSystem.isAllied(getCaster(), target)) && target.isSneaking() && onlyAllies)){
                        if (SpellCreation.useMana(target, (distance * distance)/100F)){
                            target.setPositionAndUpdate(stored.x, stored.y, stored.z);
                            target.timeUntilPortal= Solver.asTicks(5);
                            target.fallDistance = 0;
                            cooldown = 20;
                        }
                    }else{
                        if (SpellCreation.useMana(this, (distance * distance)/100F)) {
                            target.setPositionAndUpdate(stored.x, stored.y, stored.z);
                            target.timeUntilPortal = Solver.asTicks(5);
                            target.fallDistance = 0;
                            cooldown = 20;
                        }
                    }
                }
            }
        }else if(this.rand.nextInt(15) == 0){
            double radius = (0.5 + rand.nextDouble() * 0.3) * width/2;
            float angle = rand.nextFloat() * (float)Math.PI * 2;
            world.spawnParticle(EnumParticleTypes.ENCHANTMENT_TABLE, this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
                    this.posZ + radius * MathHelper.sin(angle), 0, 0, 0);
        }
    }

    @Nonnull
    @Override
    public EnumActionResult applyPlayerInteraction(@Nonnull EntityPlayer player, @Nonnull Vec3d vec, @Nonnull EnumHand hand) {
        return super.applyPlayerInteraction(player, vec, hand);
    }

    public void setStoredPosition(Vec3d store){
        stored = store;
    }

    public Vec3d getStoredPosition(){
        return stored;
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
        super.writeEntityToNBT(nbt);
        nbt.setDouble("stored_x", stored.x);
        nbt.setDouble("stored_y", stored.y);
        nbt.setDouble("stored_z", stored.z);
        nbt.setInteger("cooldown", cooldown);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt){
        super.readEntityFromNBT(nbt);
        setStoredPosition(new Vec3d(nbt.getDouble("stored_x"), nbt.getDouble("stored_y"), nbt.getDouble("stored_z")));
        this.cooldown = nbt.getInteger("cooldown");
    }

    @Override
    public void writeSpawnData(ByteBuf data){
        super.writeSpawnData(data);
        data.writeDouble(stored.x);
        data.writeDouble(stored.y);
        data.writeDouble(stored.z);
        data.writeInt(cooldown);
    }

    @Override
    public void readSpawnData(ByteBuf data){
        super.readSpawnData(data);
        setStoredPosition(new Vec3d(data.readDouble(), data.readDouble(), data.readDouble()));
        this.cooldown = data.readInt();
    }


}
