package astramusfate.wizardry_tales.items.artefacts;

import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.events.SpellCreation;
import astramusfate.wizardry_tales.items.TalesArtefact;
import net.minecraft.item.EnumRarity;

public class ChantingRing extends TalesArtefact implements IInscribed {
    public ChantingRing(String name, EnumRarity rarity, Type type) {
        super(name, rarity, type);
    }

    @Override
    public boolean applyConditions() {
        return true;
    }

    @Override
    public boolean canApplyCondition(String condition) {
        return true;
    }

    @Override
    public boolean applyParameters() {
        return true;
    }
}
