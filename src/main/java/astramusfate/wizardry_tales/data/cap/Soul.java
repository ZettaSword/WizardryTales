package astramusfate.wizardry_tales.data.cap;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.PacketMagic;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.packets.*;
import electroblob.wizardry.spell.None;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class Soul implements ISoul {
 private double MP = 0.0D;
 private double maxMP = Tales.mp.initial;
 private int cooldown = 0;
 private int mode = 0;
 private String race = "human";

 /** Strength increases melee damage. **/
 private int strength = 0;
 /** Constitution increases max hp. **/
 private int constitution = 0;
 /** Agility increases movement speed. **/
 private int agility = 0;
 /** Intelligence decreases spells mana cost. **/
 private int intelligence = 0;
 private int first_enter = 0;
 private int status = 0;
 private Map<Spell, Integer> learnedSpells = new HashMap<>();

 @Override
 public void addMana(double add) {
     MP=Math.min(Math.max(MP + add, 0), maxMP);
 }
 @Override
 public void addMana(EntityPlayer player, double add) { MP=Math.min(Math.max(MP + add, 0), maxMP); syncMana(player, MP);
 }

 @Override
 public void setMP(double set) { MP=Math.min(Math.max(set, 0), maxMP); }

 @Override
 public void setMP(EntityPlayer player, double set) { MP=Math.min(Math.max(set, 0), maxMP); syncMana(player, MP);
 }

 @Override
 public void setMaxMP(double set) { maxMP =Math.min(set, Tales.mp.max);}

 @Override
 public void setMaxMP(EntityPlayer player, double set) { maxMP = Math.min(set, Tales.mp.max); syncMaxMana(player, maxMP);
 }

 @Override
 public void addMaxMana(double add) { maxMP =Math.min(Math.max(maxMP + add, 0), Tales.mp.max);}

 @Override
 public void addMaxMana(EntityPlayer player, double add) { maxMP=Math.min(Math.max(maxMP + add, 0), Tales.mp.max);
  syncMaxMana(player, maxMP);
 }

 @Override
 public double getMP() { return MP; }

 @Override
 public double getMaxMP() { return maxMP; }
 @Override
 public int getMode() {
  return mode;
 }

 @Override
 public void setMode(EntityPlayer player, int mode) {
   this.mode = mode; syncMode(player, mode);
 }

 @Override
 public void setMode(int mode) {
  this.mode = mode;
 }

 @Override
 public String getRace() {
  return this.race;
 }

 @Override
 public void setRace(EntityPlayer player, String race) {
    this.race=race; syncRace(player, race);
 }

 @Override
 public void setRace(String race) {
  this.race=race;
 }

 @Override
 public void addStat(EntityPlayer player, int id, int amount) {
     switch (id) {
         case StatIds.str_id:
             strength = Math.max(strength + amount, 0);
             syncStat(player, id, strength);
             break;
         case StatIds.con_id:
             constitution = Math.max(constitution + amount, 0);
             syncStat(player, id, constitution);
             break;
         case StatIds.agi_id:
             agility = Math.max(agility + amount, 0);
             syncStat(player, id, agility);
             break;
         case StatIds.int_id:
             intelligence = Math.max(intelligence + amount, 0);
             syncStat(player, id, intelligence);
             break;
         case StatIds.first_enter:
             first_enter = Math.max(first_enter + amount, 0);
             syncStat(player, id, first_enter);
             break;
         case StatIds.status:
             status = Math.max(status + amount, 0);
             syncStat(player, id, status);
             break;
     }
 }

 @Override
 public void setStat(@Nullable EntityPlayer player, int id, int amount) {
     switch (id) {
         case StatIds.str_id:
             strength = Math.max(amount, 0);
             syncStat(player, id, strength);
             break;
         case StatIds.con_id:
             constitution = Math.max(amount, 0);
             syncStat(player, id, constitution);
             break;
         case StatIds.agi_id:
             agility = Math.max(amount, 0);
             syncStat(player, id, agility);
             break;
         case StatIds.int_id:
             intelligence = Math.max(amount, 0);
             syncStat(player, id, intelligence);
             break;
         case StatIds.first_enter:
             first_enter = amount;
             syncStat(player, id, first_enter);
             break;
     }
 }

 @Override
 public void setStat(int id, int amount) {
  setStat(null, id, amount);
 }

 @Override
 public int getStat(int id) {
   switch (id){
    case StatIds.str_id: return this.strength;
    case StatIds.con_id: return this.constitution;
    case StatIds.agi_id: return this.agility;
    case StatIds.int_id: return this.intelligence;
    case StatIds.first_enter: return this.first_enter;
   }
  return -1;
 }

 @Override
 public void setCooldown(EntityPlayer player, int set) {
   cooldown = set; syncCooldown(player, cooldown);
 }

 @Override
 public void setCooldown(int set) {
  cooldown = set;
 }

 @Override
 public void decreaseCooldown(@Nullable EntityPlayer player, int decrease) {
  cooldown-=decrease; syncCooldown(player, cooldown);
 }

 @Override
 public int getCooldown() {
  return cooldown;
 }

 @Override
 public void sync(EntityPlayer player) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if (soul == null){
    WizardryTales.log.warn("[TALES SOUL]: LOST SOUL " + player.getGameProfile().getName()); //TODO: Remove after testing.
    return;
   }
   PacketMagic.net.sendTo(new PacketSoulMana(this.MP), (EntityPlayerMP) player);
   PacketMagic.net.sendTo(new PacketSoulMaxMana(this.maxMP), (EntityPlayerMP) player);
  }
 }

 @Override
 public void syncMana(EntityPlayer player, double mana) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   assert soul != null;
   PacketMagic.net.sendTo(new PacketSoulMana(mana), (EntityPlayerMP) player);
  }
 }

 @Override
 public void syncMaxMana(EntityPlayer player, double maxMana) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   assert soul != null;
   PacketMagic.net.sendTo(new PacketSoulMaxMana(maxMana), (EntityPlayerMP) player);
  }
 }

 @Override
 public void syncCooldown(EntityPlayer player, int cooldown) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    PacketMagic.net.sendTo(new PacketCastingRingCooldown(cooldown), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void syncMode(EntityPlayer player, int mode) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    PacketMagic.net.sendTo(new PacketSyncMode(mode), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void syncRace(EntityPlayer player, String race) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    PacketMagic.net.sendTo(new PacketSyncRace(race), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void syncStat(EntityPlayer player, int id, int stat) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    PacketMagic.net.sendTo(new PacketSyncStat(id, stat), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void syncLearnedSpells(EntityPlayer player) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    if (Tales.addon.learning) PacketMagic.net.sendTo(new PacketSyncLearning(soul.getLearnedSpells()), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void syncLearnSpell(EntityPlayer player, Spell spell) {
  if(player instanceof EntityPlayerMP){
   ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
   if(soul != null) {
    if (Tales.addon.learning) PacketMagic.net.sendTo(new PacketLearnSpell(spell), (EntityPlayerMP) player);
   }
  }
 }

 @Override
 public void setLearnedSpells(EntityPlayer player, Map<Spell, Integer> spellsLearned) {
  this.learnedSpells=spellsLearned;
  this.syncLearnedSpells(player);
 }

 @Override
 public void learnSpell(EntityPlayer player, Spell spell) {
  if(learnedSpells == null){
   learnedSpells = new HashMap<>();
  }
  // The 'none' spell cannot be discovered
  if(spell instanceof None) return;

  // Tries to add the spell to the list of discovered spells, and returns false if it was already present
  if (learnedSpells.containsKey(spell)){
   learnedSpells.replace(spell, learnedSpells.get(spell) + 1);
  }else{
   learnedSpells.put(spell, 1);
  }

  syncLearnSpell(player, spell);
 }

 @Override
 public int getLearned(Spell spell) {
  if (!Tales.addon.learning) return 11;
   if (this.learnedSpells == null) this.learnedSpells = new HashMap<>();
  if (!this.learnedSpells.containsKey(spell)) {
   this.learnedSpells.put(spell, 1);
  }
  return this.learnedSpells.get(spell);
 }

 @Override
 public Map<Spell, Integer> getLearnedSpells() {
  return this.learnedSpells;
 }
}