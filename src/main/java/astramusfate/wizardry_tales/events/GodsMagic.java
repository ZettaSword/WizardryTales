package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.*;
import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.api.wizardry.*;
import astramusfate.wizardry_tales.chanting.Chanting;
import astramusfate.wizardry_tales.chanting.SpellPart;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.TalesElement;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.StatIds;
import astramusfate.wizardry_tales.data.chanting.SpellParams;
import astramusfate.wizardry_tales.entity.construct.EntityMagic;
import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.entity.construct.EntityMagicScaled;
import astramusfate.wizardry_tales.entity.construct.EntityRitual;
import astramusfate.wizardry_tales.entity.construct.sigils.EntityMagicCircleVertical;
import astramusfate.wizardry_tales.entity.construct.sigils.chanting.EntityCircleAreaOnceCast;
import astramusfate.wizardry_tales.entity.construct.sigils.chanting.EntityCircleArray;
import astramusfate.wizardry_tales.entity.construct.sigils.chanting.EntityCustomSigil;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.construct.EntityForcefield;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import electroblob.wizardry.entity.construct.EntityScaledConstruct;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.tileentity.TileEntityTimer;
import electroblob.wizardry.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.*;


@Mod.EventBusSubscriber
public class GodsMagic extends SpellCreationHelper {

    public static final List<String> gods = Lists.newArrayList("blaze", "arcticus","selena",
            "ashura", "lux", "tenebria", "chronos", "nox", "yuna", "aterna");
    public static String allGods = "";

