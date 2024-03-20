package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.worldgen.*;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class TalesWorldGen {
    public static void init(){
        //GameRegistry.registerWorldGenerator(new WorldGenWizardTower(), 20);
        //GameRegistry.registerWorldGenerator(new WorldGenLibraryRuins(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenAltar(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenAnchor(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenPeacefulAnchor(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenFlyingLibrary(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenFlyingLibraryBig(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenLibrary(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenUndergroundHouse(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenShrineHealing(), 20);
        GameRegistry.registerWorldGenerator(new WorldGenSpellWitchHut(), 20);
        //GameRegistry.registerWorldGenerator(new WorldGenShrine(), 20);
    }
}
