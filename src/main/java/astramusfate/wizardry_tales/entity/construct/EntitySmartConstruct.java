package astramusfate.wizardry_tales.entity.construct;

import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/** Contains spell modifiers, and lifetime utils **/
public abstract class EntitySmartConstruct extends EntityMagic {
    protected SpellModifiers modifiers;

    public EntitySmartConstruct(World world){
        super(world);
        this.modifiers = new SpellModifiers();
    }

    public EntitySmartConstruct(World world, SpellModifiers mods) {
        super(world);
        this.modifiers =mods;
    }

    public SpellModifiers getModifiers(){return this.modifiers;}
    public void setModifiers(SpellModifiers modifiers){this.modifiers =modifiers;}

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.setModifiers(SpellModifiers.fromNBT(tag.getCompoundTag("spellModifiers")));
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        NBTExtras.storeTagSafely(tag, "spellModifiers", modifiers.toNBT());
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        this.getModifiers().read(data);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        this.getModifiers().write(data);
    }
}
