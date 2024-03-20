package astramusfate.wizardry_tales.items.artefacts;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.items.MagicWeapon;
import astramusfate.wizardry_tales.registry.TalesTabs;
import astramusfate.wizardry_tales.spells.TalesSpells;
import com.google.common.collect.Multimap;
import electroblob.wizardry.item.IConjuredItem;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.MagicDamage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

public class IceHalberd extends MagicWeapon implements IConjuredItem {
    public IceHalberd() {
        super(4,-3, EMPTY_EFFECTIVE,"ice_halberd", WizardryItems.Materials.MAGICAL);
        setMaxDamage(1200);
        setNoRepair();
        setCreativeTab(TalesTabs.Items);
    }

    @Override
    public void magicAttack(World world, ItemStack stack, EntityLivingBase attacker, EntityLivingBase target) {
        // Nope, we do not want summons to do this yet! TODO: Artefact for this
    }

    @Override
    public boolean hitEntity(@Nonnull ItemStack stack, @Nonnull EntityLivingBase target, @Nonnull EntityLivingBase attacker) {
        if(!MagicDamage.isEntityImmune(MagicDamage.DamageType.FROST, target))
            Alchemy.produceWeaker(target, WizardryPotions.frost, 40);
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    // Why does this still exist? Item models deal with this now, right?
    public boolean isFull3D(){
        return true;
    }


    @Override
    public boolean getIsRepairable(@Nonnull ItemStack stack, @Nonnull ItemStack par2ItemStack){
        return false;
    }

    @Override
    public int getItemEnchantability(){
        return 0;
    }

    @Override
    public boolean isEnchantable(@Nonnull ItemStack stack){
        return false;
    }

    @Override
    public boolean isBookEnchantable(@Nonnull ItemStack stack, @Nonnull ItemStack book){
        return false;
    }

    // Cannot be dropped
    @Override
    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player){
        return false;
    }

    @Override
    public int getMaxDamage(@Nonnull ItemStack stack){
        return this.getMaxDamageFromNBT(stack, TalesSpells.conjure_ice_halberd);
    }

    @Override
    public int getRGBDurabilityForDisplay(@Nonnull ItemStack stack){
        return IConjuredItem.getTimerBarColour(stack);
    }

    @Override
    // This method allows the code for the item's timer to be greatly simplified by damaging it directly from
    // onUpdate() and removing the workaround that involved WizardData and all sorts of crazy stuff.
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, @Nonnull ItemStack newStack, boolean slotChanged){

        if(!oldStack.isEmpty() || !newStack.isEmpty()){
            // We only care about the situation where we specifically want the animation NOT to play.
            if(oldStack.getItem() == newStack.getItem() && !slotChanged) return false;
        }

        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged);
    }

    @Override
    public void onUpdate(ItemStack stack, @Nonnull World world, @Nonnull Entity entity, int slot, boolean selected){
        int damage = stack.getItemDamage();
        if(damage > stack.getMaxDamage()) InventoryUtils.replaceItemInInventory(entity, slot, stack, ItemStack.EMPTY);
        stack.setItemDamage(damage + 1);
    }

    @Nonnull
    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(@Nonnull EntityEquipmentSlot slot, @Nonnull ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getItemAttributeModifiers(slot);

        if (slot == EntityEquipmentSlot.MAINHAND)
        {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(POTENCY_MODIFIER,
                    "Potency modifier", IConjuredItem.getDamageMultiplier(stack) - 1, EntityUtils.Operations.MULTIPLY_CUMULATIVE));

            multimap.put(EntityPlayer.REACH_DISTANCE.getName(), new AttributeModifier(RANGE_MODIFIER, "Weapon modifier", 1, Solver.ADD));
        }

        return multimap;
    }
}
