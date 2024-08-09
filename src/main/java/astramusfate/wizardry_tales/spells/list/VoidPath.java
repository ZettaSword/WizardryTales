package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesBlocks;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.api.classes.ITemporaryBlock;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
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

public class VoidPath extends SpellRay {
    public VoidPath() {
        super(WizardryTales.MODID, "void_path", SpellActions.POINT, false);
        this.ignoreLivingEntities(true);
        this.ignoreUncollidables(true);
        this.addProperties(DURATION);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
            IBlockState state = world.getBlockState(pos);
            pos = pos.offset(side);
            if (state.getMaterial().isSolid() && caster != null && EntityUtils.canDamageBlocks(caster, world)) {
                if (!world.isRemote)ITemporaryBlock.placeTemporaryBlock(caster, world, TalesBlocks.conjured_air, pos, this.getProperty(DURATION).intValue(), false);
                Wizard.castParticles(world, Element.SORCERY, new Vec3d(pos.up()), 2);
                for (int i = 0; i < 5; i++) {
                    // Facing
                    BlockPos place = pos.offset(caster.getHorizontalFacing(), i);
                    if (!world.isRemote)ITemporaryBlock.placeTemporaryBlock(caster, world, TalesBlocks.conjured_air,
                            place, this.getProperty(DURATION).intValue(), false);
                    Wizard.castParticles(world, Element.SORCERY, new Vec3d(place.up()), 2);
                    // Up
                    place = place.offset(EnumFacing.UP);
                    if (!world.isRemote)ITemporaryBlock.placeTemporaryBlock(caster, world, TalesBlocks.conjured_air,
                            place, this.getProperty(DURATION).intValue(), false);
                    Wizard.castParticles(world, Element.SORCERY, new Vec3d(place.up()), 2);
                }
                return true;
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
