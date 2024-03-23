package astramusfate.wizardry_tales.data;

import astramusfate.wizardry_tales.WizardryTales;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.Arrays;
import java.util.Locale;

@GameRegistry.ObjectHolder(WizardryTales.MODID)
@Config(modid = WizardryTales.MODID, type = Config.Type.INSTANCE, name = "WizardryTales-2.0+")
public class Tales {

    @Config.Comment("Mana Pool System, which makes player really become master wizard slowly!")
    @Config.Name("Player Mana System")
    public static ManaPoolSystem mp = new ManaPoolSystem();

    public static class ManaPoolSystem {
        @Config.Comment("If it is true - then mana pool exist")
        @Config.Name("0: Active")
        public boolean manaPool = true;

        @Config.Comment("If it is true - then we see mana pool as bar")
        @Config.Name("0: Bar")
        public boolean manaPoolBar = true;
        @Config.Comment("If it is true - then mana from wands when you cast spell - will not be used")
        @Config.Name("Is items mana not used anymore?")
        public boolean noMoreManaUse = true;
        @Config.Comment("If it is true - then casting cost bases on spell cost, if it's false - then it bases of spell tier")
        @Config.Name("Is casting Cost based on Spell Cost?")
        public boolean isCastingCostBased = true;

        @Config.Comment("If it is true - then if your mana is near 0, you'll get debuffs.")
        @Config.Name("Are you get debuff when mana is low? ")
        public boolean lowOnMana = true;
        @Config.RangeDouble(min = 0.0D)
        @Config.Comment({"Allows to change cost of using spells, by multiplying final cost on this value (cost * this)",
                "Cost of spell is calculated: this * (Spell-Tier * Spell-Tier)", "So setting it to 0.0 will make spellcasting not use Mana Pool at all"})
        @Config.Name("1: Spell Cost Multiplier")
        public double spell_multiplier = 1.0D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Allows to change cost of using Chanting, by multiplying cost of each chant part on this value (cost * this)")
        @Config.Name("1: Chanting Cost Multiplier")
        public double chant_multiplier = 0.5D;

        @Config.RangeDouble(min = 1.0D)
        @Config.Comment("Max Mana in your pool available from the beginning. It'll grow as you cast spells up to Max. ")
        @Config.Name("1: Mana Pool Initial")
        public double initial = 10.0D;

        @Config.RangeDouble(min = 1.0D)
        @Config.Comment("Max Mana in your pool available at all. Can't be higher then this [value]")
        @Config.Name("1: Mana Pool Max")
        public double max = 2500.0D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment({"Each successful cast increases Mana Pool, this way you can define how much Max mana added. Can't be higher then [Mana Pool Max]", "After successful cast, your progress is",
                "[This] * ([Progression Multiplier] * (spell-cost-for-pool / player-current-max-mana))"})
        @Config.Name("2: Mana Pool Progression")
        public double progression = 0.02D;

        @Config.RangeDouble(min = 1.0D)
        @Config.Comment({"Each successful cast increases Mana Pool, this way you can define how much Max mana added. Can't be higher then [Mana Pool Max]", "After successful cast, your progress is",
                "[This] * ([Progression Multiplier] * (spell-cost-for-pool / player-current-max-mana))"})
        @Config.Name("2: Mana Pool Progression Multiplier")
        public double progression_multiplier = 4.0D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Each [Mana Pool Regeneration Frequency] seconds regenerates this [value] to your mana pool")
        @Config.Name("2: Mana Pool Regeneration")
        public double regeneration = 0.1D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("The maximum plus to your mana pool regen you can get. It scales of how close you are to Mana Pool Max")
        @Config.Name("2: Mana Pool Regeneration Bonus")
        public double bonus_regen = 1D;

