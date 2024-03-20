package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWizardArmour;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import vazkii.patchouli.common.item.ItemModBook;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/** Module for working with Inventory **/
public class Thief {

    /** Check is Player have needed item in the inventory(checks armor and hands too) **/
    public static boolean hasItem(EntityPlayer player, Item item){
        if(player.isCreative()) return true;

        for(ItemStack stack : player.inventory.mainInventory){
            if(stack.getItem() == item){
                return true;
            }
        }

        for(ItemStack stack : player.inventory.armorInventory){
            if(stack.getItem() == item){
                return true;
            }
        }

        for(ItemStack stack : player.inventory.offHandInventory){
            if(stack.getItem() == item){
                return true;
            }
        }

        return false;
    }

    /** Check is Player have needed item in the inventory(checks armor and hands too) **/
    public static ItemStack getItem(EntityPlayer player, Item item){
        if(player.isCreative()) return new ItemStack(item);

        for(ItemStack stack : player.inventory.mainInventory){
            if(stack.getItem() == item){
                return stack;
            }
        }

        for(ItemStack stack : player.inventory.armorInventory){
            if(stack.getItem() == item){
                return stack;
            }
        }

        for(ItemStack stack : player.inventory.offHandInventory){
            if(stack.getItem() == item){
                return stack;
            }
        }

        if(item == player.getHeldItemMainhand().getItem()) return player.getHeldItemMainhand();
        if(item == player.getHeldItemOffhand().getItem()) return player.getHeldItemOffhand();
        return null;
    }

    /** Check is Player have needed item in the inventory(checks armor and hands too) **/
    public static ItemStack getItem(EntityPlayer player, Predicate<ItemStack> item, ItemStack creative){
        if(item.test(player.getHeldItemMainhand())) return player.getHeldItemMainhand();
        if(item.test(player.getHeldItemOffhand())) return player.getHeldItemOffhand();

        if(player.isCreative() && creative != null) return creative;

        for(ItemStack stack : player.inventory.mainInventory){
            if(item.test(stack)){
                return stack;
            }
        }

        for(ItemStack stack : player.inventory.armorInventory){
            if(item.test(stack)){
                return stack;
            }
        }

        for(ItemStack stack : player.inventory.offHandInventory){
            if(item.test(stack)){
                return stack;
            }
        }

        return null;
    }

    /** Check is Player have needed item in the inventory(checks armor and hands too) **/
    public static ItemStack getItem(EntityPlayer player, Predicate<ItemStack> item){
       return getItem(player, item, null);
    }

    /** If player/entity has no items in hand! **/
    public static boolean noItemsInHands(EntityLivingBase player){
        return player.getHeldItem(EnumHand.MAIN_HAND).getItem() == Items.AIR &&
                player.getHeldItem(EnumHand.OFF_HAND).getItem() == Items.AIR;
    }

    public static boolean testPredicateHands(EntityLivingBase player, Predicate<ItemStack> predicate){
        return predicate.test(player.getHeldItem(EnumHand.MAIN_HAND)) &&
                predicate.test(player.getHeldItem(EnumHand.OFF_HAND));
    }


    /** Yup, just to check inventory .-. **/
    public static boolean checkInventory(EntityPlayer player, Item item){
        for(ItemStack stack : player.inventory.mainInventory){
            if(stack.getItem() == item){
                return true;
            }
        }
        return false;
    }

    /** Checking some Armor on player **/
    public static boolean checkArmor(EntityPlayer player, Item item){
        for(ItemStack stack : player.inventory.armorInventory){
            if(stack.getItem() == item){
                return true;
            }
        }
        return false;
    }

    /** Is there THIS item in there in one of hands??? **/
    public static boolean checkHands(EntityPlayer player, Item item){
        return player.getHeldItem(EnumHand.MAIN_HAND).getItem() == item || player.getHeldItem(EnumHand.OFF_HAND).getItem() == item;
    }

    /** Takes item in Hands, else returns null **/
    @Nullable
    public static ItemStack getInHands(EntityPlayer player){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(left.isEmpty() && right.isEmpty()) return null;
        if(right.isEmpty()) return left;
        return right;
    }

    /** Takes item in Hands, else returns null **/
    @Nullable
    public static ItemStack getInHands(EntityPlayer player, Predicate<ItemStack> predicate){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(right.isEmpty() && predicate.test(left))
            return left;
        if(!right.isEmpty() && predicate.test(right))
            return right;
        return null;
    }

    /** Takes item in Hands, else returns null **/
    @Nullable
    public static ItemStack getSpellCasting(EntityLivingBase player){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(right.getItem() instanceof ISpellCastingItem) return right;
        else if(left.getItem() instanceof ISpellCastingItem) return left;
        return null;
    }

    /** Takes item in Hands, else returns null **/
    @Nullable
    public static ItemStack getInHands(EntityLivingBase player, Item item){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(right.getItem() == item) return right;
        if(left.getItem() == item) return left;
        return null;
    }

    /** Find the Hand with item **/
    @Nullable
    public static EnumHand getHand(EntityLivingBase player, Item item){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(right.getItem() == item) return EnumHand.MAIN_HAND;
        if(left.getItem() == item) return EnumHand.OFF_HAND;
        return null;
    }

    /** Is there THIS item in there??? **/
    public static boolean checkOffhand(EntityPlayer player, Item item){
        for(ItemStack stack : player.inventory.offHandInventory){
            if(stack.getItem() == item){
                return true;
            }
        }
        return false;
    }

    /** Adds item to the player inventory **/
    public static void addItem(EntityPlayer player, Item item){
        if(!player.addItemStackToInventory(new ItemStack(item, 1))){
            player.dropItem(new ItemStack(item, 1), false);
        }
    }

    /** Adds ItemStack to the player inventory **/
    public static void addItem(EntityPlayer player, ItemStack stack){
        if(!player.addItemStackToInventory(stack)){
            player.dropItem(stack, false);
        }
    }

    /** If you need Meta be changed, use stack(item, meta) instead **/
    public static ItemStack stack(Item item){
        return new ItemStack(item, 1, 0);
    }

    /** If you want to get more then one - use stacked method instead **/
    public static ItemStack stack(Item item, int meta){
        return new ItemStack(item, 1, meta);
    }

    /** Hello **/
    public static ItemStack stacked(Item item, int count, int meta){
        return new ItemStack(item, count, meta);
    }

    public static ItemStack getWandInUse(EntityPlayer player){
        if(ItemArtefact.isArtefactActive(player, TalesItems.casting_ring)
                && Thief.testPredicateHands(player, i -> !(i.getItem() instanceof ISpellCastingItem))) {
            return Thief.getItem(player, item -> item.getItem() instanceof ISpellCastingItem
                    && item.getItem() instanceof IManaStoringItem);
        }
        return null;
    }

    public static ItemStack getWandInUseUniversal(EntityPlayer player){
        if(Thief.testPredicateHands(player, i -> !(i.getItem() instanceof ISpellCastingItem))) {
            return Thief.getItem(player, item -> item.getItem() instanceof ISpellCastingItem
                    && item.getItem() instanceof IManaStoringItem);
        }
        return null;
    }
}
