package astramusfate.wizardry_tales.data.cap;

import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.Lists;
import electroblob.wizardry.spell.Spell;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import java.util.*;

public class SoulStorage implements Capability.IStorage<ISoul>
{
   @Override
   public NBTBase writeNBT(Capability<ISoul> capability, ISoul soul, EnumFacing side) {
       NBTTagCompound tag = new NBTTagCompound();

       tag.setDouble("MP", soul.getMP());
       tag.setDouble("maxMP", soul.getMaxMP());
       tag.setInteger("cooldown", soul.getCooldown());
       tag.setInteger("mode", soul.getMode());
       tag.setString("race", soul.getRace());

       tag.setInteger("first_enter", soul.getStat(StatIds.first_enter));
       tag.setInteger("status", soul.getStat(StatIds.status));
       tag.setInteger("str", soul.getStat(StatIds.str_id));
       tag.setInteger("con", soul.getStat(StatIds.con_id));
       tag.setInteger("agi", soul.getStat(StatIds.agi_id));
       tag.setInteger("int", soul.getStat(StatIds.int_id));

       // Saving the Spells
       List<Spell> keys = Lists.newArrayList(soul.getLearnedSpells().keySet());
       int[] spells = new int[keys.size()];
       for (int i = 0; i < keys.size(); i++) {
           spells[i] = keys.get(i).networkID();
       }
       tag.setIntArray("spells", spells);

       // Saving the Values of Spells
       List<Integer> values = Lists.newArrayList(soul.getLearnedSpells().values());
       spells = new int[values.size()];
       for (int i = 0; i < values.size(); i++) {
           spells[i] = values.get(i);
       }

       tag.setIntArray("data", spells);
    return tag;
   }

   @Override
   public void readNBT(Capability<ISoul> capability, ISoul soul, EnumFacing side, NBTBase nbt) {
       NBTTagCompound tag = (NBTTagCompound)nbt;

       soul.setMP(tag.getDouble("MP"));
       soul.setMaxMP(tag.getDouble("maxMP"));
       soul.setCooldown(tag.getInteger("cooldown"));
       soul.setMode(tag.getInteger("mode"));
       soul.setRace(tag.getString("race"));

       soul.setStat(StatIds.first_enter, tag.getInteger("first_enter"));
       soul.setStat(StatIds.status, tag.getInteger("status"));
       soul.setStat(StatIds.str_id, tag.getInteger("str"));
       soul.setStat(StatIds.con_id, tag.getInteger("con"));
       soul.setStat(StatIds.agi_id, tag.getInteger("agi"));
       soul.setStat(StatIds.int_id, tag.getInteger("int"));


       // Loading Spells with their data

       Map<Spell, Integer> map = new HashMap<>();
       for (int i : tag.getIntArray("spells")){
           map.put(Spell.byNetworkID(i), 0);
       }

       int[] intArray = tag.getIntArray("data");
       for (int i = 0; i < intArray.length; i++) {
           int data = intArray[i];
           map.replace((Spell) map.keySet().toArray()[i], data);
       }

       soul.setLearnedSpells(null, map);
       // Done!

   }
}