package astramusfate.wizardry_tales.particle;

import astramusfate.wizardry_tales.api.Solver;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.awt.*;
import java.util.Random;

public class TalesFx {

    public static void create(World world, String name, BlockPos pos){
        create(world, name, pos, 20 + Solver.randInt(0, 20));
    }

    public static void create(World world, String name, BlockPos pos, int lifetime){
        spawn(world, getSimpleParticle(world,name, pos, lifetime).setInitalTint(Color.WHITE).setFinalTint(Color.RED));
    }

    public static void create(World world, String name, BlockPos pos, int lifetime, Color color){
        spawn(world, getSimpleParticle(world,name, pos, lifetime).setInitalTint(color).setFinalTint(color));
    }

    public static SimpleParticle create(World world, String name, BlockPos pos, int lifetime, Color initColor, Color finalColor){
       return spawn(world, getSimpleParticle(world,name, pos, lifetime).setInitalTint(initColor).setFinalTint(finalColor));
    }

    public static SimpleParticle getSimpleParticle(World world,String name, BlockPos pos, int lifetime) {
        Random rand = new Random();
        double xPos = pos.getX() + 0.5D;
        double yPos = pos.getY() + 1.0D;
        double zPos = pos.getZ() + 0.5D;
        double xMotion = 0.1D * rand.nextDouble() - 0.05D;
        double yMotion = 0.3D;
        double zMotion = 0.1D * rand.nextDouble() - 0.05D;

        return new SimpleParticle(
                new SimpleParticle.TextureDefinition(name),
                world,
                xPos, yPos, zPos,
                xMotion, yMotion, zMotion)
                .setRotSpeed(((float) Math.random() - 0.5F) * 0.1F)
                .setLifeSpan(lifetime) //20 + rand.nextInt(20)
                .setGravity(0.2F)
                .setScale(2.0F)
                .setInitialAlpha(1.0F)
                .setFinalAlpha(0.0F);
    }


    public static SimpleParticle spawn(World world, SimpleParticle particle){
        if (world.isRemote) Minecraft.getMinecraft().effectRenderer.addEffect(particle);
        return particle;
    }


}
