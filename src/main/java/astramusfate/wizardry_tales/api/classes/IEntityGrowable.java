package astramusfate.wizardry_tales.api.classes;

import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/** Do not forgot to add Spawn data overrides, and write nbt data for Growth! **/
public interface IEntityGrowable extends IEntityAdditionalSpawnData {

    /** Sets the growth of the summoned creature in ticks. */
    void setGrowth(int growth);

    int getGrowth();

    void addGrowth(int growth);
    boolean isGrown();

}
