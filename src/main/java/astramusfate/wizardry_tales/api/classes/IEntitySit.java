package astramusfate.wizardry_tales.api.classes;

import astramusfate.wizardry_tales.entity.ai.EntityAISummonSit;
import net.minecraft.nbt.NBTTagCompound;

/** This interface does helps with EntitySitting AI, but you need by yourself do client writeData and interaction to sit **/
public interface IEntitySit {
    EntityAISummonSit getAiSit();

    boolean isSitting();
    void setSitting(boolean set);


    /**
     * Implementors should call this from writeEntityToNBT. Can be overridden as long as super is called, but there's
     * very little point in doing that since anything extra could just be added to writeEntityToNBT anyway.
     */
    default void writeSittingNBT(NBTTagCompound tagcompound){
        tagcompound.setBoolean("Sitting", isSitting());
    }

    /**
     * Implementors should call this from readEntityFromNBT. Can be overridden as long as super is called, but there's
     * very little point in doing that since anything extra could just be added to readEntityFromNBT anyway.
     */
    default void readSittingNBT(NBTTagCompound tagcompound){
        this.setSitting(tagcompound.getBoolean("Sitting"));
    }
}
