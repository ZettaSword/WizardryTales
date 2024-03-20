package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockRunestone;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.integration.antiqueatlas.WizardryAntiqueAtlasIntegration;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import electroblob.wizardry.worldgen.WorldGenSurfaceStructure;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

public class WorldGenLibrary extends WorldGenSurfaceStructure {

    private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.SWAMP,
            BiomeDictionary.Type.MESA,BiomeDictionary.Type.MOUNTAIN,BiomeDictionary.Type.NETHER,BiomeDictionary.Type.SANDY);
    private static final String WIZARD_DATA_BLOCK_TAG = "wizard";
    private static final String YOUNG_WIZARD_DATA_BLOCK_TAG = "young";

    private final Map<BiomeDictionary.Type, IBlockState> specialWallBlocks;

    public WorldGenLibrary() {
        // These are initialised here because it's a convenient point after the blocks are registered
        specialWallBlocks = ImmutableMap.of(
                BiomeDictionary.Type.MESA, Blocks.RED_SANDSTONE.getDefaultState(),
                BiomeDictionary.Type.MOUNTAIN, Blocks.STONEBRICK.getDefaultState(),
                BiomeDictionary.Type.NETHER, Blocks.NETHER_BRICK.getDefaultState(),
                BiomeDictionary.Type.SANDY, Blocks.SANDSTONE.getDefaultState()
        );
    }

    @Override
    public String getStructureName() {
        return "library";
    }

    @Override
    public ResourceLocation getStructureFile(Random random) {
        return new ResourceLocation(WizardryTales.MODID, "library");
    }

    @Override
    public long getRandomSeedModifier() {
        return 15867967L;
    }


    @Override
    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
        return ArrayUtils.contains(Tales.struct.lib_dims, world.provider.getDimension())
                // +8 for the anti-cascading offset, and +8 for the middle of the generated area makes +16 in total
                && BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
                .stream().anyMatch(BIOME_TYPES::contains)
                && Tales.struct.lib > 0 && random.nextInt(Tales.struct.lib) == 0;
    }

    @Override
    public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
        final Biome biome = world.getBiome(origin);
        final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);


        final Element element = Element.values()[1 + random.nextInt(Element.values().length-1)];

        final IBlockState wallMaterial = specialWallBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
                .findFirst().map(specialWallBlocks::get).orElse(Blocks.COBBLESTONE.getDefaultState());

        final Set<BlockPos> blocksPlaced = new HashSet<>();


        ITemplateProcessor processor = new MultiTemplateProcessor(true,
                new WoodTypeTemplateProcessor(woodType),
                // Bookshelf marker
                (w, p, i) -> {
                    TileEntityBookshelf.markAsNatural(i.tileentityData);
                    return i;
                },
                // Element change
                (w, p, i) -> i.blockState.getBlock() instanceof BlockRunestone ? new Template.BlockInfo(
                        i.pos, i.blockState.withProperty(BlockRunestone.ELEMENT, element), i.tileentityData) : i,
                // Wall material
                (w, p, i) -> i.blockState.getBlock() == Blocks.COBBLESTONE ? new Template.BlockInfo(i.pos,
                        wallMaterial, i.tileentityData) : i,
                // Block recording (the process() method doesn't get called for structure voids)
                (w, p, i) -> {if(i.blockState.getBlock() != Blocks.AIR) blocksPlaced.add(p); return i;}
                );

        template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);

        WizardryAntiqueAtlasIntegration.markLibrary(world, origin.getX(), origin.getZ(), false);

        // Wizards spawning
        Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);

        for(Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()){

            Vec3d vec = GeometryUtils.getCentre(entry.getKey());

            if(entry.getValue().equals(WIZARD_DATA_BLOCK_TAG)){

                EntityWizard wizard = new EntityWizard(world);
                wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
                wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
                wizard.setTowerBlocks(blocksPlaced);
                world.spawnEntity(wizard);

            }else if(entry.getValue().equals(YOUNG_WIZARD_DATA_BLOCK_TAG)){
                if(Solver.chance(30)) {
                    //TODO: ADD YOUNG WIZARDS BACK
                    /*EntityYoungWizard wizard = new EntityYoungWizard(world);
                    wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
                    wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
                    wizard.setTowerBlocks(blocksPlaced);
                    world.spawnEntity(wizard);*/
                    EntityWizard wizard = new EntityWizard(world);
                    wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
                    wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
                    wizard.setTowerBlocks(blocksPlaced);
                    world.spawnEntity(wizard);
                }
            }else{
                // This probably shouldn't happen...
                Wizardry.logger.info("Unrecognised data block value {} in structure {}", entry.getValue(), structureFile);
            }
        }
    }
}
