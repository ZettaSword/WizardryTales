package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Objects;

public class Race {
    public static String human = "human";
    public static String elf = "elf";
    public static String dwarf = "dwarf";

    public static String undead = "undead";

    public static String get(EntityPlayer player){
        ISoul soul = Mana.getSoul(player);
        return soul != null ? soul.getRace() : Race.human;
    }

    public static boolean isUndead(EntityLivingBase target){
        return target.isPotionActive(WizardryPotions.curse_of_undeath) ||
                (target instanceof EntityPlayer && Objects.equals(get((EntityPlayer) target), undead));
    }

    public static boolean is(EntityLivingBase target, String race){
        return target instanceof EntityPlayer && get((EntityPlayer) target).equals(race);
    }

    public static void set(EntityPlayer player, String race){
        ISoul soul = Mana.getSoul(player);
        if (soul != null) soul.setRace(player, race);
    }
}
