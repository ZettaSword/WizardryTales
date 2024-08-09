package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesBlocks;
import astramusfate.wizardry_tales.api.classes.ITemporaryBlock;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.List;

public class RepelEffect extends PotionMagicEffect {
    public RepelEffect() {
        super(true,
                Color.RED.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/repel.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":repel");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return Solver.doEvery(duration, 1);
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        List<BlockPos> blocks = BlockUtils.getBlockSphere(entity.getPosition(), 0.5f + 0.73f * Math.min(1 + amplifier, 3));
        World world = entity.getEntityWorld();
        if (EntityUtils.canDamageBlocks(entity, world) && !world.isRemote) {
            for (BlockPos pos : blocks) {
                if (!BlockUtils.isBlockUnbreakable(world, pos) && BlockUtils.canBlockBeReplaced(world, pos) &&
                        (world.getBlockState(pos).getMaterial().isLiquid() ||
                                world.getBlockState(pos).getBlock() == Blocks.WATER ||
                                world.getBlockState(pos).getBlock() == Blocks.FLOWING_WATER)) {
                    ITemporaryBlock.placeTemporaryBlock(entity, world, TalesBlocks.conjured_air, pos, 100, false);
                }
            }
        }
    }


}
