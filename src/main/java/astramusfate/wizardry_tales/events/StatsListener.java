package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.StatIds;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.*;
import java.util.UUID;

public class StatsListener extends EventsBase {

    public static final UUID id = UUID.fromString("a3dae6b1-aaf9-4a86-a1c8-affb0e3e452c");

    @SubscribeEvent
    public static void statsTicking(TickEvent.PlayerTickEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            if (Solver.doEvery(event.player, 5)){
                EntityPlayer player = event.player;
                tick(player);
            }
            IAttributeInstance speed = event.player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);

            ISoul soul = Mana.getSoul(event.player);
            if (soul == null) return;
            // Agility
            if (event.player.isSprinting()) {
                checkAttributes(speed, "agility",
                        (getStat(soul, StatIds.agi_id, Tales.stats.agi_cost, Tales.stats.agi_max) / 100), Solver.MULTIPLY);
            } else {
                checkAttributes(speed, "agility",
                        0, Solver.MULTIPLY);
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void statsLogin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            tick(player);
        }
    }

    @SubscribeEvent
    public void statsQuit(PlayerEvent.PlayerLoggedOutEvent event){
        EntityPlayer player = event.player;
        tick(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void statsClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){
        tick(event.getEntityPlayer());
    }

    @SubscribeEvent
    public void statsHurtEvent(LivingHurtEvent event){
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            ISoul soul = Mana.getSoul(player);
            if (soul == null) return;
            event.setAmount(event.getAmount() * (1.0F - getStat2(soul, StatIds.con_id, Tales.stats.con_cost, 0.5F)));
            //tick(player);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void entityAddStats(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityLiving && Tales.addon.monsters_leveling) {
            EntityLiving living = (EntityLiving) event.getEntity();
            if (living.getEntityData().hasKey("lvl")) return;
            IAttributeInstance health = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
            IAttributeInstance attack = living.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
            float level = getLevel(living.getMaxHealth());
            checkAttributes(attack, "Attack Stat", Solver.limit(level/10F, 10), Solver.MULTIPLY);
            checkAttributes(health, "Health Stat", Solver.limit(level/10F, 100), Solver.MULTIPLY);
            living.setHealth(living.getHealth()-1);
            living.setHealth(living.getHealth());
            int lvl = Math.round(level);
            if (lvl < 1) lvl = 1;
            living.getEntityData().setInteger("lvl", lvl);
            living.setCustomNameTag(TextFormatting.RESET + "" + living.getDisplayName() + " Lvl." + lvl);
            living.setHealth(living.getHealth());

        }
    }

    public static void tick(EntityPlayer player){
        ISoul soul = Mana.getSoul(player);
        if (soul == null) return;


        IAttributeInstance health = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance attack = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
        IAttributeInstance armor = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR);
        IAttributeInstance knockback = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        IAttributeInstance speed = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);

        // Constitution
        checkAttributes(player, health, "constitution",
                Math.min((soul.getStat(StatIds.con_id)/Tales.stats.con_cost), Tales.stats.con_max), Solver.MULTIPLY);

        checkAttributes(knockback, "constitution",
                Math.min((float) soul.getStat(StatIds.con_id) /Tales.stats.con_cost,
                        0.5F * ((float) StatIds.con_id /Tales.stats.con_cost)/Tales.stats.con_max), Solver.MULTIPLY);

        // Strength
        checkAttributes(attack, "strength",
                getStat(soul, StatIds.str_id, Tales.stats.str_cost, Tales.stats.str_max), Solver.ADD);

        // Strength
        checkAttributes(attack, "strength",
                getStat(soul, StatIds.str_id, Tales.stats.str_cost, Tales.stats.str_max), Solver.ADD);
    }

    public static void nullAttributes(EntityPlayer player){
        for (IAttributeInstance instance : player.getAttributeMap().getAllAttributes()) {
            if (instance.getModifier(id) != null) instance.removeModifier(id);
        }
    }

    public static boolean isAir(EntityPlayer player){
       return  player.world.getBlockState(player.getPosition().up(1)).getBlock() == Blocks.AIR;
    }

    public static AttributeModifier newMod(String name, double amount, int operation){
        return new AttributeModifier(id, name, amount, operation);
    }

    /** Specifically to force update on client. Eh. **/
    public static void checkAttributes(EntityPlayer player, IAttributeInstance check, String name,  double value, int operation){
        AttributeModifier mod = newMod(name, value, operation);
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
            if (check.getAttribute() == SharedMonsterAttributes.MAX_HEALTH){
                player.setHealth(player.getHealth() -1);
                player.setHealth(player.getHealth());
            }
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() != value){
                    check.removeModifier(id);
                    check.applyModifier(mod);
                    if (check.getAttribute() == SharedMonsterAttributes.MAX_HEALTH){
                        player.setHealth(player.getHealth() -1);
                        player.setHealth(player.getHealth());
                    }
                }
            }
        }
    }


    public static void checkAttributes(IAttributeInstance check, String name,  double value, int operation){
        AttributeModifier mod = newMod(name, value, operation).setSaved(true);
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() != value){
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }
            }else{
                check.applyModifier(mod);
            }
        }
    }

    public static void checkAttributes(IAttributeInstance check, String name,  double value, int operation, double speed){
        AttributeModifier mod = newMod(name, value, operation).setSaved(true);
        double difference;
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() > value){
                    difference=par.getAmount() - value;
                    mod = newMod(name, par.getAmount() - difference/speed, operation).setSaved(true);
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }else if(par.getAmount() < value){
                    difference=value - par.getAmount();
                    mod = newMod(name, par.getAmount() + difference/speed, operation).setSaved(true);
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }
            }
        }
    }
    public static float getStat(ISoul soul, int id, int cost, float max){
        return Math.min(((float) soul.getStat(id) /cost), max);
    }
    public static float getStat2(ISoul soul, int id, int cost, float max){
       return Math.min(((float) soul.getStat(id) /cost)/10F, max);
    }

    public static float getLevel(float health){
        float level = 1.0F;
        float cost = 4F;
        if (health > cost){
            level = Solver.randFloat(health/cost, (health/cost)*2F);
        }
        return level;
    }
}
