package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Selena;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WindSlash extends SpellRay {
    public WindSlash() {
        super(WizardryTales.MODID, "wind_slash", SpellActions.POINT, true);
        this.addProperties(DAMAGE);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(target instanceof EntityLivingBase) {
            target.setFire(0);
            target.attackEntityFrom(new DamageSource(MagicDamage.DamageType.BLAST.name()), this.getProperty(DAMAGE).intValue() * modifiers.get(SpellModifiers.POTENCY));
            if(caster != null) {
                EntityUtils.applyStandardKnockback(caster, (EntityLivingBase) target, 0.4F);
            }else{
                Selena.pushOne((EntityLivingBase) target, 0.4F);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        IBlockState state = world.getBlockState(pos);
        if(state.getBlockHardness(world, pos) == 0.0f || state.getBlock() == Blocks.WEB
                || state.getBlock() == Blocks.LEAVES || state.getBlock() == Blocks.LEAVES2){
            world.destroyBlock(pos, false);
            return true;
        }
        if(state.getBlock() == Blocks.FIRE){
            world.setBlockToAir(pos);
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
