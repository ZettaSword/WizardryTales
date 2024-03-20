package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.*;
import java.util.function.Predicate;

public class Trader {

    /**
     * Helper method to handle addon spell books.
     * @param spell the spell to look up.
     * @return Checks all items of the spell's namespace (modid) if they are applicable for the spell and returns the first match, or falls back to WizardryItems.spell_book if no match was found.
     */
    public static ItemStack getBookStackForSpell(Spell spell) {
        String modid = Objects.requireNonNull(spell.getRegistryName()).getResourceDomain();
        if (modid.equals(Wizardry.MODID)) {
            return new ItemStack(WizardryItems.spell_book, 1, spell.metadata());
        }
        Optional<Item> firstMatch = ForgeRegistries.ITEMS.getValuesCollection().stream().filter(v -> v instanceof ItemSpellBook && spell.applicableForItem(v)
                && Objects.requireNonNull(v.getRegistryName()).getResourceDomain().equals(modid)).findFirst();

        return firstMatch.map(item -> new ItemStack(item, 1, spell.metadata())).orElseGet(() -> new ItemStack(WizardryItems.spell_book, 1, spell.metadata()));
    }

    /**
     * Helper method to handle item trades.
     * @param predicate to follow, and find item matching it.
     */
    public static ItemStack getItemToSell(Predicate<Item> predicate) {
        List<Item> items = Lists.newArrayList(ForgeRegistries.ITEMS.getValuesCollection());
        Collections.shuffle(items);
        Optional<Item> anyMatch = items.stream().filter(predicate).findFirst();

        return anyMatch.map(item -> new ItemStack(item, 1)).orElseGet(() -> new ItemStack(TalesItems.chanting_stone));
    }

    /**
     * Helper method to handle item withing registry.
     * @param predicate to follow, and find item matching it.
     */
    public static Item getItem(Predicate<Item> predicate) {
        List<Item> items = Lists.newArrayList(ForgeRegistries.ITEMS.getValuesCollection());
        Collections.shuffle(items);
        Optional<Item> anyMatch = items.stream().filter(predicate).findFirst();

        return anyMatch.orElseGet(() -> Item.getItemFromBlock(Blocks.STONE));
    }
}
