package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.spell.SpellBuff;
import net.minecraft.item.Item;

public class RepelWater extends SpellBuff {
    public RepelWater() {
        super(WizardryTales.MODID, "repel_water", 64/255f, 124/255f, 223/255f, () -> TalesEffects.repel);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
