package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.*;
import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.api.wizardry.ParticleCreator;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.chanting.Chanting;
import astramusfate.wizardry_tales.chanting.SpellPart;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
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
import electroblob.wizardry.entity.living.*;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryBlocks;
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
public class SpellCreation extends SpellCreationHelper {
    public static final String keyword = "mana";

    @SubscribeEvent
    public static void spellCreationServerEvent(ServerChatEvent event) {
        String msg = getMsg(event.getComponent().getUnformattedText());
        EntityPlayerMP player = event.getPlayer();
        List<String> spell = getSpell(msg);

        if(!containsAny(spell, Arrays.asList(keyword.toLowerCase(Locale.ROOT).split(" ")))) return;

        createSpell(spell, player, player, true, event.getMessage());
        Style style = event.getComponent().getStyle().setColor(TextFormatting.GOLD);
        event.setComponent(event.getComponent().setStyle(style));

        if (contains(spell, "hide hidden")){
            event.setComponent(new TextComponentString(""));
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void spellCreationClientEvent(ClientChatEvent event) {
        String msg = getMsg(event.getOriginalMessage());
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        List<String> spell = getSpell(msg);

        if(!containsAny(spell, Arrays.asList(keyword.toLowerCase(Locale.ROOT).split(" ")))) return;

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

        EntityLivingBase caster = getCaster(focal);

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
                    raycast(world, caster, mods.shape.val(), mods);

                    if (mods.stopCast) break;

                    // ? Position changes, etc.
                    calcMods(caster, mods);

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

                    shaping(caster, mods);
                    if (!mods.condition.test(target)) break;
                    if (mods.stopCast) break;

                    // Spell-casting
                    applyActions(caster, mods);

                    previous = word;
                }
        }catch (Exception exception){
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");
            exception.printStackTrace();
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");

            if (caster instanceof EntityPlayer)
                Aterna.messageBar((EntityPlayer) caster, "Spell is broken!");

            if (Tales.chanting.debug)
                throw new RuntimeException(exception);
        }

        attachCastingVisual(world, caster, mods);
    }
    
    public static int stopCasting(String[] spell, int spellIndex){
        for (int i = spellIndex; i < spell.length; i++) {
            String next = getWord(spell, i, 1), word = spell[i];
            if (findIn(word, "new") && findIn(next, "spell")){
                if (i+2 < spell.length) return i+2;
            }
        }
        return -1;
    }

    public static void shaping(@Nullable EntityLivingBase caster, SpellParams m){
        String shape = m.shape.val();
        World world = m.world;
        String word = m.keys.get("word");
        String next = m.keys.get("next");
        String next2 = m.keys.get("next2");
        String next3 = m.keys.get("next3");
        String next4 = m.keys.get("next4");
        List<String> set = m.set;
        if (set == null) return;
        if (m.pos == null){
            if (!m.isRay)
                m.pos = m.focal.getPositionVector();
            else return;
        }

        double dist = m.pos.distanceTo(m.focal.getPositionVector());
        BlockPos pos = new BlockPos(m.pos);

        int dynamicLifetime = m.lifetime.num() < 0 ? -1 : Solver.asTicks(m.lifetime.num());

        // ? Area
        if (findIn(shape, shape_area) && useMana(m.focal, dist)) {
            m.set.remove(shape);
            EntityCircleAreaOnceCast entity = new EntityCircleAreaOnceCast(world, set);
            entity.setLocation(m.element.func_176610_l());
            entity.setCaster(caster);
            if (m.canAlly) entity.onlyAllies();
            entity.filter = m.filter;
            entity.words = m.set;
            entity.setLifetime(Solver.asTicks(1));
            entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, m.focal.rotationYaw, m.focal.rotationPitch);
            entity.setSizeMultiplier((m.size.num()));
            Tenebria.create(world, entity);
            m.stopCast=true;
            return;
        }

