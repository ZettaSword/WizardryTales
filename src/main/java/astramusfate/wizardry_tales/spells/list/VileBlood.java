package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class VileBlood extends SpellRay {
    public VileBlood() {
        super(WizardryTales.MODID, "vile_blood", SpellActions.POINT, false);
        this.addProperties(DURATION, EFFECT_STRENGTH);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(target instanceof EntityLivingBase && caster != null){
            EntityLivingBase base = (EntityLivingBase) target;
            Alchemy.applyPotion(base, (int)(getProperty(DURATION).intValue() * modifiers.get(Sage.POTENCY)),
                    getProperty(EFFECT_STRENGTH).intValue(), MobEffects.SLOWNESS);
            if(!world.isRemote) Wizard.conjureCircle(world, "u_vampires", base.getPositionVector());
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
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
