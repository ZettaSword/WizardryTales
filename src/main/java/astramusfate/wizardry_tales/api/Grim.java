package astramusfate.wizardry_tales.api;

import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/** Necromancy and Darkness alive module, that gives us to spawn entities easier! And to use for InWorld search**/
public class Grim {

    /** Get Player from Minecraft, it's EntityPlayerSP **/
    public static EntityPlayerSP getPlayer(){
        return Minecraft.getMinecraft().player;
    }

    /** Checking is Object non Null **/
    public static boolean notNull(Object object){ return object != null; }

    public static boolean isBoss(Entity entity){
        return entity != null && !entity.isNonBoss() || (entity != null && entity.getEntityData().getBoolean("boss"));
    }

    public static void highlightBlock(World world, List<BlockPos> positions, int color){
        for(BlockPos pos : positions) {
            for (EnumFacing side : EnumFacing.VALUES) {
                ParticleBuilder.create(ParticleBuilder.Type.BLOCK_HIGHLIGHT).pos(
                        GeometryUtils.getFaceCentre(pos, side)
                                .add(new Vec3d(side.getDirectionVec())
                                        .scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET)))
                        .face(side).clr(color).fade(0, 0, 0).spawn(world);


            }
        }
    }

    public static void highlightBlock(World world, BlockPos pos, int color){
        for(EnumFacing side : EnumFacing.VALUES){
            ParticleBuilder.create(ParticleBuilder.Type.BLOCK_HIGHLIGHT).pos(
                    GeometryUtils.getFaceCentre(pos, side)
                            .add(new Vec3d(side.getDirectionVec())
                                    .scale(GeometryUtils.ANTI_Z_FIGHTING_OFFSET)))
                    .face(side).clr(color).fade(color).spawn(world);
        }
    }

    public static List<BlockPos> findBlocks(BlockPos centre, int searchRadius, Predicate<BlockPos> predicate){

        List<BlockPos> blocks = new ArrayList<>();

        for(int x = -searchRadius; x <= searchRadius; x++){
            for(int y = -searchRadius; y <= searchRadius; y++){
                for(int z = -searchRadius; z <= searchRadius; z++){

                    BlockPos pos = centre.add(x, y, z);

                    if(predicate.test(pos)){
                        blocks.add(pos);
                    }
                }
            }
        }

        return blocks;
    }

    /** This way we make this be a bit shorter **/
    public static void spawn(Entity entity, World world, BlockPos pos){
        entity.setPosition(pos.getX(), pos.getY(), pos.getZ());
        if(!world.isRemote)
            world.spawnEntity(entity);
    }

    /** This way we make this be a bit shorter **/
    public static void spawn(Entity entity, World world, double x, double y, double z){
        entity.setPosition(x, y, z);
        if(!world.isRemote)
            world.spawnEntity(entity);
    }

    /** This way we make this be a bit shorter **/
    public static void spawn(Entity entity, World world, float x, float y, float z){
        entity.setPosition(x, y, z);
        if(!world.isRemote)
            world.spawnEntity(entity);
    }

    public static BlockPos randomiseSpawn(Entity entity, BlockPos position){
        BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
        Vec3d vec = new Vec3d(pos);
        Vec3d to = EntityUtils.findSpaceForTeleport(entity, vec, true);
        /*for(int k = 0; k!=1; k+=imp){
            pos = new BlockPos(position.getX() + rand.nextDouble() * 2 - 1, position.getY(), position.getZ() + rand.nextDouble() * 2 - 1);
            if(world.getBlockState(pos).getBlock() == Blocks.AIR){ imp=1; }
        }*/
        return new BlockPos(to);
    }
}
