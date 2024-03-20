package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.items.*;
import astramusfate.wizardry_tales.items.artefacts.ChantingCloak;
import astramusfate.wizardry_tales.items.artefacts.ChantingRing;
import astramusfate.wizardry_tales.items.artefacts.IceHalberd;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemBlockMultiTextured;
import electroblob.wizardry.item.ItemManaFlask;
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

		reg.register(new IceHalberd());
		registerItem(reg, "mana_bomb", new ItemManaBomb());
		registerItem(reg, "chanting_stone", new ItemTales(16));
		registerItem(reg, "mana_dust", new ItemManaDust());
		registerItem(reg, "infinite_summon", new ItemInfiniteSummon());
		reg.register(new ItemTenebriaCookie());
		registerItem(reg, "status_page", new ItemTales(1));

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
	}

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
		final ResourceLocation regName = item.getRegistryName();// Не забываем, что getRegistryName может вернуть Null!
		assert regName != null;
		final ModelResourceLocation mrl = new ModelResourceLocation(regName, "inventory");
		ModelBakery.registerItemVariants(item, mrl);// Регистрация вариантов предмета. Это нужно если мы хотим использовать подтипы предметов/блоков(см. статью подтипы)
		ModelLoader.setCustomModelResourceLocation(item, 0, mrl);// Устанавливаем вариант модели для нашего предмета. Без регистрации варианта модели, сама модель не будет установлена для предмета/блока(см. статью подтипы)
	}

	@SideOnly(Side.CLIENT)
	private static void registryModel(Item item, String regMame) {
		item.setRegistryName(regMame);
		item.setUnlocalizedName(regMame);
		final ResourceLocation regName = item.getRegistryName();
		assert regName != null;
		final ModelResourceLocation mrl = new ModelResourceLocation(regName, "inventory");
		ModelBakery.registerItemVariants(item, mrl);// Регистрация вариантов предмета. Это нужно если мы хотим использовать подтипы предметов/блоков(см. статью подтипы)
		ModelLoader.setCustomModelResourceLocation(item, 0, mrl);// Устанавливаем вариант модели для нашего предмета. Без регистрации варианта модели, сама модель не будет установлена для предмета/блока(см. статью подтипы)
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