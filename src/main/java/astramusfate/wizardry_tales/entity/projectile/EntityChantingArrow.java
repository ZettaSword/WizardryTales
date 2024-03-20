package astramusfate.wizardry_tales.entity.projectile;

import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class EntityChantingArrow extends EntityCustomArrow{


    /**
     * Creates a new projectile in the given world.
     *
     * @param world World where entity spawns in
     */
    public EntityChantingArrow(World world) {
        super(world);
    }

    /**
     * @param world World
     * @param words Spell to cast if touches entity/ground
     */
    public EntityChantingArrow(World world, List<String> words) {
        super(world);
        setWords(words);
    }

    public void setWords(List<String> words) {
        this.words = words;
        this.amount = words.size() - 1;
    }

    /** Computes the velocity the projectile should be launched at to achieve the required range. */
    // Long story short, it doesn't make much sense to me to have the JSON file specify the velocity - even less so if
    // the velocity is masquerading under the tag 'range' - so we'll let the code do the heavy lifting so people can
    // input something meaningful.
    public float calculateVelocity(EntityChantingArrow projectile, float range, float launchHeight){
        if(!projectile.doGravity()){
            // No sensible spell will do this - range is meaningless if the particle has no gravity or lifetime
            if(projectile.getLifetime() <= 0) return 2.0F;
            // Speed = distance/time (trivial, I know, but I've put it here for the sake of completeness)
            return range / projectile.getLifetime();
        }else{
            // Arrows have gravity 0.05
            float g = 0.05f;
            // Assume horizontal projection
            return range / MathHelper.sqrt(2 * launchHeight/g);
        }
    }
}
