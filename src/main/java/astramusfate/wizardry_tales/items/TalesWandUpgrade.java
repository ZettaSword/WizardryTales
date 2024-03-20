package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.item.ItemWandUpgrade;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.creativetab.CreativeTabs;

public class TalesWandUpgrade extends ItemWandUpgrade {
    public TalesWandUpgrade(){
        super();
        setCreativeTab(TalesTabs.Items);
    }
    public TalesWandUpgrade(CreativeTabs tab){
        super();
        setCreativeTab(tab);
    }

    public static void init() {
        WandHelper.registerSpecialUpgrade(TalesItems.chant_upgrade_range, "chant_upgrade_range");
        WandHelper.registerSpecialUpgrade(TalesItems.chant_upgrade_power, "chant_upgrade_power");
        WandHelper.registerSpecialUpgrade(TalesItems.chant_upgrade_duration, "chant_upgrade_duration");
        WandHelper.registerSpecialUpgrade(TalesItems.chant_upgrade_delay, "chant_upgrade_delay");
        WandHelper.registerSpecialUpgrade(TalesItems.chant_upgrade_count, "chant_upgrade_count");
    }
}
