package astramusfate.wizardry_tales.blocks;

import astramusfate.wizardry_tales.api.classes.ITemporaryBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockConjuredAir extends Block implements ITileEntityProvider, ITemporaryBlock {

	public BlockConjuredAir() {
		super(Material.STRUCTURE_VOID);
		setTemporaryBlockProperties(this);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState p_149645_1_) {
		return EnumBlockRenderType.INVISIBLE;
	}

	@Nullable
	public AxisAlignedBB getCollisionBoundingBox(IBlockState p_180646_1_, IBlockAccess p_180646_2_, BlockPos p_180646_3_) {
		return NULL_AABB;
	}

	@Override
	public boolean isOpaqueCube(IBlockState p_149662_1_) {
		return false;
	}

	@Override
	public boolean canCollideCheck(IBlockState p_176209_1_, boolean p_176209_2_) {
		return false;
	}

	@Override
	public void dropBlockAsItemWithChance(World p_180653_1_, BlockPos p_180653_2_, IBlockState p_180653_3_, float p_180653_4_, int p_180653_5_) {
	}

	@Override
	public boolean isReplaceable(IBlockAccess p_176200_1_, BlockPos p_176200_2_) {
		return true;
	}

	@Override
	public boolean isFullCube(IBlockState p_149686_1_) {
		return false;
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess p_193383_1_, IBlockState p_193383_2_, BlockPos p_193383_3_, EnumFacing p_193383_4_) {
		return BlockFaceShape.UNDEFINED;
	}

	//////////////// ITemporaryBlock Interface implementation ////////////////

	@Override
	public boolean isToolEffective(String type, IBlockState state) { return false; }

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) { return getItemDroppedDelegate(state, rand, fortune); }

	@Override
	protected boolean canSilkHarvest() {
		return false;
	}

	@Override
	public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
		harvestBlockDelegate(worldIn, player, pos, state, te, stack);
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(World world, int meta) { return createNewTileEntityDelegate(world, meta); }
}