        @Config.RangeDouble(min = 0.1D)
        @Config.Comment("Frequency of Mana Regeneration in seconds (0.5 = 10 ticks)")
        @Config.Name("2: Mana Pool Regeneration Seconds Frequency")
        public double seconds_frequency = 0.2D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Spell Cost * This multiplier for Casting ring casting")
        @Config.Name("3: Casting Ring Cost Multiplier")
        public double casting_ring_cost = 1.0D;

        @Config.RangeInt(min = 0)
        @Config.Comment("Not to allow just spam too much with cast. 20 = 1 second")
        @Config.Name("3: Casting Ring Cooldown")
        public int casting_ring_cooldown = 20;

        @Config.RangeInt(min = 0)
        @Config.Comment("How much it gives mana upon usage?")
        @Config.Name("3: Mana Flask: Small")
        public int mana_flask_small = 75;

        @Config.RangeInt(min = 0)
        @Config.Comment("How much it gives mana upon usage?")
        @Config.Name("3: Mana Flask: Medium")
        public int mana_flask_medium = 350;

        @Config.RangeInt(min = 0)
        @Config.Comment("How much it gives mana upon usage?")
        @Config.Name("3: Mana Flask: Large")
        public int mana_flask_large = 1400;
    }

    @Config.Comment("Mana Pool System, which makes player not looks like master wizard from beginning!")
    @Config.Name("Visual Effects")
    public static VisualEffects effects = new VisualEffects();

    public static class VisualEffects{
        @Config.RequiresMcRestart
        @Config.Comment("Will Visual Effects appear at all?")
        @Config.Name("0: Is Enabled?")
        public boolean enabled=true;

        @Config.Comment("Does circles appear on Summon of someone?")
        @Config.Name("1: On Summon")
        public boolean onSummon = true;

        @Config.Comment("Magical Circles that appear on continuous or charge-up-required spells")
        @Config.Name("1: Circles appearing on Long spells")
        public boolean long_spelled =true;

        @Config.Comment("Magical Circles that appear on continuous or charge-up-required spells")
        @Config.Name("1: Vertical Circles appearing on Long spells")
        public boolean vertical_long_spelled =true;

        @Config.Comment("When you use continuous spell - you'll see circle rotating")
        @Config.Name("1: Render Continuous Circles")
        public boolean rendered_circles =true;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Distance from the player to point where circle appears")
        @Config.Name("Vertical Circles Distance")
        public double look_distance = 2.5D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Offset by Y from player foot to where circle appears")
        @Config.Name("Vertical Circles Y Offset")
        public double look_y_offset = 1.4D;

        @Config.RangeDouble(min = 0.0D)
        @Config.Comment("Offset by Y from player foot to where circle appears")
        @Config.Name("Circles Y Offset")
        public double y_offset = 0.0D;

        @Config.RangeDouble(min = 0.1, max = 20.0)
        @Config.Comment("Size of Circles created. Not related to Vertical ones")
        @Config.Name("Circles Size")
        public double circle_size=2.0D;

        @Config.RangeDouble(min = 0.1, max = 20.0)
        @Config.Comment("Size of Vertical Circles created. Not related to not-Vertical ones")
        @Config.Name("Vertical Circles Size")
        public double vertical_circle_size=1.0D;
    }

    @Config.Comment("Change their different options!")
    @Config.Name("Entities of Tales")
    public static Entities entities = new Entities();

    public static class Entities{

        @Config.Comment("Biomes where hostile Tales entities can't spawn in")
        @Config.Name("Restricted to spawn Biomes")
        public String[] mobSpawnBiomeBlacklist = new String[]{"mushroom_island", "mushroom_island_shore"};

        @Config.Comment("Dimensions where hostile Tales entities can spawn in")
        @Config.Name("Allowed to spawn Dimensions")
        public int[] mobSpawnDimensions = {0};
        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Mushroom: Spawnrate")
        public int mushroomSpawnRate = 50;

