package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;

import javax.annotation.Nullable;

/** Helper class for easier all SpellModifiers access!
 *  * <br><br/> Contains causeDamage(), to deal direct magical damage
 *  **/
public class Sage {
    public static final String POTENCY = SpellModifiers.POTENCY;
    public static final String COST = SpellModifiers.COST;
    public static final String CHARGEUP = SpellModifiers.CHARGEUP;
    public static final String PROGRESSION = SpellModifiers.PROGRESSION;

    public static final Item DURATION = WizardryItems.duration_upgrade;
    public static final Item ATTUNEMENT = WizardryItems.attunement_upgrade;
    public static final Item BLAST = WizardryItems.blast_upgrade;
    public static final Item CONDENSER = WizardryItems.condenser_upgrade;
    public static final Item COOLDOWN = WizardryItems.cooldown_upgrade;
    public static final Item MELEE = WizardryItems.melee_upgrade;
    public static final Item RANGE = WizardryItems.range_upgrade;
    public static final Item SIPHON = WizardryItems.siphon_upgrade;
    public static final Item STORAGE = WizardryItems.storage_upgrade;

    public static final Item CHANT_POWER = TalesItems.chant_upgrade_power;
    public static final Item CHANT_DURATION = TalesItems.chant_upgrade_duration;
    public static final Item CHANT_RANGE = TalesItems.chant_upgrade_range;
    public static final Item CHANT_DELAY = TalesItems.chant_upgrade_delay;
    public static final Item CHANT_COST = TalesItems.chant_upgrade_count;

    /** Finds Element or Type by word. Deals damage to entity. **/
    public static void smartDamage(String word, @Nullable EntityLivingBase caster, Entity living, float damage){
        MagicDamage.DamageType type = getTypeByText(word);
        Element element;
        if (type == null) {
            element = Element.fromName(word, Element.MAGIC);
            assert element != null;
            type = Sage.getTypeByElement(element);
        }else element = getElementByType(type);

        if (!MagicDamage.isEntityImmune(type, living))
            causeDamage(type, caster, living, damage);

        Wizard.castParticles(living.world, element, living.getPositionVector());
    }

    /** Deals damage to entity. **/
    public static boolean causeDamage(MagicDamage.DamageType type, @Nullable EntityLivingBase caster, Entity living, float damage){
      return living.attackEntityFrom(MagicDamage.causeDirectMagicDamage(caster, type), damage);
    }

    public static MagicDamage.DamageType getTypeByElement(Element element){
        switch (element){
            case FIRE: return MagicDamage.DamageType.FIRE;
            case ICE: return MagicDamage.DamageType.FROST;
            case LIGHTNING: return MagicDamage.DamageType.SHOCK;
            case NECROMANCY: return MagicDamage.DamageType.WITHER;
            case EARTH: return MagicDamage.DamageType.POISON;
            case SORCERY: return MagicDamage.DamageType.FORCE;
            case HEALING: return MagicDamage.DamageType.RADIANT;
            default: return MagicDamage.DamageType.MAGIC;
        }
    }

    public static Element getElementByType(MagicDamage.DamageType type){
        if (type == null) return Element.MAGIC;
        switch (type){
            case FIRE: return Element.FIRE;
            case FROST: return Element.ICE;
            case SHOCK: return Element.LIGHTNING;
            case WITHER: return Element.NECROMANCY;
            case POISON: return Element.EARTH;
            case FORCE: return Element.SORCERY;
            case RADIANT: return Element.HEALING;
            default: return Element.MAGIC;
        }
    }

    @Nullable
    public static MagicDamage.DamageType getTypeByText(String text){
        try {
           return MagicDamage.DamageType.valueOf(text);
        }catch (Exception ignore){}
        return null;
    }

    public static Element getElementByText(String text){
      return  Element.fromName(text, Element.MAGIC);
    }

}
