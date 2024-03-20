package astramusfate.wizardry_tales.data.cap;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;

public class SoulProvider implements ICapabilitySerializable<NBTBase>
{

 @Nonnull
 @SuppressWarnings("ConstantConditions")
 private static <T> T placeholder() { return null; }

 @CapabilityInject(ISoul.class)
 public static final Capability<ISoul> SOUL_CAP = placeholder();

 private final ISoul instance = SOUL_CAP.getDefaultInstance();

 @Override
 public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing)
 {
 return capability == SOUL_CAP;
 }

 @Override
 public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing)
 {
 return capability == SOUL_CAP ? SOUL_CAP.<T> cast(this.instance) : null;
 }

 @Override
 public NBTBase serializeNBT()
 {
 return SOUL_CAP.getStorage().writeNBT(SOUL_CAP, this.instance, null);
 }

 @Override
 public void deserializeNBT(NBTBase nbt)
 {
 SOUL_CAP.getStorage().readNBT(SOUL_CAP, this.instance, null, nbt);
 }
}