package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class ParticleCreator {
    public static class Type {
        /** 3D-rendered expanding ring.<p></p><b>Defaults:</b><<br>Lifetime: 6 ticks<br>Colour: white */
        public static final ResourceLocation RING = new ResourceLocation(WizardryTales.MODID,"ring");

    }
    
    public static void createSmoke(World world, Entity entity){
        Random rand = new Random();
        if (world.isRemote){
            for(int i = 0; i < 20; i++){
                ParticleBuilder.create(ParticleBuilder.Type.CLOUD)
                        .pos(entity.posX + (rand.nextDouble() - 0.5) * entity.width, entity.posY
                                + rand.nextDouble() * entity.height, entity.posZ + (rand.nextDouble() - 0.5) * entity.width)
                        .time(40)
                        .clr(ArcaneColor.DARKNESS.getRGB())
                        .shaded(true)
                        .spawn(world);
            }
        }
    }

    public static void createSmoke(World world, Entity entity, Color color){
        Random rand = new Random();
        if (world.isRemote){
            for(int i = 0; i < 20; i++){
                ParticleBuilder.create(ParticleBuilder.Type.CLOUD)
                        .pos(entity.posX + (rand.nextDouble() - 0.5) * entity.width, entity.posY
                                + rand.nextDouble() * entity.height, entity.posZ + (rand.nextDouble() - 0.5) * entity.width)
                        .time(40)
                        .clr(color.getRGB())
                        .shaded(true)
                        .spawn(world);
            }
        }
    }
}
