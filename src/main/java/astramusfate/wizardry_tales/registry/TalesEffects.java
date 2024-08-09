package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Tenebria;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.potion.*;
import astramusfate.wizardry_tales.spells.TalesSpells;
import astramusfate.wizardry_tales.spells.list.BurningDisease;
import astramusfate.wizardry_tales.spells.list.Entangle;
import astramusfate.wizardry_tales.spells.list.TeleportationCurse;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.Objects;
import java.util.UUID;

@GameRegistry.ObjectHolder(WizardryTales.MODID)
@Mod.EventBusSubscriber
public class TalesEffects {

    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder() { return null; }

    public static final Potion ore_mark = placeholder();
    public static final Potion entangled = placeholder();
    public static final Potion leaf_disguise = placeholder();
    public static final Potion burning_disease = placeholder();
    public static final Potion wrapped = placeholder();
    public static final Potion storm_armour = placeholder();

    //2.0.0
    public static final Potion mantis_agility = placeholder();
    public static final Potion teleportation_curse = placeholder();

    //2.2.1
    public static final Potion mage_hand = placeholder();

    public static final Potion magic_exhaust = placeholder();

    //2.2.7
    public static final Potion repel = placeholder();
    public static final Potion phasing = placeholder();
    public static final Potion charm = placeholder();


