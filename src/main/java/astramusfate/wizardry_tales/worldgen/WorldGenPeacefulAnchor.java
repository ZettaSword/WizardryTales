package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableList;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.tileentity.TileEntityTimer;
import electroblob.wizardry.worldgen.WorldGenSurfaceStructure;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Random;

public class WorldGenPeacefulAnchor extends WorldGenSurfaceStructure {

    private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.SWAMP,
            BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.HOT);

    public WorldGenPeacefulAnchor() {}

    @Override
    public String getStructureName() {
        return "anchor_peaceful";
    }

    @Override
    public ResourceLocation getStructureFile(Random random) {
        return new ResourceLocation(WizardryTales.MODID, "anchor_peaceful");
    }

    @Override
    public long getRandomSeedModifier() {
        return 16792834L;
    }


    @Override
    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
        return ArrayUtils.contains(Tales.struct.anchor_dims, world.provider.getDimension())
                // +8 for the anti-cascading offset, and +8 for the middle of the generated area makes +16 in total
                && BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
                .stream().anyMatch(BIOME_TYPES::contains)
                && Tales.struct.anchor > 0 && random.nextInt(Tales.struct.anchor) == 0;
    }

    @Override
    public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
        ITemplateProcessor processor = (w, p, i) -> {
            TileEntity tile = w.getTileEntity(p);
            if (w.getBlockState(p).getBlock() == WizardryBlocks.magic_light && tile instanceof TileEntityTimer) {
                ((TileEntityTimer) tile).setLifetime(-1);
            }
            return i;
        };

        template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);
    }
}
