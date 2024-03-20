package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableList;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockPedestal;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.spell.ArcaneLock;
import electroblob.wizardry.tileentity.TileEntityShrineCore;
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
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class WorldGenAnchor extends WorldGenSurfaceStructure {

    private static final String CORE_DATA_BLOCK_TAG = "core";

    private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.FOREST, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.SWAMP,
            BiomeDictionary.Type.WASTELAND, BiomeDictionary.Type.HOT);

    public WorldGenAnchor() {}

    @Override
    public String getStructureName() {
        return "anchor";
    }

    @Override
    public ResourceLocation getStructureFile(Random random) {
        return new ResourceLocation(WizardryTales.MODID, "anchor");
    }

    @Override
    public long getRandomSeedModifier() {
        return 15512579L;
    }


    @Override
    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
        return ArrayUtils.contains(Tales.struct.anchor_angry_dims, world.provider.getDimension())
                // +8 for the anti-cascading offset, and +8 for the middle of the generated area makes +16 in total
                && BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
                .stream().anyMatch(BIOME_TYPES::contains)
                && Tales.struct.anchor_angry > 0 && random.nextInt(Tales.struct.anchor_angry) == 0;
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

        // Shrine core
        Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);

        for(Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()){

            if(entry.getValue().equals(CORE_DATA_BLOCK_TAG)){
                // This bit could have been done with a template processor, but we also need to link the chest and lock it
                world.setBlockState(entry.getKey(), WizardryBlocks.runestone_pedestal.getDefaultState()
                        .withProperty(BlockPedestal.ELEMENT, Element.SORCERY).withProperty(BlockPedestal.NATURAL, true));

                TileEntity core = world.getTileEntity(entry.getKey());
                TileEntity container = world.getTileEntity(entry.getKey().up());

                if(container != null){

                    container.getTileData().setUniqueId(ArcaneLock.NBT_KEY, new UUID(0, 0)); // Nil UUID

                    if(core instanceof TileEntityShrineCore){
                        ((TileEntityShrineCore)core).linkContainer(container);
                    }else{
                        Wizardry.logger.info("What?!");
                    }

                }else{
                    Wizardry.logger.info("Expected chest or other container at {} in structure {}, found no tile entity", entry.getKey(), structureFile);
                }

            }else{
                // This probably shouldn't happen...
                Wizardry.logger.info("Unrecognised data block value {} in structure {}", entry.getValue(), structureFile);
            }
        }
    }
}