        @Config.RequiresMcRestart
        @Config.Comment("Biomes where they can spawn")
        @Config.Name("Mushroom: Allowed to spawn Biomes")
        public String[] mushroomBiomeWhitelist = new String[]{"roofed_forest", "mutated_roofed_forest", "mushroom_island", "mushroom_island_shore"};

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Big Mushroom: Spawnrate")
        public int big_mushroomSpawnRate = 5;

        @Config.RequiresMcRestart
        @Config.Comment("Biomes where they can spawn")
        @Config.Name("Big Mushroom: Allowed to spawn Biomes")
        public String[] big_mushroomBiomeWhitelist = new String[]{"roofed_forest", "mutated_roofed_forest", "mushroom_island", "mushroom_island_shore"};

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Envenomed Blade: Spawnrate")
        public int envenomed_bladeSpawnRate = 30;

        @Config.RequiresMcRestart
        @Config.Comment("Biomes where they can spawn")
        @Config.Name("Envenomed Blade: Allowed to spawn Biomes")
        public String[] envenomed_bladeBiomeWhitelist = new String[]{"roofed_forest", "mutated_roofed_forest", "swamp", "swampland", "mutated_swampland"};


        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Lightning Spiders Spawnrate")
        public int lightningSpiderSpawnRate = 10;

        @Config.Comment("Biomes where Elemental spiders can spawn")
        @Config.Name("Allowed to spawn Biomes for Elemental spiders")
        public String[] spidersSpawnBiomeWhitelist = new String[]{"plains","forest", "forest_hills","birch_forest_hills","taiga_hills", "roofed_forest", "mutated_roofed_forest","mutated_birch_forest", "mutated_forest", "birch_forest", "vampirism:vampireforest"};

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Spellcasting Witch Spawnrate")
        public int spellWitchSpawnRate = 5;

        @Config.Comment("Biomes where Spellcasting Witch can spawn")
        @Config.Name("Allowed to spawn Biomes for Spellcasting Witch")
        public String[] spellWitchBiomeWhitelist = new String[]{"forest", "forest_hills","birch_forest_hills","taiga_hills", "roofed_forest", "mutated_roofed_forest","mutated_birch_forest", "mutated_forest", "birch_forest", "vampirism:vampireforest", "swamp"};

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Earth Wolf Spawnrate")
        public int earthWolfSpawnRate = 10;

        @Config.RequiresMcRestart
        @Config.RangeInt(min = 0, max = 100)
        @Config.SlidingOption()
        @Config.Comment("Higher numbers mean more them will spawn. 5 is equivalent to witches, 100 is equivalent to zombies, skeletons and creepers. Set to 0 to disable spawning entirely.")
        @Config.Name("Earth Wolf Spawnrate")
        public int thunderWolfSpawnRate = 10;

        @Config.Comment("Biomes where Elemental wolfs can spawn")
        @Config.Name("Allowed to spawn Biomes for Elemental wolfs")
        public String[] elementalWolfsBiomeWhitelist = new String[]{"forest", "forest_hills","birch_forest_hills","taiga_hills", "roofed_forest", "mutated_roofed_forest","mutated_birch_forest", "mutated_forest", "birch_forest"};


    }

    @Config.Comment("Some things for balance")
    @Config.Name("Chanting")
    public static Chanting chanting = new Chanting();

    public static class Chanting{

        @Config.Comment("If it is true - then Chanting can cause throwing the error")
        @Config.Name("Debug Mode")
        public boolean debug = false;
        @Config.Comment("You can use 'apply' action to apply to yourself or others potion effects")
        @Config.Name("Apply effect Blacklist")
        public String[] applyBlacklist = new String[]{"minecraft:instant_damage", "minecraft:instant_health", "minecraft:wither",
                "wizardry_tales:magic_exhaust", "potioncore:flight"};

        @Config.Comment("You can say 'summon' + minion id, to summon the minion you want")
        @Config.Name("Action: Minion Blacklist")
        public String[] minionBlacklist = new String[]{"example_modid:example_entry", "ebwizardry:example"};

