package astramusfate.wizardry_tales.api;

import electroblob.wizardry.constants.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/** Aterna is special "alive" module to replicate some things about magic with Talking, or Translation **/
public class Aterna {

    public static void translate(EntityPlayer caster, String msg){
        caster.sendMessage(new TextComponentTranslation(msg));
    }

    public static void translate(EntityPlayer caster, String msg, Object... args){
        caster.sendMessage(new TextComponentTranslation(msg, args));
    }

    public static void translate(EntityPlayer caster, String msg, boolean inBar){
        caster.sendStatusMessage(new TextComponentTranslation(msg), inBar);
    }

    public static void translate(EntityPlayer caster, boolean inBar, String msg){
        caster.sendStatusMessage(new TextComponentTranslation(msg), inBar);
    }

    public static void translate(EntityPlayer caster, boolean inBar, String msg, Object... args){
        caster.sendStatusMessage(new TextComponentTranslation(msg, args), inBar);
    }

    /** Chooses random message from all the Messages provided **/
    public static void translateRandom(EntityPlayer caster, boolean inBar, String... messages){
        caster.sendStatusMessage(new TextComponentTranslation(messages[Solver.randInt(0, messages.length)]), inBar);
    }

    public static void translate(String msg){
        Minecraft.getMinecraft().player.sendMessage(new TextComponentTranslation(msg));
    }

    /** Will change things from this 'aterna is here' to this: 'Aterna is here' **/
    public static String capitalize(@Nullable String msg){
        if(msg != null)
            return msg.substring(0, 1).toUpperCase() + msg.substring(1);
        else
            return "";
    }

    /** Will change things from this 'aterna is here' to this: 'Aterna is here' if msg is not Null, else will replace with what you entered **/
    public static String capitalize(@Nullable String msg, String replace){
        if(msg != null)
            return msg.substring(0, 1).toUpperCase() + msg.substring(1);
        else
            return replace;
    }

    /** Allows to make System messages easier **/
    public static String sysMessage(String msg){
        return "["+msg+"]";
    }

    /** The word to replace long-long thing :ъ **/
    public static void messageBar(EntityPlayer caster, ITextComponent msg){
        caster.sendStatusMessage(msg, true);
    }

    /** Sends message to the caster chat **/
    public static void messageChat(EntityPlayer caster, ITextComponent message){
        caster.sendStatusMessage(message, false);
    }


    /** The word to replace long-long thing :ъ **/
    public static void messageBar(EntityPlayer caster, String message){
        if (!caster.world.isRemote) caster.sendStatusMessage(new TextComponentString(message), true);
    }

    /** Says about Chanting error in most cases! **/
    public static void chant(@Nullable EntityPlayer caster, String message){
        if (caster == null || caster.getEntityWorld().isRemote) return;
        messageBar(caster, message);
    }

    /** The word to replace really long-long things :ъ **/
    public static void messageBar(EntityPlayer caster, String... message){
        for (String s : message) {
            caster.sendStatusMessage(new TextComponentString(s), true);
        }
    }

    /** Sends message to the caster chat **/
    public static void messageChat(EntityPlayer caster, String message){
        caster.sendStatusMessage(new TextComponentString(message), false);
    }

    /** Sends message to the caster chat, but long! **/
    public static void messageChat(EntityPlayer caster, String... message){
        for (String s : message) {
            caster.sendStatusMessage(new TextComponentString(s), false);
        }
    }

    /** Sends message, probably to server... or client... or both .-. ... Probably **/
    public static void message(EntityPlayer caster, String message){
        caster.sendMessage(new TextComponentString(message));
    }

    /** Allows easier chat simulation! **/
    public static void dialogue(EntityPlayer caster, String name, String message){
        caster.sendMessage(new TextComponentString("<" + name + ">: " + message));
    }

    public static void dialogue(EntityPlayer caster, TextFormatting formatting, String name, String message){
        caster.sendMessage(new TextComponentString(formatting + "<" + name + ">: " + message));
    }

    /** Returns Text Formatting color of element for you! **/
    public static TextFormatting getColor(Element element){
        return element.getColour().getColor();
    }


    /** Simple method to just play sound without any changes to it **/
    public static void playSound(EntityPlayer player, SoundEvent sound){
        player.playSound(sound, 0.7f, 1.0f);
    }

    /** Simple method to just play sound with a bit of changes to it **/
    public static void playSound(EntityPlayer player, SoundEvent sound, float volume){
        player.playSound(sound, volume, 1.0f);
    }

    /** Simple method to just play sound with changes to it **/
    public static void playSound(EntityPlayer player, SoundEvent sound, float volume, float pitch){
        player.playSound(sound, volume, pitch);
    }

    /** Simple method to just play sound with changes to it **/
    public static void playSound(EntityLivingBase player, SoundEvent sound, float volume, float pitch){
        player.playSound(sound, volume, pitch);
    }

    /** Simple method to just play sound without any changes to it **/
    public static void playSound(World world, BlockPos pos, SoundEvent sound){
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.MASTER, 0.7f, 1.0f, false);
    }

    /** Simple method to just play sound without any changes to it **/
    public static void playSound(World world, BlockPos pos, SoundEvent sound, boolean distanceDelay){
        world.playSound(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.MASTER, 1.0f, 1.0f, distanceDelay);
    }

    /** Aterna can Log some things you want **/
    public static void log(Object text){
        System.out.print(text);
    }

}
