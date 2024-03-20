package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;

public class LeafDisguise extends SpellBuff {
    public LeafDisguise() {
        super(WizardryTales.MODID, "leaf_disguise", 0.7f, 1, 0.7f);
        this.addProperties(DURATION);
    }

    @Override
    protected boolean applyEffects(EntityLivingBase caster, SpellModifiers spellModifiers){
        caster.addPotionEffect(new PotionEffect(TalesEffects.leaf_disguise, (int)(getProperty(DURATION).intValue()  * spellModifiers.get(WizardryItems.duration_upgrade)), 0));
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
