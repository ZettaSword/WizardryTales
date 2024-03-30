package astramusfate.wizardry_tales.spells;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.EntityManaBomb;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.spells.list.*;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.projectile.EntitySmokeBomb;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellProjectile;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(WizardryTales.MODID)
@Mod.EventBusSubscriber
public class TalesSpells {
    @Nonnull
    @SuppressWarnings("ConstantConditions")
    private static <T> T placeholder(){ return null; }

    public static final Spell water_healing = placeholder();
    public static final Spell great_water_healing = placeholder();
    public static final Spell wind_cut = placeholder();
    public static final Spell wind_slash = placeholder();
    public static final Spell focused_teleportation = placeholder();
    public static final Spell necro_attack = placeholder();

    public static final Spell vile_blood = placeholder();

    //1.4.0!
    public static final Spell entangle = placeholder();
    public static final Spell summon_ember = placeholder();
    public static final Spell leaf_disguise = placeholder();
    public static final Spell burning_disease = placeholder();
    public static final Spell blooming = placeholder();
    public static final Spell binding = placeholder();
    public static final Spell conjure_ice_halberd = placeholder();
    public static final Spell storm_armour = placeholder();

    //2.0.0
    public static final Spell mantis_agility = placeholder();
    public static final Spell mana_bomb = placeholder();
    public static final Spell teleportation_curse = placeholder();

    //2.2.1
    public static final Spell chanting = placeholder();

    //2.2.6
    public static final Spell allocate = placeholder();
    public static final Spell befriend = placeholder();

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Spell> event) {
        IForgeRegistry<Spell> reg = event.getRegistry();
        //Wind Spells
        if(!Tales.addon.spells) return;
        reg.register(new WindCut());
        reg.register(new WindSlash());

        reg.register(new Meow());
        reg.register(new Locating());
        reg.register(new WaterHealing());
        reg.register(new EarthBending());
        reg.register(new NecroAttackSpell());
        reg.register(new GreaterWaterHealing());

        reg.register(new FocusedTeleportation());

        //1.4.0!
        reg.register(new Entangle());
        reg.register(new SummonEmber());
        reg.register(new LeafDisguise());
        reg.register(new BurningDisease());
        reg.register(new Blooming());
        reg.register(new Binding());
        reg.register(new ConjureIceHalberd());
        reg.register(new StormArmour());

        //2.0.0!
        reg.register(new MantisAgility());
        reg.register(new TeleportationCurse());
        reg.register(new SpellProjectile<EntityManaBomb>(WizardryTales.MODID,"mana_bomb", EntityManaBomb::new){
            @Override
            public boolean applicableForItem(Item item) {
                return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
            }
        }.addProperties(Spell.BLAST_RADIUS, Spell.EFFECT_DURATION).soundValues(0.5f, 0.4f, 0.2f));


        //2.2.1
        reg.register(new Chanting());

        //2.2.6
        reg.register(new Allocate());
        reg.register(new Befriend());
    }

    public static Spell getSpell(String modid, String spell_id){
        ResourceLocation id = new ResourceLocation(modid, spell_id);
        if(Spell.registry.containsKey(id)){
            return Spell.registry.getValue(id);
        }
        return Spells.none;
    }

    public static Spell getSpell(String spell_id){
        ResourceLocation id = new ResourceLocation(WizardryTales.MODID, spell_id);
        if(Spell.registry.containsKey(id)){
            return Spell.registry.getValue(id);
        }
        return Spells.none;
    }

}