        @Config.Comment("You can say 'summon' + construct id, to summon the construct you want")
        @Config.Name("Action: Construct Blacklist")
        public String[] constructBlacklist = new String[]{"ebwizardry:zombie_spawner"};

        @Config.Comment("You can say 'replicate' + spell id, to replicate spell you discovered and have learned.")
        @Config.Name("Action: Replication Blacklist")
        public String[] replicationBlacklist = new String[]{"modid:example_spell"};
    }

    @Config.Comment("Stats and their configs! Like Strength stat, Agility, and more.")
    @Config.Name("Stats System")
    public static StatsSystem stats = new StatsSystem();

    public static class StatsSystem{

        @Config.Comment("If not allowed, status card will not work at all and will do nothing!")
        @Config.Name("Allow Status card")
        public boolean allow_status=true;
        @Config.RangeInt(min = 1)
        @Config.Comment("Each time stat passes this value, it gives +1 to this stat value.")
        @Config.Name("1: Strength cost")
        public int str_cost = 1000;

        @Config.RangeInt(min = 1)
        @Config.Comment("Maximum value this stat can get to.")
        @Config.Name("1: Strength max value")
        public int str_max = 20;

        @Config.RangeInt(min = 1)
        @Config.Comment("Each time stat passes this value, it gives +1 to this stat value.")
        @Config.Name("1: Constitution cost")
        public int con_cost = 2000;

        @Config.RangeInt(min = 1)
        @Config.Comment("Maximum value this stat can get to.")
        @Config.Name("1: Constitution max value")
        public int con_max = 20;

        @Config.RangeInt(min = 1)
        @Config.Comment("Each time stat passes this value, it gives +1 to this stat value.")
        @Config.Name("1: Agility cost")
        public int agi_cost = 500;

        @Config.RangeInt(min = 1)
        @Config.Comment("Maximum value this stat can get to.")
        @Config.Name("1: Agility max value")
        public int agi_max = 100;

        @Config.RangeInt(min = 1)
        @Config.Comment("Each time stat passes this value, it gives +1 to this stat value.")
        @Config.Name("1: Intelligence cost")
        public int int_cost = 2000;

        @Config.RangeInt(min = 1)
        @Config.Comment("Maximum value this stat can get to.")
        @Config.Name("1: Intelligence max value")
        public int int_max = 20;
    }

    @Config.Comment("Tweaks and Additions Tales does")
    @Config.Name("Additions")
    public static Additions addon = new Additions();

    public static class Additions{

        @Config.Comment("Should we run dialogue with Tenebria when player is Reincarnated and enters first time?")
        @Config.Name("First enter Dialogue")
        public boolean first_enter_dialogue=true;

        @Config.Comment("Should we give player Tales guidebook when player first time enters world?")
        @Config.Name("First enter Book")
        public boolean first_enter_book=true;

        @Config.Comment("Should we give player EBWiz guidebook when player first time enters world?")
        @Config.Name("First enter EBWiz Book")
        public boolean first_enter_book_ebw=false;

        @Config.Comment("Should we give player Status card on first time?")
        @Config.Name("First enter Status Card")
        public boolean first_enter_status=true;

        @Config.Comment("Used by me to see what happens in mod during development. Nothing interesting for players.")
        @Config.Name("Debug (For Development)")
        public boolean debug=false;

        @Config.Comment("Testers and those who helped Tales have a special circle above their head")
        @Config.Name("Testers Perk")
        public boolean testers_perk=true;

        @Config.Comment("If enabled - Tales has spells, if disabled - Tales has no spells. Simple!")
        @Config.Name("Spells")
        public boolean spells=true;

        @Config.Comment("If enabled - You need to learn how to cast spells first to use them properly!")
        @Config.Name("Spells Learning")
        public boolean learning=false;

