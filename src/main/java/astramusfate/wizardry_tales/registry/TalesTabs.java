package astramusfate.wizardry_tales.registry;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class TalesTabs {

    public static final CreativeTabs Items = new CreativeTabs("tales_items") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(TalesItems.ring_protector);
        }
    };
}
