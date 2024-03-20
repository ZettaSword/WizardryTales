package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableList;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import electroblob.wizardry.worldgen.WorldGenUndergroundStructure;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockStoneBrick;
import net.minecraft.block.BlockStoneSlab;
import net.minecraft.block.BlockStoneSlab.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Random;

public class WorldGenUndergroundHouse extends WorldGenUndergroundStructure {

	private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.SANDY, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.SWAMP);


	public WorldGenUndergroundHouse(){ }

	@Override
	public String getStructureName(){
		return "underground_house";
	}

	@Override
	public long getRandomSeedModifier(){
		return 12016745L;
	}

	@Override
	public ResourceLocation getStructureFile(Random random){
		return new ResourceLocation(WizardryTales.MODID, "underground_house");
	}

	@Override
	public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
		return ArrayUtils.contains(Tales.struct.underground_house_dims, world.provider.getDimension())
				&& BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
				.stream().anyMatch(BIOME_TYPES::contains)
				&&  Tales.struct.underground_house > 0 && random.nextInt(Tales.struct.underground_house) == 0;
	}

	@Override
	public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile){

		final Biome biome = world.getBiome(origin);
		final float stoneBrickChance = random.nextFloat();
		final float mossiness = 0.6f; // Underground is a mossier place!
		final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

		ITemplateProcessor processor = new MultiTemplateProcessor(true,
				// Erase walls and ceilings where caves were
				(w, p, i) -> w.isAirBlock(p) ? null : i,
				// Cobblestone/stone brick
				(w, p, i) -> {
					if(w.rand.nextFloat() > stoneBrickChance){
						// Behold, three different ways of doing the same thing, because this is pre-flattening!
						// Also, stone bricks are about the least consistently-named thing in the entire game, so yay
						if(i.blockState.getBlock() == Blocks.COBBLESTONE){
							return new Template.BlockInfo(i.pos, Blocks.STONEBRICK.getDefaultState(), i.tileentityData);
						}else if(i.blockState.getBlock() == Blocks.STONE_SLAB
								&& i.blockState.getValue(BlockStoneSlab.VARIANT) == EnumType.COBBLESTONE){
							return new Template.BlockInfo(i.pos, i.blockState.withProperty(BlockStoneSlab.VARIANT, EnumType.SMOOTHBRICK), i.tileentityData);
						}else if(i.blockState.getBlock() == Blocks.STONE_STAIRS){ // "Stone" stairs are actually cobblestone
							return new Template.BlockInfo(i.pos, BlockUtils.copyState(Blocks.STONE_BRICK_STAIRS, i.blockState), i.tileentityData);
						}
					}
					return i;
				},
				// Wood type
				new WoodTypeTemplateProcessor(woodType),
				// Mossifier
				new MossifierTemplateProcessor(mossiness, 0.04f, origin.getY() + 1),
				// Stone brick smasher-upper
				(w, p, i) -> i.blockState.getBlock() == Blocks.STONEBRICK && w.rand.nextFloat() < 0.1f ?
						new Template.BlockInfo(i.pos, Blocks.STONEBRICK.getDefaultState().withProperty(
								BlockStoneBrick.VARIANT, BlockStoneBrick.EnumType.CRACKED), i.tileentityData) : i,
				// Bookshelf marker
				(w, p, i) -> {
					TileEntityBookshelf.markAsNatural(i.tileentityData);
					return i;
				}
		);

		template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);
	}

}
