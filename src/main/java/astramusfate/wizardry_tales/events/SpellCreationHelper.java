package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.data.ChantWorker;
import astramusfate.wizardry_tales.data.Lexicon;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static astramusfate.wizardry_tales.data.chanting.SpellParams.shapes;

public class SpellCreationHelper extends ChantWorker implements Lexicon {

    public static final String[] conditions = {condition_damage, condition_hit, condition_night, condition_day,
            condition_sneak, condition_use, condition_manual, condition_tick, condition_light_level, condition_spellcast};
    public static String getMsg(String original){
        return original.toLowerCase(Locale.ROOT).replace(",", "")
                .replace(".", "").replace("!", "")
                .replace("?", "").replace("/", "");
    }

    public static List<String> getSpell(String msg){
        return  Arrays.asList(msg.split(" "));
    }

    public static float getFloat(String word, float fail){
        float number;
        try {
            number = Float.parseFloat(word);
        } catch (Exception exception) {
            number = fail;
        }
        return number;
    }

    public static int getInteger(String word, int fail){
        int number;
        try {
            number = Integer.parseInt(word);
        } catch (Exception exception) {
            number = fail;
        }
        return number;
    }


    public static Number getFloat(String next, String next2, String next3, float fail){
        Number number;
        try{
            number = Float.parseFloat(next);
        } catch (Exception exception1){
            try {
                number = Float.parseFloat(next2);
            } catch (Exception exception2){
                try {
                    number = Float.parseFloat(next3);
                } catch (Exception exception3){
                    number = fail;
                }
            }
        }
        return number;
    }

    public static float getFloat(String[] spell, String match, float fail){
        float value = fail;
        for (int i = 0; i < spell.length; i++) {
            String next = i + 1 < spell.length ? spell[i+1] : "";
            String next2 = i + 2 < spell.length ? spell[i+2] : "";
            String next3 = i + 3 < spell.length ? spell[i+3] : "";
            String word = spell[i];

            float number;
            try{number = Float.parseFloat(next);
            } catch (Exception exception1){
                try {
                    number = Float.parseFloat(next2);
                } catch (Exception exception2){
                    try {
                        number = Float.parseFloat(next3);
                    } catch (Exception exception3){
                        number = 0.0F;
                    }
                }
            }

            // Numbers required
            if(number > 0.0F) {
                if (SpellCreation.findIn(word, match)) value = number;
            }
        }
        return value;
    }

    /** Use it only when you're okay with that item will have nbt from that point! **/
    public static NBTTagCompound getOrCreateTagCompound(ItemStack stack){
        if (stack == null) return new NBTTagCompound();
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (stack.hasTagCompound())
            return stack.getTagCompound();
        else return new NBTTagCompound();
    }

    public static String findShape(String... words){
        for(String word : words){
            for (String shape : shapes){
                if (findIn(word, shape)) return shape;
            }
        }
        return "me";
    }

    public static String findWithin(String[] array, String fail, String... words){
        for(String word : words){
            for (String entry : array){
                if (findIn(word, entry)) return word;
            }
        }
        return fail;
    }

    public static int getChosenSpellIndex(ItemStack wand){

        Spell[] spells = WandHelper.getSpells(wand);

        if(wand.getTagCompound() != null){

            int selectedSpell = wand.getTagCompound().getInteger(WandHelper.SELECTED_SPELL_KEY);

            if(selectedSpell >= 0 && selectedSpell < spells.length){
                return selectedSpell;
            }
        }

        return -1;
    }
}
