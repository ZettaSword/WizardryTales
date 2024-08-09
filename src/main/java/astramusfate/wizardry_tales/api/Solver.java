package astramusfate.wizardry_tales.api;

import electroblob.wizardry.util.RayTracer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Solver {

    /**
     * The attribute modifier value is added onto the total value
     */
    public static final int ADD = 0;
    /**
     * The attribute modifier value is multiplied by the original base value then added onto the total value
     */
    public static final int ADD_MULTIPLE = 1;
    /**
     * The total value is multiplied by 1 + the attribute modifier value
     */
    public static final int MULTIPLY = 2;

    /** Normalize value, and gives you it from 0 to 1 **/
    public static float normalize(float value, float min, float max) { return (value - min) / (max - min); }

    /** Every seconds mentioned, it will return true, else it will be false (Seconds can be like: 0.3, 3.6, and etc.)
     * If we talks about Math, then, when ticksExisted can be divided on (Seconds * 20) without remains - it returns true!
     * **/
    public static boolean doEvery(Entity entity, double seconds){
        return entity.ticksExisted % (int) (seconds * 20) == 0;
    }

    /** Every seconds mentioned, it will return true, else it will be false (Seconds can be like: 0.3, 3.6, and etc.)
     * If we talks about Math, then, when Value can be divided on (Seconds * 20) without remains - it returns true!
     * **/
    public static boolean doEvery(int Value, double seconds){
        return Value % (int) (seconds * 20) == 0;
    }

    /** This method will calc Percentage of Value **/
    public static int percentOf(double value, double max){
        return (int)(value/max) * 100;
    }

    /** This method will return value in Ticks, for provided Seconds value, one seconds = 20 ticks **/
    public static int asTicks(double seconds){
        return (int)(seconds * 20);
    }

    /** Copy of asTicks method, just with other name. Returns Ticks value **/
    public static int duration(double seconds){
        return (int)(seconds * 20);
    }

    /** This method will calc current progress **/
    public static double calcProgress(double value, double max){
        return value/max;
    }

    /** This method will calc current Inverted progress **/
    public static double calcInvProgress(double progress){
        return 1.0f - progress;
    }

    /** This method will return value in Seconds, for provided Ticks value, one second = 20 ticks **/
    public static double asSeconds(double ticks){
        return ticks/20;
    }

    /** Checks is value is in Range of this values(min and max, inclusive) **/
    public static boolean rangeCheck(double value, double range_min, double range_max){
        return value >= range_min && value <= range_max;
    }

    /** Randomize Integer! **/
    public static int randInt(int min, int max){
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }

    /** Randomize Float! **/
    public static float randFloat(float min, float max){
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }

    /** Randomize Double! **/
    public static double randDouble(double min, double max){
        Random r = new Random();
        return min + r.nextDouble() * (max - min);
    }

    /** Randomize Double! **/
    public static double range(double value){
        return randDouble(-value, value);
    }

    /** Use this when you need to do something with chance value!
     *  Not sure is it works ._. **/
    public static boolean chance(int chance) {
        return randInt(1, 100) <= chance;
    }

    /** Returns random particle lifetime **/
    public static int particle(){return 20 + Solver.randInt(0, 20);}

    /** Used to find the biggest Integer among all of them
     * **/
    public static int biggestInt(int[] integers){
        int biggest = 0;
        int index = 0;
        for (int i = 0; i < integers.length; i++) {
            int value = integers[i];
            if(biggest < value){ biggest = value; index = i;}
        }
        return index;
    }

    /** Basically same as Math.min(), but to just not be confused - it can be used!
     * <br><br/> Limits the value, so it can't be higher than max value! **/
    public static float limit(float value, float max){
        return Math.min(value, max);
    }

    public static boolean invert(boolean bool){ return !bool;}
    @Nullable
    public static RayTraceResult standardBlockRayTrace(World world, Entity entity, double range, boolean hitLiquids,
                                                       boolean ignoreUncollidables, boolean returnLastUncollidable){
        // This method does not apply an offset like ray spells do, since it is not desirable in most other use cases.
        Vec3d origin = entity.getPositionEyes(1);
        Vec3d endpoint = origin.add(entity.getLookVec().scale(range));
        return world.rayTraceBlocks(origin, endpoint, hitLiquids, ignoreUncollidables, returnLastUncollidable);
    }


    /**
     * Helper method which performs a ray trace for <b>blocks only</b> from an entity's eye position in the direction
     * they are looking, over a specified range. This is a shorthand for
     * {@link #standardBlockRayTrace(World, Entity, double, boolean, boolean, boolean)}; ignoreUncollidables
     * and returnLastUncollidable default to false.
     */
    @Nullable
    public static RayTraceResult standardBlockRayTrace(World world, Entity entity, double range, boolean hitLiquids){
        return standardBlockRayTrace(world, entity, range, hitLiquids, false, false);
    }

    @Nullable
    public static RayTraceResult standardEntityRayTrace(World world, Entity entity, double range, boolean hitLiquids, Predicate<Entity> ignore){
        // This method does not apply an offset like ray spells do, since it is not desirable in most other use cases.
        Vec3d origin = entity.getPositionEyes(1);
        Vec3d endpoint = origin.add(entity.getLookVec().scale(range));
        return RayTracer.rayTrace(world, origin, endpoint, 0, hitLiquids, true, false,
                Entity.class, ignore);
    }
}
