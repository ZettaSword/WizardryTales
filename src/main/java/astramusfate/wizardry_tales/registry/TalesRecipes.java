package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.items.TalesScroll;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryBlocks;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

import javax.annotation.Nonnull;
import java.util.List;

public class TalesRecipes {

    static final int ANY_META= 32767;

    public static void init(){
        shapelessRecipe(new ItemStack(TalesItems.mana_bomb, 3), Ingredient.fromItem(Items.GLASS_BOTTLE),
                Ingredient.fromItem(Items.GUNPOWDER),
                Ingredient.fromItem(WizardryItems.crystal_shard));

        // Flasks
        shapelessRecipe(TalesItems.small_pool_flask, Ingredient.fromItem(WizardryItems.small_mana_flask));
        shapelessRecipe(TalesItems.medium_pool_flask, Ingredient.fromItem(WizardryItems.medium_mana_flask));
        shapelessRecipe(TalesItems.large_pool_flask, Ingredient.fromItem(WizardryItems.large_mana_flask));

        // Back conversion
        shapelessRecipe(WizardryItems.small_mana_flask, Ingredient.fromItem(TalesItems.small_pool_flask));
        shapelessRecipe(WizardryItems.medium_mana_flask, Ingredient.fromItem(TalesItems.medium_pool_flask));
        shapelessRecipe(WizardryItems.large_mana_flask, Ingredient.fromItem(TalesItems.large_pool_flask));

        // Mana Dust!
        shapelessRecipe(TalesItems.mana_dust, Ingredient.fromItems(TalesItems.small_pool_flask, WizardryItems.small_mana_flask),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_crystal,1, ANY_META)),
                        Ingredient.fromStacks(new ItemStack(WizardryItems.spectral_dust,1, Element.NECROMANCY.ordinal())));

        shapelessRecipe(Items.IRON_INGOT,
                Ingredient.fromItem(TalesItems.mana_dust),
                Ingredient.fromItem(Items.COAL),Ingredient.fromItem(Items.COAL),
                Ingredient.fromItem(Items.COAL),Ingredient.fromItem(Items.COAL),
                Ingredient.fromItem(WizardryItems.magic_silk),Ingredient.fromItem(Items.REDSTONE));

        shapelessRecipe(Items.GOLD_INGOT,
                Ingredient.fromItem(TalesItems.mana_dust),
                Ingredient.fromItem(Items.IRON_NUGGET),Ingredient.fromItem(Items.IRON_NUGGET),
                Ingredient.fromItem(Items.GUNPOWDER),Ingredient.fromItem(Items.GUNPOWDER),
                Ingredient.fromItem(Items.COAL),Ingredient.fromItem(Items.COAL));

        shapelessRecipe(new ItemStack(Items.DYE, 1, EnumDyeColor.CYAN.getDyeDamage()),
                Ingredient.fromItem(TalesItems.mana_dust),
                Ingredient.fromItem(Items.IRON_NUGGET),Ingredient.fromItem(Items.GOLD_NUGGET),
                Ingredient.fromItem(Items.REDSTONE), Ingredient.fromStacks(new ItemStack(Items.DYE, 1,
                        EnumDyeColor.WHITE.getDyeDamage())),
                Ingredient.fromItem(Items.GUNPOWDER), Ingredient.fromItem(Items.GUNPOWDER));

        shapelessRecipe(Items.REDSTONE,
                Ingredient.fromItem(TalesItems.mana_dust),
                Ingredient.fromItem(Items.COAL),Ingredient.fromItem(Items.GOLD_NUGGET),
                Ingredient.fromItems(TalesItems.small_pool_flask, WizardryItems.small_mana_flask)
                ,Ingredient.fromItem(WizardryItems.magic_silk));

        try {
            List<Spell> spells = Spell.getAllSpells();
            int data = TalesSpells.chanting.metadata();
            for (Spell spell : spells) {
                if (spell.networkID() == TalesSpells.chanting.networkID()) {
                    data = spell.metadata();
                }
            }

        shapelessRecipe(new ItemStack(TalesItems.tales_book, 1, data), "chanting_spellbook",
                Ingredient.fromItem(TalesItems.mana_dust),
                Ingredient.fromItem(TalesItems.chanting_stone), Ingredient.fromItem(WizardryItems.spell_book));
        } catch (Exception ignore){}

        // Usual shapeless
        shapelessRecipe(TalesItems.casting_ring, "casting_ring",  Ingredient.fromItem(WizardryItems.medium_mana_flask),
                Ingredient.fromItem(TalesItems.chanting_ring),
                Ingredient.fromStacks(new ItemStack(WizardryItems.spectral_dust, 1, ANY_META)));

        shapelessRecipe(TalesItems.chanting_ring, "chanting_ring",  Ingredient.fromItem(WizardryItems.medium_mana_flask),
                Ingredient.fromItem(TalesItems.dull_ring),
                Ingredient.fromStacks(new ItemStack(WizardryItems.spectral_dust, 1, ANY_META)));

        shapelessRecipe(TalesItems.chanting_scroll, "chanting_scroll",  Ingredient.fromItem(WizardryItems.medium_mana_flask),
                Ingredient.fromItem(WizardryItems.blank_scroll),
                Ingredient.fromStacks(new ItemStack(WizardryItems.spectral_dust, 1, ANY_META)));

        shapelessRecipe(TalesItems.chanting_cloak, "chanting_cloak",  Ingredient.fromItem(WizardryItems.medium_mana_flask),
                Ingredient.fromItem(WizardryItems.magic_silk),Ingredient.fromItem(WizardryItems.magic_silk),
                Ingredient.fromStacks(new ItemStack(WizardryItems.spectral_dust, 1, ANY_META)));

        // Special items

        shapelessRecipe(TalesItems.infinite_summon, Ingredient.fromItems(WizardryItems.blank_scroll),
                Ingredient.fromStacks(new ItemStack(WizardryItems.grand_crystal, 1)),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_silk, 1)),
                Ingredient.fromStacks(new ItemStack(WizardryItems.astral_diamond, 1)),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_crystal, 1, Element.EARTH.ordinal())),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_crystal, 1, Element.LIGHTNING.ordinal())),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_crystal, 1, Element.HEALING.ordinal())),
                Ingredient.fromStacks(new ItemStack(WizardryItems.magic_crystal, 1, Element.SORCERY.ordinal())));


        // Spellcasting Items
        if(Tales.addon.magical_wands) {
            shapedRecipe(TalesItems.wand_novice, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.MAGIC.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_fire, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.FIRE.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_ice, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.ICE.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_nature, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.EARTH.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_thunder, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.LIGHTNING.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_darkness, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.NECROMANCY.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_light, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.HEALING.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
            shapedRecipe(TalesItems.wand_novice_sorcery, "  S", " F ", "X  ",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.SORCERY.ordinal()), 'S', Items.STICK, 'F', WizardryItems.magic_silk);
        }


        if(Tales.addon.magical_grimoires){
            shapedRecipe(TalesItems.grimoire_novice, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.MAGIC.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_fire, "XGX", " B ", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.FIRE.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_ice, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.ICE.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_nature, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.EARTH.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_thunder, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.LIGHTNING.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_darkness, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.NECROMANCY.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_light, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.HEALING.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
            shapedRecipe(TalesItems.grimoire_novice_sorcery, "XGX", "GBG", "XGX",
                    'X', new ItemStack(WizardryItems.magic_crystal, 1, Element.SORCERY.ordinal()), 'B', Items.ENCHANTED_BOOK, 'G', Items.GOLD_NUGGET);
        }
    }

    public static void shapedRecipe(Item result, @Nonnull Object... recipe){
        GameRegistry.addShapedRecipe(new ResourceLocation(WizardryTales.MODID, result.getUnlocalizedName()),
                new ResourceLocation(WizardryTales.MODID), new ItemStack(result, 1), recipe);
    }

    public static void shapedRecipe(ItemStack result, @Nonnull Object... recipe){
        GameRegistry.addShapedRecipe(new ResourceLocation(WizardryTales.MODID, result.getUnlocalizedName()),
                new ResourceLocation(WizardryTales.MODID), result, recipe);
    }

    public static void shapelessRecipe(Item result, @Nonnull Ingredient... recipe){
        shapelessRecipe(result, result.getUnlocalizedName(), recipe);
    }

    public static void shapelessRecipe(Item result, String recipeName, @Nonnull Ingredient... recipe){
        GameRegistry.addShapelessRecipe(new ResourceLocation(WizardryTales.MODID, recipeName),
                new ResourceLocation(WizardryTales.MODID), new ItemStack(result, 1), recipe);
    }

    public static void shapelessRecipe(ItemStack result, @Nonnull Ingredient... recipe){
        GameRegistry.addShapelessRecipe(new ResourceLocation(WizardryTales.MODID, result.getUnlocalizedName()),
                new ResourceLocation(WizardryTales.MODID), result, recipe);
    }

    public static void shapelessRecipe(ItemStack result, String recipeName, @Nonnull Ingredient... recipe){
        GameRegistry.addShapelessRecipe(new ResourceLocation(WizardryTales.MODID, recipeName),
                new ResourceLocation(WizardryTales.MODID), result, recipe);
    }
}
