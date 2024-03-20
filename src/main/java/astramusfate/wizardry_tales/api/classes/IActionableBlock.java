package astramusfate.wizardry_tales.api.classes;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IActionableBlock {
    void action(World world, BlockPos pos, EntityLivingBase target);
}
