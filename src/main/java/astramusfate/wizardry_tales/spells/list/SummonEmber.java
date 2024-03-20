package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.summon.EntityEmber;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.spell.SpellMinion;
import net.minecraft.item.Item;

public class SummonEmber extends SpellMinion<EntityEmber> {
    public SummonEmber() {
        super(WizardryTales.MODID, "summon_ember", EntityEmber::new);
        this.soundValues(7, 0.6f, 0);
        this.flying(true);
        this.addProperties(DIRECT_DAMAGE, BURN_DURATION);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
