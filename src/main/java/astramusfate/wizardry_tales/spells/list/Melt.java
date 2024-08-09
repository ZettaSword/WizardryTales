package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.entity.living.EntityIceGiant;
import electroblob.wizardry.entity.living.EntityIceWraith;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.*;
import electroblob.wizardry.util.MagicDamage.DamageType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class Melt extends SpellRay {

	public Melt(){
		super(WizardryTales.MODID,"melt", SpellActions.POINT, false);
		this.soundValues(1, 1, 0.4f);
		addProperties(DAMAGE, BURN_DURATION);
		this.hitLiquids(true);
		this.ignoreUncollidables(false);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		if(EntityUtils.isLiving(target)){

			if(target instanceof EntitySnowman || target instanceof EntityIceWraith || target instanceof EntityIceGiant){
				target.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, DamageType.FIRE),
						getProperty(DAMAGE).floatValue() * modifiers.get(SpellModifiers.POTENCY));
			}

			if(MagicDamage.isEntityImmune(DamageType.FIRE, target)){
				if(!world.isRemote && caster instanceof EntityPlayer) ((EntityPlayer)caster).sendStatusMessage(
						new TextComponentTranslation("spell.resist", target.getName(), this.getNameForTranslationFormatted()), true);
			}else{
				target.setFire((int)(getProperty(BURN_DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)));
			}

			if(target.isBurning()) target.extinguish();

			return true;
		}
		
		return false; // If the spell hit a non-living entity
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){

		if(!world.isRemote && BlockUtils.canPlaceBlock(caster, world, pos)){
			melt(world, pos, true);
		}
		
		return true; // Always succeeds if it hits a block
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz){
		world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
	}


	/**
	 * Freezes the given block, either by turning water to ice, lava to obsidian/cobblestone or by placing snow on top
	 * of it if possible.
	 * @param world The world the block is in
	 * @param pos The position of the block to freeze
	 * @param meltToLava True to melt some blocks to lava state, false to leave it unchanged
	 * @return True if any blocks were changed, false if not.
	 */
	public static boolean melt(World world, BlockPos pos, boolean meltToLava){

		IBlockState state = world.getBlockState(pos);
		Block block = state.getBlock();

		if((state.getMaterial() == Material.ICE || block == Blocks.ICE || block == Blocks.FROSTED_ICE)){
			world.setBlockState(pos, Blocks.WATER.getDefaultState());
		}else if(meltToLava && (state.getMaterial() == Material.ROCK || block == Blocks.OBSIDIAN || block == Blocks.COBBLESTONE
				|| block == Blocks.COBBLESTONE_WALL || block == Blocks.MOSSY_COBBLESTONE
				|| block == Blocks.STONE)){
			world.setBlockState(pos, Blocks.LAVA.getDefaultState());
		}else if(BlockUtils.canBlockBeReplaced(world, pos.up()) && world.getBlockState(pos.up()).getBlock() == Blocks.SNOW_LAYER){
			world.setBlockState(pos.up(), Blocks.AIR.getDefaultState());
		}else if(block == Blocks.GRASS || block == Blocks.DIRT){
			world.setBlockState(pos, Blocks.SAND.getDefaultState());
		}else if(block == Blocks.SAND){
			world.setBlockState(pos, Blocks.GLASS.getDefaultState());
		}else if(meltToLava && block == Blocks.GLASS){
			world.setBlockState(pos, Blocks.LAVA.getDefaultState());
		}else{
			return false;
		}

		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
	}
}
