package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class BurningDisease extends SpellRay {

    public static final String PROC_DAMAGE = "proc_damage";
    public static final String PROC_CHANCE = "proc_chance";

    public BurningDisease() {
        super(WizardryTales.MODID, "burning_disease", SpellActions.POINT, false);
        this.addProperties(EFFECT_DURATION, EFFECT_STRENGTH, DAMAGE, PROC_DAMAGE, PROC_CHANCE);
        this.soundValues(1, 1.4f, 0.4f);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

        if(EntityUtils.isLiving(target)){
            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(TalesEffects.burning_disease,
                    (int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(Sage.DURATION)),
                    getProperty(EFFECT_STRENGTH).intValue()));

            return true;
        }

        return false; // If the spell hit a non-living entity
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
    protected void spawnParticle(World world, double x, double y, double z, double vx, double vy, double vz){
        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0, 0, 0);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
