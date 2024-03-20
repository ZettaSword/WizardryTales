package astramusfate.wizardry_tales.api.wizardry;

import electroblob.wizardry.constants.Element;

public class ArcaneColor {
    public static int byElement(Element element){
        switch (element){
            case MAGIC: return 0xD6790B;
            case FIRE: return 0xde2000;
            case ICE: return 0x51ADA6;
            case LIGHTNING: return 0x283472;
            case NECROMANCY: return 0x3C0A68;
            case EARTH: return 0x41943E;
            case SORCERY: return 0x55A962;
            case HEALING: return 0xCDCE64;
            default: return 0xB484B8;
        }
    }
}
