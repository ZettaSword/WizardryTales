package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.items.*;
import astramusfate.wizardry_tales.items.artefacts.ChantingCloak;
import astramusfate.wizardry_tales.items.artefacts.ChantingRing;
import astramusfate.wizardry_tales.items.artefacts.IceHalberd;
import astramusfate.wizardry_tales.items.artefacts.TenebriaCrown;
import astramusfate.wizardry_tales.items.rituals.RitualMidnightTrading;
import astramusfate.wizardry_tales.items.rituals.RitualOfForestLife;
import astramusfate.wizardry_tales.items.rituals.RitualRemnantFire;
import astramusfate.wizardry_tales.items.rituals.RitualRingOfFire;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemBlockMultiTextured;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import java.util.Objects;

@GameRegistry.ObjectHolder(WizardryTales.MODID)
@Mod.EventBusSubscriber
public final class TalesItems {

	private TalesItems() {
	} // No instances!

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	@GameRegistry.ObjectHolder("chant_upgrade_range")
	public static final Item chant_upgrade_range = placeholder();
	@GameRegistry.ObjectHolder("chant_upgrade_power")
	public static final Item chant_upgrade_power = placeholder();
	@GameRegistry.ObjectHolder("chant_upgrade_duration")
	public static final Item chant_upgrade_duration = placeholder();
	@GameRegistry.ObjectHolder("chant_upgrade_delay")
	public static final Item chant_upgrade_delay = placeholder();
	@GameRegistry.ObjectHolder("chant_upgrade_count")
	public static final Item chant_upgrade_count = placeholder();

	@GameRegistry.ObjectHolder("tales_book")
	public static final Item tales_book = placeholder();

	@GameRegistry.ObjectHolder("tales_scroll")
	public static final Item tales_scroll = placeholder();

	@GameRegistry.ObjectHolder("chanting_scroll")
	public static final Item chanting_scroll = placeholder();

	//public static final Item arcana_book = placeholder();

	// Artefacts
	@GameRegistry.ObjectHolder("ring_poison_entangle")
	public static final Item ring_poison_entangle = placeholder();
	@GameRegistry.ObjectHolder("ring_protector")
	public static final Item ring_protector = placeholder();
	@GameRegistry.ObjectHolder("ring_new_moon")
	public static final Item ring_new_moon = placeholder();

	@GameRegistry.ObjectHolder("amulet_petal_blooming")
	public static final Item amulet_petal_blooming = placeholder();

	@GameRegistry.ObjectHolder("artefact_earth")
	public static final Item artefact_earth = placeholder();

	@GameRegistry.ObjectHolder("ice_halberd")
	public static final Item ice_halberd = placeholder();

	@GameRegistry.ObjectHolder("casting_ring")
	public static final Item casting_ring = placeholder();

	@GameRegistry.ObjectHolder("chanting_cloak")
	public static final Item chanting_cloak = placeholder();

	@GameRegistry.ObjectHolder("chanting_ring")
	public static final Item chanting_ring = placeholder();

	@GameRegistry.ObjectHolder("tenebria_crown")
	public static final Item tenebria_crown = placeholder();

	// Items
	@GameRegistry.ObjectHolder("mana_bomb")
	public static final Item mana_bomb = placeholder();

	@GameRegistry.ObjectHolder("dull_ring")
	public static final Item dull_ring = placeholder();

	@GameRegistry.ObjectHolder("chanting_stone")
	public static final Item chanting_stone = placeholder();

	@GameRegistry.ObjectHolder("small_pool_flask")
	public static final Item small_pool_flask = placeholder();
	@GameRegistry.ObjectHolder("medium_pool_flask")
	public static final Item medium_pool_flask = placeholder();
	@GameRegistry.ObjectHolder("large_pool_flask")
	public static final Item large_pool_flask = placeholder();

	@GameRegistry.ObjectHolder("mana_dust")
	public static final Item mana_dust = placeholder();

	@GameRegistry.ObjectHolder("infinite_summon")
	public static final Item infinite_summon = placeholder();

	@GameRegistry.ObjectHolder("tenebria_cookie")
	public static final Item tenebria_cookie = placeholder();

