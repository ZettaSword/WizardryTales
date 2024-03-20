package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class EarthBending extends SpellRay {
    public EarthBending() {
        super(WizardryTales.MODID, "earth_bending", SpellActions.POINT, true);
        this.ignoreLivingEntities(true);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(Solver.doEvery(ticksInUse, 0.23)) {
            boolean artefact = false;
            boolean stone = false;
            Block got = world.getBlockState(pos).getBlock();
            if (caster instanceof EntityPlayer) {
                stone = got == Blocks.STONE || got == Blocks.COBBLESTONE;
                artefact = ItemArtefact.isArtefactActive((EntityPlayer) caster, TalesItems.artefact_earth)
                        && stone;
            }
            if (got == Blocks.DIRT || got == Blocks.GRASS || artefact) {
                BlockPos offset = pos.offset(side);
                Block block = world.getBlockState(offset).getBlock();
                AxisAlignedBB axisalignedbb = world.getBlockState(offset).getCollisionBoundingBox(world, pos);
                if(axisalignedbb != null) {
                    List<Entity> list = world.getEntitiesWithinAABBExcludingEntity((Entity) null, axisalignedbb.expand(0, 1, 0));
                    for (Entity entity : list) {
                        if (entity instanceof EntityLivingBase)
                        {
                            return false;
                        }
                    }
                }

                if (world.mayPlace(block, offset, false, side, caster)) {
                    world.setBlockState(offset, !artefact ? Blocks.DIRT.getDefaultState() : Blocks.STONE.getDefaultState());
                    Wizard.castParticles(world, Element.EARTH, new Vec3d(offset.offset(EnumFacing.UP)), 2);
                    if(world.isRemote)
                        world.playSound(offset.getX(), offset.getY(), offset.getZ(),
                                !stone ? SoundEvents.BLOCK_GRASS_PLACE : SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS,
                                1.0f, 1.0f, true);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
