package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.WizardryTales;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

public class TalesTeleporter extends Teleporter {


    public TalesTeleporter(WorldServer world, double x, double y, double z) {
        super(world);
        this.worldServer = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private final WorldServer worldServer;
    private double x;
    private double y;
    private double z;

    @Override
    public void placeInPortal(@Nonnull Entity entity, float rotationYaw) {
        // The main purpose of this function is to *not* create a nether portal
        this.worldServer.getBlockState(new BlockPos((int) this.x, (int) this.y, (int) this.z));

        entity.setPosition(this.x, this.y, this.z);
        entity.motionX = 0.0f;
        entity.motionY = 0.0f;
        entity.motionZ = 0.0f;
    }

    public static void teleportPlayer(EntityPlayerMP player, int targetDim, double x, double y, double z){
        if (ForgeHooks.onTravelToDimension(player, targetDim)) {
            teleportPlayerToDimension(player, targetDim, new BlockPos(x, y, z), player.cameraYaw);
        }
    }


    /**
     * This method is based on the work of Zarathul and licensed under the MIT license
     * Author: Zarathul
     * License: MIT License - https://github.com/Zarathul/simpleportals/blob/1.12.2/LICENSE.md
     * https://github.com/Zarathul/simpleportals/blob/1.12.2/src/main/java/net/zarathul/simpleportals/common/Utils.java
     *
     * @param player      The player to teleport.
     * @param dimension   The dimension to port to.
     * @param destination The position to port to.
     * @param yaw         The rotation yaw the entity should have after porting.
     */
    private static void teleportPlayerToDimension(EntityPlayerMP player, int dimension, BlockPos destination, float yaw) {
        int startDimension = player.dimension;
        MinecraftServer server = player.getServer();
        PlayerList playerList = server.getPlayerList();
        WorldServer startWorld = server.getWorld(startDimension);
        WorldServer destinationWorld = server.getWorld(dimension);

        player.dimension = dimension;
        player.connection.sendPacket(new SPacketRespawn(
                dimension,
                destinationWorld.getDifficulty(),
                destinationWorld.getWorldInfo().getTerrainType(),
                player.interactionManager.getGameType()));

        playerList.updatePermissionLevel(player);
        startWorld.removeEntityDangerously(player);
        player.isDead = false;

        player.setLocationAndAngles(
                destination.getX() + 0.5d,
                destination.getY(),
                destination.getZ() + 0.5d,
                yaw,
                player.rotationPitch);

        destinationWorld.spawnEntity(player);
        destinationWorld.updateEntityWithOptionalForce(player, false);
        player.setWorld(destinationWorld);

        playerList.preparePlayer(player, startWorld);
        player.connection.setPlayerLocation(
                destination.getX() + 0.5d,
                destination.getY(),
                destination.getZ() + 0.5d,
                yaw,
                player.rotationPitch);

        player.interactionManager.setWorld(destinationWorld);
        player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
        playerList.updateTimeAndWeatherForPlayer(player, destinationWorld);
        playerList.syncPlayerInventory(player);

        // Reapply potion effects

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potionEffect));
        }

        // Resend player XP otherwise the XP bar won't show up until XP is either gained or lost

        player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));

        // Remove the ender dragon hp bar when porting out of the End, otherwise if the dragon is still alive
        // the hp bar won't go away and if you then reenter the End, you will have multiple boss hp bars.

        FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, startDimension, dimension);
    }

    public static void teleportEntity(EntityLivingBase entityLivingBase, int targetDim, double x, double y, double z) {
        MinecraftServer server = entityLivingBase.getEntityWorld().getMinecraftServer();
       // WizardryTales.log.warn("1) Got entity world to change dim.");
        if (server != null) {
            //WizardryTales.log.warn("2) Server is not null.");
            WorldServer worldServer = server.getWorld(targetDim);
            BlockPos pos = new BlockPos(x, y, z);
            if (!worldServer.isAreaLoaded(pos, 1)) {
                //WizardryTales.log.warn("3) Area is not loaded!");
                ForgeChunkManager.Ticket tk = ForgeChunkManager.requestTicket(WizardryTales.MODID, worldServer, ForgeChunkManager.Type.ENTITY);
                ForgeChunkManager.forceChunk(tk, new ChunkPos(pos));
            }
            //WizardryTales.log.warn("4) Changed entity position.");
            entityLivingBase.setPositionAndUpdate(x, y, z);
            entityLivingBase.dimension=targetDim;
            entityLivingBase.setWorld(worldServer);
            worldServer.spawnEntity(entityLivingBase);
        }
    }

}