	@GameRegistry.ObjectHolder("status_page")
	public static final Item status_page = placeholder();

	/** Casting Items **/

	/** Staffs **/
	@GameRegistry.ObjectHolder("staff_novice_magic")
	public static final Item staff_novice_magic = placeholder();
	@GameRegistry.ObjectHolder("staff_apprentice_magic")
	public static final Item staff_apprentice_magic = placeholder();
	@GameRegistry.ObjectHolder("staff_advanced_magic")
	public static final Item staff_advanced_magic = placeholder();
	@GameRegistry.ObjectHolder("staff_master_magic")
	public static final Item staff_master_magic = placeholder();

	@GameRegistry.ObjectHolder("staff_novice_light")
	public static final Item staff_novice_light = placeholder();
	@GameRegistry.ObjectHolder("staff_apprentice_light")
	public static final Item staff_apprentice_light = placeholder();
	@GameRegistry.ObjectHolder("staff_advanced_light")
	public static final Item staff_advanced_light = placeholder();
	@GameRegistry.ObjectHolder("staff_master_light")
	public static final Item staff_master_light = placeholder();

	/** Grimoires **/
	@GameRegistry.ObjectHolder("grimoire_novice")
	public static final Item grimoire_novice = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice")
	public static final Item grimoire_apprentice = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced")
	public static final Item grimoire_advanced = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master")
	public static final Item grimoire_master = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_fire")
	public static final Item grimoire_novice_fire = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_fire")
	public static final Item grimoire_apprentice_fire = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_fire")
	public static final Item grimoire_advanced_fire = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_fire")
	public static final Item grimoire_master_fire = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_ice")
	public static final Item grimoire_novice_ice = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_ice")
	public static final Item grimoire_apprentice_ice = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_ice")
	public static final Item grimoire_advanced_ice = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_ice")
	public static final Item grimoire_master_ice = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_nature")
	public static final Item grimoire_novice_nature = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_nature")
	public static final Item grimoire_apprentice_nature = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_nature")
	public static final Item grimoire_advanced_nature = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_nature")
	public static final Item grimoire_master_nature = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_thunder")
	public static final Item grimoire_novice_thunder = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_thunder")
	public static final Item grimoire_apprentice_thunder = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_thunder")
	public static final Item grimoire_advanced_thunder = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_thunder")
	public static final Item grimoire_master_thunder = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_darkness")
	public static final Item grimoire_novice_darkness = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_darkness")
	public static final Item grimoire_apprentice_darkness = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_darkness")
	public static final Item grimoire_advanced_darkness = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_darkness")
	public static final Item grimoire_master_darkness = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_light")
	public static final Item grimoire_novice_light = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_light")
	public static final Item grimoire_apprentice_light = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_light")
	public static final Item grimoire_advanced_light = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_light")
	public static final Item grimoire_master_light = placeholder();

	@GameRegistry.ObjectHolder("grimoire_novice_sorcery")
	public static final Item grimoire_novice_sorcery = placeholder();
	@GameRegistry.ObjectHolder("grimoire_apprentice_sorcery")
	public static final Item grimoire_apprentice_sorcery = placeholder();
	@GameRegistry.ObjectHolder("grimoire_advanced_sorcery")
	public static final Item grimoire_advanced_sorcery = placeholder();
	@GameRegistry.ObjectHolder("grimoire_master_sorcery")
	public static final Item grimoire_master_sorcery = placeholder();

