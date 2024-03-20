package astramusfate.wizardry_tales.data.cap;

import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;

public interface ISoul {
 void addMana(EntityPlayer player, double add);
 void setMP(EntityPlayer player, double set);
 void addMaxMana(EntityPlayer player, double add);
 void setMaxMP(EntityPlayer player, double set);

 void setCooldown(EntityPlayer player, int set);
 void setCooldown(int set);
 void decreaseCooldown(EntityPlayer player, int decrease);

 int getCooldown();

 void addMana(double add);
 void setMP(double set);
 void addMaxMana(double add);
 void setMaxMP(double set);

 double getMP();
 double getMaxMP();

 int getMode();
 void setMode(EntityPlayer player, int mode);
 void setMode(int mode);

 String getRace();
 void setRace(EntityPlayer player, String race);
 void setRace(String race);

 void addStat(EntityPlayer player, int id, int amount);
 void setStat(EntityPlayer player, int id, int amount);
 void setStat(int id, int amount);
 int getStat(int id);


 void sync(EntityPlayer player);
 void syncMana(EntityPlayer player, double mana);
 void syncMaxMana(EntityPlayer player, double maxMana);
 void syncCooldown(EntityPlayer player, int cooldown);
 void syncMode(EntityPlayer player, int mode);
 void syncRace(EntityPlayer player, String race);
 void syncStat(EntityPlayer player, int id, int stat);

 void syncLearnedSpells(EntityPlayer player);
 void syncLearnSpell(EntityPlayer player, Spell spell);
 void setLearnedSpells(EntityPlayer player, Map<Spell, Integer> spellsLearned);

 void learnSpell(EntityPlayer player, Spell spell);
 int getLearned(Spell spell);

 Map<Spell, Integer> getLearnedSpells();
}