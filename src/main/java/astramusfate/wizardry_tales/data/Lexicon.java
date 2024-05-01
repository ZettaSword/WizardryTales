package astramusfate.wizardry_tales.data;

public interface Lexicon {

    int TARGET_ALL = 0;
    int TARGET_BLOCKS = 1;
    int TARGET_ENTITIES = 2;

    String par_potency = "power potency damage";
    String par_range = "range";
    String par_size = "size radius";
    String par_duration = "duration";
    String par_lifetime = "lifetime";
    String par_health = "health";
    String par_shape = "shape";
    String par_element = "element";
    String par_position = "position";
    String par_target = "target";
    String par_condition = "condition";
    String ignore = "set to is";

    String shape_entity = "entity entities";
    String shape_block = "block blocks";
    String shape_area = "area";
    String shape_sigil = "sigil";
    String shape_minion = "minion";
    String shape_projectile = "projectile";
    String shape_construct = "construct";
    String shape_inscribe = "inscribe scribe";
    String shape_adjust = "adjust change";
    String shape_nearest = "near nearest close closest";

    String shape_collector = "collector";
    String shape_array = "array";

    /** Do not forget that each condition should be added to SpellCreationHelper#conditions **/
    String condition_sneak = "sneak sneaking";
    String condition_night = "night";
    String condition_day = "day";
    /** When DAMAGED by someone **/
    String condition_damage = "damage damaged";
    /** When attack someone, hitting with inscribed item **/

    String condition_hit = "hit hitting";
    String condition_use = "use";
    String condition_tick = "constant tick update";
    String condition_light_level = "light";
    String condition_spellcast = "spell spellcast cast";
    String condition_manual = "manual";
}