	/** Magic Wands **/
	@GameRegistry.ObjectHolder("m_wand_novice")
	public static final Item wand_novice = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice")
	public static final Item wand_apprentice = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced")
	public static final Item wand_advanced = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master")
	public static final Item wand_master = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_fire")
	public static final Item wand_novice_fire = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_fire")
	public static final Item wand_apprentice_fire = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_fire")
	public static final Item wand_advanced_fire = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_fire")
	public static final Item wand_master_fire = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_ice")
	public static final Item wand_novice_ice = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_ice")
	public static final Item wand_apprentice_ice = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_ice")
	public static final Item wand_advanced_ice = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_ice")
	public static final Item wand_master_ice = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_nature")
	public static final Item wand_novice_nature = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_nature")
	public static final Item wand_apprentice_nature = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_nature")
	public static final Item wand_advanced_nature = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_nature")
	public static final Item wand_master_nature = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_thunder")
	public static final Item wand_novice_thunder = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_thunder")
	public static final Item wand_apprentice_thunder = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_thunder")
	public static final Item wand_advanced_thunder = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_thunder")
	public static final Item wand_master_thunder = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_darkness")
	public static final Item wand_novice_darkness = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_darkness")
	public static final Item wand_apprentice_darkness = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_darkness")
	public static final Item wand_advanced_darkness = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_darkness")
	public static final Item wand_master_darkness = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_light")
	public static final Item wand_novice_light = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_light")
	public static final Item wand_apprentice_light = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_light")
	public static final Item wand_advanced_light = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_light")
	public static final Item wand_master_light = placeholder();

	@GameRegistry.ObjectHolder("m_wand_novice_sorcery")
	public static final Item wand_novice_sorcery = placeholder();
	@GameRegistry.ObjectHolder("m_wand_apprentice_sorcery")
	public static final Item wand_apprentice_sorcery = placeholder();
	@GameRegistry.ObjectHolder("m_wand_advanced_sorcery")
	public static final Item wand_advanced_sorcery = placeholder();
	@GameRegistry.ObjectHolder("m_wand_master_sorcery")
	public static final Item wand_master_sorcery = placeholder();

	// Rituals
	@GameRegistry.ObjectHolder("ritual_ring_of_fire")
	public static final Item ritual_ring_of_fire = placeholder();

	@GameRegistry.ObjectHolder("ritual_midnight_trading")
	public static final Item ritual_midnight_trading = placeholder();

	@GameRegistry.ObjectHolder("ritual_remnant_fire")
	public static final Item ritual_remnant_fire = placeholder();

	@GameRegistry.ObjectHolder("ritual_of_forest_life")
	public static final Item ritual_of_forest_life = placeholder();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> reg = event.getRegistry();

		registerItem(reg, "chant_upgrade_range", new TalesWandUpgrade());
		registerItem(reg, "chant_upgrade_power", new TalesWandUpgrade());
		registerItem(reg, "chant_upgrade_duration", new TalesWandUpgrade());
		registerItem(reg, "chant_upgrade_delay", new TalesWandUpgrade());
		registerItem(reg, "chant_upgrade_count", new TalesWandUpgrade(null));

		registerItem(reg, "small_pool_flask", new ItemPoolFlask(ItemPoolFlask.Size.SMALL));
		registerItem(reg, "medium_pool_flask", new ItemPoolFlask(ItemPoolFlask.Size.MEDIUM));
		registerItem(reg, "large_pool_flask", new ItemPoolFlask(ItemPoolFlask.Size.LARGE));

		reg.register(new TalesBook());
		reg.register(new TalesScroll());
		reg.register(new ItemChantingScroll());

		// Artefacts
		reg.register(new TalesArtefact("artefact_earth", EnumRarity.RARE, ItemArtefact.Type.AMULET));
		reg.register(new TalesArtefact("amulet_petal_blooming", EnumRarity.UNCOMMON, ItemArtefact.Type.AMULET));
		reg.register(new TalesArtefact("ring_protector", EnumRarity.RARE, ItemArtefact.Type.RING));
		reg.register(new TalesArtefact("ring_new_moon", EnumRarity.COMMON, ItemArtefact.Type.RING));
		reg.register(new TalesArtefact("ring_poison_entangle", EnumRarity.RARE, ItemArtefact.Type.RING));
		reg.register(new TalesArtefact("casting_ring", EnumRarity.EPIC, ItemArtefact.Type.RING));

		// For artefacts creation
		reg.register(new TalesArtefact("dull_ring", EnumRarity.COMMON, ItemArtefact.Type.RING));
		reg.register(new ChantingCloak("chanting_cloak"));
		reg.register(new ChantingRing("chanting_ring", EnumRarity.COMMON, ItemArtefact.Type.RING));
		reg.register(new TenebriaCrown());

