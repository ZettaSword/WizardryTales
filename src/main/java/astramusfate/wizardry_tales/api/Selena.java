package astramusfate.wizardry_tales.api;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

/** Selena controls the nature in the overworld and all other worlds,
 *  there was rumors, that she has own Dimension, but who knows are they truth or not...
 *  Selena as well interacts with most of mobs, but do not talk them
 **/
public class Selena {

    public static AttributeModifier newMod(UUID id, String name, double amount, int operation){
        return new AttributeModifier(id, name, amount, operation);
    }

    public static void nullAttributes(EntityPlayer player, UUID uuid) {
        for (IAttributeInstance instance : player.getAttributeMap().getAllAttributes()) {
            if (instance.getModifier(uuid) != null) instance.removeModifier(uuid);
        }
    }

    /** Allows optimised way to remove attributes modifier, by providing list of attributes modified
     * <br><br/> player - player, attributes modified
     * <br><br/> uuid - UUID of modifiers
     * <br><br/> attributes - custom list of all attributes modified with modifier **/
    public static void nullAttributes(EntityPlayer player, UUID uuid, IAttributeInstance... attributes) {
        for (IAttributeInstance attribute : attributes){
            IAttributeInstance instance = player.getAttributeMap().getAttributeInstance(attribute.getAttribute());
            if (instance.getModifier(uuid) != null) instance.removeModifier(uuid);
        }
    }

    public static void checkAttributes(IAttributeInstance check, UUID id, String name, double value, int operation){
        AttributeModifier mod = newMod(id, name, value, operation).setSaved(true);
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() != value){
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }
            }
        }
    }

    @Nullable
    public static <T extends EntityLivingBase> EntityLivingBase findNearestLiving(Vec3d position, List<T> list){
        double distance=16;
        T nearest = null;
        if(list.isEmpty()) return null;
        for(T entity : list){
            if(distance > entity.getDistance(position.x, position.y, position.z)){
                distance=entity.getDistance(position.x, position.y, position.z);
                nearest=entity;
            }
        }
        return nearest;
    }

    public static <T extends Entity> Entity findNearest(Vec3d position, List<T> list){
        double distance=16;
        T nearest = null;
        for(T entity : list){
            if(distance > entity.getDistance(position.x, position.y, position.z)){
                distance=entity.getDistance(position.x, position.y, position.z);
                nearest=entity;
            }
        }
        return nearest;
    }

    public static <T extends Entity> List<T> getAround(World world, double radius, BlockPos pos, Class<T> entityType, Predicate<? super T> removeIf){
        List<T> list = getAround(world, radius, pos.getX(), pos.getY(), pos.getZ(), entityType);
        list.removeIf(removeIf);
        return list;
    }

    public static <T extends Entity> List<T> getAround(World world, double radius, BlockPos pos, Class<T> entityType){
        return getAround(world, radius, pos.getX(), pos.getY(), pos.getZ(), entityType);
    }

    /** Copy of EBWizardry method **/
    private static <T extends Entity> List<T> getAround(World world, double radius, double x, double y, double z,  Class<T> entityType){
        AxisAlignedBB aabb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
        List<T> entityList = world.getEntitiesWithinAABB(entityType, aabb);
        for(int i = 0; i < entityList.size(); i++){
            if(entityList.get(i).getDistance(x, y, z) > radius){
                entityList.remove(i);
                break;
            }
        }
        return entityList;
    }


    public static void pushOne(EntityLivingBase creature){
        double dx = creature.motionX;
        double dz;
        for (dz = creature.motionZ; dx * dx + dz * dz < 1.0E-4D; dz = (Math.random() - Math.random()) * 0.01D) {
            dx = (Math.random() - Math.random()) * 0.01D;
        }
        creature.knockBack(creature, 0.6f, dx, dz);
    }

    /** Used to push non-living entities **/
    public static void pushEntity(Entity attacker, Entity target, float strength){
        double xRatio = attacker.posX - target.posX;
        double zRatio;
        for(zRatio = attacker.posZ - target.posZ; xRatio * xRatio + zRatio * zRatio < 1.0E-4D; zRatio = (Math.random() - Math.random())
                * 0.01D){
            xRatio = (Math.random() - Math.random()) * 0.01D;
        }

        target.isAirBorne = true;
        float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
        target.motionX /= 2.0D;
        target.motionZ /= 2.0D;
        target.motionX -= xRatio / (double)f * (double)strength;
        target.motionZ -= zRatio / (double)f * (double)strength;

        if (target.onGround)
        {
            target.motionY /= 2.0D;
            target.motionY += (double)strength;

            if (target.motionY > 0.4000000059604645D)
            {
                target.motionY = 0.4000000059604645D;
            }
        }
    }

    public static void pushOne(EntityLivingBase creature, float strength){
        double dx = creature.motionX;
        double dz;
        for (dz = creature.motionZ; dx * dx + dz * dz < 1.0E-4D; dz = (Math.random() - Math.random()) * 0.01D) {
            dx = (Math.random() - Math.random()) * 0.01D;
        }
        creature.knockBack(creature, strength, dx, dz);
    }

    /** Upgrade stat of Mob, not a player mostly WARNING: IT DOES IT WITH BASE STAT VALUE, not Adds modifiers to it **/
    public static void upgradeStat(EntityLiving living, IAttribute attribute, double upgrade, double max){
        double value = living.getEntityAttribute(attribute).getBaseValue() + upgrade;
        if(value > max){ value = max;}
        living.getEntityAttribute(attribute).setBaseValue(value);
    }

    /** Upgrade stat of Mob, not a player mostly WARNING: IT DOES IT WITH BASE STAT VALUE, not Adds modifiers to it **/
    public static void upgradeStat(EntityLiving living, IAttribute attribute, double upgrade){
        living.getEntityAttribute(attribute).setBaseValue(living.getEntityAttribute(attribute).getBaseValue() + upgrade);
    }
}
