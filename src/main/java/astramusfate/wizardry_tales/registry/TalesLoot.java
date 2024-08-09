package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import electroblob.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

@Mod.EventBusSubscriber
public class TalesLoot {

    private static LootTable WAND_UPGRADES;
    private static LootTable UNCOMMON_ARTEFACTS;

    private static LootTable RARE_ARTEFACTS;
    private static LootTable EPIC_ARTEFACTS;
    public static ResourceLocation ENVENOMED_BLADE;

    private TalesLoot(){}

    public static void preInit() {
        // subsets
        //LootTableList.register(new ResourceLocation(WizardryTales.MODID, "subsets/wand_upgrades"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "subsets/uncommon_artefacts"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "subsets/rare_artefacts"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "subsets/epic_artefacts"));

        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "chests/shrine"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "chests/anchor"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "chests/underground_house"));
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "chests/high_values_double"));

        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "chests/dungeon_additions"));

        ENVENOMED_BLADE = LootTableList.register(new ResourceLocation(WizardryTales.MODID, "entities/envenomed_blade"));

        // additional
        LootTableList.register(new ResourceLocation(WizardryTales.MODID, "grant_book_on_first_join"));
    }

    @SubscribeEvent
    public static void onLootTableLoadEvent(LootTableLoadEvent event) {
        if (Arrays.asList(Tales.struct.tales_loot_entries).contains(event.getName())) {
            event.getTable().addPool(getAdditive(WizardryTales.MODID + ":chests/dungeon_additions", WizardryTales.MODID + "_tales_dungeon_additions"));
        }
        /*
        if (event.getName().toString().equals(WizardryTales.MODID + ":subsets/wand_upgrades")) {
            WAND_UPGRADES = event.getTable();
        }
         */

        if (event.getName().toString().equals(WizardryTales.MODID + ":subsets/uncommon_artefacts")) {
            UNCOMMON_ARTEFACTS = event.getTable();
        }
        if (event.getName().toString().equals(WizardryTales.MODID + ":subsets/rare_artefacts")) {
            RARE_ARTEFACTS = event.getTable();
        }
        if (event.getName().toString().equals(WizardryTales.MODID + ":subsets/epic_artefacts")) {
            EPIC_ARTEFACTS = event.getTable();
        }

        /*
        if (event.getName().toString().equals(Wizardry.MODID + ":subsets/wand_upgrades") && WAND_UPGRADES != null) {
            LootPool targetPool = event.getTable().getPool("upgrades");
            LootPool sourcePool = WAND_UPGRADES.getPool("wizardry_tales_wand_upgrades");
            injectEntries(sourcePool, targetPool);
        }*/

        // inject artefacts to ebwiz tables
        if (event.getName().toString().equals(Wizardry.MODID + ":subsets/uncommon_artefacts") && UNCOMMON_ARTEFACTS != null) {
            LootPool targetPool = event.getTable().getPool("uncommon_artefacts");
            LootPool sourcePool = UNCOMMON_ARTEFACTS.getPool("main");
            injectEntries(sourcePool, targetPool);
        }
        if (event.getName().toString().equals(Wizardry.MODID + ":subsets/rare_artefacts") && RARE_ARTEFACTS != null) {
            LootPool targetPool = event.getTable().getPool("rare_artefacts");
            LootPool sourcePool = RARE_ARTEFACTS.getPool("main");
            injectEntries(sourcePool, targetPool);
        }
        if (event.getName().toString().equals(Wizardry.MODID + ":subsets/epic_artefacts") && EPIC_ARTEFACTS != null) {
            LootPool targetPool = event.getTable().getPool("epic_artefacts");
            LootPool sourcePool = EPIC_ARTEFACTS.getPool("main");
            injectEntries(sourcePool, targetPool);
        }


    }

    /**
     * Injects every element of sourcePool into targetPool
     */
    private static void injectEntries(LootPool sourcePool, LootPool targetPool) {
        // Accessing {@link net.minecraft.world.storage.loot.LootPool.lootEntries}
        if (sourcePool != null && targetPool != null) {
            List<LootEntry> lootEntries = ObfuscationReflectionHelper.getPrivateValue(LootPool.class, sourcePool, "field_186453_a");

            for (LootEntry entry : lootEntries) {
                targetPool.addEntry(entry);
            }
        } else {
            WizardryTales.log.warn("Attempted to inject to null pool source or target.");
        }

    }

    private static LootPool getAdditive(String entryName, String poolName) {
        return new LootPool(new LootEntry[] {getAdditiveEntry(entryName, 1)}, new LootCondition[0],
                new RandomValueRange(1), new RandomValueRange(0, 1), WizardryTales.MODID + "_" + poolName);
    }

    private static LootEntryTable getAdditiveEntry(String name, int weight) {
        return new LootEntryTable(new ResourceLocation(name), weight, 0, new LootCondition[0],
                WizardryTales.MODID + "_additive_entry");
    }

}
