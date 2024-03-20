package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.WizardrySounds;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.IRarity;

public class ItemInfiniteSummon extends ItemTales {
    public ItemInfiniteSummon(){
        super(16);
        setCreativeTab(TalesTabs.Items);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase living, EnumHand hand) {
        if (living instanceof ISummonedCreature){
            ((ISummonedCreature) living).setLifetime(-1);
            player.playSound(WizardrySounds.MISC_DISCOVER_SPELL, 0.7F, 1.0f);
            player.getCooldownTracker().setCooldown(this, 60);
            stack.shrink(1);
            return true;
        }
        return super.itemInteractionForEntity(stack, player, living, hand);
    }

    @Override
    public IRarity getForgeRarity(ItemStack p_getForgeRarity_1_) {
        return EnumRarity.EPIC;
    }
}
