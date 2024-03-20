package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.entity.EntityManaBomb;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.registry.WizardryTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ItemManaBomb extends Item {
    public ItemManaBomb(){
        setMaxStackSize(16);
        setCreativeTab(WizardryTabs.WIZARDRY);
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand){

        ItemStack stack = player.getHeldItem(hand);

        if(!player.isCreative()){
            stack.shrink(1);
        }

        player.playSound(WizardrySounds.ENTITY_SMOKE_BOMB_THROW, 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));

        player.getCooldownTracker().setCooldown(this, 20);

        if(!world.isRemote){
            EntityManaBomb bomb = new EntityManaBomb(world);
            bomb.aim(player, 1);
            world.spawnEntity(bomb);
        }

        return ActionResult.newResult(EnumActionResult.SUCCESS, stack);
    }
}
