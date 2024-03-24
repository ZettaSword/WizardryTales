package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.registry.TalesMaps;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStaff extends SimpleCastingDevice {
    public ItemStaff(Tier tier, Element element, String name) {
        super(tier, element, name);
        this.setMaxStackSize(1);
        this.addPropertyOverride(new ResourceLocation("element"), new IItemPropertyGetter()
        {
            @SideOnly(Side.CLIENT)
            public float apply(@Nonnull ItemStack stack, @Nullable World world, @Nullable EntityLivingBase entity)
            {
                Spell spell = WandHelper.getCurrentSpell(stack);
                return spell.getElement().ordinal();
            }
        });
        //this.setCreativeTab(TalesTabs.Scrolls);
    }


    public void setSpellIn(ItemStack stack, Spell spell){
        WandHelper.setSpells(stack, new Spell[]{spell});
    }

    @Override
    @SideOnly(Side.CLIENT)
    public net.minecraft.client.gui.FontRenderer getFontRenderer(ItemStack stack){
        return Wizardry.proxy.getFontRenderer(stack);
    }

    @Override
    public boolean isEnchantable(ItemStack stack){
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book){
        return false;
    }

    @Override
    public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn){
        setMana(stack, this.getMaxDamage(stack)); // Wands are empty when first crafted
    }

    /**
     * Convenience method that decreases the amount of mana contained in the given item stack by the given value. This
     * method automatically limits the mana to a minimum of 0 and performs the relevant checks for creative mode, etc.
     *
     * @param stack
     * @param mana
     * @param wielder
     */
    @Override
    public void consumeMana(ItemStack stack, int mana, @Nullable EntityLivingBase wielder) {
        super.consumeMana(stack, mana, wielder);
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers){

        World world = caster.world;

        if(world.isRemote && !spell.isContinuous && spell.requiresPacket()) return false;

        if(spell.cast(world, caster, hand, castingTick, modifiers)){

            if(castingTick == 0) MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.WAND, spell, caster, modifiers));

            if(!world.isRemote){

                // Continuous spells never require packets so don't rely on the requiresPacket method to specify it
                if(!spell.isContinuous && spell.requiresPacket()){
                    // Sends a packet to all players in dimension to tell them to spawn particles.
                    IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), hand, spell, modifiers);
                    WizardryPacketHandler.net.sendToDimension(msg, world.provider.getDimension());
                }

            }

            caster.setActiveHand(hand);

            // Cooldown
            if(!spell.isContinuous && !caster.isCreative()){ // Spells only have a cooldown in survival
                WandHelper.setCurrentCooldown(stack, (int)(spell.getCooldown() * modifiers.get(WizardryItems.cooldown_upgrade)));
            }

            // Progression
            if(this.tier.level < Tier.MASTER.level && castingTick % CONTINUOUS_TRACKING_INTERVAL == 0){

                // We don't care about cost modifiers here, otherwise players would be penalised for wearing robes!
                int progression = (int)(spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
                WandHelper.addProgression(stack, progression);

                if(!Wizardry.settings.legacyWandLevelling){ // Don't display the message if legacy wand levelling is enabled
                    // If the wand just gained enough progression to be upgraded...
                    Tier nextTier = tier.next();
                    int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
                    if(excess >= 0 && excess < progression){
                        // ...display a message above the player's hotbar
                        caster.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
                        WizardryAdvancementTriggers.wand_levelup.triggerFor(caster);
                        if(!world.isRemote)
                            caster.sendMessage(new TextComponentTranslation("item." + Wizardry.MODID + ":wand.levelup",
                                    this.getItemStackDisplayName(stack), nextTier.getNameForTranslationFormatted()));
                    }
                }

                WizardData.get(caster).trackRecentSpell(spell);
            }

            return true;
        }

        return false;
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return WandHelper.getCurrentSpell(stack) == Spells.none ?
                I18n.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim() :
                WandHelper.getCurrentSpell(stack).getDisplayNameWithFormatting();
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

                ItemStack newWand = new ItemStack(TalesMaps.getStaff(tier, this.element));
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
