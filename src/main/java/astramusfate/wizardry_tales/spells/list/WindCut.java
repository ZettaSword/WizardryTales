package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Selena;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class WindCut extends SpellRay {
    public WindCut() {
        super(WizardryTales.MODID, "wind_cut", SpellActions.POINT, true);
        ignoreUncollidables(false);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(caster != null && target instanceof EntityLivingBase){
            if(target instanceof IShearable && !world.isRemote) {
                IShearable sheep = (IShearable) target;
                if (sheep.isShearable(caster.getActiveItemStack(), world, target.getPosition())) {
                    List<ItemStack> drops = sheep.onSheared(caster.getActiveItemStack(), world, target.getPosition(), 1);
                    Random rand = new Random();

                    for (ItemStack stack : drops) {
                        EntityItem ent = target.entityDropItem(stack, 1.0F);
                        if (ent != null) {
                            ent.motionY += rand.nextFloat() * 0.05F;
                            ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                            ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        }
                    }
                }
            }
            EntityUtils.applyStandardKnockback(caster, (EntityLivingBase) target, 0.4F);
            //Selena.pushOne((EntityLivingBase) target, 0.4f);
        }
        return true;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlockHardness(world, pos) == 0.0f || state.getBlock() == Blocks.WEB
                || state.getBlock() == Blocks.LEAVES || state.getBlock() == Blocks.LEAVES2){
            world.destroyBlock(pos, true);
        }
        if(world.getBlockState(pos).getBlock() == Blocks.FIRE){
            world.setBlockToAir(pos);
        }
        return true;
    }

    @Override
    protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