		reg.register(new IceHalberd());
		registerItem(reg, "mana_bomb", new ItemManaBomb());
		registerItem(reg, "chanting_stone", new ItemTales(16));
		registerItem(reg, "mana_dust", new ItemManaDust());
		registerItem(reg, "infinite_summon", new ItemInfiniteSummon());
		reg.register(new ItemTenebriaCookie());
		registerItem(reg, "status_page", new ItemTales(1));

		registerSpellcastingItems(reg);
		//2.2.6
		//2.2.7
		registerRituals(reg);

		//registerItem(registry, "arcana_book", new ItemArcanaBook());
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void onRegistryModel(ModelRegistryEvent e) {

		registerItemModel(tales_book);
		registerItemModel(tales_scroll);
		registerItemModel(chanting_scroll);

		//Artefacts
		registryModel(ring_protector);
		registryModel(ring_new_moon);
		registryModel(ring_poison_entangle);
		registryModel(casting_ring);
		registryModel(chanting_cloak);
		registryModel(chanting_ring);
		registryModel(tenebria_crown);

		registryModel(amulet_petal_blooming);

		registryModel(artefact_earth);

		registryModel(ice_halberd);

		registryModel(mana_bomb);
		registryModel(dull_ring);
		registryModel(chanting_stone);

		registryModel(small_pool_flask);
		registryModel(medium_pool_flask);
		registryModel(large_pool_flask);

		registryModel(mana_dust);
		registryModel(infinite_summon);
		registryModel(tenebria_cookie);
		registryModel(status_page);

		registerSpellcastingItemsModels();
		registerRitualsModels();
	}