    @SubscribeEvent
    public static void register(RegistryEvent.Register<Potion> event) {
        IForgeRegistry<Potion> registry = event.getRegistry();
        registerPotion(registry, "ore_mark", new OreMarkPotion());
        registerPotion(registry, "entangled", new EntangledEffect());
        registerPotion(registry, "leaf_disguise", new LeafDisguiseEffect());
        registerPotion(registry, "burning_disease", new BurningDiseaseEffect());
        registerPotion(registry, "storm_armour", new StormArmourEffect());
        registerPotion(registry, "mantis_agility", new MantisAgilityEffect());
        registerPotion(registry, "teleportation_curse", new TeleportationCurseEffect());
        registerPotion(registry, "mage_hand", new MageHandEffect());

        registerPotion(registry, "magic_exhaust", new TalesPresetEffect("magic_exhaust", "magic_exhaust", 0xD65B00){
            @Override
            public void applyAttributeModifiers() {
                UUID id = UUID.fromString("faa112b1-33f0-5a43-7f3f-06ad62d384f9");
                this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), -0.2, Solver.MULTIPLY);
                this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, id.toString(), -0.2, Solver.MULTIPLY);
            }
        });

        registerPotion(registry, "wrapped", new TalesPresetEffect("wrapped", "curse_of_nausea", Color.MAGENTA.getRGB()){
            @Override
            public void applyAttributeModifiers() {
                UUID id = UUID.fromString("faa141b0-33f0-4a64-8f2f-06ad62d384f9");
                this.registerPotionAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, id.toString(), -0.2, Solver.MULTIPLY);
                this.registerPotionAttributeModifier(SharedMonsterAttributes.ATTACK_SPEED, id.toString(), -0.2, Solver.MULTIPLY);
            }
        });

        //2.2.7
        registerPotion(registry, "repel", new RepelEffect());
        registerPotion(registry, "phasing", new PhasingEffect());
        registerPotion(registry, "charm", new CharmEffect());

    }

    public static void registerPotion(IForgeRegistry<Potion> registry, String name, Potion potion) {
        potion.setRegistryName(WizardryTales.MODID, name);
        potion.setPotionName("potion." + Objects.requireNonNull(potion.getRegistryName()).toString());
        registry.register(potion);
    }

    public static boolean is(PotionEffect potion, Potion effect){
        return potion.getPotion() == effect;
    }

    public static boolean is(PotionEffect potion, Potion... effects){
        boolean bool=false;
        for(Potion effect : effects) {
            if(potion.getPotion() == effect) bool= true;
            break;
        }
        return bool;
    }

    public static boolean isUndead(EntityLivingBase target){
        return Race.isUndead(target);
    }

    public static boolean isUndeadInclMobs(EntityLivingBase target){
        return target.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD ||
                target.isPotionActive(WizardryPotions.curse_of_undeath)
                || Race.isUndead(target);
    }
    /*
    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event){
        if(event.getSource() != null){
            // Prevents all blockable damage while transience is active (not includes Wither damage from Necromancy spells, and damage from Undead entities)
            if(event.getEntityLiving().isPotionActive(TalesEffects.angels_bless)
                    && (event.getSource() != DamageSource.OUT_OF_WORLD &&
                    event.getSource() != DamageSource.MAGIC &&
                    Arrays.stream(MagicDamage.DamageType.values()).anyMatch( e -> event.getSource().damageType.equals(e.name()) &&
                            !event.getSource().damageType.equals(MagicDamage.DamageType.RADIANT.name())))){
                event.setCanceled(true);
            }

            // Prevents angels entities from causing any damage to not-undeads
            if(event.getSource().getTrueSource() instanceof EntityLivingBase
                    && ((EntityLivingBase)event.getSource().getTrueSource()).isPotionActive(TalesEffects.angels_bless)
                    && !event.getEntityLiving().isEntityUndead() && !isCursedUndeadly(event.getEntityLiving())){
                event.setCanceled(true);
            }
        }
    }*/

    @SubscribeEvent
    public static void onLivingHurtEvent(LivingHurtEvent event){
        EntityLivingBase living = event.getEntityLiving();
        if(living.isPotionActive(TalesEffects.entangled)) {
            if (event.getSource().isFireDamage() || event.getSource() == DamageSource.IN_FIRE
                    || event.getSource() == DamageSource.ON_FIRE || event.getSource().damageType.equals(MagicDamage.DamageType.FIRE.name())) {
                event.setAmount(event.getAmount() *
                       (1.0f + (TalesSpells.entangle.getProperty(Entangle.FIRE_DAMAGE).floatValue()/100f)));
            }
        }

        if(living.isPotionActive(TalesEffects.leaf_disguise)){
            living.removePotionEffect(TalesEffects.leaf_disguise);
        }

        if(event.getSource().getTrueSource() instanceof EntityLivingBase
        && EntityUtils.isMeleeDamage(event.getSource())){
            EntityLivingBase attacker = ((EntityLivingBase)event.getSource().getTrueSource());

            if (attacker.isPotionActive(TalesEffects.storm_armour) && attacker.world.canBlockSeeSky(living.getPosition().up())){
                EntityLightningBolt lightning = new EntityLightningBolt(attacker.world,
                        living.getPosition().getX(), living.getPosition().getY(),
                        living.getPosition().getZ(), true);
                Tenebria.create(attacker.world, lightning);
                if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, living) && living.isEntityAlive()) {
                    event.setAmount(event.getAmount() + 4);
                }
            }

            if (attacker.isPotionActive(TalesEffects.leaf_disguise)) {
                ((EntityLivingBase) event.getSource().getTrueSource()).removePotionEffect(TalesEffects.leaf_disguise);
                removeDisguise(event.getSource().getTrueSource().world, (EntityLivingBase) event.getSource().getTrueSource(), 8);
                event.setAmount(event.getAmount() * 1.5f);
            }
        }

        if(living.isPotionActive(TalesEffects.burning_disease)){
            if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, living) && living.isEntityAlive()) {
                event.setAmount(event.getAmount() + TalesSpells.burning_disease.getProperty(Spell.DAMAGE).floatValue());

                PotionEffect effect = living.getActivePotionEffect(TalesEffects.burning_disease);
                Alchemy.decreaseDuration(living, effect, 40);

                if (Solver.chance(TalesSpells.burning_disease.getProperty(BurningDisease.PROC_CHANCE).intValue()
                        + ((living.getHealth() <= living.getMaxHealth() * 0.7f && living.isBurning()) ? 40 : 0))){ // by default 30% + 40% if target is lower then 70%
                    event.setAmount(event.getAmount() + TalesSpells.burning_disease.getProperty(BurningDisease.PROC_DAMAGE).floatValue());
                    living.setFire(2);
                }
            }
        }
    }

    // This event is called every tick, not just when a movement key is pressed
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onInputUpdateEvent(InputUpdateEvent event){
        // Prevents the player moving when paralysed
        if(event.getEntityPlayer().isPotionActive(TalesEffects.magic_exhaust)
                && Objects.requireNonNull(event.getEntityPlayer().getActivePotionEffect(TalesEffects.magic_exhaust)).getAmplifier() > 3){
            event.getMovementInput().jump = false;
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractEvent(PlayerInteractEvent event){
        if(event.getEntityPlayer().isPotionActive(TalesEffects.leaf_disguise)
                && !(event.getItemStack().getItem() instanceof ISpellCastingItem)){
            event.getEntityPlayer().removePotionEffect(TalesEffects.leaf_disguise);
            removeDisguise(event.getEntityPlayer().world, event.getEntityPlayer(), 8);
        }
    }

    @SubscribeEvent
    public static void onPlayerPickedAsTarget(LivingSetAttackTargetEvent event){
        if(event.getTarget() instanceof EntityPlayer && event.getTarget().isPotionActive(TalesEffects.leaf_disguise) && event.getTarget().isSneaking()) {
            if (event.getEntityLiving().getRevengeTarget() != event.getTarget() && event.getEntityLiving() instanceof EntityLiving) {
                ((EntityLiving)event.getEntityLiving()).setAttackTarget(null);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSpellcast(SpellCastEvent.Pre event){
        if(event.getCaster() != null){
            if(event.getCaster().isPotionActive(TalesEffects.leaf_disguise)){
                event.getCaster().removePotionEffect(TalesEffects.leaf_disguise);
                removeDisguise(event.getWorld(), event.getCaster(), 8);
            }
        }
    }

    public static void removeDisguise(World world, EntityLivingBase target, int count){
        if(world.isRemote){
            for(int i = 0; i < count; i++) {
                double x = target.posX - 0.25 + world.rand.nextDouble() / 2;
                double y = target.posY + world.rand.nextDouble();
                double z = target.posZ - 0.25 + world.rand.nextDouble() / 2;
                ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).time(20).spawn(world);
            }
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingUpdateEvent event){
        if(Solver.doEvery(event.getEntityLiving().ticksExisted, 10)) {
            if (event.getEntityLiving().isEntityAlive()) {
                EntityLivingBase entity = event.getEntityLiving();
                if (entity.isPotionActive(TalesEffects.teleportation_curse)) {
                    World world = entity.world;
                    double minRadius = TalesSpells.teleportation_curse.getProperty("minimum_teleport_distance").doubleValue();
                    double maxRadius = TalesSpells.teleportation_curse.getProperty("maximum_teleport_distance").doubleValue();
                    double radius = (minRadius + world.rand.nextDouble() * maxRadius-minRadius);

                    TeleportationCurse.teleport(entity, world, radius);
                }
            }
        }
    }
}
