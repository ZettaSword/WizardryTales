package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.registry.TalesMaps;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.client.DrawingUtils;
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
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.SpellProperties;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public class ItemGrimoire extends ItemWand implements IWorkbenchItem, ISpellCastingItem, IManaStoringItem {
    /** The number of spell slots a wand has with no attunement upgrades applied. */
    public static final int BASE_SPELL_SLOTS = 8;

    /** The number of ticks between each time a continuous spell is added to the player's recently-cast spells. */
    private static final int CONTINUOUS_TRACKING_INTERVAL = 20;
    /** The increase in progression for casting spells of the matching element. */
    private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
    /** The increase in progression for casting an undiscovered spell (can only happen once per spell for each player). */
    private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
    /** The increase in progression for tiers that the player has already reached. */
    private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
    /** The fraction of progression lost when all recently-cast spells are the same as the one being cast. */
    private static final float MAX_PROGRESSION_REDUCTION = 0.75f;


    private static final float TIME_TO_PAGE = Solver.duration(0.5);


    public ItemGrimoire(String name, Tier tier, Element element) {
        super(tier, element);
        this.setRegistryName(WizardryTales.MODID,name);
        this.setUnlocalizedName(WizardryTales.MODID + ":" + name);
        this.setCreativeTab(TalesTabs.Items);
        this.setMaxStackSize(1);
        setMaxDamage((int)(tier.maxCharge*1.25));
        addPropertyOverride(new ResourceLocation("open"), (s, w, e) -> {
            ItemStack book = null;
            if (e != null) {
                book = Thief.getInHands(e, this);
            }
            return book != null && (((e.isHandActive() &&
                    e.getActiveItemStack() == book) || EntityUtils.isCasting(e, WandHelper.getCurrentSpell(book))) )
                    && WandHelper.getCurrentSpell(book) != Spells.none ? 1 : 0;
        });
    }

    @Override
    public boolean canContinueUsing(ItemStack oldStack, ItemStack newStack) {
        return super.canContinueUsing(oldStack, newStack);
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
        if(caster.world.isRemote && castingTick < Solver.duration(TIME_TO_PAGE)) caster.playSound(WizardrySounds.MISC_BOOK_OPEN, 0.25f, 1.0f);

        return super.cast(stack, spell, caster, hand, castingTick, modifiers);
    }

    @Override
    public int getSpellSlotCount(ItemStack stack){
        return 8; //+ WandHelper.getUpgradeLevel(stack, WizardryItems.attunement_upgrade);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return DrawingUtils.mix(0x00ffd5, 0xff14fd, (float)getDurabilityForDisplay(stack));
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

                ItemStack newWand = new ItemStack(TalesMaps.getGrimoire(tier, this.element));
                newWand.setTagCompound(wand.getTagCompound());
                // This needs to be done after copying the tag compound so the mana capacity for the new wand
                // takes storage upgrades into account
                // Note the usage of the new wand item and not 'this' to ensure the correct capacity is used
                if (!newWand.isEmpty() && newWand.getItem() instanceof IManaStoringItem) {
                    ((IManaStoringItem) newWand.getItem()).setMana(newWand, this.getMana(wand));
                }
                upgrade.shrink(1);

                return newWand;
            }

        }else if(WandHelper.isWandUpgrade(upgrade.getItem())){

            // Special upgrades
            Item specialUpgrade = upgrade.getItem();

            int maxUpgrades = this.tier.upgradeLimit + 5;
            if(this.element == Element.MAGIC) maxUpgrades += Constants.NON_ELEMENTAL_UPGRADE_BONUS + 5;

            if(WandHelper.getTotalUpgrades(wand) < maxUpgrades
                    && WandHelper.getUpgradeLevel(wand, specialUpgrade) < 5){

                // Used to preserve existing mana when upgrading storage rather than creating free mana.
                int prevMana = this.getMana(wand);

                WandHelper.applyUpgrade(wand, specialUpgrade);

                // Special behaviours for specific upgrades
                if(specialUpgrade == WizardryItems.storage_upgrade) {

                    this.setMana(wand, prevMana);

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

    @Override
    public boolean onApplyButtonPressed(EntityPlayer player, Slot centre, Slot crystals, Slot upgrade, Slot[] spellBooks){

        boolean changed = false; // Used for advancements

        if(upgrade.getHasStack()){
            ItemStack original = centre.getStack().copy();
            centre.putStack(this.applyUpgrade(player, centre.getStack(), upgrade.getStack()));
            changed = !ItemStack.areItemStacksEqual(centre.getStack(), original);
        }

        // Reads NBT spell metadata array to variable, edits this, then writes it back to NBT.
        // Original spells are preserved; if a slot is left empty the existing spell binding will remain.
        // Accounts for spells which cannot be applied because they are above the wand's tier; these spells
        // will not bind but the existing spell in that slot will remain and other applicable spells will
        // be bound as normal, along with any upgrades and crystals.
        Spell[] spells = WandHelper.getSpells(centre.getStack());

        if(spells.length <= 0){
            // Base value here because if the spell array doesn't exist, the wand can't possibly have attunement upgrades
            spells = new Spell[BASE_SPELL_SLOTS];
        }

        for(int i = 0; i < spells.length; i++){
            if(spellBooks[i].getStack() != ItemStack.EMPTY){

                Spell spell = Spell.byMetadata(spellBooks[i].getStack().getItemDamage());
                // If the wand is powerful enough for the spell, it's not already bound to that slot and it's enabled for wands
                if(!(spell.getTier().level > this.tier.level) && spells[i] != spell && spell.isEnabled(SpellProperties.Context.WANDS)){
                    spells[i] = spell;
                    changed = true;
                }
            }
        }

        WandHelper.setSpells(centre.getStack(), spells);

        // Charges wand by appropriate amount
        if(crystals.getStack() != ItemStack.EMPTY && !this.isManaFull(centre.getStack())){

            int chargeDepleted = this.getManaCapacity(centre.getStack()) - this.getMana(centre.getStack());

            int manaPerItem = Constants.MANA_PER_CRYSTAL;
            if(crystals.getStack().getItem() == WizardryItems.crystal_shard) manaPerItem = Constants.MANA_PER_SHARD;
            if(crystals.getStack().getItem() == WizardryItems.grand_crystal) manaPerItem = Constants.GRAND_CRYSTAL_MANA;

            if(crystals.getStack().getCount() * manaPerItem < chargeDepleted){
                // If there aren't enough crystals to fully charge the wand
                this.rechargeMana(centre.getStack(), crystals.getStack().getCount() * manaPerItem);
                crystals.decrStackSize(crystals.getStack().getCount());

            }else{
                // If there are excess crystals (or just enough)
                this.setMana(centre.getStack(), this.getManaCapacity(centre.getStack()));
                crystals.decrStackSize((int)Math.ceil(((double)chargeDepleted) / manaPerItem));
            }

            changed = true;
        }

        return changed;
    }


}
