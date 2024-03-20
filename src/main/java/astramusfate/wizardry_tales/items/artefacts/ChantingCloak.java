package astramusfate.wizardry_tales.items.artefacts;

import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.events.SpellCreation;
import astramusfate.wizardry_tales.items.TalesArtefact;
import astramusfate.wizardry_tales.items.TalesBauble;
import baubles.api.BaubleType;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;

public class ChantingCloak extends TalesBauble implements IInscribed {
    public ChantingCloak(String name) {
        super(name);
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.COMMON;
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

    @Override
    protected BaubleType type() {
        return BaubleType.BODY;
    }
}