        // ? Array
        if (findIn(shape, shape_array) && useMana(m.focal, dist)) {
            m.set.remove(shape);
            EntityCircleArray entity = new EntityCircleArray(world, set);
            entity.setLocation(m.element.func_176610_l());
            entity.setCaster(caster);
            if (m.canAlly) entity.onlyAllies();
            entity.filter = m.filter;
            entity.words = m.set;
            entity.setLifetime(dynamicLifetime);
            entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, m.focal.rotationYaw, m.focal.rotationPitch);
            entity.setSizeMultiplier((m.size.num()));
            Tenebria.create(world, entity);
            m.stopCast=true;
            return;
        }

        // ? Sigil
        if (findIn(shape, shape_sigil) && useMana(m.focal, dist)) {
            m.set.remove(shape);
            EntityCustomSigil entity = new EntityCustomSigil(world, set);
            entity.setLocation(m.element.func_176610_l());
            entity.hasOwner=m.hasOwner;
            entity.setCaster(caster);
            entity.setLifetime(dynamicLifetime);
            if (m.canAlly) entity.onlyAllies();
            entity.filter = m.filter;
            entity.words = m.set;
            entity.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, m.focal.rotationYaw, m.focal.rotationPitch);
            entity.setSizeMultiplier(m.size.num());
            if(world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox()).isEmpty())
                Tenebria.create(world, entity);
            m.stopCast=true;
            return;
        }

        Number number = getInteger(next, Integer.MAX_VALUE);
        if (number.floatValue() == Integer.MAX_VALUE) number = 0;

        // ? Inscribe
        if (findInSmart(shape, shape_inscribe) && caster instanceof EntityPlayer && m.original != null){
            ItemStack stack = Thief.getInHands((EntityPlayer) caster);
            if (stack == null) return;

            ItemStack reagent = Thief.getItem((EntityPlayer) caster, p -> p.getItem() == TalesItems.chanting_stone,
                    new ItemStack(TalesItems.chanting_stone));
            if (reagent == null){
                m.stopCast=true;
                Aterna.messageBar((EntityPlayer) caster, "You need one Chanting Stone in inventory!");
                return;
            }
            reagent.shrink(1);

            NBTTagCompound tag = getOrCreateTagCompound(stack);
            if (stack.getItem() instanceof ISpellCastingItem) {
                int id = getChosenSpellIndex(stack);
                tag.setString("spell" + id, m.original);
            }else{
                tag.setString("spell", m.original);
            }

            if (containsAny(set, Lists.newArrayList("hide", "hidden"))){
                tag.setBoolean("hidden", true);
            }

            Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
            m.stopCast=true;
            return;
        }

        // ? Adjust
        if (findInSmart(shape, shape_adjust) && caster instanceof EntityPlayer && m.original != null &&
                findIn(m.keys.get("previous"), "add set")
                && (findIn(word, "parameter") || findIn(word, "condition"))){
            if (findIn(word, "parameter")) {
                ItemStack stack = Thief.getInHands((EntityPlayer) caster, (s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyParameters())));
                if (stack == null) return;
                NBTTagCompound tag = getOrCreateTagCompound(stack);
                if ((number.intValue() == 0 && tag.hasKey("parameter"))) tag.removeTag("parameter");
                else tag.setFloat("parameter", number.floatValue());

                Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
                return;
            }

            if (findIn(word, "condition")) {
                try {
                    set.remove("add");
                    set.remove("condition");
                }catch (Exception ignore){}
                ItemStack stack = Thief.getInHands((EntityPlayer) caster,(s -> !(s.getItem() instanceof IInscribed) || (s.getItem() instanceof IInscribed && ((IInscribed)s.getItem()).applyConditions())));
                if (stack == null) return;
                ItemStack reagent = Thief.getItem((EntityPlayer) caster, p -> p.getItem() == TalesItems.chanting_stone,
                        new ItemStack(TalesItems.chanting_stone));
                if (reagent == null){
                    Aterna.messageBar((EntityPlayer) caster, "You need one Chanting Stone in inventory!");
                    return;
                }
                reagent.shrink(1);
                String condition = findWithin(conditions, "", next, next2, next3, next4);
                if (!condition.isEmpty() && SpellcastingHandler.checkCondition(stack, condition)) {
                    NBTTagCompound tag = getOrCreateTagCompound(stack);
                    tag.setString("condition", condition);
                }

                Wizard.castParticles(world, Element.MAGIC, caster.getPositionVector(), 18);
            }
        }

        // ? Finds nearest entity.
        if (findIn(shape, shape_nearest)){
            double radius = m.range.num();
            List<Entity> targets = EntityUtils.getEntitiesWithinRadius(radius, m.focal.posX, m.focal.posY, m.focal.posZ, world, Entity.class);
            if (targets.isEmpty()) return;
            // Sort by distance from the origin for consistency in ordering for spells with a limit
            targets.sort(Comparator.comparingDouble(e -> e.getDistanceSq(m.focal.posX, m.focal.posY, m.focal.posZ)));
            if (caster != null){
                targets.removeIf(e -> (AllyDesignationSystem.isAllied(caster, (EntityLivingBase) e) && !m.canAlly));
                targets.removeIf(e -> e == caster);
            }
            m.target = targets.get(0);
            m.isRay = true;
            m.castingTargeting = TARGET_ENTITIES;
        }
    }

    public static void raycast(World world, @Nullable EntityLivingBase caster, String shape, SpellParams m){
        // ? Raycast Entity
        if (findIn(shape, shape_entity)){
            boolean finalCanAlly = m.canAlly;
            RayTraceResult ray = Solver.standardEntityRayTrace(world, m.focal, m.range.num(), false,
                    e -> {
                        if (e == caster || e == m.focal){
                            return true;
                        }
                        if (e instanceof EntityLivingBase) {
                            return (AllyDesignationSystem.isAllied(caster, (EntityLivingBase) e) && !finalCanAlly)
                                    || (((EntityLivingBase) e).deathTime > 0);
                        }
                        return false;
                    });
            if ((ray != null && ray.typeOfHit == RayTraceResult.Type.ENTITY && ray.entityHit != null)) {
                m.target = ray.entityHit;
                m.pos = ray.entityHit.getPositionVector();
            }else {
                m.stopCast=true;
            }
            m.isRay = true;
            m.castingTargeting = TARGET_ENTITIES;
            return;
        }

        // ? Raycast Block
        if (findIn(shape, shape_block)){
            RayTraceResult blockRay = Solver.standardBlockRayTrace(world, m.focal, m.range.num(),
                    false, true, false);
            if (blockRay != null) {
                if (blockRay.typeOfHit == RayTraceResult.Type.BLOCK) {
                    m.pos = new Vec3d(blockRay.getBlockPos().offset(blockRay.sideHit));
                }
                if (blockRay.typeOfHit == RayTraceResult.Type.ENTITY && blockRay.entityHit != null){
                    m.pos = blockRay.entityHit.getPositionVector();
                }
            }

            if (blockRay == null || blockRay.typeOfHit == RayTraceResult.Type.MISS){
                m.stopCast = true;
            }
            m.isRay=true;
            m.castingTargeting = TARGET_BLOCKS;
            return;
        }
    }

    /** Calculates parameters of the spell.
     * @param caster - Caster of the spell
     * @param mods - Spell parameters
     */
    public static void calcMods(@Nullable EntityLivingBase caster, SpellParams mods){
        // Initial setup!
        String word = mods.keys.get("word");
        String next = mods.keys.get("next");
        String next2 = mods.keys.get("next2");
        String next3 = mods.keys.get("next3");
        String next4 = mods.keys.get("next4");
        String previous = mods.keys.get("previous");

        // Starting
        Number number = getFloat(next, next2, next3, Float.MAX_VALUE);

        if (number.intValue() != Integer.MAX_VALUE){
            mods.calcParam(word, number);
        }
        mods.calcParam(word, next);

        if (findIn(word, "focus")){
            if (findIn(next, "caster") && caster != null) mods.target=caster;
            if (findIn(next, "focal") && mods.focal != null) mods.target=mods.focal;
            if (findIn(next, "block blocks")) mods.castingTargeting=TARGET_BLOCKS;
            if (findIn(next, "entity entities")) mods.castingTargeting=TARGET_ENTITIES;
            if (findIn(next, "minion") && mods.last_summon != null) mods.target=mods.last_summon;
        }

        if (findIn(word, "owner")){
            if (findIn(previous, "add")) mods.hasOwner = true;
            if (findIn(previous, "remove")) mods.hasOwner = false;
        }

        if (findIn(word, "allies ally")){
            if (findIn(previous, "add allow")) mods.canAlly = true;
            if (findIn(previous, "remove disallow")) mods.canAlly = false;
        }

        if (findIn(word, "filter") && !findIn(previous, "no")){
            if (findIn(next, "mobs")) mods.filter = e -> e instanceof IMob;
            else if (findIn(next, "construct constructs magic")) mods.filter = e -> e instanceof EntityMagicConstruct || e instanceof EntityMagic;
            else if (findIn(next, "player players human humans")) mods.filter = e -> e instanceof EntityPlayer || e instanceof EntityWizard || e instanceof EntityEvilWizard;
            else if (findIn(next, "undead undeads")) mods.filter = e -> e instanceof EntityLivingBase && ((EntityLivingBase) e).isEntityUndead();
            else if (findIn(next, "creature creatures")) mods.filter = e -> e instanceof EntityCreature;
            else if (findIn(next, "living")) mods.filter = e -> e instanceof EntityLivingBase;
            else if (findIn(next, "entity entities")) mods.filter = e -> !(e instanceof EntityLivingBase);
            else if (findIn(next, "ally allies")){ mods.filter = e -> e == caster || (e instanceof EntityLivingBase
                    && AllyDesignationSystem.isAllied(caster, ((EntityLivingBase) e)));
                mods.canAlly=true;}
        }

        if (findIn(word, "no") && findIn(next, "filter")){
            mods.condition = Objects::nonNull;
        }


        // This plays role in does spell continues after this word.
        if (findIn(word, "if") && !findIn(previous, "no")){
            int value = -1;
            try{value = Integer.parseInt(next2);} catch (Exception ignore){}
            if (findIn(next, "mob mobs")) mods.condition = e -> e instanceof IMob;
            else if (findIn(next, "construct constructs magic")) mods.condition = e -> e instanceof EntityMagicConstruct || e instanceof EntityMagic;
            else if (findIn(next, "player players human humans")) mods.condition = e -> e instanceof EntityPlayer || e instanceof EntityWizard || e instanceof EntityEvilWizard;
            else if (findIn(next, "undead undeads")) mods.condition = e -> e instanceof EntityLivingBase && ((EntityLivingBase) e).isEntityUndead();
            else if (findIn(next, "creature creatures")) mods.condition = e -> e instanceof EntityCreature;
            else if (findIn(next, "living livings")) mods.condition = e -> e instanceof EntityLivingBase;
            else if (findIn(next, "entity entities")) mods.condition = e -> !(e instanceof EntityLivingBase);
            else if (findIn(next, "ally allies")){ mods.condition = e -> e == caster || (e instanceof EntityLivingBase
                    && AllyDesignationSystem.isAllied(caster, ((EntityLivingBase) e)));
                mods.canAlly=true;}
            else if (findIn(next, "health") && value > 0){
                mods.condition = e -> e instanceof EntityLivingBase && ((EntityLivingBase) e).getHealth() <= Integer.parseInt(next2);
            }
            else if (findIn(next, condition_sneak)) mods.condition = Entity::isSneaking;
            else if (findIn(next, condition_day)) mods.condition = entity -> entity.getEntityWorld().isDaytime();
            else if (findIn(next, condition_night)) mods.condition = entity -> !entity.getEntityWorld().isDaytime();
            else if (findIn(next, condition_light_level) && value >= 0) {
                int finalValue = value;
                mods.condition = entity -> entity.getBrightness() <= Math.max(finalValue, 0.0F)/100F;
            }
        }

        if (findIn(word, "no") && findIn(next, "if")){
            mods.condition = Objects::nonNull;
        }

        if (findIn(word, "vector")){
            float x;
            float y;
            float z;
            if(!findIn(next, ignore)) {
                x = getInteger(next, 0);
                y = getInteger(next2, 0);
                z = getInteger(next3, 0);
            }else{
                x = getInteger(next2, 0);
                y = getInteger(next3, 0);
                z = getInteger(next4, 0);
            }
            mods.vector = new Vec3d(x,y,z);
        }

        if (findIn(word, "element")){
            if (findIn(next, "magic arcane")) mods.element=Element.MAGIC;
            else if (findIn(next, "fire blaze")) mods.element = Element.FIRE;
            else if (findIn(next, "ice frost")) mods.element = Element.ICE;
            else if (findIn(next, "earth nature wind water")) mods.element = Element.EARTH;
            else if (findIn(next, "lightning storm thunder")) mods.element = Element.LIGHTNING;
            else if (findIn(next, "necromancy darkness")) mods.element = Element.NECROMANCY;
            else if (findIn(next, "healing heal light")) mods.element = Element.HEALING;
            else if (findIn(next, "sorcery time")) mods.element = Element.SORCERY;
        }

        if (findIn(word, "effects fx particles")){
            if (findInSmart(previous + " "+ next, "off disable remove")){
                mods.hasEffects=false;
            }
            if (findInSmart(previous + " "+ next, "on enable add")){
                mods.hasEffects=true;
            }
        }

        for (SpellPart param : Chanting.params){
            param.useParam(caster, mods);
        }
    }

    /** Creates visual representation of spell creation for Caster.
     * @param world - World
     * @param caster - Caster of spell
     * @param mods - Parameters of spell
     */
    public static void attachCastingVisual(World world, @Nullable EntityLivingBase caster, SpellParams mods){
        if (caster != null && mods.focal == caster && mods.hasEffects){
            caster.playSound(WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND, 0.4F, 1.2F + Solver.randFloat(-0.2F, 0.4F));

            String location = mods.element == Element.MAGIC ? "construct" : mods.element.func_176610_l();
            if (!world.isRemote) {
                if (mods.isRay) {
                    EntityMagicCircleVertical circle = Wizard.getVerticalCircle(world, location, caster.getPositionVector(), caster);
                    circle.setSizeMultiplier(2);
                    Tenebria.create(world, circle);
                } else {
                    EntityMagicCircle circle = Wizard.getCircle(world, location, caster.getPositionVector());
                    circle.setSizeMultiplier(3);
                    Tenebria.create(world, circle);
                }
            } else {
                if (mods.isBuff) Wizard.castBuff(world, mods.target, ArcaneColor.byElement(mods.element));
                Wizard.castParticles(world, mods.element, mods.target.getPositionVector());
            }
        }
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

        // ? Starting
        // ! This section made specifically for Replication, so we wouldn't suffer from same wording
        // * Allows replication of known spell
        if (findIn(word, "replicate replica") && target instanceof EntityPlayer){
            //if (next.split(":").length <= 1) next = "ebwizardry:" + next;
            Spell spell = null;
            try {
                spell = Spell.get(next);
                if (spell == Spells.none){
                    for (Spell look : Spell.registry.getValuesCollection()) {
                        if (findIn(next, Objects.requireNonNull(look.getRegistryName()).getResourcePath().toLowerCase())){
                            spell = look;
                        }
                    }
                }
                if (Arrays.asList(Tales.chanting.replicationBlacklist).contains(next))
                    spell = Spells.none;
            } catch (Exception ignore) {
            }


            if (spell != null && spell != Spells.none){
                EntityPlayer player = (EntityPlayer) target;
                ISoul soul = Mana.getSoul(player);
                SpellModifiers modifiers = new SpellModifiers();
                int seconds = Math.round(duration);
                int ticks = seconds * 20;

                modifiers.set(Sage.POTENCY, 1 + potency/10, false);
                modifiers.set(Sage.BLAST, 1 + potency/10, false);
                modifiers.set(Sage.RANGE, 1 + m.range.num()/10, false);

                // If spell isn't discovered - return
                if (WizardData.get(player) != null && !WizardData.get(player).hasSpellBeenDiscovered(spell) && !player.isCreative()){
                    Aterna.messageBar(player, TextFormatting.DARK_RED + "Spell isn't discovered!");
                    return;
                }

                // If spell isn't learned - return
                if (soul != null && soul.getLearned(spell) < 10 && Tales.addon.learning && !player.isCreative()){
                    Aterna.messageBar(player, TextFormatting.DARK_RED + "Spell isn't learned!");
                    return;
                }

                // Mana cost
                int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
                if(spell.isContinuous) cost = cost * seconds;

                // If anything stops the spell working at this point, nothing else happens.
                if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, (EntityLivingBase) target, modifiers)))
                    return;

                cost= (int) (cost * modifiers.get(Sage.CHANT_COST) + 0.1f);

                cost *= (int) ((m.potency.num() * m.potency.num()) + (m.range.num()/4) + (m.duration.num()/10)); // Our custom mods.

                if(spell.isContinuous){

                    WizardData data = WizardData.get(player);

                    // Events/packets for continuous spell casting via commands are dealt with in WizardData.
                    if(data != null){
                        if(data.isCasting()){
                            data.stopCastingContinuousSpell(); // I think on balance this is quite a nice feature to leave in
                        }

                        if(!useMana(target, cost)) return;
                        data.startCastingContinuousSpell(spell, modifiers, ticks);
                    }
                }else{
                    if(!useMana(target, cost)) return;

                    if(spell.cast(world, (EntityPlayer) target, EnumHand.MAIN_HAND, 0, modifiers)){

                        MinecraftForge.EVENT_BUS.post(new SpellCastEvent
                                .Post(SpellCastEvent.Source.OTHER, spell, (EntityLivingBase) target, modifiers));

                        if(spell.requiresPacket()){
                            // Sends a packet to all players in dimension to tell them to spawn particles.
                            // Only sent if the spell succeeded, because if the spell failed, you wouldn't
                            // need to spawn any particles!
                            IMessage msg = new PacketCastSpell.Message(target.getEntityId(), null, spell, modifiers);
                            WizardryPacketHandler.net.sendToDimension(msg, target.world.provider.getDimension());
                        }
                    }
                }
            }
        }

        if (findIn(previous, "replicate replica")) return;

        if (findIn(word, "become choose") && caster instanceof EntityPlayerMP){
            EntityPlayerMP playerMP = (EntityPlayerMP) caster;
            ISoul soul = Mana.getSoul(playerMP);
            if (soul == null) return;
            if ((findIn(next, Race.human))){
                soul.setRace(playerMP, Race.human);
                RaceListener.tick(playerMP);
            }
            else if (findIn(next, Race.elf)){
                soul.setRace(playerMP, Race.elf);
                RaceListener.tick(playerMP);
            }
            else if (findIn(next, Race.dwarf)){
                soul.setRace(playerMP, Race.dwarf);
                RaceListener.tick(playerMP);
            }
        }

        // ! No living required:

        // * Summon Minion
        if (findIn(word, "summon") && !world.isRemote) {
            //if (next.split(":").length <= 1) next = "ebwizardry:" + next;

            Entity summon = null;
            EntityLiving entity = null;
            try {
                EntityEntry entry = ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next));
                if (entry != null) summon = entry.newInstance(world);
                if (summon == null){
                    for (ResourceLocation location : ForgeRegistries.ENTITIES.getKeys()){
                        EntityEntry p = ForgeRegistries.ENTITIES.getValue(location);
                        if (p != null && p.newInstance(world) instanceof EntityLiving
                        && findIn(next, Objects.requireNonNull(p.getRegistryName()).getResourcePath().replace(":", " "))){
                            summon = p.newInstance(world);
                        }
                    }
                }
                if (Arrays.asList(Tales.chanting.minionBlacklist).contains(next))
                    summon = null;

                if (summon instanceof EntityLiving){
                    entity = (EntityLiving) summon;
                }

            } catch (Exception exception) {
                WizardryTales.log.error("---- [Wizardry Tales] - While summoning! ----");
                exception.printStackTrace();
                WizardryTales.log.error("---- [Wizardry Tales] - While summoning! ----");
            }

            if (entity instanceof EntityTameable || entity instanceof AbstractHorse){
                m.element = Element.NECROMANCY;
                if (caster instanceof EntityPlayer && m.hasOwner) {
                    if (entity instanceof EntityTameable) {
                        ((EntityTameable) entity).setTamedBy((EntityPlayer) caster);
                    } else {
                        ((AbstractHorse) entity).setTamedBy((EntityPlayer) caster);
                        ((AbstractHorse) entity).setHorseSaddled(true);
                    }
                }
                entity.getEntityData().setInteger("lifetime", m.lifetime.num() < 0.0F ? 1 :
                        Solver.asTicks(m.lifetime.num()));
                BlockPos position = BlockUtils.findNearbyFloorSpace(target, (int) (2 + m.range.num() / 10), 4);
                if (position != null && useMana(focal,
                        ((m.lifetime.num() / 10F) * (m.health.num() / 2F) * m.potency.num())
                        , true)) {
                    entity.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
                    entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(position)), null);
                    // To not get hammered!
                    try {

                        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(m.health.num());
                        entity.setHealth(entity.getMaxHealth()); // Need to set this because we may have just modified the value

                        // We do this on the end for a reason - Entity can not have Attack Damage as example!
                        entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(m.potency.num());
                    } catch (Exception ignore){}
                    Tenebria.create(world, entity);
                    world.playSound(focal.posX, focal.posY, focal.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                    return;
                }
            }

            if (entity instanceof ISummonedCreature) {
                m.element = Element.NECROMANCY;
                if (m.hasOwner) ((ISummonedCreature) entity).setCaster(caster);
                ((ISummonedCreature) entity).setLifetime(m.lifetime.num() < 0.0F ? 1 :
                        Solver.asTicks(m.lifetime.num()));
                BlockPos position = BlockUtils.findNearbyFloorSpace(target, (int) (2 + m.range.num() / 10), 4);
                if (position != null && useMana(focal,
                        ((m.lifetime.num() / 10F) * (m.health.num() / 2F) * m.potency.num())
                        , true)) {
                    entity.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
                    entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(position)), null);
                    ((ISummonedCreature) entity).onSpawn();
                    try {
                        entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(m.health.num());
                        entity.setHealth(entity.getMaxHealth()); // Need to set this because we may have just modified the value
                        entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(m.potency.num());
                    } catch (Exception ignore){};
                    Tenebria.create(world, entity);
                    m.last_summon=entity;
                    world.playSound(focal.posX, focal.posY, focal.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                    ParticleCreator.createSmoke(world, entity);
                    return;
                }
            }
        }

        // * Summon Construct
        if (findIn(word, "summon") && !world.isRemote) {
            //if (next.split(":").length <= 1) next = "ebwizardry:" + next; - No need since we auto-search now!

            Entity entity = null;
            try {
                EntityEntry entry = ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next));
                if (entry != null) entity = entry.newInstance(world);
                if (entity == null){
                    for (ResourceLocation location : ForgeRegistries.ENTITIES.getKeys()){
                        EntityEntry p = ForgeRegistries.ENTITIES.getValue(location);
                        if (p != null && !(p.newInstance(world) instanceof EntityLivingBase)
                                && findIn(next, Objects.requireNonNull(p.getRegistryName()).getResourcePath().toLowerCase())){
                            entity = p.newInstance(world);
                        }
                    }
                }
                if (Arrays.asList(Tales.chanting.constructBlacklist).contains(next))
                    entity = null;
                if (entity instanceof EntityRitual)
                    entity = null;


            } catch (Exception exception) {
                WizardryTales.log.error("---- [Wizardry Tales] - While summoning! ----");
                exception.printStackTrace();
                WizardryTales.log.error("---- [Wizardry Tales] - While summoning! ----");
            }

            if (entity != null) {
                boolean summon = false;
                if (entity instanceof EntityMagicConstruct) {
                    if (m.hasOwner) ((EntityMagicConstruct) entity).setCaster(caster);
                    ((EntityMagicConstruct) entity).lifetime = (lifetime < 0.0F ? 20 : Solver.asTicks(lifetime));
                    ((EntityMagicConstruct) entity).damageMultiplier = potency / 100F;
                    summon = true;
                } else if (entity instanceof EntityMagic) {
                    if (m.hasOwner) ((EntityMagic) entity).setCaster(caster);
                    ((EntityMagic) entity).setLifetime(lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                    ((EntityMagic) entity).damageMultiplier = potency / 100F;
                    summon = true;
                }
                int type = 0;
                if (entity instanceof EntityScaledConstruct) {
                    type = 1;
                    ((EntityScaledConstruct) entity).setSizeMultiplier(2 + m.size.num());
                } else if (entity instanceof EntityMagicScaled) {
                    type = 2;
                    ((EntityMagicScaled) entity).setSizeMultiplier(2 + m.size.num());
                }else if (entity instanceof EntityForcefield){
                    type = 3;
                    ((EntityForcefield) entity).setRadius(2 + m.size.num());
                }
                if (!summon) return;
                m.element=Element.MAGIC;
                entity.setPositionAndRotation(spellBlock.getX() + 0.5, spellBlock.getY(),
                        spellBlock.getZ() + 0.5, m.focal.rotationYaw, m.focal.rotationPitch);

                if (useMana(focal, type > 0 ? lifetime * m.size.num() * (potency) : lifetime * (potency), true)) {
                    Tenebria.create(world, entity);
                    m.last_summon=entity;
                    world.playSound(focal.posX, focal.posY, focal.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                    ParticleCreator.createSmoke(world, entity);
                }
            }
        }

        // * Ignites target or block
        if (findIn(word, "ignite") && useMana(focal, 2 * duration, true)) {
            if (m.castingTargeting != TARGET_BLOCKS) target.setFire((int) duration);

            m.element = Element.FIRE;
            if (m.castingTargeting == TARGET_BLOCKS && world.isAirBlock(spellBlock)) {
                if (!world.isRemote && BlockUtils.canPlaceBlock(caster, world, spellBlock)) {
                    world.setBlockState(spellBlock, Blocks.FIRE.getDefaultState());
                }
            }
        }

        // * Accelerates block
        if (findIn(word, "accelerate")) {
            IBlockState state = world.getBlockState(spellBlock);

            if (m.castingTargeting != TARGET_ENTITIES && state.getBlock() != Blocks.AIR) {
                TileEntity tile = world.getTileEntity(spellBlock);
                if (tile instanceof ITickable) {
                    for (int i = 0; i<20; i++) {
                        ITickable tickable = (ITickable) tile;
                        tickable.update();
                    }
                    m.element = Element.SORCERY;
                }

                if (state.getBlock().getTickRandomly()) {
                    for (int i = 0; i<20; i++) {
                        state.getBlock().randomTick(world, spellBlock, state, world.rand);
                    }
                    m.element = Element.SORCERY;
                }
            }
        }

        // * Places conjured block
        if (findIn(word, "place")) {
            IBlockState state = world.getBlockState(spellBlock);
            m.element = Element.SORCERY;

            if (caster != null && caster.isSneaking() && state.getBlock() == WizardryBlocks.spectral_block && useMana(focal, 5)) {

                if (!world.isRemote) {
                    // Dispelling of blocks
                    world.setBlockToAir(spellBlock);
                } else {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                            .clr(0.75f, 1, 0.85f).spawn(world);
                }

                return;
            }

            if (world.isRemote) {
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                        .clr(0.75f, 1, 0.85f).spawn(world);
            }
            double summary1 = Math.abs(m.vector.x) + Math.abs(m.vector.y) + Math.abs(m.vector.z);
            double summary2 = Math.abs(spellBlock.getX()) + Math.abs(spellBlock.getY()) + Math.abs(spellBlock.getZ());
            if (summary1 - summary2 == 0){
                if (BlockUtils.canBlockBeReplaced(world, spellBlock) && useMana(focal, 10)) {
                    if (!world.isRemote) {
                        boolean isCreative = caster instanceof EntityPlayer && ((EntityPlayer) caster).isCreative();
                        Block block = WizardryBlocks.spectral_block;
                        if (caster instanceof EntityPlayer){
                            ItemStack stack = Thief.getInHands((EntityPlayer)caster, i -> i.getItem() instanceof ItemBlock);
                            if (stack != null){
                                block = Block.getBlockFromItem(stack.getItem());
                                if (!isCreative) stack.shrink(1);
                            }
                        }
                        world.setBlockState(spellBlock, block.getBlockState().getBaseState());

                        if (world.getTileEntity(spellBlock) instanceof TileEntityTimer) {
                            float durate = dynamicDuration < 0 && isCreative ? -1.0F : dynamicDuration;
                            ((TileEntityTimer) Objects.requireNonNull(world.getTileEntity(spellBlock)))
                                    .setLifetime(Solver.asTicks(durate));
                        }
                    }

                    return;
                }
            }else {
                boolean flag = false;
                BlockPos destination = new BlockPos(spellBlock);
                destination = destination.add(m.vector.x, m.vector.y, m.vector.z);
                for (int x = 0; flag;) {
                    for (int y = 0; flag;) {
                        for (int z = 0; flag;) {
                            BlockPos position = new BlockPos(spellBlock);
                            position.add(x,y,z);
                            x+=m.vector.x > 0 ? 1 : -1; y+=m.vector.y > 0 ? 1 : -1; z+=m.vector.z > 0 ? 1 : -1;
                            flag = position.getDistance(destination.getX(), destination.getY(), destination.getZ()) > 0;
                            boolean isCreative = caster instanceof EntityPlayer && ((EntityPlayer) caster).isCreative();
                            if (BlockUtils.canBlockBeReplaced(world, spellBlock) && useMana(focal, 10)) {
                                if (!world.isRemote) {
                                    Block block = WizardryBlocks.spectral_block;
                                    if (caster instanceof EntityPlayer) {
                                        ItemStack stack = Thief.getInHands((EntityPlayer) caster, i -> i.getItem() instanceof ItemBlock);
                                        if (stack != null) {
                                            block = Block.getBlockFromItem(stack.getItem());
                                            if(!isCreative) stack.shrink(1);
                                        }
                                    }
                                    world.setBlockState(spellBlock, block.getBlockState().getBaseState());

                                    if (world.getTileEntity(spellBlock) instanceof TileEntityTimer) {
                                        float durate = dynamicDuration < 0 && isCreative ? -1.0F : dynamicDuration;
                                        ((TileEntityTimer) Objects.requireNonNull(world.getTileEntity(spellBlock)))
                                                .setLifetime(Solver.asTicks(durate));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // * Removes conjured block
        if (findIn(word, "remove")) {
            IBlockState state = world.getBlockState(spellBlock);

            if (state.getBlock() == WizardryBlocks.spectral_block && useMana(focal, 10)) {

                if (!world.isRemote) {
                    // Dispelling of blocks
                    world.setBlockToAir(spellBlock);
                } else {
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                            .clr(0.75f, 1, 0.85f).spawn(world);
                }
                m.element = Element.SORCERY;
                return;
            }
        }

        // * Grows block targeted
        if (findIn(word, "grow")) {
            IBlockState state = world.getBlockState(spellBlock);

            if (m.castingTargeting != TARGET_ENTITIES && state.getBlock() instanceof IGrowable) {

                IGrowable plant = (IGrowable) state.getBlock();

                if (plant.canGrow(world, spellBlock, state, world.isRemote) && useMana(focal, 10, true)) {
                    if (!world.isRemote) {
                        if (plant.canUseBonemeal(world, world.rand, spellBlock, state)) {
                            plant.grow(world, world.rand, spellBlock, state);
                        }
                    } else {
                        // Yes, it's meant to be 0, and it automatically changes it to 15.
                        ItemDye.spawnBonemealParticles(world, spellBlock, 0);
                    }
                    m.element = Element.EARTH;
                }
            }

            if (m.castingTargeting != TARGET_BLOCKS && target.getDistance(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5) < 2) {
                if (target instanceof EntityLivingBase && useMana(focal, duration * 2)) {
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(TalesEffects.entangled,
                            Solver.asTicks(duration), 2));
                }
            }
        }

        // * Locks/Unlocks block with Magic!
        if (findIn(word, "lock") && caster != null){
            if(!world.isRemote){
                if(toggleLock(world, spellBlock, (EntityPlayer)caster)){
                    BlockPos otherHalf = BlockUtils.getConnectedChest(world, spellBlock);
                    if(otherHalf != null) toggleLock(world, otherHalf, (EntityPlayer)caster);
                }
            }
        }

        // * Pulls target in or out
        if (findIn(word, "pull")){
            float distance = target.getDistance(focal);
            if (findIn(next, "in") && useMana(focal, distance, true)){
                Tenebria.moveTowards(target, focal.getPositionVector(), 2);
                m.element = Element.SORCERY;
            }

            if (findIn(next, "out") && useMana(focal, distance, true)){
                Tenebria.moveTowards(target, focal.getPositionVector(), -1 * 2);
                m.element = Element.SORCERY;
            }
        }


        // * Removes gravity for target. Removes fall damage.
        if (findIn(word, "undo") && findIn(next, "gravity") && useMana(focal, 5)){
            EntityUtils.undoGravity(target);
            target.fallDistance = 0;
            if (world.isRemote) Wizard.castParticles(world, Element.SORCERY, target.getPositionVector());
        }

        // * Undo Magic allows to remove magical entities
        if (findIn(word, "undo") && findIn(next, "magic") && useMana(focal, 10)){
            if (target instanceof EntityMagic){
                ((EntityMagic)target).despawn();
            }else if (target instanceof EntityMagicConstruct){
                ((EntityMagicConstruct)target).despawn();
            }

            if (target instanceof ISummonedCreature){
                ((ISummonedCreature)target).onDespawn();
                ((ISummonedCreature)target).setLifetime(0);
            }
        }

        // * Moves target towards vector
        if(findIn(word, "move")){
            float x;
            float y;
            float z;
            if(!findIn(next, ignore)) {
                x = getInteger(next, 0);
                y = getInteger(next2, 0);
                z = getInteger(next3, 0);
            }else{
                x = getInteger(next2, 0);
                y = getInteger(next3, 0);
                z = getInteger(next4, 0);
            }
            float distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
            if (distance == 0.0F){
                if (findIn(next, "look")){
                    x = (float) target.getLookVec().x * potency;
                    y = (float) target.getLookVec().y * potency;
                    z = (float) target.getLookVec().z * potency;
                }
                if (findIn(next, "inverted invert inv") && findIn(next2, "look")){
                    x = (float) -target.getLookVec().x * potency;
                    y = (float) -target.getLookVec().y * potency;
                    z = (float) -target.getLookVec().z * potency;
                }
                distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
            }

            if(useMana(focal, (distance * distance) * 0.5, true) && !world.isRemote) {
                target.motionX=x * 0.1f;
                target.motionY=y * 0.1f;
                target.motionZ=z * 0.1f;
                target.velocityChanged = true;
                target.fallDistance = 0;
            }

            if(world.isRemote){ Wizard.castParticles(world, Element.SORCERY, target.getPositionVector());}
        }



        // ! Only Living ones:
        if (!(target instanceof EntityLivingBase)) return;
        EntityLivingBase living = (EntityLivingBase) target;

        // * Applies potion effect
        if (findIn(word, "apply")) {
            //if (next.split(":").length <= 1) next = "minecraft:" + next;
            Potion potion = null;
            try {
                potion = Potion.getPotionFromResourceLocation(next);
                if (potion == null){
                    for (ResourceLocation location : Potion.REGISTRY.getKeys()){
                        Potion p = Potion.getPotionFromResourceLocation(location.toString());
                        if (p != null && findIn(next, Objects.requireNonNull(p.getRegistryName()).getResourcePath().replace(":", " ").replace(".", " "))){
                            potion = p;
                        }
                    }

                }
                Potion finalPotion = potion;
                if (Arrays.stream(Tales.chanting.applyBlacklist)
                        .anyMatch(p -> p.equals(finalPotion != null ?
                                Objects.requireNonNull(finalPotion.getRegistryName()).toString() : null))) {
                    potion = null;
                }
            } catch (Exception ignore) {
                Aterna.chant((EntityPlayer) caster, "Effect is not found!");
            }
            if (potion != null) {
                PotionEffect effect = new PotionEffect(potion,
                        Solver.asTicks(duration), Math.max((int) (potency - 1), 0));
                if (living.isPotionActive(potion) && living.getActivePotionEffect(potion) != null
                        && (Objects.requireNonNull(living.getActivePotionEffect(potion)).getAmplifier() > effect.getAmplifier()
                 || Objects.requireNonNull(living.getActivePotionEffect(potion)).getDuration() > effect.getDuration()/2)){
                    return;
                }
                if (living.isPotionApplicable(effect) && useMana(focal, (potency * potency) * (duration / 10), true)){
                    if (!world.isRemote) {
                        living.addPotionEffect(effect);
                    }
                    m.isBuff = true;
                }
            }
        }

        // * Attacks with Magic of some said type
        if (findIn(word, "attack") && findIn(next, "with") &&
                useMana(focal, potency * potency, true)){
            MagicDamage.DamageType type = Sage.getTypeByText(word);
            Element element;
            if (type == null) {
                element = Element.fromName(word, Element.MAGIC);
            }else element = Sage.getElementByType(type);

            Sage.smartDamage(word, caster, living, 10);
            m.element = element;
        }

        // * Heals for certain amount of HP
        if (findIn(word, "heal")){
            if (useMana(focal, potency * potency, true)) {
                if (!living.isEntityUndead()) {
                    living.heal(potency);
                } else {
                    Sage.causeDamage(MagicDamage.DamageType.RADIANT, caster, living, potency * 2);
                    living.setFire(2);
                }
                m.element = Element.HEALING;
                m.isBuff=true;
            }
        }

        // * Satiates target for some Food points with Magic
        if (findIn(word, "satiate satiety saturate")){
            if (target instanceof EntityPlayer && useMana(focal, potency * potency, true)) {
                EntityPlayer player = (EntityPlayer) target;
                player.getFoodStats().addStats(m.potency.getNumber().intValue(), 0.1F);
                m.element = Element.HEALING;
                m.isBuff=true;
            }
        }

        // * Show feature!
        if (findIn(word, "show") && caster != null) {
            if (findIn(next, "potion potions")) {
                Collection<Potion> potionList = ForgeRegistries.POTIONS.getValuesCollection();
                Collection<Potion> potions = new ArrayList<>(Collections.emptyList());
                for (Potion p : potionList){
                    if (p.getRegistryName() != null) {
                        if (findIn(p.getRegistryName().getResourceDomain().toLowerCase(), next2)) {
                            potions.add(p);
                        }
                    }
                }

                if (potions.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    potions.forEach(pot -> {
                        if (pot.getRegistryName() != null) builder.append(pot.getRegistryName().toString()).append(" ");
                    });
                    String result = builder.toString();
                    Aterna.message((EntityPlayer) caster, result);
                }
            }
        }

        // * Create wood item!
        if (findIn(word, "create") && living instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) living;
            if (findIn(next, "pickaxe") && useMana(focal,20, true)) Thief.addItem(player, Thief.stack(Items.WOODEN_PICKAXE));
            else if (findIn(next, "hoe") && useMana(focal,15, true)) Thief.addItem(player, Thief.stack(Items.WOODEN_HOE));
            else if (findIn(next, "axe") && useMana(focal,20, true)) Thief.addItem(player, Thief.stack(Items.WOODEN_AXE));
            else if (findIn(next, "shovel") && useMana(focal,20, true)) Thief.addItem(player, Thief.stack(Items.WOODEN_SHOVEL));
            else if (findIn(next, "sword") && useMana(focal,20, true)) Thief.addItem(player, Thief.stack(Items.WOODEN_SWORD));
            else if (findIn(next, "wood log") && useMana(focal,30, true)) Thief.addItem(player, Thief.stack(Item.getItemFromBlock(Blocks.LOG)));
            m.element = Element.EARTH;
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

    /*
    public static void applyActions(List<String> words, SpellParams mods, List<String> set){
        // Initial setup!
        String word = words.get(0);
        String next = words.get(1);
        String next2 = words.get(2);
        String next3 = words.get(3);
        String next4 = words.get(4);
        String previous = words.get(5);

        World world = mods.focal.world;
        if(world == null){
            world = mods.target.world;
            if (world == null){
                throw new RuntimeException("World is null!");
            }
        }

        String shape = mods.shape.val();
        Entity focal = mods.focal;
        BlockPos spellBlock = mods.spellBlock;
        if (spellBlock == null) spellBlock = focal.getPosition();
        EntityLivingBase caster = getCaster(focal);
        Entity target = mods.target;
        boolean isServer = mods.isServer;
        int castingTargeting = mods.castingTargeting;

        float potency = mods.potency.num();
        float duration = mods.duration.num();
        float range = mods.range.num();
        float lifetime = mods.lifetime.num();

        if (target == null) return;

        if(findIn(word, "ignite") && useMana(focal, 2 * duration, true)){
            if (mods.castingTargeting != 1 && target instanceof EntityLivingBase) {
                target.setFire((int) duration);
            }
            if (mods.castingTargeting != 2) {
                BlockPos pos;
                if(mods.spellBlock == null) pos = spellBlock;
                else pos = mods.spellBlock.offset(mods.ray.sideHit.getOpposite());

                if (world.isAirBlock(pos)) {
                    if (!world.isRemote && BlockUtils.canPlaceBlock(caster, world, pos)) {
                        world.setBlockState(pos, Blocks.FIRE.getDefaultState());
                    }
                }
            }

            if (!isServer) {
                Wizard.castParticles(world, Element.FIRE, target.getPositionVector());
            }
        }

        if (findIn(word, "attack") && findIn(next, "with")){
            if (useMana(focal, potency * potency, true)) {
                Sage.smartDamage(next2, caster, target, potency);
            }
        }

        if (findIn(word, "accelerate")){
            IBlockState state = world.getBlockState(spellBlock);

            if(mods.castingTargeting != 2 && state.getBlock() == Blocks.AIR){

                TileEntity tile = world.getTileEntity(spellBlock);
                if (tile instanceof ITickable){
                    ITickable tickable = (ITickable) tile;
                    tickable.update();
                }

                if(state.getBlock().getTickRandomly()){
                    state.getBlock().randomTick(world, spellBlock,state, world.rand);
                }
            }
        }

        if (findIn(word, "remove") && mods.castingTargeting != 2){
            IBlockState state = world.getBlockState(spellBlock);

            if(state.getBlock() == WizardryBlocks.spectral_block){

                if(isServer){
                    // Dispelling of blocks
                    world.setBlockToAir(spellBlock);
                }else{
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                            .clr(0.75f, 1, 0.85f).spawn(world);
                }

                return;
            }
        }

        if (findIn(word, "place") && mods.castingTargeting != 2){
            IBlockState state = world.getBlockState(spellBlock);

            if(caster != null && caster.isSneaking() && state.getBlock() == WizardryBlocks.spectral_block){

                if(isServer){
                    // Dispelling of blocks
                    world.setBlockToAir(spellBlock);
                }else{
                    ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                            .clr(0.75f, 1, 0.85f).spawn(world);
                }

                return;
            }

            if(!isServer){
                ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(spellBlock.getX() + 0.5, spellBlock.getY() + 0.5, spellBlock.getZ() + 0.5).scale(3)
                        .clr(0.75f, 1, 0.85f).spawn(world);
            }

            if(BlockUtils.canBlockBeReplaced(world, spellBlock)){

                if(isServer){

                    world.setBlockState(spellBlock, WizardryBlocks.spectral_block.getDefaultState());

                    if(world.getTileEntity(spellBlock) instanceof TileEntityTimer){
                        ((TileEntityTimer) Objects.requireNonNull(world.getTileEntity(spellBlock))).setLifetime(Solver.asTicks(duration));
                    }
                }

                return;
            }
        }

        if (findIn(word, "grow")){
            IBlockState state = world.getBlockState(spellBlock);

            if(mods.castingTargeting != 2 && state.getBlock() instanceof IGrowable){

                IGrowable plant = (IGrowable)state.getBlock();

                if(plant.canGrow(world, spellBlock, state, !isServer) && useMana(focal, 10, true)){
                    if(isServer){
                        if(plant.canUseBonemeal(world, world.rand, spellBlock, state)){
                            plant.grow(world, world.rand, spellBlock, state);
                        }
                    }else{
                        // Yes, it's meant to be 0, and it automatically changes it to 15.
                        ItemDye.spawnBonemealParticles(world, spellBlock, 0);
                    }
                }
            }

            if (mods.castingTargeting != 1 && target.getDistance(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5) < 2){
                if (target instanceof EntityLivingBase && useMana(focal, duration * 2)){
                    ((EntityLivingBase) target).addPotionEffect(new PotionEffect(TalesEffects.entangled,
                            Solver.asTicks(duration), 2));
                }
            }
        }


        if (findIn(word, "heal")){
            if (useMana(focal, potency * potency, true)) {
                if (target instanceof EntityLivingBase) {
                    EntityLivingBase living = (EntityLivingBase) target;
                    if (!living.isEntityUndead()) {
                        living.heal(potency);
                    } else {
                        Sage.causeDamage(MagicDamage.DamageType.RADIANT, caster, living, potency * 2);
                        living.setFire(2);
                    }

                    if (!isServer) Wizard.castBuff(world, living, 0xD6C05C, 18);

                }
            }
        }

        if (findIn(word, "disarm") && useMana(focal, 10, true)){
            if (target instanceof EntityPlayer){
                EntityPlayer living = (EntityPlayer) target;
                if(!living.getHeldItemMainhand().isEmpty()){
                    if(isServer){
                        EntityItem item = living.entityDropItem(living.getHeldItemMainhand(), 0);
                        // Makes the item move towards the caster
                        if (item != null) {
                            Tenebria.moveTowards(item, focal.posX, focal.posZ);
                        }
                    }else{
                        Wizard.castParticles(world, Element.SORCERY, living.getPositionVector());
                    }
                    living.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
                }
            }
        }

        if(findIn(word, "turn") && findIn(next, "around") && useMana(focal, potency, true)){
            target.turn(potency * 2, potency * 2);
            if(!isServer){ Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                    target.getPositionVector(), new Vec3d(0, 0.02, 0), 18);}
        }

        if (findIn(word, "apply") && target instanceof EntityLivingBase){
                if (next.split(":").length <= 1) next= "minecraft:"+next;
                Potion potion = null;
                try {
                    potion = Potion.getPotionFromResourceLocation(next);
                    Potion finalPotion = potion;
                    if (Arrays.stream(Tales.chanting.applyBlacklist)
                            .anyMatch(p -> p.equals(finalPotion != null ?
                                    Objects.requireNonNull(finalPotion.getRegistryName()).toString() : null))){
                        potion = null;
                    }

                }catch (Exception ignore){}

                if (potion != null && useMana(focal, potency * (duration/10), true)) {
                    if (isServer) {
                        ((EntityLivingBase) target).addPotionEffect(new PotionEffect(potion,
                                Solver.asTicks(duration), Math.max((int) (potency - 1), 0)));
                    }else {
                        Wizard.castBuff(world, target, 0xD6790B);}
                }

                //Wizard.conjureCircle(world, "construct", target.getPositionVector());

        }

        if (findIn(word, "undo") && findIn(next, "gravity") && useMana(focal, 5)){
            EntityUtils.undoGravity(target);
            if (!isServer) Wizard.castParticles(world, Element.SORCERY, target.getPositionVector());
        }

        if(findIn(word, "move")){
            float x;
            float y;
            float z;
            if(!findIn(next, ignore)) {
                x = getFloat(next, 0.0F);
                y = getFloat(next2, 0.0F);
                z = getFloat(next3, 0.0F);
            }else{
                x = getFloat(next2, 0.0F);
                y = getFloat(next3, 0.0F);
                z = getFloat(next4, 0.0F);
            }
            float distance = Math.abs(x) + Math.abs(y) + Math.abs(z);
            if(useMana(focal, distance * distance, true)) {
                target.setVelocity(x * 0.1f, y * 0.1f, z * 0.1f);
                target.velocityChanged = true;
                target.fallDistance = 0;
            }

            if(!isServer){ Wizard.castParticles(world, Element.SORCERY, target.getPositionVector());}
        }

        // Depends on shape
        if (shape.equals(shape_minion) && findIn(word, "summon") && isServer) {
            try {
                set.remove("summon");
                set.remove(next);
            }catch (Exception ignore){}

            if (next.split(":").length <= 1) next= "ebwizardry:"+ next;

            EntityLiving entity = null;
            try {
                entity = (EntityLiving) Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next)))
                        .newInstance(world);
                if (Arrays.asList(Tales.chanting.minionBlacklist).contains(next))
                    entity = null;

            }catch (Exception ignore){}

            if (entity instanceof ISummonedCreature) {
                if (mods.hasOwner) ((ISummonedCreature) entity).setCaster(caster);
                ((ISummonedCreature) entity).setLifetime(mods.lifetime.num() < 0.0F ? 1 :
                        Solver.asTicks(mods.lifetime.num()));
                BlockPos pos = BlockUtils.findNearbyFloorSpace(focal, (int) (2 + mods.range.num()/4), 4);
                if (pos != null && useMana(focal, ((mods.lifetime.num()/10F) * (mods.health.num()/2F) * mods.potency.num())/5F
                        , true)) {
                    entity.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    entity.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(pos)), null);
                    ((ISummonedCreature) entity).onSpawn();
                    entity.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(mods.health.num());
                    entity.setHealth(entity.getMaxHealth()); // Need to set this because we may have just modified the value
                    entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(mods.potency.num());
                    Tenebria.create(world, entity);

                    world.playSound(focal.posX, focal.posY, focal.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                }
            }
        }

        if (findIn(shape, shape_construct)){
            if(findIn(word, "teleport")){
                float x;
                float y;
                float z;
                if(!findIn(next, ignore)) {
                    x = getFloat(next, 0.0F);
                    y = getFloat(next2, 0.0F);
                    z = getFloat(next3, 0.0F);
                }else{
                    x = getFloat(next2, 0.0F);
                    y = getFloat(next3, 0.0F);
                    z = getFloat(next4, 0.0F);
                }

                if (Math.abs(x + y + z) == 0.0F){
                    x = (float) ((spellBlock.getX() + 0.5) - target.getPositionVector().x);
                    y = (float) (spellBlock.getY() - target.getPositionVector().y);
                    z = (float) ((spellBlock.getZ() + 0.5) - target.getPositionVector().z);
                }

                Vec3d pos = target.getPositionVector();
                Vec3d targetPos = target.getPositionVector().add(new Vec3d(x,y,z));

                if(lifetime != 0.0F){
                    EntityCircleTeleportation circle1 = new EntityCircleTeleportation(world);
                    circle1.setLocation("u_sorcery");
                    circle1.setSizeMultiplier(5.0f);
                    EntityCircleTeleportation circle2 = new EntityCircleTeleportation(world);
                    circle2.setLocation("u_sorcery");
                    circle2.setSizeMultiplier(5.0f);

                    if (mods.hasOwner) {
                        circle1.setCaster(caster);
                        circle2.setCaster(caster);
                    }

                    if (mods.canAlly){ circle1.workOnAllies(); circle2.workOnAllies();}

                    circle1.setPosition(pos.x, pos.y, pos.z);
                    circle1.setStoredPosition(new Vec3d(targetPos.x, targetPos.y + 0.1, targetPos.z));
                    circle1.setLifetime(Solver.asTicks(lifetime));

                    //circle2.setPosition(targetPos.x, targetPos.y, targetPos.z);
                    //circle2.setStoredPosition(new Vec3d(pos.x, pos.y + 0.1, pos.z));
                    //circle2.setLifetime(Solver.asTicks(lifetime));

                    Tenebria.create(world, circle1);
                    //target.setPositionAndUpdate(targetPos.x, targetPos.y, targetPos.z);
                    //Tenebria.create(world, circle2);

                    if(!isServer){
                        Wizard.castParticles(world, Element.SORCERY, new Vec3d(pos.x, pos.y, pos.z));

                        Wizard.castParticles(world, Element.SORCERY, new Vec3d(targetPos.x, targetPos.y, targetPos.z));
                    }

                    world.playSound(target.posX, target.posY, target.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                }
            }

            if (findIn(word, "summon") && isServer) {
                try {
                    set.remove("summon");
                    set.remove(next);
                }catch (Exception ignore){}

                if (next.split(":").length <= 1) next= "ebwizardry:"+ next;

                Entity entity = null;
                try {
                    entity = Objects.requireNonNull(ForgeRegistries.ENTITIES.getValue(Tales.toResourceLocation(next)))
                            .newInstance(world);
                    if (Arrays.asList(Tales.chanting.constructBlacklist).contains(next))
                        entity = null;

                }catch (Exception ignore){}

                if (entity != null) {
                    if (entity instanceof EntityMagicConstruct) {
                        if (mods.hasOwner)((EntityMagicConstruct) entity).setCaster(caster);
                        ((EntityMagicConstruct) entity).lifetime = (lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                        ((EntityMagicConstruct) entity).damageMultiplier = potency / 100F;
                    }else if (entity instanceof EntityMagic){
                        if (mods.hasOwner) ((EntityMagic) entity).setCaster(caster);
                        ((EntityMagic) entity).setLifetime(lifetime < 0.0F ? 1 : Solver.asTicks(lifetime));
                        ((EntityMagic) entity).damageMultiplier = potency / 100F;
                    }
                    int type = 0;
                    if (entity instanceof EntityScaledConstruct){
                        type = 1;
                        ((EntityScaledConstruct) entity).setSizeMultiplier(2 + mods.size.num());
                    }else if (entity instanceof EntityMagicScaled){
                        type = 2;
                        ((EntityMagicScaled) entity).setSizeMultiplier(2 +  mods.size.num());
                    }

                    entity.setPosition(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5);


                    if (useMana(focal, type > 0 ? lifetime *  mods.size.num() * (potency/100F) : lifetime * (potency/100F), true)) {
                        Tenebria.create(world, entity);
                        world.playSound(focal.posX, focal.posY, focal.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                                SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                    }
                }
            }
        }else {
            if (findIn(word, "teleport")) {
                float x;
                float y;
                float z;
                if (!findIn(next, ignore)) {
                    x = getFloat(next, 0.0F);
                    y = getFloat(next2, 0.0F);
                    z = getFloat(next3, 0.0F);
                } else {
                    x = getFloat(next2, 0.0F);
                    y = getFloat(next3, 0.0F);
                    z = getFloat(next4, 0.0F);
                }

                if (Math.abs(x + y + z) == 0.0F) {
                    x = (float) ((spellBlock.getX() + 0.5) - target.getPositionVector().x);
                    y = (float) (spellBlock.getY() - target.getPositionVector().y);
                    z = (float) ((spellBlock.getZ() + 0.5) - target.getPositionVector().z);
                }

                Vec3d pos = target.getPositionVector();
                Vec3d targetPos = target.getPositionVector().add(new Vec3d(x, y, z));
                float distance = (float) pos.distanceTo(targetPos);

                if (lifetime > 0.0F && SpellCreation.useMana(focal, (distance * distance) / 100F)) {
                    EntityMagicCircle circle1 = new EntityMagicCircle(world);
                    circle1.setLocation("u_sorcery");
                    circle1.setSizeMultiplier(5.0f);
                    EntityMagicCircle circle2 = new EntityMagicCircle(world);
                    circle2.setLocation("u_sorcery");
                    circle2.setSizeMultiplier(5.0f);

                    if (mods.hasOwner) {
                        circle1.setCaster(caster);
                        circle2.setCaster(caster);
                    }

                    circle1.setPosition(pos.x, pos.y, pos.z);
                    circle1.setLifetime(Solver.asTicks(2));

                    circle2.setPosition(targetPos.x, targetPos.y, targetPos.z);
                    circle2.setLifetime(Solver.asTicks(2));

                    Tenebria.create(world, circle1);
                    target.setPositionAndUpdate(targetPos.x, targetPos.y + 0.1, targetPos.z);
                    target.fallDistance = 0;
                    Tenebria.create(world, circle2);

                    if (!isServer) {
                        Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                new Vec3d(pos.x, pos.y, pos.z), new Vec3d(0, 0.02, 0), 18);

                        Wizard.castParticles(world, EnumParticleTypes.ENCHANTMENT_TABLE,
                                new Vec3d(targetPos.x, targetPos.y, targetPos.z), new Vec3d(0, 0.2, 0), 18);
                    }

                    world.playSound(target.posX, target.posY, target.posZ, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT,
                            SoundCategory.PLAYERS, 0.7f, 1.0f, true);
                }
            }
        }
    }
    */

    @Nullable
    public static EntityLivingBase getCaster(Entity focal){
        if (focal instanceof EntityLivingBase) return  (EntityLivingBase) focal;
        if(focal instanceof EntityMagic) return  ((EntityMagic) focal).getCaster();
        return null;
    }



}
