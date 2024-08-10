package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.events.SpellCreation;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.EntityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EntityCustomSigil extends EntityCircleWords {
    public boolean canWork = true;

    public Predicate<Entity> filter = Objects::nonNull;

    public EntityCustomSigil(World world){
        super(world);
    }

    public EntityCustomSigil(World world, List<String> words){
        super(world, words);
    }

    @Override
    protected boolean shouldScaleHeight(){
        return false;
    }

    @Override
    public void onUpdate(){
        super.onUpdate();
            if (words == null) {
                return;
            }
            List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width/2, this.posX, this.posY,
                    this.posZ, this.world);

            if (targets.isEmpty()) {
                return;
            }

            for(EntityLivingBase target : targets) {
                if (this.isValidTarget(target) && filter.test(target)) {
                    if (this.getCaster() instanceof EntityPlayerMP) {
                        SpellCreation.createSpell(words, this, target, true);
                    }
                    if (this.world.isRemote) {
                        SpellCreation.createSpell(words, this, target, false);
                    }
                    if (!this.world.isRemote) {
                        this.setDead();
                        break;
                    }
                }
            }


        if(this.world.isRemote && this.rand.nextInt(15) == 0){
            double radius = (0.5 + rand.nextDouble() * 0.3) * width/2;
            float angle = rand.nextFloat() * (float)Math.PI * 2;
            Wizard.castParticlesWithoutRange(world, Element.MAGIC,new Vec3d(this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
                    this.posZ + radius * MathHelper.sin(angle)), 1);
        }
    }

    @Override
    public boolean canRenderOnFire(){
        return false;
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setBoolean("canWork", canWork);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.canWork = tag.getBoolean("canWork");
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        data.writeBoolean(canWork);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        this.canWork = data.readBoolean();
    }
}
