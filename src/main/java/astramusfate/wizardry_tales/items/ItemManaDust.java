package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.registry.TalesTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class ItemManaDust extends Item {

    public ItemManaDust(){
        super();
        this.setCreativeTab(TalesTabs.Items);
        this.setMaxStackSize(16);
    }

    @Override
    public boolean hasEffect(@Nonnull ItemStack stack) {
        return true;
    }


}
