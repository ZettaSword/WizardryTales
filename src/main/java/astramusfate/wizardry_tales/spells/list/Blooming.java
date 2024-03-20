package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.blocks.tile.TileEntityBlooming;
import astramusfate.wizardry_tales.registry.TalesBlocks;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.RayTracer;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class Blooming extends Spell {

	public Blooming(){
		super(WizardryTales.MODID,"blooming", SpellActions.POINT, false);
		addProperties(RANGE, DURATION, EFFECT_DURATION, EFFECT_STRENGTH);
	}

	@Override
	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers){

		double range = getProperty(RANGE).floatValue() * modifiers.get(WizardryItems.range_upgrade);

		RayTraceResult rayTrace = RayTracer.standardBlockRayTrace(world, caster, range, false);

		if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK){

			BlockPos pos = rayTrace.getBlockPos();

			if(rayTrace.sideHit == EnumFacing.UP && world.isSideSolid(pos, EnumFacing.UP) && BlockUtils.canBlockBeReplaced(world, pos.up())){
				if(!world.isRemote){
					world.setBlockState(pos.up(), TalesBlocks.blooming_flower.getDefaultState());
					TileEntity tileEntity = world.getTileEntity(pos.up());
					if(tileEntity instanceof TileEntityBlooming){
						TileEntityBlooming tile = (TileEntityBlooming)tileEntity;
						//int lifetime = (int)(getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade));

						//tile.setLifetime(lifetime);
						tile.setCaster(caster);
						tile.setDurationMod(modifiers.get(WizardryItems.duration_upgrade));
						tile.setPotencyMod(modifiers.get(SpellModifiers.POTENCY));
						world.markAndNotifyBlock(pos.up(),
								null, world.getBlockState(pos.up()), world.getBlockState(pos.up()), 3);
					}
				}

				this.playSound(world, caster, ticksInUse, -1, modifiers);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
	}
}
