package astramusfate.wizardry_tales.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class Alchemy {

    /** Applies hidden effects to the creature **/
    public static void applyPotionHide(EntityLivingBase entity, int duration, int multiplier, Potion... effects){
        if (!entity.world.isRemote)
        for (Potion effect : effects)
            entity.addPotionEffect(new PotionEffect(effect, duration, multiplier, false, false));
    }

    /** Applies hidden effects to the creature **/
    public static void applyPotionHide(EntityLivingBase entity, int duration, Potion... effects){
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, duration, 0, false, false));
    }

    /** Applies hidden effects to the creature **/
    public static void applyPotionHide(EntityLivingBase entity, Potion... effects){
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, 220, 0, false, false));
    }

    /** Applies effects to the creature **/
    public static void applyPotion(EntityLivingBase entity, int duration, int multiplier, Potion... effects){
        if (!entity.world.isRemote)
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, duration, multiplier));
    }

    /** Applies effects to the creature for Spellwording **/
    public static void applyPotionCast(EntityLivingBase entity, int duration, int multiplier, Potion... effects){
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, duration, multiplier));
    }


    /** Applies not stealth(like last assassins) effects to the creature **/
    public static void applyPotion(EntityLivingBase entity, int duration, Potion... effects){
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, duration, 0));
    }

    /** Applies not stealth(like last assassins) effects to the creature **/
    public static void applyPotion(EntityLivingBase entity, Potion... effects){
        for (Potion effect : effects) entity.addPotionEffect(new PotionEffect(effect, 220, 0));
    }

    /** Takes care to remove those effects from target, heh **/
    public static void removePotion(EntityLivingBase entity, Potion... effects){
        for(Potion effect : effects){
            if(entity.isPotionActive(effect))
                entity.removePotionEffect(effect);
        }
    }

    /** Looks is there this potion on target! **/
    public static boolean hasPotion(EntityLivingBase entity, Potion... effects){
        for(Potion effect : effects){
            if(entity.isPotionActive(effect))
                return true;
        }
        return false;
    }

    /** If target has potion - produce WORSE potion version then it was before **/
    public static void produce(EntityLivingBase entity, Potion effect){
        if (entity.isPotionActive(effect)) {
            PotionEffect potion = entity.getActivePotionEffect(effect);
            if (potion != null) {
                Alchemy.applyPotion(entity, potion.getDuration() + 120,
                        potion.getAmplifier() + 1, effect);
            }
        } else {
            Alchemy.applyPotion(entity, 120, 0, effect);
        }
    }

    /** Same as produce method, but, do not adds additional multiplier **/
    public static void produceWeaker(EntityLivingBase entity, Potion effect){
        if (entity.isPotionActive(effect)) {
            PotionEffect potion = entity.getActivePotionEffect(effect);
            if (potion != null) {
                Alchemy.applyPotion(entity, potion.getDuration() + 120,
                        potion.getAmplifier(), effect);
            }
        } else {
            Alchemy.applyPotion(entity, 120, 0, effect);
        }
    }

    /** Same as produce method, but, do not adds additional multiplier **/
    public static void produceWeaker(EntityLivingBase entity, Potion effect, int base){
        if (entity.isPotionActive(effect)) {
            PotionEffect potion = entity.getActivePotionEffect(effect);
            if (potion != null) {
                Alchemy.applyPotion(entity, potion.getDuration() + base,
                        potion.getAmplifier(), effect);
            }
        } else {
            Alchemy.applyPotion(entity, base, 0, effect);
        }
    }

    public static void decreaseDuration(EntityLivingBase entity, PotionEffect effect, int to_remove){
        if(effect != null){
            entity.removePotionEffect(effect.getPotion());
            int duration = effect.getDuration()-to_remove;
            if(duration > 0) Alchemy.applyPotion(entity, duration, effect.getAmplifier(), effect.getPotion());
        }
    }

}
