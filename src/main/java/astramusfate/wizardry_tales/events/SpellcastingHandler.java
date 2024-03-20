package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.items.TalesBauble;
import astramusfate.wizardry_tales.spells.TalesSpells;
import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import com.google.common.collect.Lists;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static astramusfate.wizardry_tales.events.EventsHandler.getCost;
import static astramusfate.wizardry_tales.events.EventsHandler.isValid;
import static astramusfate.wizardry_tales.events.SpellCreationHelper.getChosenSpellIndex;

@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class SpellcastingHandler extends EventsBase implements Lexicon {

    @SubscribeEvent
    public static void onInscribedItemHit(LivingHurtEvent event){
        if (event.getSource().getTrueSource() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getSource().getTrueSource();
            List<ItemStack> stacks = getChantedItems(player);
            for (ItemStack stack : stacks) {
                if (stack != null && checkCondition(stack, Lexicon.condition_hit)
                        && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null) {
                        if (tag.hasKey("spell") && tag.hasKey("condition")
                                && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_hit)) {
                            player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                            String msg = SpellCreation.getMsg(tag.getString("spell"));
                            List<String> words = SpellCreation.getSpell(msg);
                            String[] spell = words.toArray(new String[0]);
                            List<String> set = Lists.newArrayList();
                            try {
                                set.addAll(Arrays.asList(spell));
                                set.remove(Lexicon.par_shape);
                                set.remove(Lexicon.shape_inscribe);
                            } catch (Exception e) {
                                Aterna.messageBar(player, "Problem when casting!");
                            }

                            SpellCreation.createSpell(set, player, event.getEntityLiving(), !player.world.isRemote);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onInscribedItemDamage(LivingDamageEvent event){
        if (event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            List<ItemStack> stacks = getChantedItems(player);
            for (ItemStack stack : stacks) {
                if (stack != null && checkCondition(stack, Lexicon.condition_damage)
                        && !player.getCooldownTracker().hasCooldown(stack.getItem())){
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null) {
                        if (tag.hasKey("spell") && tag.hasKey("condition")
                                && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_damage)) {
                            if (stopUsageIf(tag, e -> (player.getHealth()/ player.getMaxHealth()) > e)) return;
                            player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                            String msg = SpellCreation.getMsg(tag.getString("spell"));
                            List<String> words = SpellCreation.getSpell(msg);
                            String[] spell = words.toArray(new String[0]);
                            List<String> set = Lists.newArrayList();
                            try {
                                set.addAll(Arrays.asList(spell));
                                set.remove(Lexicon.par_shape);
                                set.remove(Lexicon.shape_inscribe);
                            } catch (Exception e) {
                                Aterna.messageBar(player, "Problem when casting!");
                            }

                            SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInscribedItemTick(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START){
            EntityPlayer player = event.player;
            List<ItemStack> stacks = getChantedItems(player);
            if (Solver.doEvery(player, 1.0D)){
                onTick(player, stacks);
                onLightLevel(player, stacks);
            }
            if (Solver.doEvery(player, 0.5D)){
                onSneak(player, stacks);
            }
        }
    }

    @SubscribeEvent
    public static void onInscribedItemUse(PlayerInteractEvent.RightClickItem event){
        onItemUse(event);
        onSpell(event);
    }


    public static void onItemUse(PlayerInteractEvent event){
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;
        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        List<ItemStack> stacks = getChantedItems(player);
        for (ItemStack stack : stacks) {
            if (stack != null && checkCondition(stack, Lexicon.condition_use)
                    && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null) {
                    if (tag.hasKey("spell") && tag.hasKey("condition")
                            && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_use)) {
                        player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                        String msg = SpellCreation.getMsg(tag.getString("spell"));
                        List<String> words = SpellCreation.getSpell(msg);
                        String[] spell = words.toArray(new String[0]);
                        List<String> set = Lists.newArrayList();
                        try {
                            set.addAll(Arrays.asList(spell));
                            set.remove(Lexicon.par_shape);
                            set.remove(Lexicon.shape_inscribe);
                        } catch (Exception e) {
                            Aterna.messageBar(player, "Problem when casting!");
                        }

                        SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                    }
                }
            }
        }
    }

    private static void onSpell(PlayerInteractEvent event){
        if(event.getItemStack().getItem() instanceof ISpellCastingItem){
            EntityPlayer player = event.getEntityPlayer();
            ISoul soul = Mana.getSoul(player);
            Spell spell = WandHelper.getCurrentSpell(event.getItemStack());
            if (soul == null) return;
            if (spell == TalesSpells.chanting){
                WizardData.get(player).discoverSpell(spell);
                SpellcastingHandler.onSpellcast(player, event.getItemStack());
                // Implementation for Spellcasting for Chants!
                cancel(event);
            }
        }
    }

    public static void onSpellcast(EntityPlayer player, ItemStack stack) {
        if (stack != null && checkCondition(stack, Lexicon.condition_spellcast)
                && !player.getCooldownTracker().hasCooldown(stack.getItem())){
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null) {
                if (tag.hasKey("spell") && tag.hasKey("condition")
                        && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_spellcast)) {
                    player.getCooldownTracker().setCooldown(stack.getItem(), 20);
                    int id = getChosenSpellIndex(stack);
                    String msg;
                    if (tag.hasKey("spell" + id)) {
                        msg = SpellCreation.getMsg(tag.getString("spell" + id));
                    }else{
                        msg = SpellCreation.getMsg(tag.getString("spell"));
                    }
                    List<String> words = SpellCreation.getSpell(msg);
                    String[] spell = words.toArray(new String[0]);
                    List<String> set = Lists.newArrayList();
                    try {
                        set.addAll(Arrays.asList(spell));
                        set.remove(Lexicon.par_shape);
                        set.remove(Lexicon.shape_inscribe);
                    } catch (Exception e) {
                        Aterna.messageBar(player, "Problem when casting!");
                    }

                    SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                }
            }
        }
    }

    private  static void onSneak(EntityPlayer player, List<ItemStack> stacks) {
        if (!player.isSneaking()) return;
        for (ItemStack stack : stacks) {
            if (stack != null && checkCondition(stack, Lexicon.condition_sneak)
                    && !player.getCooldownTracker().hasCooldown(stack.getItem())){
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null) {
                    if (tag.hasKey("spell") && tag.hasKey("condition")
                            && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_sneak)) {
                        player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                        String msg = SpellCreation.getMsg(tag.getString("spell"));
                        List<String> words = SpellCreation.getSpell(msg);
                        String[] spell = words.toArray(new String[0]);
                        List<String> set = Lists.newArrayList();
                        try {
                            set.addAll(Arrays.asList(spell));
                            set.remove(Lexicon.par_shape);
                            set.remove(Lexicon.shape_inscribe);
                        } catch (Exception e) {
                            Aterna.messageBar(player, "Problem when casting!");
                        }

                        SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                    }
                }
            }
        }
    }

    private  static void onTick(EntityPlayer player, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null && checkCondition(stack, Lexicon.condition_tick)
                    && !player.getCooldownTracker().hasCooldown(stack.getItem())){
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null) {
                    if (tag.hasKey("spell") && tag.hasKey("condition")
                            && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_tick)) {
                        player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                        String msg = SpellCreation.getMsg(tag.getString("spell"));
                        List<String> words = SpellCreation.getSpell(msg);
                        String[] spell = words.toArray(new String[0]);
                        List<String> set = Lists.newArrayList();
                        try {
                            set.addAll(Arrays.asList(spell));
                            set.remove(Lexicon.par_shape);
                            set.remove(Lexicon.shape_inscribe);
                        } catch (Exception e) {
                            Aterna.messageBar(player, "Problem when casting!");
                        }

                        SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                    }
                }
            }
        }
    }

    private  static void onLightLevel(EntityPlayer player, List<ItemStack> stacks) {
        for (ItemStack stack : stacks) {
            if (stack != null && checkCondition(stack, Lexicon.condition_light_level)
                    && !player.getCooldownTracker().hasCooldown(stack.getItem())){

                float f = player.getBrightness();
                NBTTagCompound tag = stack.getTagCompound();

                if (tag != null) {
                    int value = Math.min(getParameter(tag), 100);
                    if (f <= Math.max(value, 0.0F)/100F && tag.hasKey("spell") && tag.hasKey("condition")
                            && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_light_level)) {
                        player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                        String msg = SpellCreation.getMsg(tag.getString("spell"));
                        List<String> words = SpellCreation.getSpell(msg);
                        String[] spell = words.toArray(new String[0]);
                        List<String> set = Lists.newArrayList();
                        try {
                            set.addAll(Arrays.asList(spell));
                            set.remove(Lexicon.par_shape);
                            set.remove(Lexicon.shape_inscribe);
                        } catch (Exception e) {
                            Aterna.messageBar(player, "Problem when casting!");
                        }

                        SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInscribedItemDayNight(TickEvent.PlayerTickEvent event){
        if (event.phase == TickEvent.Phase.START && Solver.doEvery(event.player, 5.0D)){
            EntityPlayer player = event.player;
            List<ItemStack> stacks = getChantedItems(player);
            for (ItemStack stack : stacks) {
                if (stack != null && (checkCondition(stack, Lexicon.condition_night) || checkCondition(stack, Lexicon.condition_day))
                        && !player.getCooldownTracker().hasCooldown(stack.getItem())){
                    NBTTagCompound tag = stack.getTagCompound();
                    if (tag != null) {
                        if (tag.hasKey("spell") && tag.hasKey("condition")) {
                            if ((player.world.provider.isDaytime() && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_day))
                                    || (!player.world.provider.isDaytime() && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_night))) {
                                player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                                String msg = SpellCreation.getMsg(tag.getString("spell"));
                                List<String> words = SpellCreation.getSpell(msg);
                                String[] spell = words.toArray(new String[0]);
                                List<String> set = Lists.newArrayList();
                                try {
                                    set.addAll(Arrays.asList(spell));
                                    set.remove(Lexicon.par_shape);
                                    set.remove(Lexicon.shape_inscribe);
                                } catch (Exception e) {
                                    Aterna.messageBar(player, "Problem when casting!");
                                }

                                SpellCreation.createSpell(set, player, player, !player.world.isRemote);
                            }
                        }
                    }
                }
            }
        }
    }

    //TODO: Add more conditions to work! ADD CAPE and etc.

    public static boolean checkCondition(ItemStack stack, String condition){
       return !(stack.getItem() instanceof IInscribed) ||
               (((IInscribed) stack.getItem()).canApplyCondition(condition));
    }

    public static boolean stopUsageIf(NBTTagCompound tag, Predicate<Integer> condition_to_meet){
        int parameter = getParameter(tag);
        if (parameter != Integer.MAX_VALUE){
            return condition_to_meet.test(parameter);
        }
        return false;
    }
    public static int getParameter(NBTTagCompound tag) {
        if (tag != null && tag.hasKey("parameter")) {
            return tag.getInteger("parameter");
        }
        return Integer.MAX_VALUE;
    }

    public static int getParameter(NBTTagCompound tag, String text) {
        if (tag != null && tag.hasKey(text)) {
            return tag.getInteger(text);
        }
        return Integer.MAX_VALUE;
    }

    public static List<ItemStack> getChantedItems(EntityPlayer player){
        List<ItemStack> stacks = Lists.newArrayList();
        ItemStack hands = Thief.getInHands(player,(s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyConditions())));
        if (hands != null) stacks.add(hands);

        List<ItemStack> artefacts = getEquippedArtefacts(player, BaubleType.RING, BaubleType.AMULET,
                BaubleType.CHARM, BaubleType.BELT, BaubleType.BODY, BaubleType.HEAD, BaubleType.TRINKET);
        for (ItemStack artefact : artefacts){
            if (artefact.getItem() instanceof IInscribed && ((IInscribed) artefact.getItem()).applyConditions()){
                stacks.add(artefact);
            }
        }
        return stacks;
    }

    public static List<ItemStack> getEquippedArtefacts(EntityPlayer player, BaubleType... types){

        List<ItemStack> artefacts = new ArrayList<>();

        for(BaubleType type : types){
            for(int slot : type.getValidSlots()){
                ItemStack stack = BaublesApi.getBaublesHandler(player).getStackInSlot(slot);
                NBTTagCompound tag = stack.getTagCompound();
                if (tag != null && (tag.hasKey("spell")
                        || stack.getItem() instanceof TalesBauble ||
                        stack.getItem() instanceof ItemArtefact)) artefacts.add(stack);
            }
        }

        return artefacts;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onCustomSpellRender(ItemTooltipEvent event){
        if(event.getEntityPlayer() != null && event.getEntityPlayer().world != null){
            ItemStack stack = event.getItemStack();

            if (stack.getTagCompound() == null) return;
            NBTTagCompound tag = stack.getTagCompound();
            if (!tag.hasKey("hidden") || !tag.getBoolean("hidden")) {
                if (stack.getItem() instanceof ISpellCastingItem) {
                    int slot = 0;
                    for (Spell ignored : WandHelper.getSpells(stack)) {
                        if (tag.hasKey("spell" + slot)) {
                            event.getToolTip().add("Spell " + (slot + 1) + ": " + TextFormatting.DARK_GRAY + TextFormatting.ITALIC +
                                    tag.getString("spell" + slot));
                        }
                        slot++;
                    }
                } else {
                    if (tag.hasKey("spell")) {
                        event.getToolTip().add("Spell: " + TextFormatting.DARK_GRAY + TextFormatting.ITALIC +
                                tag.getString("spell"));
                    }
                }
            }

            if (tag.hasKey("condition")){
                event.getToolTip().add("Condition: " + TextFormatting.DARK_GRAY + TextFormatting.ITALIC + Aterna.capitalize(tag.getString("condition")));
            }

            if (tag.hasKey("parameter")){
                event.getToolTip().add("Parameter: " + TextFormatting.DARK_GRAY + TextFormatting.ITALIC + ((int)tag.getFloat("parameter")));
            }
        }
    }


    private static final ResourceLocation bar_mana =
            new ResourceLocation(WizardryTales.MODID + ":textures/gui/bar_mana.png");

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onEntityShowcase(RenderGameOverlayEvent.Post event){
        if (event.getType() != RenderGameOverlayEvent.ElementType.FOOD) return;
        if (!Tales.mp.manaPool || !Tales.mp.manaPoolBar) return;

        EntityPlayerSP player = Minecraft.getMinecraft().player;

        if (Minecraft.getMinecraft().playerController.shouldDrawHUD()) {
            //event.setCanceled(true);
            ISoul soul = Mana.getSoul(player);
            if(soul == null) return;
            GlStateManager.enableBlend();
            Minecraft.getMinecraft().getTextureManager().bindTexture(bar_mana);
            int left = event.getResolution().getScaledWidth() / 2 + 91;
            int y = event.getResolution().getScaledHeight() - GuiIngameForge.right_height;
            GuiIngameForge.right_height += 10;
            double mana = soul.getMP();
            double maxMana = soul.getMaxMP();
            double manaValue = Math.floor(mana/maxMana * 20.0D);

            for (int i = 0; i < 10; ++i) {
                int idx = i * 2 + 1;
                int x = left - i * 8 - 9;

                // Draw Background
                DrawingUtils.drawTexturedRect(x, y, 0, 0, 9, 9,
                        27, 9);

                    if (idx < manaValue) // 9 - full, 18 - half-full, 0 - zero
                        DrawingUtils.drawTexturedRect(x, y, 9, 0,
                                9, 9, 27, 9);
                    else if(idx == manaValue){
                        DrawingUtils.drawTexturedRect(x, y, 18, 0,
                                9, 9, 27, 9);
                    }
            }

            int x = left - 8 - 9;
            Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(((int)Math.floor(mana))+ "", x + 20, y, 0x907FB8);

            Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
            GlStateManager.disableBlend();
        }
    }
}
