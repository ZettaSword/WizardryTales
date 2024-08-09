package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.api.wizardry.TalesVampirism;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Alohomora extends SpellRay {

    /** The NBT tag name for storing the owner's UUID in the tile entity data. */
    public static final String NBT_KEY = "arcaneLockOwner";

    public Alohomora() {
        super(WizardryTales.MODID, "alohomora", SpellActions.POINT, false);
    }

    @Override public boolean canBeCastBy(TileEntityDispenser dispenser){ return false; }

    @Override public boolean canBeCastBy(EntityLiving npc, boolean override){ return false; }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(caster instanceof EntityPlayer && !world.isRemote){
            EntityPlayer player = (EntityPlayer)caster;
            IBlockState blockstate = world.getBlockState(pos);

            if(blockstate.getBlock().onBlockActivated(world, pos, blockstate, player, EnumHand.MAIN_HAND,
                    side, 0, 0, 0)){
                return true;
            }

            if (blockstate.getBlock() instanceof BlockDoor){
                ((BlockDoor) blockstate.getBlock()).toggleDoor(world, pos, true);
                return true;
            }

            if (blockstate.getBlock() instanceof BlockTrapDoor){
                blockstate = blockstate.cycleProperty(BlockTrapDoor.OPEN);
                world.setBlockState(pos, blockstate, 2);
                boolean open = blockstate.getValue(BlockTrapDoor.OPEN);
                if (open)
                    world.playEvent(player, 1007, pos, 0);
                else
                    world.playEvent(player, 1013, pos, 0);
                return true;
            }

            if(Tales.spells.alohomora_arcane_locks && toggleLock(world, pos, (EntityPlayer)caster)){
                BlockPos otherHalf = BlockUtils.getConnectedChest(world, pos);
                if(otherHalf != null) toggleLock(world, otherHalf, (EntityPlayer)caster);
                return true;
            }

        }
        return false;
    }

    private boolean toggleLock(World world, BlockPos pos, EntityPlayer player){

        TileEntity tileentity = world.getTileEntity(pos);

        if(tileentity != null){

            if(tileentity.getTileData().hasUniqueId(NBT_KEY)){
                // Unlocking
                NBTExtras.removeUniqueId(tileentity.getTileData(), NBT_KEY);
                world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 3);
                return true;
            }
        }

        return false;
    }

    @Override
    protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
