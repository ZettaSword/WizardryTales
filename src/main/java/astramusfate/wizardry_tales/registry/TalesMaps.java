package astramusfate.wizardry_tales.registry;

import baubles.api.BaubleType;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import static astramusfate.wizardry_tales.registry.TalesItems.*;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class TalesMaps {
    public static final Map<ItemArtefact.Type, BaubleType> ARTEFACT_TYPE_MAP = new EnumMap<>(ItemArtefact.Type.class);

    private static final Map<Pair<Tier, Element>, Item> MWAND_MAP = new HashMap<>();
    private static final Map<Pair<Tier, Element>, Item> STAFF_MAP = new HashMap<>();
    private static final Map<Pair<Tier, Element>, Item> GRIMOIRE_MAP = new HashMap<>();


    public static void preInit(){
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.RING, BaubleType.RING);
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.AMULET, BaubleType.AMULET);
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.CHARM, BaubleType.CHARM);

        populateMagicWandsMap();
        populateGrimoiresMap();
        populateStaffsMap();
    }


    public static Item getMWand(Tier tier, Element element){
        if(tier == null) throw new NullPointerException("The given tier cannot be null.");
        if(element == null) element = Element.MAGIC;
        return MWAND_MAP.get(ImmutablePair.of(tier, element));
    }

    public static Item getStaff(Tier tier, Element element){
        if(tier == null) throw new NullPointerException("The given tier cannot be null.");
        if(element == null) element = Element.MAGIC;
        return STAFF_MAP.get(ImmutablePair.of(tier, element));
    }

    public static Item getGrimoire(Tier tier, Element element){
        if(tier == null) throw new NullPointerException("The given tier cannot be null.");
        if(element == null) element = Element.MAGIC;
        return GRIMOIRE_MAP.get(ImmutablePair.of(tier, element));
    }

    public static void populateGrimoiresMap(){
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.MAGIC), grimoire_novice);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.MAGIC), grimoire_apprentice);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.MAGIC), grimoire_advanced);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.MAGIC), grimoire_master);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.FIRE), grimoire_novice_fire);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.FIRE), grimoire_apprentice_fire);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.FIRE), grimoire_advanced_fire);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.FIRE), grimoire_master_fire);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.ICE), grimoire_novice_ice);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.ICE), grimoire_apprentice_ice);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.ICE), grimoire_advanced_ice);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.ICE), grimoire_master_ice);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.EARTH), grimoire_novice_nature);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.EARTH), grimoire_apprentice_nature);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.EARTH), grimoire_advanced_nature);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.EARTH), grimoire_master_nature);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.LIGHTNING), grimoire_novice_thunder);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.LIGHTNING), grimoire_apprentice_thunder);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.LIGHTNING), grimoire_advanced_thunder);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.LIGHTNING), grimoire_master_thunder);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.NECROMANCY), grimoire_novice_darkness);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.NECROMANCY), grimoire_apprentice_darkness);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.NECROMANCY), grimoire_advanced_darkness);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.NECROMANCY), grimoire_master_darkness);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.HEALING), grimoire_novice_light);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.HEALING), grimoire_apprentice_light);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.HEALING), grimoire_advanced_light);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.HEALING), grimoire_master_light);

        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.SORCERY), grimoire_novice_sorcery);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.SORCERY), grimoire_apprentice_sorcery);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.SORCERY), grimoire_advanced_sorcery);
        GRIMOIRE_MAP.put(ImmutablePair.of(Tier.MASTER, Element.SORCERY), grimoire_master_sorcery);
    }

    public static void populateMagicWandsMap(){
        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.MAGIC), wand_novice);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.MAGIC), wand_apprentice);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.MAGIC), wand_advanced);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.MAGIC), wand_master);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.FIRE), wand_novice_fire);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.FIRE), wand_apprentice_fire);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.FIRE), wand_advanced_fire);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.FIRE), wand_master_fire);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.ICE), wand_novice_ice);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.ICE), wand_apprentice_ice);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.ICE), wand_advanced_ice);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.ICE), wand_master_ice);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.EARTH), wand_novice_nature);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.EARTH), wand_apprentice_nature);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.EARTH), wand_advanced_nature);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.EARTH), wand_master_nature);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.LIGHTNING), wand_novice_thunder);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.LIGHTNING), wand_apprentice_thunder);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.LIGHTNING), wand_advanced_thunder);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.LIGHTNING), wand_master_thunder);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.NECROMANCY), wand_novice_darkness);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.NECROMANCY), wand_apprentice_darkness);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.NECROMANCY), wand_advanced_darkness);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.NECROMANCY), wand_master_darkness);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.HEALING), wand_novice_light);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.HEALING), wand_apprentice_light);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.HEALING), wand_advanced_light);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.HEALING), wand_master_light);

        MWAND_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.SORCERY), wand_novice_sorcery);
        MWAND_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.SORCERY), wand_apprentice_sorcery);
        MWAND_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.SORCERY), wand_advanced_sorcery);
        MWAND_MAP.put(ImmutablePair.of(Tier.MASTER, Element.SORCERY), wand_master_sorcery);
    }

    public static void populateStaffsMap(){

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.MAGIC), staff_novice_magic);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.MAGIC), staff_apprentice_magic);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.MAGIC), staff_advanced_magic);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.MAGIC), staff_master_magic);
        /*
        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.FIRE), staff_novice_fire);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.FIRE), staff_apprentice_fire);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.FIRE), staff_advanced_fire);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.FIRE), staff_master_fire);

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.ICE), staff_novice_ice);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.ICE), staff_apprentice_ice);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.ICE), staff_advanced_ice);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.ICE), staff_master_ice);

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.EARTH), staff_novice_nature);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.EARTH), staff_apprentice_nature);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.EARTH), staff_advanced_nature);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.EARTH), staff_master_nature);

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.LIGHTNING), staff_novice_thunder);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.LIGHTNING), staff_apprentice_thunder);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.LIGHTNING), staff_advanced_thunder);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.LIGHTNING), staff_master_thunder);

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.NECROMANCY), staff_novice_darkness);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.NECROMANCY), staff_apprentice_darkness);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.NECROMANCY), staff_advanced_darkness);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.NECROMANCY), staff_master_darkness);
        */

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.HEALING), staff_novice_light);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.HEALING), staff_apprentice_light);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.HEALING), staff_advanced_light);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.HEALING), staff_master_light);
        /*

        STAFF_MAP.put(ImmutablePair.of(Tier.NOVICE, Element.SORCERY), staff_novice_sorcery);
        STAFF_MAP.put(ImmutablePair.of(Tier.APPRENTICE, Element.SORCERY), staff_apprentice_sorcery);
        STAFF_MAP.put(ImmutablePair.of(Tier.ADVANCED, Element.SORCERY), staff_advanced_sorcery);
        STAFF_MAP.put(ImmutablePair.of(Tier.MASTER, Element.SORCERY), staff_master_sorcery);
         */
    }
}
