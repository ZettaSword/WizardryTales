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

    public static final CreativeTabs Rituals = new CreativeTabs("tales_rituals") {
        @Override
        public ItemStack getTabIconItem() {
            return new ItemStack(TalesItems.ritual_ring_of_fire);
        }
    };
}
