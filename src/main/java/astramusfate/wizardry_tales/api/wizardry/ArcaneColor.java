package astramusfate.wizardry_tales.api.wizardry;

import electroblob.wizardry.constants.Element;
import net.minecraft.util.text.TextFormatting;

import java.awt.*;

public class ArcaneColor {

    public static Color MAGIC = new Color(1, 1, 0.65f);
    public static Color MAGIC_FADE = new Color(0.7f, 0, 1);


    public final static Color VIOLET   = new Color(76/255f, 29/255f, 133/255f);
    public final static Color FROST = new Color(0f, 159/255f, 255/255f);
    public final static Color SORCERY = new Color(0f, 255/255f, 40/255f);
    public final static Color EXPLOSION = new Color(255/255f, 25/255f, 0f);
    public final static Color NATURE = new Color(12/255f, 141/255f, 12/255f);
    public final static Color THUNDER = new Color(0/255f, 34/255f, 255/255f);
    public final static Color HOLY = new Color(255/255f, 205/255f, 0/255f);
    public final static Color DARKNESS = new Color(119/255f, 0/255f, 255/255f);
    public final static Color BLOOD = new Color(210/255f, 10/255f, 0f);
    public final static Color WATER = new Color(0f, 100/255f, 255/255f);
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

    public static Color colorByElement(Element element){
        switch (element){
            case FIRE: return Color.RED;
            case ICE: return Color.CYAN;
            case LIGHTNING: return Color.BLUE;
            case NECROMANCY: return Color.MAGENTA.darker().darker();
            case EARTH: return Color.GREEN;
            case SORCERY: return Color.GREEN.brighter();
            case HEALING: return Color.YELLOW.brighter();
            default: return MAGIC;
        }
    }

    /** This simple word gives us create color easier **/
    public static Color chooseOld(Element element){
        switch (element){
            case FIRE: return EXPLOSION;
            case ICE: return FROST;
            case LIGHTNING: return THUNDER;
            case NECROMANCY: return DARKNESS;
            case EARTH: return NATURE;
            case SORCERY: return SORCERY;
            case HEALING: return HOLY;
            default: return MAGIC;
        }
    }
}