    @SubscribeEvent
    public static void spellCreationServerEvent(ServerChatEvent event) {
        String msg = getMsg(event.getComponent().getUnformattedText());
        EntityPlayerMP player = event.getPlayer();
        List<String> spell = getSpell(msg);

        if(!containsAny(spell, gods)) return;
        StringBuilder builder = new StringBuilder();
        for (String s : gods){
            builder.append(" ").append(s);
        }
        allGods = builder.toString();

        createSpell(spell, player, player, true, event.getMessage());
        Style style = event.getComponent().getStyle().setColor(TextFormatting.GOLD);
        event.setComponent(event.getComponent().setStyle(style));

        if (contains(spell, "hide hidden")){
            player.sendMessage(event.getComponent().setStyle(style));
            event.setCanceled(true);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void spellCreationClientEvent(ClientChatEvent event) {
        String msg = getMsg(event.getOriginalMessage());
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        List<String> spell = getSpell(msg);

        if(!containsAny(spell, gods)) return;

        createSpell(spell, player, player, false, event.getOriginalMessage());
    }

    public static void createSpell(List<String> words, Entity focal, @Nullable Entity target, boolean isServer){
        createSpell(words, focal, target, isServer, null);
    }

    /** Handles actions of spell:
     * <br>1) Handle targets externally, if you need.<br/>
     * <br>2) Use this<br/>**/
    public static void createSpell(List<String> words, Entity focal, @Nullable Entity target, boolean isServer, @Nullable String original) {
        World world = focal.world;
        if (world == null) return;

        String[] spell = words.toArray(new String[0]);
        SpellParams mods = new SpellParams();
        // Setting up parameters
        mods.world = world;
        mods.focal = focal;
        mods.isServer = isServer;
        mods.original = original;
        mods.target = target;

        mods.prayingTo = getAny(words, gods);

        EntityLivingBase caster = getCaster(focal);
        if (caster instanceof EntityPlayer){
            ISoul soul = Mana.getSoul((EntityPlayer) caster);
            if (soul != null) {
                mods.followingGod = soul.getStat(StatIds.god);
            }
        }

        mods.set = Lists.newArrayList();
        mods.set.addAll(Arrays.asList(spell));
        mods.stopCast = false;

        String previous = "";
        try {
                for (int i = 0; i < spell.length; i++) {
                    String next = getWord(spell, i, 1), next2 = getWord(spell, i, 2),
                            next3 = getWord(spell, i, 3), next4 = getWord(spell, i, 4), word = spell[i];

                    HashMap<String, String> keys = new HashMap<>();
                    keys.put("word", word);
                    keys.put("next", next);
                    keys.put("next2", next2);
                    keys.put("next3", next3);
                    keys.put("next4", next4);
                    keys.put("previous", previous);
                    mods.keys = keys;

                    // ? Raycast Entity && Raycast Block
                    SpellCreation.raycast(world, caster, mods.shape.val(), mods);

                    if (mods.stopCast) break;

                    // ? Position changes, etc.
                    SpellCreation.calcMods(caster, mods);

                    if (mods.pos == null) {
                        if (!mods.isRay)
                            mods.pos = focal.getPositionVector();
                        else return;
                    }
                    mods.pos.add(mods.change);

                    //if (mods.pos == null) mods.pos = focal.getPositionVector();

                    // ? If we apply cool shapes like Sigil, we could return!

                    // We remove this for a reason, to make Shapes be better.
                    try {
                        if (next.isEmpty()) mods.set.remove(word);
                        mods.set.remove(previous);
                    } catch (Exception ignore) {
                    }

                    SpellCreation.shaping(caster, mods);
                    if (!mods.condition.test(target)) break;
                    if (mods.stopCast) break;

                    // Spell-casting
                    applyActions(caster, mods);

                    previous = word;
                }
        }catch (Exception exception){
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Praying! ----");
            exception.printStackTrace();
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Praying! ----");

            if (caster instanceof EntityPlayer)
                Aterna.messageBar((EntityPlayer) caster, "Gods you ask do not hear your voice.");

            if (Tales.chanting.debug)
                throw new RuntimeException(exception);
        }

        SpellCreation.attachCastingVisual(world, caster, mods);
    }

    /** Apply actions to the target. **/
    public static void applyActions(@Nullable EntityLivingBase caster, SpellParams m){
        // Initial setup
        World world = m.focal.world;
        if (world == null) world = m.target.world;
        if (world == null) return;
        String word = m.keys.get("word");
        String next = m.keys.get("next");
        String next2 = m.keys.get("next2");
        String next3 = m.keys.get("next3");
        String next4 = m.keys.get("next4");
        String previous = m.keys.get("previous");

        Entity focal = m.focal;
        Entity target = m.target;
        if (target == null) return;
        Vec3d pos = m.pos;
        BlockPos spellBlock = new BlockPos(pos);

        float potency = m.potency.num();
        float duration = m.duration.num();
        float lifetime = m.lifetime.num();

        float dynamicDuration = m.duration.num() < 0 ? -1 : Solver.asTicks(m.duration.num());
        if (findIn(word, allGods)){
            m.prayingTo = word;
        }
        String prayingTo = m.prayingTo;
        int followingGod = m.followingGod;

        if (prayingTo.equals(god_necromancy)){
            if (findIn(word, "become") && findIn(next, "follower")){
                m.followingGod = getOrdinal(Element.NECROMANCY);
                if (caster instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) caster;
                    ISoul soul = Mana.getSoul(player);
                    if (soul != null){
                        soul.setStat(player, StatIds.god, getOrdinal(Element.NECROMANCY));
                    }
                }
            }

            if (findIn(word, "turn transform")) {
                if (findIn(next, "undead") && useMana(focal,100)){
                    if (caster != null && caster.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD){
                        if (caster instanceof EntityPlayer){
                            EntityPlayer player = (EntityPlayer) caster;
                            ISoul soul = Mana.getSoul(player);
                            if (soul != null && !Objects.equals(soul.getRace(), Race.undead)){
                                soul.setRace(player, Race.undead);
                                if (TalesArtemis.enabled()) {
                                    RaceListener.tick(player);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Vampirism integration
        if (prayingTo.equals(god_moon)) {
            if (findIn(word, "turn transform") && TalesVampirism.enabled()
                    && followingGod == getOrdinal(TalesElement.BLOOD) && caster != null) {
                    if (findIn(next, "vampire") && useAllMana(focal, 100, false)){
                        TalesVampirism.turnVampire(caster);
                    }
            }
        }
    }

    /**
     * @param spell - whole spell, containing all words
     * @param i - index on which word we are
     * @param x - index we will try to get
     * @return returns Word at x position further then i
     */
    public static String getWord(String[] spell, int i, int x){
        return i + x < spell.length ? spell[i + x] : "";
    }

    /**
     * @param spell - whole spell, containing all words
     * @param i - index on which word we are
     * @param x - index we will try to get
     * @return returns Word at x position further then i
     */
    public static String getWord(List<String> spell, int i, int x){
        return i + x < spell.size() ? spell.get(i + x) : "";
    }

    public static int getOrdinal(Element element){
        switch (element){
            case FIRE:return Element.FIRE.ordinal() + 1;
            case ICE:return Element.ICE.ordinal() + 1;
            case LIGHTNING:return Element.LIGHTNING.ordinal() + 1;
            case NECROMANCY:return Element.NECROMANCY.ordinal() + 1;
            case EARTH:return Element.EARTH.ordinal() + 1;
            case SORCERY:return Element.SORCERY.ordinal() + 1;
            case HEALING:return Element.HEALING.ordinal() + 1;
            default: return Element.MAGIC.ordinal() + 1;
        }
    }

    public static int getOrdinal(TalesElement element) {
        if (element == TalesElement.BLOOD) {
            return TalesElement.BLOOD.id();
        }
        return -1;
    }

    @Nullable
    public static EntityLivingBase getCaster(Entity focal){
        if (focal instanceof EntityLivingBase) return  (EntityLivingBase) focal;
        if(focal instanceof EntityMagic) return  ((EntityMagic) focal).getCaster();
        return null;
    }



}