        @Config.Comment("If enabled - After death monsters will drop crystals basing of their max health.")
        @Config.Name("Monsters Drop Crystals?")
        public boolean monsters_drop_crystals=true;

        @Config.Comment("Now when shift+clicking with wand on block, if minion is chosen, it will make it move to it (in 16 blocks radius)")
        @Config.Name("Command Minions Additions")
        public boolean command_minions=true;

        @Config.Comment("If this true - Wizards will anger at you if you'll open their chest")
        @Config.Name("Wizards Anger at opening their Chests?")
        public boolean anger_chests_opened=true;

        @Config.Comment("If this true - Wizards will anger at you if you'll use their Arcane Workbench")
        @Config.Name("Wizards Anger at using their Arcane Workbench?")
        public boolean anger_table_use=false;

        @Config.Comment("When shift-clicked spell-book, you can cast spell using your sources of mana(wands, armor)")
        @Config.Name("Cast with Book")
        public boolean cast_with_book=false;

        @Config.Comment("Curses like Undead Curse will make Undead mobs passive to you?")
        @Config.Name("Undead Mobs Passive to Undead cursed Players?")
        public boolean curses_passive=true;

        @Config.Comment("Do Curses can migrate from one body of player to another?(This means player will still have curse after death, same with Blessings)")
        @Config.Name("Curses will migrate")
        public boolean curse_migration=true;

        @Config.Comment("If you will enter in water with spellbook in hand, it will turn ruined one...")
        @Config.Name("Ruin Spell books if in water?")
        public boolean spellbook_in_water=false;

        @Config.Comment("If enabled - Then all monsters will have leveling system which can make game harder. Is made specifically for player stats system.")
        @Config.Name("Monsters Leveling")
        public boolean monsters_leveling=true;

        @Config.Comment("If enabled - Then all summons will try to follow caster if possible.")
        @Config.Name("Summon follows Caster")
        public boolean summons_follow=true;
    }

    @Config.Comment("Change their frequency, or allowed dimensions")
    @Config.Name("Structures of Tales")
    public static Structures struct = new Structures();

    public static class Structures{
        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of Altar structure, so bigger - means lower chance")
        @Config.Name("1: Altar Rarity")
        public int altar =800;

        @Config.Comment("Allowed to spawn Altar dimensions")
        @Config.Name("1: Altar Dimensions")
        public int[] altar_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of Altar structure, so bigger - means lower chance")
        @Config.Name("1: Teleportation Anchor Rarity")
        public int anchor =1000;

        @Config.Comment("Allowed to spawn Anchor dimensions")
        @Config.Name("1: Teleportation Anchor Dimensions")
        public int[] anchor_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Teleportation Anchor(Not Peaceful) Rarity")
        public int anchor_angry =900;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Teleportation Anchor(Not Peaceful) Dimensions")
        public int[] anchor_angry_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Young Wizards Houses Rarity")
        public int novice_house =700;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Young Wizards Houses Dimensions")
        public int[] novice_house_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Flying Libraries Rarity")
        public int flying_lib =1200;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Flying Libraries Dimensions")
        public int[] flying_lib_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Flying Big Libraries Rarity")
        public int flying_lib_big =1500;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Flying Big Libraries Dimensions")
        public int[] flying_lib_big_dims={0};

        @Config.RangeDouble(min = 0.0, max = 1.0)
        @Config.Comment("The chance for wizard towers to have an evil female wizard and chest inside.")
        @Config.Name("1: Old Female Wizards Evil chance")
        public double evilLadyWizardChance = 0.2;

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Old Female Wizards Towers Rarity")
        public int lady_wizard =900;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Old Female Wizards Dimensions")
        public int[] lady_wizard_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Libraries Rarity")
        public int lib =1500;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Libraries Dimensions")
        public int[] lib_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Underground House Rarity")
        public int underground_house =900;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Underground House Dimensions")
        public int[] underground_house_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of Altar structure, so bigger - means lower chance")
        @Config.Name("1: Aterna's Shrine")
        public int shrine_healing = 1700;

