package astramusfate.wizardry_tales.compats;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardryItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.botania.api.BotaniaAPI;
import vazkii.botania.common.item.ModItems;

public class BotaniaRecipes {
    public static void init(){
        //Infuse
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ModItems.manaResource, 1, 22), new ItemStack(WizardryItems.magic_silk, 2), 9000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(ModItems.manaResource, 1, 23), new ItemStack(WizardryItems.crystal_shard), 5000);
        BotaniaAPI.registerManaInfusionRecipe(new ItemStack(WizardryItems.grand_crystal), new ItemStack(WizardryItems.magic_crystal, 1, Element.SORCERY.ordinal()), 100000);


        //Conjure
        BotaniaAPI.registerManaConjurationRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 0), new ItemStack(ModItems.dye, 2, 32767), 1000);

        //Astral Diamond conjure
        BotaniaAPI.registerManaConjurationRecipe(new ItemStack(WizardryItems.astral_diamond, 1), new ItemStack(ModItems.manaResource, 2, 5), 50000);


        //Conversion
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 0), new ItemStack(Items.GOLD_NUGGET, 4), 1000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 1), new ItemStack(WizardryItems.spectral_dust, 2, 1), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 2), new ItemStack(WizardryItems.spectral_dust, 2, 2), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 3), new ItemStack(WizardryItems.spectral_dust, 2, 3), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 4), new ItemStack(WizardryItems.spectral_dust, 2, 4), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 5), new ItemStack(WizardryItems.spectral_dust, 2, 5), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 6), new ItemStack(WizardryItems.spectral_dust, 2, 6), 4000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.magic_crystal, 1, 7), new ItemStack(WizardryItems.spectral_dust, 2, 7), 4000);

        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 1), new ItemStack(Blocks.MAGMA, 32), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 2), new ItemStack(Blocks.SNOW, 32), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 3), new ItemStack(Blocks.OBSERVER, 9), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 4), new ItemStack(Blocks.SOUL_SAND, 32), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 5), new ItemStack(Blocks.OBSIDIAN, 24), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 6), new ItemStack(Blocks.LAPIS_BLOCK, 12), 5000);
        BotaniaAPI.registerManaAlchemyRecipe(new ItemStack(WizardryItems.spectral_dust, 1, 7), new ItemStack(Items.GOLD_INGOT, 18), 5000);

    }
}
