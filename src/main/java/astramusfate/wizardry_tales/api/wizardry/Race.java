package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import net.minecraft.entity.player.EntityPlayer;

public class Race {
    public static String human = "human";
    public static String elf = "elf";
    public static String dwarf = "dwarf";

    public static String get(EntityPlayer player){
        ISoul soul = Mana.getSoul(player);
        return soul != null ? soul.getRace() : Race.human;
    }
}
