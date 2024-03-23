package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.StatIds;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.patchouli.common.item.ItemModBook;

/** Stat events are divided on two categories: Triggers - things that add/remove stats, and Actions - things that use stats values. **/
@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class StatEvents extends EventsBase {

    private static final ResourceLocation ui =
            new ResourceLocation(WizardryTales.MODID + ":textures/gui/status.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onStatusUI(RenderGameOverlayEvent.Post event){
        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;
        if (!Tales.stats.allow_status) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = Mana.getSoul(player);
        ItemStack stack = player.getHeldItemMainhand();
        if(soul == null || stack.getItem() != TalesItems.status_page) return;

        //event.setCanceled(true);
        GlStateManager.enableBlend();
        Minecraft.getMinecraft().getTextureManager().bindTexture(ui);
        int left = event.getResolution().getScaledWidth() / 2 - 70;
        int y = event.getResolution().getScaledHeight() - 250;
        double str = soul.getStat(StatIds.str_id);
        double con = soul.getStat(StatIds.con_id);
        double agi = soul.getStat(StatIds.agi_id);
        double intell = soul.getStat(StatIds.int_id);

        DrawingUtils.drawTexturedRect(left, y, 0, 0, 145, 180,
                145, 180);

        Arcanist.push();
        Minecraft.getMinecraft().fontRenderer.drawString(player.getDisplayNameString(), left + 45, y + 22, 0x0D1228);
        Minecraft.getMinecraft().fontRenderer.drawString("Health Points: " + (int)(player.getHealth()) + "/" + (int)((player.getMaxHealth())), left + 20, y + 50, 0x902C2E);
        Minecraft.getMinecraft().fontRenderer.drawString("Mana Points: " + (int)(Math.floor(soul.getMP())) + "/" + (int)(Math.floor(soul.getMaxMP())), left + 20, y + 70, 0x1D2A60);

        Minecraft.getMinecraft().fontRenderer.drawString(("Strength: " + (int)(1 + (str/Tales.stats.str_cost))), left + 20, y + 90, 0x975D31);
        Minecraft.getMinecraft().fontRenderer.drawString("Constitution: " + (int)(1 + (con/Tales.stats.con_cost)), left + 20, y + 100, 0x902C2E);
        Minecraft.getMinecraft().fontRenderer.drawString("Agility: " + (int)(1 + (agi/Tales.stats.agi_cost)), left + 20, y + 110, 0x329050);
        Minecraft.getMinecraft().fontRenderer.drawString("Intelligence: " + (int)(4 + (intell/Tales.stats.int_cost)), left + 20, y + 120, 0x5C5690);
        Arcanist.pop();

        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        GlStateManager.disableBlend();
    }

    // TRIGGERS

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTickTrigger(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START && event.player != null) {
            EntityPlayer player = event.player;
            ISoul soul = Mana.getSoul(player);
            if (soul != null) {
                if (Solver.doEvery(player, 5) && player.isSprinting()) {
                    soul.addStat(player, StatIds.agi_id, 1);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttackTrigger(AttackEntityEvent event){
        EntityPlayer player = event.getEntityPlayer();
        ISoul soul = Mana.getSoul(player);
        if (soul != null) {
            soul.addStat(player, StatIds.str_id, 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamageGetTrigger(LivingDamageEvent event){
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            ISoul soul = Mana.getSoul(player);
            if (soul != null) {
                if (event.getAmount() >= 2.0F){
                    soul.addStat(player, StatIds.con_id, 1);
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDamageDoTrigger(LivingDamageEvent event){
        if (event.getSource().getTrueSource() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            ISoul soul = Mana.getSoul(player);
            if (soul != null) {
                if (event.getSource() == DamageSource.MAGIC){
                    event.setAmount(event.getAmount() + Math.max(soul.getStat(StatIds.int_id)/Tales.stats.int_cost, Tales.stats.int_max));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSpellcastTrigger(SpellCastEvent event){
        if (event.getCaster() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            if (soul != null) {
                soul.addStat(player, StatIds.int_id, 1);
            }
        }
    }

    @SubscribeEvent
    public static void onFirstEnter(PlayerEvent.PlayerLoggedInEvent event){
        EntityPlayer player = event.player;
        if(!player.world.isRemote) {
            ISoul soul = Mana.getSoul(player);
            // First enter variable
            if (soul != null && soul.getStat(StatIds.first_enter) == 0) {
                if (Tales.addon.first_enter_dialogue) {
                    Aterna.dialogue(player, "???", "Oh, you're new here!~");
                    Aterna.dialogue(player, "???", "My name is Tenebria. Not sure if you understand this language");
                    Aterna.dialogue(player, "Tenebria", "You were reincarnated in this world after your death in previous one");
                    Aterna.dialogue(player, "Tenebria", "I hope you'll enjoy your time in this new world, bye~");
                }
                if (Tales.addon.first_enter_status){
                    Aterna.dialogue(player, "Tenebria", "You'll need status card to see your stats");
                    Thief.addItem(player, new ItemStack(TalesItems.status_page, 1));
                }
                if (Tales.addon.first_enter_book || Tales.addon.first_enter_book_ebw){
                    Aterna.dialogue(player, "Tenebria", "Oh, and also here's small gift from me! :)");
                    if (Tales.addon.first_enter_book) {
                        if (WizardryTales.canCompat("patchouli"))
                            Thief.addItem(player, ItemModBook.forBook("wizardry_tales:tales"));
                    }
                    if (Tales.addon.first_enter_book_ebw) {
                        Thief.addItem(player, new ItemStack(WizardryItems.wizard_handbook, 1));
                    }
                    ItemStack cookie = new ItemStack(TalesItems.tenebria_cookie, 1);
                    cookie.setStackDisplayName("Special Cookie");
                    Thief.addItem(player, cookie);
                }

                soul.setStat(player, StatIds.first_enter, 1);
            }
        }
    }

    // ACTIONS
}
