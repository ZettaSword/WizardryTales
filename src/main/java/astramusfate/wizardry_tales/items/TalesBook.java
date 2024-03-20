package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;

public class TalesBook extends ItemSpellBook {
    ResourceLocation book = new ResourceLocation(WizardryTales.MODID, "textures/gui/spell_book_tales.png");

    public TalesBook() {
        super();
        this.setRegistryName(new ResourceLocation(WizardryTales.MODID, "tales_book"));
        this.setUnlocalizedName(new ResourceLocation(WizardryTales.MODID, "tales_book").toString());
    }

    @Override
    public ResourceLocation getGuiTexture(Spell spell) {
        return book;
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}
