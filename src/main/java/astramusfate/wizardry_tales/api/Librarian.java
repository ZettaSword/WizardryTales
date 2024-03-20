package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.inventory.ContainerBookshelf;
import net.minecraft.util.ResourceLocation;

import static electroblob.wizardry.block.BlockBookshelf.registerBookModelTexture;

public class Librarian {
    public static void preInitBookShelfModelTextures() {
        registerBookModelTexture(() -> TalesItems.tales_book, new ResourceLocation(WizardryTales.MODID, "blocks/tales_spellbook"));
        registerBookModelTexture(() -> TalesItems.tales_scroll, new ResourceLocation(WizardryTales.MODID, "blocks/tales_spellscroll"));

        registerBookModelTexture(() -> TalesItems.chanting_scroll, new ResourceLocation(WizardryTales.MODID, "blocks/tales_spellscroll"));

        // Contracts Scrolls
        //registerBookModelTexture(() -> ZItems.pact_summon, new ResourceLocation(ZettaiMagic.MODID, "blocks/zettai_spellscroll"));
        //registerBookModelTexture(() -> ZItems.pact_teleport, new ResourceLocation(ZettaiMagic.MODID, "blocks/zettai_spellscroll"));
        //registerBookModelTexture(() -> ZItems.pact_projection, new ResourceLocation(ZettaiMagic.MODID, "blocks/zettai_spellscroll"));

    }

    public static void InitBookshelfItems() {
        ContainerBookshelf.registerBookItem(TalesItems.tales_book);
        ContainerBookshelf.registerBookItem(TalesItems.tales_scroll);
        ContainerBookshelf.registerBookItem(TalesItems.chanting_scroll);

        // Contracts Scrolls
        //ContainerBookshelf.registerBookItem(ZItems.pact_summon);
        //ContainerBookshelf.registerBookItem(ZItems.pact_teleport);
       // ContainerBookshelf.registerBookItem(ZItems.pact_projection);
    }
}
