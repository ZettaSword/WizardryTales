package astramusfate.wizardry_tales.entity.ai;

import astramusfate.wizardry_tales.api.Tenebria;
import astramusfate.wizardry_tales.api.classes.IEntitySit;
import com.google.common.collect.Streams;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateFlying;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityAIFollowFriend extends EntityAIBase
{
    EntityLiving creature;
    private EntityLivingBase friend;
    World world;
    private final double followSpeed;
    private final PathNavigate petPathfinder;
    private int timeToRecalcPath;
    float maxDist;
    float minDist;
    private float oldWaterCost;

    public EntityAIFollowFriend(EntityLiving creature, double followSpeedIn, float minDistIn, float maxDistIn)
    {
        this.creature = creature;
        this.world = creature.world;
        this.followSpeed = followSpeedIn;
        this.petPathfinder = creature.getNavigator();
        this.minDist = minDistIn;
        this.maxDist = maxDistIn;
        this.setMutexBits(3);

        if (!(creature.getNavigator() instanceof PathNavigateGround) && !(creature.getNavigator() instanceof PathNavigateFlying))
        {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean shouldExecute()
    {
        EntityLivingBase entitylivingbase = Tenebria.getUniqueIdOwner(this.creature);

        if (entitylivingbase == null)
        {
            return false;
        }
        else if (entitylivingbase instanceof EntityPlayer && ((EntityPlayer)entitylivingbase).isSpectator())
        {
            return false;
        }
        else if (this.creature.getDistanceSq(entitylivingbase) < (double)(this.minDist * this.minDist))
        {
            return false;
        }
        else
        {
            this.friend = entitylivingbase;
            return true;
        }
    }

    public boolean shouldContinueExecuting()
    {
        return !this.petPathfinder.noPath() && this.creature.getDistanceSq(this.friend) > (double)(this.maxDist * this.maxDist)
                && !(this.creature instanceof IEntitySit && ((IEntitySit)this.creature).isSitting());
    }

    public void startExecuting()
    {
        this.timeToRecalcPath = 0;
        this.creature.setPathPriority(PathNodeType.WATER, 0.0F);
    }

    public void resetTask()
    {
        this.friend = null;
        this.petPathfinder.clearPath();
        this.creature.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
    }

    public void updateTask()
    {
        this.creature.getLookHelper().setLookPositionWithEntity(this.friend, 10.0F, (float)this.creature.getVerticalFaceSpeed());

        if (--this.timeToRecalcPath <= 0)
        {
            this.timeToRecalcPath = 10;

            if (!this.petPathfinder.tryMoveToEntityLiving(this.friend, this.followSpeed))
            {
                if (!this.creature.getLeashed() && !this.creature.isRiding())
                {
                    if (this.creature.getDistanceSq(this.friend) >= 144.0D)
                    {
                        int i = MathHelper.floor(this.friend.posX) - 2;
                        int j = MathHelper.floor(this.friend.posZ) - 2;
                        int k = MathHelper.floor(this.friend.getEntityBoundingBox().minY);

                        for (int l = 0; l <= 4; ++l)
                        {
                            for (int i1 = 0; i1 <= 4; ++i1)
                            {
                                if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1))
                                {
                                    this.creature.setLocationAndAngles((float)(i + l) + 0.5F, k, (float)(j + i1) + 0.5F, this.creature.rotationYaw, this.creature.rotationPitch);
                                    this.petPathfinder.clearPath();
                                    return;
                                }
                            }
                        }
                        /*
                        int x = MathHelper.floor(this.owner.posX) - 2;
                        int y = MathHelper.floor(this.owner.getEntityBoundingBox().minY);
                        int z = MathHelper.floor(this.owner.posZ) - 2;


                        Vec3d destination = EntityUtils.findSpaceForTeleport(this.creature, new Vec3d(x,y,z), false);
                        if(destination != null && isTeleportFriendlyBlock(destination.x, destination.y, destination.z)){
                            this.creature.setLocationAndAngles(destination.x, destination.y, destination.z, this.creature.rotationYaw, this.creature.rotationPitch);
                            this.petPathfinder.clearPath();
                        }

                         */
                    }
                }
            }
        }
    }

    protected boolean isTeleportFriendlyBlock(double x, double y, double z)
    {
        BlockPos blockpos = new BlockPos(x, y - 1, z);
        IBlockState iblockstate = this.world.getBlockState(blockpos);

        return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.creature) && this.world.isAirBlock(blockpos.up())
                && this.world.isAirBlock(blockpos.up(2));
    }

    protected boolean isTeleportFriendlyBlock(int x, int z, int y, int x_offset, int z_offset)
    {
        BlockPos blockpos = new BlockPos(x + x_offset, y - 1, z + z_offset);
        IBlockState iblockstate = this.world.getBlockState(blockpos);
        return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.creature) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
    }


    public static Vec3d findSafeTeleportPoint(Entity entity, Vec3d destination){

        World world = entity.world;
        AxisAlignedBB box = entity.getEntityBoundingBox();

        box = box.offset(destination.subtract(entity.posX, entity.posY, entity.posZ));

        // All the parameters of this method are INCLUSIVE, so even the max coordinates should be rounded down
        Iterable<BlockPos> cuboid = BlockPos.getAllInBox(MathHelper.floor(box.minX), MathHelper.floor(box.minY),
                MathHelper.floor(box.minZ), MathHelper.floor(box.maxX), MathHelper.floor(box.maxY), MathHelper.floor(box.maxZ));

        if(Streams.stream(cuboid).noneMatch(b -> world.collidesWithAnyBlock(new AxisAlignedBB(b)))){
            // Nothing in the way
            return destination;
        }else{
            return null;
        }
    }
}