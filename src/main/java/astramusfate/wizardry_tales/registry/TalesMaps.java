package astramusfate.wizardry_tales.registry;

import baubles.api.BaubleType;
import electroblob.wizardry.item.ItemArtefact;

import java.util.EnumMap;
import java.util.Map;

public class TalesMaps {
    public static final Map<ItemArtefact.Type, BaubleType> ARTEFACT_TYPE_MAP = new EnumMap<>(ItemArtefact.Type.class);

    public static void preInit(){
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.RING, BaubleType.RING);
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.AMULET, BaubleType.AMULET);
        ARTEFACT_TYPE_MAP.put(ItemArtefact.Type.CHARM, BaubleType.CHARM);
    }
}
