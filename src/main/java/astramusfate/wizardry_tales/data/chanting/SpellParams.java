package astramusfate.wizardry_tales.data.chanting;

import astramusfate.wizardry_tales.data.ChantWorker;
import astramusfate.wizardry_tales.data.Lexicon;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static astramusfate.wizardry_tales.data.ChantWorker.findInSmart;

/** We have usual parameters, which are usually calculated each word, and unusual, which requires me to setup all special for them **/
public class SpellParams implements Lexicon {

    public SpellParam potency = new SpellParam(par_potency, 1.0F){
        @Override
        public boolean canApply(Number number) {
            return number.floatValue() >= 1.0F;
        }
    };
    public SpellParam duration = new SpellParam(par_duration, 5);
    public SpellParam range = new SpellParam(par_range, 12){
        @Override
        public boolean canApply(Number number) {
            return number.intValue() > 0;
        }
    };
    public SpellParam lifetime = new SpellParam(par_lifetime, 5){
        @Override
        public boolean canApply(Number number) {
            return number.intValue() != 0;
        }
    };
    public SpellParam health = new SpellParam(par_health, 1){
        @Override
        public boolean canApply(Number number) {
            return number.intValue() > 0;
        }
    };

    public SpellParam size = new SpellParam(par_size, 2){
        @Override
        public boolean canApply(Number number) {
            return number.intValue() > 0;
        }
    };

    public static final String[] shapes = {shape_entity, shape_area, shape_sigil, shape_minion, shape_projectile,
            shape_construct, shape_inscribe, shape_adjust, shape_block, "me", shape_collector, shape_array};

    public SpellParam shape = new SpellParam(par_shape, "me"){
        @Override
        public boolean canApply(String value) {
            for (String shape : shapes){
                if (findInSmart(value, shape)) return true;
            }

            return false;
        }

        @Override
        public boolean canApply(Number number) {
            return false;
        }
    };

    /** Used for parameters that can be set without worries **/
    public SpellParam[] autoCalc = {potency, duration, size, range, lifetime, health, shape};

    // NEW

    public Vec3d pos = null;
    public Vec3d change = new Vec3d(0, 0,0);
    public boolean isRay = false;
    public boolean isBuff = false;

    public boolean stopCast = false;

    public Element element = Element.MAGIC;

    public World world;

    public List<String> set;

    public HashMap<String, String> keys = new HashMap<>();

    public String prayingTo = "";
    public int followingGod = 0;

    public List<BlockPos> targetedBlocks;

    //OLD

    public BlockPos savedPos = null;
    public  boolean canAlly = false;
    public  boolean hasOwner = true;

    public  boolean hasEffects = true;

    /** 0 - all. 1 - only blocks. 2 - only entities. **/
    public int castingTargeting = 0;

    public RayTraceResult ray = null;

    public Entity focal;
    public List<Entity> targets;
    public Entity target;
    public Entity last_summon;
    public boolean isServer;
    public String original;

    public EntityPlayer playerCaster;
    public SpellModifiers spellModifiers;

    public Predicate<Entity> filter = Objects::nonNull;
    public Predicate<Entity> condition = Objects::nonNull;
    /** Used for Place as example! **/
    public Vec3d vector = new Vec3d(0,0,0);
    public boolean vecBuilding;

    public void calcParam(String word, Object value){
        for (SpellParam param : autoCalc){
            if (ChantWorker.findInSmart(word, param.name())){
                if (value instanceof Number && param.canApply((Number) value)) param.setNumber((Number) value);
                if (value instanceof String && param.canApply((String) value)) param.setValue((String) value);
                break;
            }
        }
    }
}
