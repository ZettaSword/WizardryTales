package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.item.ItemScroll;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;

public class TalesScroll extends ItemScroll {

    public TalesScroll(){
        super();
        this.setRegistryName(new ResourceLocation(WizardryTales.MODID, "tales_scroll"));
        this.setUnlocalizedName(new ResourceLocation(WizardryTales.MODID, "tales_scroll").toString());
    }

    @Nonnull
    @Override
    public IRarity getForgeRarity(@Nonnull ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }
}