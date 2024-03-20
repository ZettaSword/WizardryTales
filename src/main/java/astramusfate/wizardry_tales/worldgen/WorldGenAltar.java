package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableList;
import electroblob.wizardry.block.BlockRunestone;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import electroblob.wizardry.worldgen.WorldGenSurfaceStructure;
import net.minecraft.block.BlockPlanks;
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

public class WorldGenAltar  extends WorldGenSurfaceStructure {

    private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.SWAMP);

    public WorldGenAltar() {}

    @Override
    public String getStructureName() {
        return "altar";
    }

    @Override
    public ResourceLocation getStructureFile(Random random) {
        return new ResourceLocation(WizardryTales.MODID, "altar");
    }

    @Override
    public long getRandomSeedModifier() {
        return 17512384L;
    }


    @Override
    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
        return ArrayUtils.contains(Tales.struct.altar_dims, world.provider.getDimension())
                // +8 for the anti-cascading offset, and +8 for the middle of the generated area makes +16 in total
                && BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
                .stream().anyMatch(BIOME_TYPES::contains)
                && Tales.struct.altar > 0 && random.nextInt(Tales.struct.altar) == 0;
    }

    @Override
    public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
        final Element element = Element.values()[1 + random.nextInt(Element.values().length-1)];
        final Biome biome = world.getBiome(origin);
        final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

        ITemplateProcessor processor = new MultiTemplateProcessor(true,
                (w, p, i) -> {
                    if (i.blockState.getBlock() instanceof BlockRunestone)
                        new Template.BlockInfo(
                                i.pos, i.blockState.withProperty(BlockRunestone.ELEMENT, element), i.tileentityData);
                    return i;
                },
                new WoodTypeTemplateProcessor(woodType));

        template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);
    }
}
