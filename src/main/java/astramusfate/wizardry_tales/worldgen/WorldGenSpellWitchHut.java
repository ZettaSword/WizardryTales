package astramusfate.wizardry_tales.worldgen;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.living.EntitySpellWitch;
import com.google.common.collect.ImmutableList;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import electroblob.wizardry.worldgen.WorldGenSurfaceStructure;
import net.minecraft.block.BlockPlanks;
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

import java.util.List;
import java.util.Map;
import java.util.Random;

public class WorldGenSpellWitchHut extends WorldGenSurfaceStructure {

    private static final List<BiomeDictionary.Type> BIOME_TYPES = ImmutableList.of(BiomeDictionary.Type.SWAMP);

    public WorldGenSpellWitchHut() {}

    @Override
    public String getStructureName() {
        return "spell_witch_hut";
    }

    @Override
    public ResourceLocation getStructureFile(Random random) {
        return new ResourceLocation(WizardryTales.MODID, "spell_witch_hut");
    }

    @Override
    public long getRandomSeedModifier() {
        return 15413699L;
    }


    @Override
    public boolean canGenerate(Random random, World world, int chunkX, int chunkZ){
        return ArrayUtils.contains(Tales.struct.spell_witch_hut_dims, world.provider.getDimension())
                // +8 for the anti-cascading offset, and +8 for the middle of the generated area makes +16 in total
                && BiomeDictionary.getTypes(world.getBiome(new BlockPos(chunkX * 16 + 16, 0, chunkZ * 16 + 16)))
                .stream().anyMatch(BIOME_TYPES::contains)
                && Tales.struct.spell_witch_hut > 0 && random.nextInt(Tales.struct.spell_witch_hut) == 0;
    }

    @Override
    public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {
        final Biome biome = world.getBiome(origin);
        final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

        ITemplateProcessor processor = new MultiTemplateProcessor(true,
                new WoodTypeTemplateProcessor(woodType),
                // Bookshelf marker
                (w, p, i) -> {
                    TileEntityBookshelf.markAsNatural(i.tileentityData);
                    return i;
                });

        template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);

        // Wizard spawning
        Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);

        for(Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()){

            Vec3d vec = GeometryUtils.getCentre(entry.getKey());

            if(entry.getValue().equals("spell_witch")){

                EntitySpellWitch wizard = new EntitySpellWitch(world);
                wizard.setLocationAndAngles(vec.x, vec.y, vec.z, 0, 0);
                wizard.onInitialSpawn(world.getDifficultyForLocation(origin), null);
                world.spawnEntity(wizard);

            }else{
                // This probably shouldn't happen...
                Wizardry.logger.info("Unrecognised data block value {} in structure {}", entry.getValue(), structureFile);
            }
        }

    }
}