        @Config.Comment("Allowed to spawn Altar dimensions")
        @Config.Name("1: Aterna's Shrine Dimensions")
        public int[] shrine_healing_dims={0};

        @Config.RangeInt(min = 20,max = 5000)
        @Config.Comment("Spawn chance of this structure, so bigger - means lower chance")
        @Config.Name("1: Spell-Witch Hut Rarity")
        public int spell_witch_hut =700;

        @Config.Comment("Allowed to spawn dimensions")
        @Config.Name("1: Spell-Witch Hut Dimensions")
        public int[] spell_witch_hut_dims={0};

        @Config.Comment("Loot injection entries")//Thanks to Dan's Ancient Spellcraft
        @Config.Name("0: Where Tales loot be found in?")
        public String[] tales_loot_entries = new String[]{
                "minecraft:chests/desert_pyramid",
                "minecraft:chests/jungle_temple",
                "minecraft:chests/stronghold_corridor",
                "minecraft:chests/stronghold_crossing",
                "minecraft:chests/stronghold_library",
                "minecraft:chests/igloo_chest",
                "minecraft:chests/woodland_mansion",
                "minecraft:chests/end_city_treasure"
        };
    }

    @Config.Comment("Different options about Talking Wizards, and some other stuff")
    @Config.Name("Living Surrounding")
    public static Surrounding living_sur = new Surrounding();

    public static class Surrounding{

        @Config.Comment("If yes - Female Wizards and Male Wizards will talk")
        @Config.Name("Do Old Wizards talk?")
        public boolean wizard_talk=true;

        @Config.Comment({"If this true - Villagers will not be okay if you open their chest when they see you", "WARNING: Potentially can make you lose Reputation at village when opening chests on Village territory, and make Golems of the Village attack you!"})
        @Config.Name("Villagers can be Angry")
        public boolean anger_opening_village_chests=false;

        @Config.RangeDouble(min = 1.0)
        @Config.Comment("How often Wizards will talk in seconds?")
        @Config.Name("Talking Interval")
        public double talk_time = 54.0;

        @Config.Comment("When they talk with you while you near them")
        @Config.Name("Chatting")
        public String[] chat={"Might I interest you in any spells, perhaps?", "Magic is everywhere, if you know where to look","There is still much to be learned about the arcane arts, adventurer",
                "Perhaps you have learned something yourself that you wish to share?", "Studying the arcane is most fascinating, don't you think?", "The duty of Wizard is to protect their house, and their hearth", "I would like to talk about Spells",
                "Cataclysm destroyed most of big buildings that was here", "I remember how it feels when you lose your loved ones", "I can't imagine how long it will take to create a new spell", "Greetings, traveller. What brings you here?",
                "Elemental Creatures are truly dangerous!", "I've seen big white spider recently, their name is White Spiders, and they manipulate the space and time around them",
                "Spellcasting Witches is my terrible nightmare...", "A lot of us become simply Mad, and becoming Angry, because of how Magic affects our old bodies... someone even just stalks others in different places",
                "Cataclysm changed everything..",
                "Someone maybe will have a way to become eternal...", "Most people do not know how this feels to lose your memories sometimes...",
                "Is it just me, or i heard strange sound over there?", "Dear traveller, you should buy something, or i will go sleep", "My memories fade...", "Weather isn't good last days"};

        @Config.Comment("When they're attacking monsters")
        @Config.Name("Attacking Monsters")
        public String[] monsters={"Be gone, foul creatures!", "Leave me alone, pests!", "Undead beings are not welcome here, shoo!", "Away with you, creatures of darkness!", "This is all I need! Get out of here, monsters!",
                "Return to the caves from whence you came, evil creatures!", "Die!"};

