package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesMaps;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.IWorkbenchItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;

public class SimpleCastingDevice extends ItemWand implements IWorkbenchItem, ISpellCastingItem, IManaStoringItem  {


    /** The number of spell slots a wand has with no attunement upgrades applied. */
    public static final int BASE_SPELL_SLOTS = 5;

    /** The number of ticks between each time a continuous spell is added to the player's recently-cast spells. */
    protected static final int CONTINUOUS_TRACKING_INTERVAL = 20;
    /** The increase in progression for casting spells of the matching element. */
    private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
    /** The increase in progression for casting an undiscovered spell (can only happen once per spell for each player). */
    private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
    /** The increase in progression for tiers that the player has already reached. */
    private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
    /** The fraction of progression lost when all recently-cast spells are the same as the one being cast. */
    private static final float MAX_PROGRESSION_REDUCTION = 0.75f;


    public SimpleCastingDevice(Tier tier, Element element, String registry_name) {
        super(tier, element);
        this.setRegistryName(WizardryTales.MODID, registry_name);
        this.setUnlocalizedName(registry_name);
        this.setCreativeTab(TalesTabs.Items);
    }

    @Override
    public int getSpellSlotCount(ItemStack stack) {
        return BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(stack, WizardryItems.attunement_upgrade);
    }

    @Override
    public ItemStack applyUpgrade(@Nullable EntityPlayer player, ItemStack wand, ItemStack upgrade){

        // Upgrades wand if necessary. Damage is copied, preserving remaining durability,
        // and also the entire NBT tag compound.
        if(upgrade.getItem() == WizardryItems.arcane_tome){

            Tier tier = Tier.values()[upgrade.getItemDamage()];

            // Checks the wand upgrade is for the tier above the wand's tier, and that either the wand has enough
            // progression or the player is in creative mode.
            if((player == null || player.isCreative() || Wizardry.settings.legacyWandLevelling
                    || WandHelper.getProgression(wand) >= tier.getProgression())
                    && tier == this.tier.next() && this.tier != Tier.MASTER){

                if(Wizardry.settings.legacyWandLevelling){
                    // Progression has little meaning with legacy upgrade mechanics so just reset it
                    // In theory, you can get 'free' progression when upgrading since progression can't be negative,
                    // so the flipside of that is you lose any excess
                    WandHelper.setProgression(wand, 0);
                }else{
                    // Carry excess progression over to the new stack
                    WandHelper.setProgression(wand, WandHelper.getProgression(wand) - tier.getProgression());
                }

                if(player != null) WizardData.get(player).setTierReached(tier);

                ItemStack newWand = new ItemStack(TalesMaps.getMWand(tier, this.element));
                newWand.setTagCompound(wand.getTagCompound());
                // This needs to be done after copying the tag compound so the mana capacity for the new wand
                // takes storage upgrades into account
                // Note the usage of the new wand item and not 'this' to ensure the correct capacity is used
                ((IManaStoringItem)newWand.getItem()).setMana(newWand, this.getMana(wand));

                upgrade.shrink(1);

                return newWand;
            }

        }else if(WandHelper.isWandUpgrade(upgrade.getItem())){

            // Special upgrades
            Item specialUpgrade = upgrade.getItem();

            int maxUpgrades = this.tier.upgradeLimit;
            if(this.element == Element.MAGIC) maxUpgrades += Constants.NON_ELEMENTAL_UPGRADE_BONUS;

            if(WandHelper.getTotalUpgrades(wand) < maxUpgrades
                    && WandHelper.getUpgradeLevel(wand, specialUpgrade) < Constants.UPGRADE_STACK_LIMIT){

                // Used to preserve existing mana when upgrading storage rather than creating free mana.
                int prevMana = this.getMana(wand);

                WandHelper.applyUpgrade(wand, specialUpgrade);

                // Special behaviours for specific upgrades
                if(specialUpgrade == WizardryItems.storage_upgrade){

                    this.setMana(wand, prevMana);

                }else if(specialUpgrade == WizardryItems.attunement_upgrade){

                    int newSlotCount = BASE_SPELL_SLOTS + WandHelper.getUpgradeLevel(wand,
                            WizardryItems.attunement_upgrade);

                    Spell[] spells = WandHelper.getSpells(wand);
                    Spell[] newSpells = new Spell[newSlotCount];

                    for(int i = 0; i < newSpells.length; i++){
                        newSpells[i] = i < spells.length && spells[i] != null ? spells[i] : Spells.none;
                    }

                    WandHelper.setSpells(wand, newSpells);

                    int[] cooldowns = WandHelper.getCooldowns(wand);
                    int[] newCooldowns = new int[newSlotCount];

                    if(cooldowns.length > 0){
                        System.arraycopy(cooldowns, 0, newCooldowns, 0, cooldowns.length);
                    }

                    WandHelper.setCooldowns(wand, newCooldowns);
                }

                upgrade.shrink(1);

                if(player != null){

                    WizardryAdvancementTriggers.special_upgrade.triggerFor(player);

                    if(WandHelper.getTotalUpgrades(wand) == Tier.MASTER.upgradeLimit){
                        WizardryAdvancementTriggers.max_out_wand.triggerFor(player);
                    }
                }

            }
        }

        return wand;
    }
}
