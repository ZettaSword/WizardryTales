package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAir;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

public class EntityCircleManaCollector extends EntityCircleWords {
    public int type = 1;
    public double mana = 0.0f;

    public EntityCircleManaCollector(World world){
        super(world);
    }

    public EntityCircleManaCollector(World world, List<String> words, int type) {
        super(world, words);
        this.type=type;
    }

    public void setMana(double mana){this.mana = mana;}
    public double getMana(){return this.mana;}
    public void addMana(double mana){this.mana += mana;}

    @Override
    public void onUpdate() {
        super.onUpdate();
        if(Solver.doEvery(this.ticksExisted, 5.0D) && getCaster() != null){
            switch (type){
                case 1:
                    break;
                case 2: List<Entity> list = EntityUtils.getEntitiesWithinRadius(this.width,
                        this.getPosition().getX(), this.getPosition().getY(), this.getPosition().getZ(), world, Entity.class);
                    list.remove(this);
                    list.removeIf(e -> !(e instanceof EntityAnimal));
                    int cast=0;
                    for (Entity entity : list) {
                        if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() > 1) {
                            if (entity.attackEntityFrom(DamageSource.MAGIC, 1)) {
                                mana += 5.0D;
                                cast++;
                            }
                        }
                        if (cast >= 5){
                            break;
                        }
                    }
                    break;
            }
        }


    }

    @Nonnull
    @Override
    public EnumActionResult applyPlayerInteraction(@Nonnull EntityPlayer player, @Nonnull Vec3d vec, @Nonnull EnumHand hand) {
        if(getCaster() == player && player.isSneaking() && player.getHeldItem(hand).isEmpty()){
            ISoul soul = Mana.getSoul(player);
            if(soul != null && soul.getMP() >= 5.0D) {
                soul.addMana(player, -5.0D);
                this.mana+=5.0D;
                return EnumActionResult.SUCCESS;
            }else {
                Aterna.translate(player, true, "mana.not_enough");
            }
        }
        return super.applyPlayerInteraction(player, vec, hand);
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("type", type);
        tag.setDouble("mana", mana);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.type = tag.getInteger("type");
        this.mana = tag.getDouble("mana");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        data.writeInt(type);
        data.writeDouble(mana);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        type = data.readInt();
        mana = data.readDouble();
    }
}
