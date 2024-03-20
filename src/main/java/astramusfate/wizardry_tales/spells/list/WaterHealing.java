package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;

public class WaterHealing extends SpellBuff {

    public WaterHealing() {
        super(WizardryTales.MODID, "water_healing",
                0, 64, 0,
                () -> MobEffects.REGENERATION);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