	public static void registerSpellcastingItems(IForgeRegistry<Item> reg){
		//Artefacts

		//reg.register(new MagicArtefact("three_casting", EnumRarity.RARE, Type.AMULET));

		reg.register(new ItemGrimoire("grimoire_novice", Tier.NOVICE, Element.MAGIC));
		reg.register(new ItemGrimoire("grimoire_apprentice", Tier.APPRENTICE, Element.MAGIC));
		reg.register(new ItemGrimoire("grimoire_advanced", Tier.ADVANCED, Element.MAGIC));
		reg.register(new ItemGrimoire("grimoire_master", Tier.MASTER, Element.MAGIC));

		reg.register(new ItemGrimoire("grimoire_novice_fire", Tier.NOVICE, Element.FIRE));
		reg.register(new ItemGrimoire("grimoire_apprentice_fire", Tier.APPRENTICE, Element.FIRE));
		reg.register(new ItemGrimoire("grimoire_advanced_fire", Tier.ADVANCED, Element.FIRE));
		reg.register(new ItemGrimoire("grimoire_master_fire", Tier.MASTER, Element.FIRE));

		reg.register(new ItemGrimoire("grimoire_novice_ice", Tier.NOVICE, Element.ICE));
		reg.register(new ItemGrimoire("grimoire_apprentice_ice", Tier.APPRENTICE, Element.ICE));
		reg.register(new ItemGrimoire("grimoire_advanced_ice", Tier.ADVANCED, Element.ICE));
		reg.register(new ItemGrimoire("grimoire_master_ice", Tier.MASTER, Element.ICE));

		reg.register(new ItemGrimoire("grimoire_novice_nature", Tier.NOVICE, Element.EARTH));
		reg.register(new ItemGrimoire("grimoire_apprentice_nature", Tier.APPRENTICE, Element.EARTH));
		reg.register(new ItemGrimoire("grimoire_advanced_nature", Tier.ADVANCED, Element.EARTH));
		reg.register(new ItemGrimoire("grimoire_master_nature", Tier.MASTER, Element.EARTH));

		reg.register(new ItemGrimoire("grimoire_novice_thunder", Tier.NOVICE, Element.LIGHTNING));
		reg.register(new ItemGrimoire("grimoire_apprentice_thunder", Tier.APPRENTICE, Element.LIGHTNING));
		reg.register(new ItemGrimoire("grimoire_advanced_thunder", Tier.ADVANCED, Element.LIGHTNING));
		reg.register(new ItemGrimoire("grimoire_master_thunder", Tier.MASTER, Element.LIGHTNING));

		reg.register(new ItemGrimoire("grimoire_novice_darkness", Tier.NOVICE, Element.NECROMANCY));
		reg.register(new ItemGrimoire("grimoire_apprentice_darkness", Tier.APPRENTICE, Element.NECROMANCY));
		reg.register(new ItemGrimoire("grimoire_advanced_darkness", Tier.ADVANCED, Element.NECROMANCY));
		reg.register(new ItemGrimoire("grimoire_master_darkness", Tier.MASTER, Element.NECROMANCY));

		reg.register(new ItemGrimoire("grimoire_novice_light", Tier.NOVICE, Element.HEALING));
		reg.register(new ItemGrimoire("grimoire_apprentice_light", Tier.APPRENTICE, Element.HEALING));
		reg.register(new ItemGrimoire("grimoire_advanced_light", Tier.ADVANCED, Element.HEALING));
		reg.register(new ItemGrimoire("grimoire_master_light", Tier.MASTER, Element.HEALING));

		reg.register(new ItemGrimoire("grimoire_novice_sorcery", Tier.NOVICE, Element.SORCERY));
		reg.register(new ItemGrimoire("grimoire_apprentice_sorcery", Tier.APPRENTICE, Element.SORCERY));
		reg.register(new ItemGrimoire("grimoire_advanced_sorcery", Tier.ADVANCED, Element.SORCERY));
		reg.register(new ItemGrimoire("grimoire_master_sorcery", Tier.MASTER, Element.SORCERY));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.MAGIC, "m_wand_novice"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.MAGIC, "m_wand_apprentice"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.MAGIC, "m_wand_advanced"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.MAGIC, "m_wand_master"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.FIRE, "m_wand_novice_fire"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.FIRE, "m_wand_apprentice_fire"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.FIRE, "m_wand_advanced_fire"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.FIRE, "m_wand_master_fire"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.ICE, "m_wand_novice_ice"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.ICE, "m_wand_apprentice_ice"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.ICE, "m_wand_advanced_ice"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.ICE, "m_wand_master_ice"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.EARTH, "m_wand_novice_nature"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.EARTH, "m_wand_apprentice_nature"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.EARTH, "m_wand_advanced_nature"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.EARTH, "m_wand_master_nature"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.LIGHTNING, "m_wand_novice_thunder"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.LIGHTNING, "m_wand_apprentice_thunder"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.LIGHTNING, "m_wand_advanced_thunder"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.LIGHTNING, "m_wand_master_thunder"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.NECROMANCY, "m_wand_novice_darkness"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.NECROMANCY, "m_wand_apprentice_darkness"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.NECROMANCY, "m_wand_advanced_darkness"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.NECROMANCY, "m_wand_master_darkness"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.HEALING, "m_wand_novice_light"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.HEALING, "m_wand_apprentice_light"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.HEALING, "m_wand_advanced_light"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.HEALING, "m_wand_master_light"));

		reg.register(new SimpleCastingDevice(Tier.NOVICE, Element.SORCERY, "m_wand_novice_sorcery"));
		reg.register(new SimpleCastingDevice(Tier.APPRENTICE, Element.SORCERY, "m_wand_apprentice_sorcery"));
		reg.register(new SimpleCastingDevice(Tier.ADVANCED, Element.SORCERY, "m_wand_advanced_sorcery"));
		reg.register(new SimpleCastingDevice(Tier.MASTER, Element.SORCERY, "m_wand_master_sorcery"));

		reg.register(new ItemStaff(Tier.NOVICE, Element.MAGIC, "staff_novice_magic"));
		reg.register(new ItemStaff(Tier.APPRENTICE, Element.MAGIC,"staff_apprentice_magic"));
		reg.register(new ItemStaff(Tier.ADVANCED, Element.MAGIC,"staff_advanced_magic"));
		reg.register(new ItemStaff(Tier.MASTER, Element.MAGIC,"staff_master_magic"));

		reg.register(new ItemStaff(Tier.NOVICE, Element.HEALING, "staff_novice_light"));
		reg.register(new ItemStaff(Tier.APPRENTICE, Element.HEALING,"staff_apprentice_light"));
		reg.register(new ItemStaff(Tier.ADVANCED, Element.HEALING,"staff_advanced_light"));
		reg.register(new ItemStaff(Tier.MASTER, Element.HEALING,"staff_master_light"));

	}

	public static void registerSpellcastingItemsModels(){
		// Grimoires
		registryModel(grimoire_novice);
		//grimoire_novice.setTileEntityItemStackRenderer(new ItemGrimoireRender());
		registryModel(grimoire_apprentice);
		registryModel(grimoire_advanced);
		registryModel(grimoire_master);

		registryModel(grimoire_novice_fire);
		registryModel(grimoire_apprentice_fire);
		registryModel(grimoire_advanced_fire);
		registryModel(grimoire_master_fire);

		registryModel(grimoire_novice_ice);
		registryModel(grimoire_apprentice_ice);
		registryModel(grimoire_advanced_ice);
		registryModel(grimoire_master_ice);

		registryModel(grimoire_novice_nature);
		registryModel(grimoire_apprentice_nature);
		registryModel(grimoire_advanced_nature);
		registryModel(grimoire_master_nature);

		registryModel(grimoire_novice_thunder);
		registryModel(grimoire_apprentice_thunder);
		registryModel(grimoire_advanced_thunder);
		registryModel(grimoire_master_thunder);

		registryModel(grimoire_novice_darkness);
		registryModel(grimoire_apprentice_darkness);
		registryModel(grimoire_advanced_darkness);
		registryModel(grimoire_master_darkness);

		registryModel(grimoire_novice_light);
		registryModel(grimoire_apprentice_light);
		registryModel(grimoire_advanced_light);
		registryModel(grimoire_master_light);

		registryModel(grimoire_novice_sorcery);
		registryModel(grimoire_apprentice_sorcery);
		registryModel(grimoire_advanced_sorcery);
		registryModel(grimoire_master_sorcery);

		// Wands
		registryModel(wand_novice);
		registryModel(wand_apprentice);
		registryModel(wand_advanced);
		registryModel(wand_master);

		registryModel(wand_novice_fire);
		registryModel(wand_apprentice_fire);
		registryModel(wand_advanced_fire);
		registryModel(wand_master_fire);

		registryModel(wand_novice_ice);
		registryModel(wand_apprentice_ice);
		registryModel(wand_advanced_ice);
		registryModel(wand_master_ice);

		registryModel(wand_novice_nature);
		registryModel(wand_apprentice_nature);
		registryModel(wand_advanced_nature);
		registryModel(wand_master_nature);

		registryModel(wand_novice_thunder);
		registryModel(wand_apprentice_thunder);
		registryModel(wand_advanced_thunder);
		registryModel(wand_master_thunder);

		registryModel(wand_novice_darkness);
		registryModel(wand_apprentice_darkness);
		registryModel(wand_advanced_darkness);
		registryModel(wand_master_darkness);

		registryModel(wand_novice_light);
		registryModel(wand_apprentice_light);
		registryModel(wand_advanced_light);
		registryModel(wand_master_light);

		registryModel(wand_novice_sorcery);
		registryModel(wand_apprentice_sorcery);
		registryModel(wand_advanced_sorcery);
		registryModel(wand_master_sorcery);

		// Staffs
		registryModel(staff_novice_magic);
		registryModel(staff_apprentice_magic);
		registryModel(staff_advanced_magic);
		registryModel(staff_master_magic);

		registryModel(staff_novice_light);
		registryModel(staff_apprentice_light);
		registryModel(staff_advanced_light);
		registryModel(staff_master_light);
	}

	// Rituals!
	public static void registerRituals(IForgeRegistry<Item> reg){
		//2.2.6
		reg.register(new RitualRingOfFire("ring_of_fire"));
		reg.register(new RitualMidnightTrading("midnight_trading"));
		//2.2.7
		reg.register(new RitualRemnantFire("remnant_fire"));
		reg.register(new RitualOfForestLife("of_forest_life"));
	}

	private static void registerRitualsModels() {
		registryModel(ritual_ring_of_fire);
		registryModel(ritual_midnight_trading);
		registryModel(ritual_remnant_fire);
		registryModel(ritual_of_forest_life);
	}

