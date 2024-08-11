package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.*;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.api.wizardry.TalesVampirism;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.SoulProvider;
import astramusfate.wizardry_tales.entity.ai.EntityAIFollowCasterNoTp;
import astramusfate.wizardry_tales.entity.living.EntityVampire;
import astramusfate.wizardry_tales.registry.TalesEffects;
import com.google.common.collect.Lists;
import electroblob.wizardry.block.BlockBookshelf;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.misc.Forfeit;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.potion.Curse;
import electroblob.wizardry.registry.*;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.*;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.registry.IThrowableEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import static astramusfate.wizardry_tales.data.ChantWorker.useMana;

@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class EventsHandler extends EventsBase {

    /** Setting the lowest priority, so we can see something close to final damage. Also Vampirism integration! **/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityAttack(LivingDamageEvent event){
        if (event.getSource().getTrueSource() instanceof EntityLivingBase
                && event.getSource() instanceof IElementalDamage &&
                ((IElementalDamage)event.getSource()).getType() == MagicDamage.DamageType.WITHER){
            EntityLivingBase caster = (EntityLivingBase) event.getSource().getTrueSource();
            if (TalesVampirism.isVampireSafe(caster) && caster instanceof  EntityPlayer){
                TalesVampirism.drinkBlood((EntityPlayer) caster, Math.round(event.getAmount()), 0.0F);
            }
            if (caster instanceof EntityVampire){
                caster.heal(event.getAmount());
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoinGiveTasks(EntityJoinWorldEvent event) {
        if (Tales.addon.summons_follow && event.getEntity() instanceof EntityLiving){
            EntityLiving entity = (EntityLiving) event.getEntity();
            if (entity instanceof ISummonedCreature) {
                entity.tasks.addTask(6, new EntityAIFollowCasterNoTp((ISummonedCreature) entity,entity, 1.5F, 2, 8));
            }
        }
    }

    @SubscribeEvent()
    public static void onPlayerTickCastingRingCooldown(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START && event.player.ticksExisted > 5) {
            EntityPlayer player = event.player;
            ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
            if (soul != null){
                // Cooldown
                if (soul.getCooldown() > 0) {
                    soul.decreaseCooldown(event.player, 1);
                }

                // Low on Mana
                if (Tales.mp.lowOnMana && !event.player.world.isRemote && !event.player.isCreative() && Solver.doEvery(player, 1)){
                    if (soul.getMP() <= soul.getMaxMP() * 0.25F){
                        player.addExhaustion(0.01F);
                        player.addPotionEffect(new PotionEffect(TalesEffects.magic_exhaust, Solver.asTicks(2), 0,true, false));
                    }

                    if (soul.getMP() <= soul.getMaxMP() * 0.1F){
                        player.addExhaustion(0.02F);
                        player.addPotionEffect(new PotionEffect(TalesEffects.magic_exhaust, Solver.asTicks(2), 5, true, false));
                    }
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void castCostReplacement(SpellCastEvent.Pre event){
        event.getModifiers().set(Sage.CHANT_COST, event.getModifiers().get(Sage.COST), true);
        if (Tales.mp.noMoreManaUse &&  Tales.mp.manaPool &&  !WizardryTales.hasPlayerMana
                && event.getCaster() instanceof EntityPlayerMP){
            event.getModifiers().set(Sage.COST, 0.0F, true);
        }
    }

    @SubscribeEvent
    public static void onConfigChangedEvent(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(WizardryTales.MODID))
        {
            ConfigManager.sync(WizardryTales.MODID, Config.Type.INSTANCE);
        }
    }


    @SubscribeEvent()
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START && Solver.doEvery(event.player, Tales.mp.seconds_frequency) && event.player instanceof EntityPlayerMP){
            ISoul soul = event.player.getCapability(SoulProvider.SOUL_CAP, null);
            if (soul == null) return;
            if(event.player.ticksExisted > 5) {
                double additional = (Tales.mp.bonus_regen * (soul.getMaxMP()/Tales.mp.max));
                soul.addMana(event.player, Tales.mp.regeneration + additional);
            }
        }
    }

    private static final List<SpellCastEvent.Source> sourceList = Lists.newArrayList(SpellCastEvent.Source.WAND,
            SpellCastEvent.Source.SCROLL, SpellCastEvent.Source.OTHER);

    @SubscribeEvent
    public static void canCastSpell(PlayerInteractEvent.RightClickItem event){
        if(event.getItemStack().getItem() instanceof ISpellCastingItem){
            EntityPlayer player = event.getEntityPlayer();
            if (player instanceof EntityPlayerMP) {
                ISoul soul = Mana.getSoul(player);
                Spell spell = WandHelper.getCurrentSpell(event.getItemStack());
                if (WandHelper.getCurrentCooldown(event.getItemStack()) > 0) {
                    cancel(event);
                    return;
                }
                if (soul == null) return;

                if (isValid(spell) && !player.isCreative() &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana) {
                    double mana = soul.getMP();

                    // If there is not enough mana...
                    if (mana < getCost(spell)) {
                        cancel(event);
                        Aterna.translate(player, true, "mana.not_enough");
                        if (!player.getCooldownTracker().hasCooldown(event.getItemStack().getItem())) {
                            player.getCooldownTracker().setCooldown(event.getItemStack().getItem(), Solver.asTicks(1));
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void canSpellContinuous(SpellCastEvent.Tick event){
        if(event.getCaster() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            Spell spell = event.getSpell();
            if(soul != null && isValid(spell) && !player.isCreative() &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana && player instanceof EntityPlayerMP) {
                double mana = soul.getMP();

                // If there is not enough mana...
                if(mana < getDistributedCost(spell, event.getCount())) {
                    cancel(event);
                }else {
                    soul.addMana(player, -getDistributedCost(spell, event.getCount())
                            * event.getModifiers().get(Sage.CHANT_COST));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void learnSpell(SpellCastEvent.Pre event){
        if(event.getCaster() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            Spell spell = event.getSpell();
            if (soul != null && spell != Spells.none && spell.getCost() > 0  &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana  && player instanceof EntityPlayerMP) {
                WizardData data = WizardData.get(player);
                if (data != null && data.hasSpellBeenDiscovered(spell)){
                    // Progress of learning the spell by this player
                    int progress = soul.getLearned(spell);

                    // Forfeit chance
                    int chance = 0;
                    if (progress >= 0){
                        chance = 100;
                        if (progress >= 3){
                            chance = 80;
                            if (progress >= 5){
                                chance = 50;
                                if (progress >= 8){
                                    chance = 25;
                                    if (progress >= 10) chance = 0;
                                }
                            }
                        }
                    }

                    if (Tales.addon.learning) {
                        if (progress == 10) {
                            if (!player.world.isRemote) {
                                Aterna.translate(player, false, "mana.learned");
                                Aterna.translate(player, true, "mana.keep_trying10");
                            }
                            EntityUtils.playSoundAtPlayer(player, WizardrySounds.MISC_DISCOVER_SPELL, 1.25f, 1);
                            Wizard.castBuff(player.world, player, 0xD69007);

                        } else if (progress < 10) {
                            if (!player.world.isRemote)
                                Aterna.translate(player, true, "mana.keep_trying" + progress);
                        }

                        if (data.synchronisedRandom.nextFloat() < chance / 100f) {
                            soul.learnSpell(player, spell);
                            fail(player, spell, event.getModifiers());
                            EntityUtils.playSoundAtPlayer(player,
                                    WizardrySounds.MISC_SPELL_FAIL, WizardrySounds.SPELLS, 1, 1);
                            cancel(event);
                            if (event.getWorld().isRemote) {
                                Vec3d centre = event.getCaster().getPositionEyes(1).add(event.getCaster().getLookVec());

                                for (int i = 0; i < 5; i++) {
                                    double x = centre.x + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                                    double y = centre.y + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                                    double z = centre.z + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                                    event.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
                                }
                            }
                            return;
                        }
                    }

                    if (Tales.addon.learning) soul.learnSpell(player, spell);
                }
            }
        }
    }

    public static void fail(EntityPlayer player, Spell spell, SpellModifiers modifiers){
        ItemStack stack = Thief.getSpellCasting(player);
        if (player.isCreative()) return;
        Forfeit forfeit = Forfeit.getRandomForfeit(WizardData.get(player).synchronisedRandom, spell.getTier(), spell.getElement());

        if(forfeit == null){ // Should never happen, but just in case...
            return;
        }

        forfeit.apply(player.world, player);

        WizardryAdvancementTriggers.spell_failure.triggerFor(player);

        EntityUtils.playSoundAtPlayer(player, forfeit.getSound(), WizardrySounds.SPELLS, 1, 1);

        if(stack != null && !stack.isEmpty()){
            // Still need to charge the player mana or consume the scroll
            if(stack.getItem() instanceof ItemScroll){
                if(!player.isCreative()) stack.shrink(1);
            }else if(stack.getItem() instanceof IManaStoringItem){
                int cost = (int)(spell.getCost() * modifiers.get(Sage.CHANT_COST) + 0.1f); // Weird floaty rounding
                ((IManaStoringItem)stack.getItem()).consumeMana(stack, cost, player);
            }

            if (stack.getItem() instanceof ISpellCastingItem){
                WandHelper.setCurrentCooldown(stack, 40);
            }

            player.getCooldownTracker().setCooldown(stack.getItem(), 40);
        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void castSpell(SpellCastEvent.Pre event){
        if(event.getCaster() instanceof EntityPlayerMP) {
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            Spell spell = event.getSpell();
            /*if (event.getSource().equals(SpellCastEvent.Source.WAND)){
                ItemStack stack = Thief.getWandInUseUniversal(player);
                if (stack != null && WandHelper.getCurrentCooldown(stack) > 0){
                    cancel(event);
                    Aterna.translate(player, true, "mana.not_enough");
                    return;
                }
            }*/
            if (soul != null && isValid(spell) && !player.isCreative() &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana) {
                double mana = soul.getMP();
                if(mana < getCost(spell)) {
                    cancel(event);
                    Aterna.translate(player, true, "mana.not_enough");
                } else {
                    soul.addMana(player, -getCost(spell) * event.getModifiers().get(Sage.CHANT_COST));
                }
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gainManaPool(SpellCastEvent.Post event){
        if(event.getCaster() instanceof EntityPlayerMP && !event.isCanceled() && sourceList.contains(event.getSource())  &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana) {
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            Spell spell = event.getSpell();
            if(soul != null && isValid(spell)){
                double maxMana = soul.getMaxMP();
                double value = (maxMana + Math.max(Tales.mp.progression * (Tales.mp.progression_multiplier *
                        (getCost(spell) / maxMana)), Tales.mp.progression));
                soul.setMaxMP(player, Math.min(value, Tales.mp.max));
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void gainManaPoolContinuous(SpellCastEvent.Finish event){
        if(event.getCaster() instanceof EntityPlayerMP && !event.isCanceled() && sourceList.contains(event.getSource())
                && event.getSpell().isContinuous &&  Tales.mp.manaPool && !WizardryTales.hasPlayerMana) {
            EntityPlayer player = (EntityPlayer) event.getCaster();
            ISoul soul = Mana.getSoul(player);
            Spell spell = event.getSpell();
            int castingTick = event.getCount();
            if(soul != null && isValid(spell)){
                double maxMana = soul.getMaxMP();
                double value = (maxMana + Math.max(Tales.mp.progression * (Tales.mp.progression_multiplier *
                        (getContinuousProgression(spell, castingTick) / maxMana)), Tales.mp.progression));
                soul.setMaxMP(player, Math.min(value, Tales.mp.max));
            }
        }
    }

    public static double getCost(Spell spell){
        return Tales.mp.spell_multiplier * (Tales.mp.isCastingCostBased ? spell.getCost() : ((spell.getTier().ordinal()+1) * spell.getTier().ordinal()+1));
    }

    /** Distributes the given cost (which should be the per-second cost of a continuous spell) over a second and
     * returns the appropriate cost to be applied for the given tick. Currently, the cost is distributed over 2
     * intervals per second, meaning the returned value is 0 unless {@code castingTick} is a multiple of 10.*/
    protected static int getDistributedCost(Spell spell, int castingTick){
        double cost = getCost(spell);
        int partialCost;

        if(castingTick % 20 == 0){ // Whole number of seconds has elapsed
            partialCost = (int) (cost / 2 + cost % 2); // Make sure cost adds up to the correct value by adding the remainder here
        }else if(castingTick % 10 == 0){ // Something-and-a-half seconds has elapsed
            partialCost = (int) (cost/2);
        }else{ // Some other number of ticks has elapsed
            partialCost = 0; // Wands aren't damaged within half-seconds
        }

        return partialCost;
    }

    /** Version for mana pool progression **/
    protected static double getContinuousProgression(Spell spell, int castingTick){
        return getCost(spell) * (castingTick/20.0D); // Because 20 ticks is one second, then second = cost of spell; We gather all seconds!
    }

    public static boolean isValid(Spell spell){
        return spell != Spells.none && spell.getCost() > 0;
    }

    /** Makes entity disappear if it was marked! **/
    @SubscribeEvent
    public static void entityTickDisappear(LivingEvent.LivingUpdateEvent event){
        if (!(event.getEntityLiving() instanceof EntityLiving)) return;
        if (event.getEntityLiving().getEntityData().hasKey("lifetime")){
            EntityLivingBase living = event.getEntityLiving();
            int lifetime = living.getEntityData().getInteger("lifetime");
            if (living.ticksExisted >= lifetime){
                living.setDead();
                if(living.world.isRemote){
                    Random rand = new Random();
                    for(int i = 0; i < 15; i++){
                        living.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, living.posX + rand.nextFloat() - 0.5f,
                                living.posY + rand.nextFloat() * 2, living.posZ + rand.nextFloat() - 0.5f, 0, 0, 0);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void moveSelectedMinion(PlayerInteractEvent.RightClickItem event){
        if(!Tales.addon.command_minions) return;
        EntityPlayer player = event.getEntityPlayer();
        EnumHand hand = event.getHand();
        ItemStack stack = player.getHeldItem(hand);

        WizardData data = WizardData.get(player);
        // Selects one of the player's minions.
        if(player.isSneaking() && stack.getItem() instanceof ISpellCastingItem){
            World world = event.getWorld();
            if(!world.isRemote && data != null){
                RayTraceResult rayTrace = RayTracer.standardBlockRayTrace(world, player, 16, false);

                if(rayTrace != null && rayTrace.typeOfHit == RayTraceResult.Type.BLOCK) {
                    if (data.selectedMinion != null) {
                        WeakReference<ISummonedCreature> ref = data.selectedMinion;
                        ISummonedCreature minion = ref.get();
                        BlockPos pos = rayTrace.getBlockPos().offset(rayTrace.sideHit);;

                        if (minion != null && minion.getCaster() != null) {
                            List<EntityLiving> list = Selena.getAround(world, 16, pos, EntityLiving.class, i -> minion != i || ((ISummonedCreature)i).getCaster() != minion.getCaster());
                            if (!list.isEmpty()) {
                                list.get(0).getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0f);
                                data.selectedMinion=null;
                            }
                        }
                        data.sync();
                    }

                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockOpenAngerEvent(PlayerInteractEvent.RightClickBlock event){
        // Makes wizards angry if a player breaks a block in their tower
        if(!(event.getEntityPlayer() instanceof FakePlayer) && (Tales.addon.anger_chests_opened || Tales.addon.anger_table_use)){
            Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
            if(block == Blocks.CHEST || block instanceof BlockBookshelf || (Tales.addon.anger_table_use && block == WizardryBlocks.arcane_workbench)) {
                List<EntityWizard> wizards = EntityUtils.getEntitiesWithinRadius(64, event.getPos().getX(),
                        event.getPos().getY(), event.getPos().getZ(), event.getWorld(), EntityWizard.class);

                if (!wizards.isEmpty()) {
                    for (EntityWizard wizard : wizards) {
                        if (wizard.isBlockPartOfTower(event.getPos()) && wizard.canEntityBeSeen(event.getEntityPlayer())) {
                            wizard.setRevengeTarget(event.getEntityPlayer());
                            WizardryAdvancementTriggers.anger_wizard.triggerFor(event.getEntityPlayer());
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockOpenVillagerEvent(PlayerInteractEvent.RightClickBlock event){
        // Makes wizards angry if a player breaks a block in their tower
        if(!(event.getEntityPlayer() instanceof FakePlayer) && Tales.living_sur.anger_opening_village_chests){
            Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
            if(block == Blocks.CHEST) {
                List<EntityVillager> wizards = EntityUtils.getEntitiesWithinRadius(64, event.getPos().getX(),
                        event.getPos().getY(), event.getPos().getZ(), event.getWorld(), EntityVillager.class);

                if (!wizards.isEmpty() && event.getEntityPlayer() instanceof EntityPlayerMP) {
                    Village village = getVillage((EntityPlayerMP) event.getEntityPlayer());
                    if(village.isBlockPosWithinSqVillageRadius(event.getPos())) {
                        for (EntityVillager villager : wizards) {
                            if (villager.canEntityBeSeen(event.getEntityPlayer())) {
                                villager.setRevengeTarget(event.getEntityPlayer());
                            }
                        }
                        List<EntityIronGolem> golems = EntityUtils.getEntitiesWithinRadius(64, event.getPos().getX(),
                                event.getPos().getY(), event.getPos().getZ(), event.getWorld(), EntityIronGolem.class);
                        for (EntityIronGolem golem : golems) {
                            if (golem.canEntityBeSeen(event.getEntityPlayer()) && golem.getVillage() == village) {
                                golem.setRevengeTarget(event.getEntityPlayer());
                            }
                        }
                        village.modifyPlayerReputation(event.getEntityPlayer().getUniqueID(), -5);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void rightClickBook(PlayerInteractEvent.RightClickItem event){
        if(!Tales.addon.cast_with_book) return;
        EntityPlayer living = event.getEntityPlayer();
        if ((!Tales.mp.manaPool || !Tales.mp.noMoreManaUse)) {
            ItemStack stack = Thief.getItem(living, i -> i.getItem() instanceof IManaStoringItem && i.getItem() instanceof ISpellCastingItem);
            if (stack != null && living.isSneaking()) {
                ISpellCastingItem casting = (ISpellCastingItem) stack.getItem();
                if (event.getItemStack().getItem() instanceof ItemSpellBook) {
                    Spell spell = Spell.byMetadata(event.getItemStack().getMetadata());
                    if (spell != Spells.none) {
                        SpellModifiers modifiers = new SpellModifiers();
                        if (casting.canCast(stack, spell, living, living.getActiveHand(), 0, modifiers)) {
                            casting.cast(stack, spell, living, living.getActiveHand(), 0, modifiers);
                            living.getCooldownTracker().setCooldown(stack.getItem(), 20);
                            living.getCooldownTracker().setCooldown(event.getItemStack().getItem(), 40);
                            cancel(event);
                        }
                    }
                }
            }
        }else{
            if (event.getItemStack().getItem() instanceof ItemSpellBook) {
                Spell spell = Spell.byMetadata(event.getItemStack().getMetadata());

                SpellModifiers modifiers = new SpellModifiers();
                int seconds = 5;
                int ticks = seconds * 20;
                // Mana cost
                int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
                if(spell.isContinuous) cost = cost * seconds;

                // If anything stops the spell working at this point, nothing else happens.
                if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, living, modifiers)))
                    return;

                cost= (int) (cost * modifiers.get(Sage.CHANT_COST) * 1.5f + 0.1f); // HARDCODED FOR A REASON OF BALANCE
            if(spell.isContinuous){

                WizardData data = WizardData.get(living);

                // Events/packets for continuous spell casting via commands are dealt with in WizardData.
                if(data != null){
                    if(data.isCasting()){
                        data.stopCastingContinuousSpell(); // I think on balance this is quite a nice feature to leave in
                    }

                    if(!useMana(living, cost)) return;
                    data.startCastingContinuousSpell(spell, modifiers, ticks);
                }
            }else{
                if(!useMana(living, cost)) return;

                if(spell.cast(event.getWorld(), living, EnumHand.MAIN_HAND, 0, modifiers)){

                    MinecraftForge.EVENT_BUS.post(new SpellCastEvent
                            .Post(SpellCastEvent.Source.OTHER, spell, living, modifiers));

                    if(spell.requiresPacket()){
                        // Sends a packet to all players in dimension to tell them to spawn particles.
                        // Only sent if the spell succeeded, because if the spell failed, you wouldn't
                        // need to spawn any particles!
                        IMessage msg = new PacketCastSpell.Message(living.getEntityId(), null, spell, modifiers);
                        WizardryPacketHandler.net.sendToDimension(msg, living.world.provider.getDimension());
                        living.getCooldownTracker().setCooldown(event.getItemStack().getItem(), 40);
                    }
                }
            }
            }
        }
    }

    public static Village getVillage(EntityPlayerMP player) {
        return player.world.getVillageCollection().getNearestVillage(player.getPosition(), 15);
    }

    @SubscribeEvent
    public static void talkingWizards(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START && Solver.doEvery(event.player, Tales.living_sur.talk_time) && Tales.living_sur.wizard_talk){
            EntityPlayer player = event.player;
            List<EntityLivingBase> list = Selena.getAround(player.world, 16, player.getPosition(), EntityLivingBase.class,
                    e -> !(e instanceof EntityWizard));
            if(list.isEmpty()) return;
            World world = player.world;
            EntityLivingBase wizard = Selena.findNearestLiving(player.getPositionVector(), list);
            if(wizard instanceof EntityWizard) {
                double distance = player.getDistance(wizard);
                EntityLivingBase target = ((EntityWizard) wizard).getAttackTarget();
                if (!player.canEntityBeSeen(wizard)) return;
                if (distance <= 6 && target == null) {
                    String msg = Tales.living_sur.chat[Solver.randInt(0, Tales.living_sur.chat.length - 1)];
                    TextComponentTranslation text = new TextComponentTranslation("entity.talk.wizard", msg);
                    if (!world.isRemote) {
                        player.sendMessage(text);
                    }
                }
                if (target instanceof EntityMob) {
                    String msg = Tales.living_sur.monsters[Solver.randInt(0, Tales.living_sur.monsters.length - 1)];
                    TextComponentTranslation text = new TextComponentTranslation("entity.talk.wizard", msg);
                    if (!world.isRemote) {
                        player.sendMessage(text);
                    }
                } else if (target instanceof EntityPlayer) {
                    String msg = Tales.living_sur.players[Solver.randInt(0, Tales.living_sur.players.length - 1)];
                    TextComponentTranslation text = new TextComponentTranslation("entity.talk.wizard", msg);
                    if (!world.isRemote) {
                        player.sendMessage(text);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void AllyWithUndeadEvent(LivingSetAttackTargetEvent event){
        if(event.getTarget() != null) {
            EntityLivingBase target = event.getTarget();
            boolean potion = target.isPotionActive(WizardryPotions.curse_of_undeath);
            if (potion && !Tales.addon.curses_passive) return;
            if (Race.is(target, Race.undead)) potion = true;
            if (potion && event.getEntityLiving().isEntityUndead()){
                if (event.getEntityLiving().getRevengeTarget() != event.getTarget() && event.getEntityLiving() instanceof EntityLiving)
                    ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
            }
        }
    }

    /**
     * Do curses exist after death?
     */
    @SubscribeEvent
    public static void CurseMigration(PlayerEvent.Clone event){
        if(Tales.addon.curse_migration && event.getEntityPlayer() instanceof EntityPlayerMP) {
            EntityPlayer copy = event.getOriginal();
            EntityPlayer orig = event.getEntityPlayer();
            Collection<PotionEffect> effects = copy.getActivePotionEffects();
            effects.removeIf(e -> !(e.getPotion() instanceof Curse));
            for (PotionEffect effect : effects) {
                orig.addPotionEffect(effect);
            }
        }
    }


    @SubscribeEvent
    public static void mobDrop(LivingDropsEvent event){
        if(!Tales.addon.monsters_drop_crystals) return;
        if(event.getEntityLiving() instanceof IMob && (event.getSource().getTrueSource() instanceof EntityPlayer ||
                event.getSource().getTrueSource() instanceof IProjectile || event.getSource().getTrueSource() instanceof IThrowableEntity)){
            EntityLivingBase living = event.getEntityLiving();
            if(!event.getDrops().isEmpty()){
                EntityItem item = new EntityItem(living.getEntityWorld(), living.posX, living.posY, living.posZ);
                ItemStack stack = getCrystalDrop(living.getMaxHealth(), !living.isNonBoss());
                if(stack != null) {
                    item.setItem(stack);
                    item.setPickupDelay(10);
                    event.getDrops().add(item);
                }
            }
        }
    }

    public static ItemStack getCrystalDrop(float maxHP, boolean boss){
        ItemStack stack=null;
        if(maxHP > 0) {
            stack = new ItemStack(WizardryItems.crystal_shard);
            if(maxHP >= 15) {
                stack = new ItemStack(Solver.chance(60) ? WizardryItems.crystal_shard : WizardryItems.magic_crystal);
                if(maxHP >= 30){
                    stack = new ItemStack(WizardryItems.magic_crystal);
                    if(maxHP >= 60) {
                        stack = new ItemStack(Solver.chance(65) ? WizardryItems.magic_crystal : WizardryItems.grand_crystal);
                        if (maxHP >= 150 || (maxHP >= 100 && boss)) {
                            stack = new ItemStack(WizardryItems.grand_crystal);
                        }
                    }
                }
            }
        }
        return stack;
    }

    @SubscribeEvent
    public static void tickEvent(TickEvent.PlayerTickEvent event){
        if(Tales.addon.spellbook_in_water && event.phase == TickEvent.Phase.START && Solver.doEvery(event.player.ticksExisted, 1)){
            EntityPlayer player = event.player;
            ItemStack stack = Thief.getInHands(player, i -> i.getItem() instanceof ItemSpellBook);
            if(stack != null && player.isInWater()){
                stack.shrink(1);
                player.addItemStackToInventory(new ItemStack(WizardryItems.ruined_spell_book));
            }
        }
    }

}