        @Config.Comment("When they're attacking player")
        @Config.Name("Attacking Player")
        public String[] players={"You will regret that decision, traveller!", "What do you think you are doing?!", "You will pay for your carelessness, adventurer!", "Be ready to defend yourself, villain!", "Prepare to feel my wrath!", "Your death is near!", "Die!"};

        @Config.Comment("When trying to talk with Young Wizards - they can say you this")
        @Config.Name("Young Wizards Phrases")
        public String[] young_tells={"Sorry, but i'm relaxing here!","Spells are so great thing to use!", "As teacher says: We always know what we want, but we don't.. But what actually that means...", "Ugh... failed spell creation once more!", "It's so terrible that Elemental Monsters appearing, so i even thinking to buy Scroll of Holy Barrier for my house!",
                "I have nothing to say to you", "Sometimes i totally lose my memory.. what i was doing... i'm like in a time loop, but different! I can't understand this really..", "Midnight Traders are best!", "Do you have Familiar?", "I've used my last Teleportation Scroll this week! Oh no!", "If you can teach me magic i'll be glad!"};

        @Config.Comment("When they're attacking monsters")
        @Config.Name("Young Wizard Monsters")
        public String[] young_monsters={"You shouldn't exist!", "I hate those monsters...", "Eh, once more i need to do this, i so tired of this!", "How... nevermind", "Oh, i found... nothing valuable! Yay! (I'm crying, okay?!)", "Just die already!"};

        @Config.Comment("When they're attacking player")
        @Config.Name("Young Wizard Player")
        public String[] young_players={"Please, just go away!", "WHAT THE OCTOPUS ARE YOU DOING!!!", "DIE!", "Defend!", "I'll try my best to kill you", "Your bones will be turned to dust!", "Why are you still alive?!"};

    }

    @Config.Comment("Compats for different mods to bridge Wizardry and them")
    @Config.Name("Compats")
    public static Compat compat = new Compat();

    public static class Compat {
        @Config.RequiresMcRestart
        @Config.Comment("Compat with Artemis lib, to allow changing player size with races/etc. (if false, there be no races such as Goblins for example)")
        @Config.Name("Artemis Lib")
        public boolean artemis_lib = true;
    }

    @Config.Comment("Take in mind some features locked if you have no ArtemisLib installed!")
    @Config.Name("Races Content")
    public static Races races = new Races();

    public static class Races{
        @Config.RangeDouble(min = 1.0D)
        @Config.Comment("Used in example for Slime race, to allow slowly scaling slime, set to")
        @Config.Name("Transition Time")
        public double transition=15.0D;

        @Config.Comment("If true, gives player choosing scroll on first enter")
        @Config.Name("Choose Race on first enter?")
        public boolean races_given_choice=true;
    }

    @Config.Comment("")
    @Config.Name("Remove after release")
    public static Debug debug = new Debug();

    public static class Debug{
        @Config.Comment("")
        @Config.Name("x")
        public int x =91;

        @Config.Comment("")
        @Config.Name("y")
        public int y =0;

        @Config.Comment("")
        @Config.Name("x2")
        public int x2 =20;

        @Config.Comment("")
        @Config.Name("y2")
        public int y2 =0;

        @Config.Comment("")
        @Config.Name("x3")
        public int x3 =20;

        @Config.Comment("")
        @Config.Name("y3")
        public int y3 =10;
    }

    /** Converts the given strings to an array of {@link ResourceLocation}s */
    public static ResourceLocation[] toResourceLocations(String[] strings){
        return Arrays.stream(strings).map(s -> new ResourceLocation(s.toLowerCase(Locale.ROOT).trim())).toArray(ResourceLocation[]::new);
    }

    /** Converts the given string to {@link ResourceLocation} */
    public static ResourceLocation toResourceLocation(String string){
        return new ResourceLocation(string.toLowerCase(Locale.ROOT).trim());
    }
}