//---------------------------------------------------------------------------
	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item) {
		registerItem(registry, name, item, false);
	}

	// below registry methods are courtesy of EB
	public static void registerItem(IForgeRegistry<Item> registry, String name, String modid, Item item) {
		registerItem(registry, name, modid, item, false);
	}

	public static void registerItem(IForgeRegistry<Item> registry, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(WizardryTales.MODID, name);
		item.setUnlocalizedName(WizardryTales.MODID + ":" + name);
		//item.setRegistryName(item.getRegistryName().toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	public static void registerItem(IForgeRegistry<Item> registry, String modid, String name, Item item, boolean setTabIcon) {
		item.setRegistryName(modid, name);
		item.setRegistryName(Objects.requireNonNull(item.getRegistryName()).toString());
		registry.register(item);

		if (setTabIcon && item.getCreativeTab() instanceof WizardryTabs.CreativeTabSorted) {
			((WizardryTabs.CreativeTabSorted) item.getCreativeTab()).setIconItem(new ItemStack(item));
		}

		if (item.getCreativeTab() instanceof WizardryTabs.CreativeTabListed) {
			((WizardryTabs.CreativeTabListed) item.getCreativeTab()).order.add(item);
		}
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block) {
		Item itemblock = new ItemBlock(block).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
		registry.register(itemblock);
	}

	private static void registerItemBlock(IForgeRegistry<Item> registry, Block block, Item itemblock) {
		itemblock.setRegistryName(Objects.requireNonNull(block.getRegistryName()));
		registry.register(itemblock);
	}

	private static void registerItemModel(Item item) {
		ModelBakery.registerItemVariants(item, new ModelResourceLocation(Objects.requireNonNull(item.getRegistryName()), "inventory"));
		ModelLoader.setCustomMeshDefinition(item, s -> new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}

	@SideOnly(Side.CLIENT)
	private static void registryModel(Item item) {
		final ResourceLocation regName = item.getRegistryName();
		assert regName != null;
		final ModelResourceLocation mrl = new ModelResourceLocation(regName, "inventory");
		ModelBakery.registerItemVariants(item, mrl);
		ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
	}

	@SideOnly(Side.CLIENT)
	private static void registryModel(Item item, String regMame) {
		item.setRegistryName(regMame);
		item.setUnlocalizedName(regMame);
		final ResourceLocation regName = item.getRegistryName();
		assert regName != null;
		final ModelResourceLocation mrl = new ModelResourceLocation(regName, "inventory");
		ModelBakery.registerItemVariants(item, mrl);
		ModelLoader.setCustomModelResourceLocation(item, 0, mrl);
	}

	/** Registers the given ItemBlock for the given block, with the same registry name as that block. This
	 * also automatically adds it to the order list for its creative tab if that tab is a {@link WizardryTabs.CreativeTabListed},
	 * meaning the order can be defined simply by the order in which the items are registered in this class. */
	private static void registerItemBlock(IForgeRegistry<Item> registry, String name, Block block, ItemBlock itemblock){
		// We don't need to keep a reference to the ItemBlock
		itemblock.setRegistryName( WizardryTales.MODID, name);
		registry.register(itemblock);

		if(block.getCreativeTabToDisplayOn() instanceof WizardryTabs.CreativeTabListed){
			((WizardryTabs.CreativeTabListed)block.getCreativeTabToDisplayOn()).order.add(itemblock);
		}
	}

	private static void registerMultiTexturedItemBlock(IForgeRegistry<Item> registry, Block block, boolean separateNames, String... prefixes){
		// We don't need to keep a reference to the ItemBlock
		Item itemblock = new ItemBlockMultiTextured(block, separateNames, prefixes).setRegistryName(Objects.requireNonNull(block.getRegistryName()));
		registry.register(itemblock);

		if(block.getCreativeTabToDisplayOn() instanceof WizardryTabs.CreativeTabListed){
			((WizardryTabs.CreativeTabListed)block.getCreativeTabToDisplayOn()).order.add(itemblock);
		}
	}
}