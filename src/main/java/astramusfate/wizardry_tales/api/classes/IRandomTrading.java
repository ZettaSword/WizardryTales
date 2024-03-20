package astramusfate.wizardry_tales.api.classes;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.MerchantRecipe;

public interface IRandomTrading {
    MerchantRecipe getRandomTrading();

    /**
     * @param stack is where item player tries to buy from trader. It's item sold.
     * @param price is where price for item sold.
     *  **/
    default MerchantRecipe getRecipe(ItemStack stack, ItemStack price){
        return new MerchantRecipe(price, stack);
    }

    /**
     * @param stack is where item player tries to buy from trader. It's item sold.
     * @param price1 is first slot where price for item sold.
     * @param price2 is second slot where price for item sold.
     *  **/
    default MerchantRecipe getRecipe(ItemStack stack, ItemStack price1, ItemStack price2){
        return new MerchantRecipe(price1, price2, stack);
    }

    default ItemStack stack(Item item){
        return new ItemStack(item);
    }

    default ItemStack stack(Item item, int meta){
        return new ItemStack(item, 1, meta);
    }
}
