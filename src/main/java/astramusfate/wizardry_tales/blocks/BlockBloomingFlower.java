package astramusfate.wizardry_tales.blocks;

import astramusfate.wizardry_tales.api.classes.IActionableBlock;
import astramusfate.wizardry_tales.blocks.tile.TileEntityBlooming;
import astramusfate.wizardry_tales.entity.construct.EntityPoisonousGas;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockBush;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Extending BlockBush allows me to remove nearly everything from this class.
public class BlockBloomingFlower extends BlockBush implements ITileEntityProvider, IActionableBlock {

	private static final AxisAlignedBB AABB = new AxisAlignedBB(0.5F - 0.2f, 0.0F, 0.5F - 0.2f, 0.5F + 0.2f,
			0.1f * 3.0F, 0.5F + 0.2f);

	public BlockBloomingFlower(Material par2Material){
		super(par2Material);
		this.setLightLevel(0.4f);
		this.setSoundType(SoundType.PLANT);
		this.setTickRandomly(true);
		//this.setCreativeTab(TalesTabs.Items);
	}

	@Override
	public boolean isCollidable(){
		// This method has nothing to do with entity movement, it's just for raytracing
		return true;
	}

	@Override
	public void onEntityCollidedWithBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Entity entity) {
		super.onEntityCollidedWithBlock(world, pos, state, entity);
		if(EntityUtils.isLiving(entity)) {
			action(world, pos, (EntityLivingBase) entity);
		}
	}

	@Nonnull
	@Override
	public AxisAlignedBB getBoundingBox(@Nonnull IBlockState state, @Nonnull IBlockAccess source, @Nonnull BlockPos pos){
		return AABB;
	}

	@Nonnull
	@Override
	public EnumPlantType getPlantType(@Nonnull IBlockAccess world, @Nonnull BlockPos pos){
		return EnumPlantType.Plains;
	}


	@Override
	public void action(World world, BlockPos pos, EntityLivingBase target) {
		TileEntityBlooming tile = world.getTileEntity(pos) instanceof TileEntityBlooming ?
				(TileEntityBlooming) world.getTileEntity(pos) : null;
		if(tile != null){
			if(!world.isRemote) {
				EntityPlayer caster = tile.getCaster();
				if (caster != null && caster != target && AllyDesignationSystem.isValidTarget(caster, target)) {
					if(ItemArtefact.isArtefactActive(caster, TalesItems.amulet_petal_blooming)) {
						SpellModifiers spellModifiers = new SpellModifiers();
						spellModifiers.set(WizardryItems.duration_upgrade, (float) tile.getDurationMod(), false);
						spellModifiers.set(SpellModifiers.POTENCY, (float) tile.getPotencyMod(), false);
						EntityPoisonousGas gas = new EntityPoisonousGas(world, spellModifiers);
						gas.setPosition(pos.getX(), pos.getY(), pos.getZ());
						gas.setCaster(caster);
						gas.setLifetime(
								(int) Math.floor((TalesSpells.blooming.getProperty(Spell.DURATION).intValue() * tile.getDurationMod())));
						world.spawnEntity(gas);
					} else {
						EntityUtils.attackEntityWithoutKnockback(target,
								MagicDamage.causeDirectMagicDamage(tile.getCaster(), MagicDamage.DamageType.POISON),
								1);

						target.addPotionEffect(new PotionEffect(MobEffects.POISON,
								(int) (TalesSpells.blooming.getProperty(Spell.EFFECT_DURATION).floatValue()),
								TalesSpells.blooming.getProperty(Spell.EFFECT_STRENGTH).intValue()));
					}
					world.destroyBlock(pos, false);
				}
			}

			tile.spawnParticleEffect();
		}
	}

	@Override
	public boolean hasTileEntity(@Nonnull IBlockState state){
		return true;
	}

	@Nullable
	@Override
	public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
		return new TileEntityBlooming(worldIn);
	}
}
