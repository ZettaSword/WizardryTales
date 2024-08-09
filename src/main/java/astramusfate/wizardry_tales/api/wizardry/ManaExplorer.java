package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.classes.IChestManaCollector;
import astramusfate.wizardry_tales.entity.construct.EntityMagic;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.spell.ArcaneLock;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import zettasword.player_mana.Tales;
import zettasword.player_mana.cap.ISoul;
import zettasword.player_mana.cap.SoulProvider;

import java.util.List;

/** This class is specifically made to not cause issues with Spellcasting with Wiz. Player Mana and Tales **/
public class ManaExplorer {
    public static boolean useMana(Entity focal, double cost, boolean addProgress){
       cost*= Tales.mp.spell_multiplier;
        if (focal instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) focal;
            return collectMana(player, cost, addProgress);
        } else {
            if (focal instanceof EntityMagic) {
                EntityMagic magic = (EntityMagic) focal;
                if (magic.getCaster() instanceof EntityPlayer) {
                    if (chestUseMana(magic, cost)) return true;
                    EntityPlayer player = (EntityPlayer) magic.getCaster();
                    return collectMana(player, cost, addProgress);
                }
            }
        }
        return false;
    }

    public static boolean useAllMana(Entity focal, double min, boolean addProgress){
        min*= Tales.mp.spell_multiplier;
        if (focal instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) focal;
            return collectMana(player, min, addProgress);
        } else {
            if (focal instanceof EntityMagic) {
                EntityMagic magic = (EntityMagic) focal;
                if (magic.getCaster() instanceof EntityPlayer) {
                    if (chestUseMana(magic, min)) return true;
                    EntityPlayer player = (EntityPlayer) magic.getCaster();
                    return collectMana(player, min, addProgress);
                }
            }
        }
        return false;
    }
    public static boolean collectAllMana(EntityPlayer player, double min, boolean progress){
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if (soul == null) return false;
        if (player.isCreative()) return true;

        if (soul.getMP() >= min) {
            soul.addMana(player, -1 * soul.getMP());
            if (progress){
                double maxMana = soul.getMaxMP();
                double value = (maxMana + Math.max(Tales.mp.progression * (astramusfate.wizardry_tales.data.Tales.mp.progression_multiplier *
                        (soul.getMP() / maxMana)), Tales.mp.progression));
                soul.setMaxMP(player, Math.min(value, Tales.mp.max));
            }
            return true;
        }

        if (!player.world.isRemote) Aterna.translate(player, true, "mana.not_enough");

        return false;
    }
    public static boolean collectMana(EntityPlayer player, double cost, boolean progress){
       ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if (soul == null) return false;
        if (player.isCreative()) return true;

        if (soul.getMP() >= cost) {
            soul.addMana(player, -1 * cost);
            if (progress){
                double maxMana = soul.getMaxMP();
                double value = (maxMana + Math.max(Tales.mp.progression * (astramusfate.wizardry_tales.data.Tales.mp.progression_multiplier *
                        (cost / maxMana)), Tales.mp.progression));
                soul.setMaxMP(player, Math.min(value, Tales.mp.max));
            }
            return true;
        }

        if (!player.world.isRemote) Aterna.translate(player, true, "mana.not_enough");

        return false;
    }

    /** Requires to have Caster. **/
    public static boolean chestUseMana(Entity magic, double cost){
        if (magic == null) return false;
        if (!(magic instanceof IChestManaCollector)) return false;
        float range = ((IChestManaCollector) magic).getChestArea();
        List<BlockPos> positions = BlockUtils.getBlockSphere(magic.getPosition(), range);
        boolean result = false;
        if (positions.isEmpty()) return false;

        for (BlockPos pos : positions){
            TileEntity tile = magic.world.getTileEntity(pos);
            if (result) break;
            if (tile == null) continue;
            if (tile instanceof TileEntityChest && tile.getTileData().hasUniqueId(ArcaneLock.NBT_KEY)){
                TileEntityChest chest = (TileEntityChest) tile;
                NBTTagCompound tag = new NBTTagCompound();
                chest.writeToNBT(tag);
                NonNullList<ItemStack> stacks = NonNullList.create();
                ItemStackHelper.loadAllItems(tag, stacks);
                for (ItemStack stack : stacks){
                    if (stack.getItem() instanceof IManaStoringItem){
                        IManaStoringItem mana = (IManaStoringItem) stack.getItem();
                        if (mana.getManaCapacity(stack) >= (int) Math.round(cost)) {
                            mana.consumeMana(stack, (int) Math.round(cost), null);
                            magic.world.markAndNotifyBlock(pos,
                                    null,
                                    magic.world.getBlockState(pos),
                                    magic.world.getBlockState(pos), 3);
                            result=true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }
}
