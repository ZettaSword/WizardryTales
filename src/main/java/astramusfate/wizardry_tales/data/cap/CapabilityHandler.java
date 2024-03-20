package astramusfate.wizardry_tales.data.cap;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.data.WizardData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class CapabilityHandler
{
   public static final ResourceLocation SOUL = new ResourceLocation(WizardryTales.MODID, "soul");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<Entity> event) {
        if(!(event.getObject() instanceof EntityPlayer)) return;
        event.addCapability(SOUL, new SoulProvider());
    }

    // This event is crucial to sync stuff with old body of player in another dimension or died one.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if(event.getEntityPlayer().world.isRemote) return; // No client allowed! We change this by packets

        EntityPlayer current = event.getEntityPlayer();
        ISoul soul = current.getCapability(SoulProvider.SOUL_CAP, null);
        if (soul == null){WizardryTales.log.warn("[Wizardry Tales]: Can't find new Soul!"); return;}

        EntityPlayer old = event.getOriginal();
        ISoul oldSoul = old.getCapability(SoulProvider.SOUL_CAP, null);
        if (oldSoul == null){WizardryTales.log.warn("[Wizardry Tales]: Can't find old Soul!"); return;}

        int MP = (int) Math.ceil(oldSoul.getMP());
        double maxMP = oldSoul.getMaxMP();

        if (current instanceof EntityPlayerMP) {
            soul.setMaxMP(current, maxMP);
            soul.setMP(current, MP);
            soul.setStat(current, StatIds.str_id, oldSoul.getStat(StatIds.str_id));
            soul.setStat(current, StatIds.con_id, oldSoul.getStat(StatIds.con_id));
            soul.setStat(current, StatIds.agi_id, oldSoul.getStat(StatIds.agi_id));
            soul.setStat(current, StatIds.int_id, oldSoul.getStat(StatIds.int_id));
            soul.setStat(current, StatIds.first_enter, oldSoul.getStat(StatIds.first_enter));
            soul.setStat(current, StatIds.status, oldSoul.getStat(StatIds.status));
        }
        soul.setCooldown(current, oldSoul.getCooldown());
        soul.setMode(current, oldSoul.getMode());
        soul.setLearnedSpells(current, oldSoul.getLearnedSpells());
        soul.setRace(current, oldSoul.getRace());

        NBTTagCompound tags = current.getEntityData();
        tags.setDouble("maxMP", soul.getMaxMP());
        tags.setDouble("MP", soul.getMP());
        tags.setInteger("cooldown", soul.getCooldown());
        tags.setInteger("mode", soul.getMode());
        tags.setString("race", soul.getRace());

        tags.setInteger("str", soul.getStat(StatIds.str_id));
        tags.setInteger("con", soul.getStat(StatIds.con_id));
        tags.setInteger("agi", soul.getStat(StatIds.agi_id));
        tags.setInteger("int", soul.getStat(StatIds.int_id));
        tags.setInteger("first_enter", soul.getStat(StatIds.first_enter));
        tags.setInteger("status", soul.getStat(StatIds.status));

        WizardData data = WizardData.get(current);
        if (data != null) data.discoverSpell(TalesSpells.chanting);
    }

    // This event is crucial to sync mana, races and stuff on player entering the world.
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogInSoul(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntity();
            ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
            if (soul == null) { WizardryTales.log.warn("[Wizardry Tales]: Can't find Soul!");return;}
            if (player instanceof EntityPlayerMP) {
                NBTTagCompound tags = player.getEntityData();
                if (tags.hasNoTags()) return;
                soul.setMaxMP(player, tags.getDouble("maxMP") == 0D ? soul.getMaxMP() : tags.getDouble("maxMP"));
                soul.setMP(player, tags.getDouble("MP"));
                soul.setCooldown(player, tags.getInteger("cooldown"));
                soul.setMode(player, tags.getInteger("mode"));
                if (tags.getString("race").isEmpty())soul.setRace(player, Race.human);
                else soul.setRace(player, tags.getString("race"));
                soul.setLearnedSpells(player, soul.getLearnedSpells());

                soul.setStat(player, StatIds.str_id, tags.getInteger("str"));
                soul.setStat(player, StatIds.con_id, tags.getInteger("con"));
                soul.setStat(player, StatIds.agi_id, tags.getInteger("agi"));
                soul.setStat(player, StatIds.int_id, tags.getInteger("int"));
                soul.setStat(player, StatIds.first_enter, tags.getInteger("first_enter"));
                soul.setStat(player, StatIds.status, tags.getInteger("status"));
            }

            if (Tales.addon.debug && player.world != null && !player.world.isRemote) {
                Aterna.dialogue(player, TextFormatting.GOLD + "Aterna", TextFormatting.GOLD + "Welcome back, it's Aterna!");

                if (!Objects.equals(soul.getRace(), ""))
                    Aterna.message(player, "Your race is: " + Aterna.capitalize(soul.getRace()));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onPlayerLogOutSoul(net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent event) {
        EntityPlayer player = event.player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if (soul == null) { WizardryTales.log.warn("[Wizardry Tales]: Can't find Soul!");return;}
        if (player instanceof EntityPlayerMP){
            NBTTagCompound tags = player.getEntityData();
            tags.setDouble("maxMP", soul.getMaxMP());
            tags.setDouble("MP", soul.getMP());
            tags.setInteger("cooldown", soul.getCooldown());
            tags.setInteger("mode", soul.getMode());
            tags.setString("race", soul.getRace());

            tags.setInteger("str", soul.getStat(StatIds.str_id));
            tags.setInteger("con", soul.getStat(StatIds.con_id));
            tags.setInteger("agi", soul.getStat(StatIds.agi_id));
            tags.setInteger("int", soul.getStat(StatIds.int_id));
            tags.setInteger("first_enter", soul.getStat(StatIds.first_enter));
            tags.setInteger("status", soul.getStat(StatIds.status));
        }
    }
}