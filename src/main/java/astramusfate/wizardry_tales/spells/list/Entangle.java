package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Entangle extends SpellRay {

    public static final String FIRE_DAMAGE = "increase_fire_damage";

    public Entangle() {
        super(WizardryTales.MODID, "entangle", SpellActions.POINT, false);
        this.addProperties(EFFECT_DURATION, EFFECT_STRENGTH, FIRE_DAMAGE, "damage_if_burning");
        this.soundValues(1, 1.4f, 0.4f);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {

        if(EntityUtils.isLiving(target)){
            if(target.isBurning()) return false;

            ((EntityLivingBase)target).addPotionEffect(new PotionEffect(TalesEffects.entangled,
                    (int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(Sage.DURATION)),
                    getProperty(EFFECT_STRENGTH).intValue()));

            if(caster instanceof EntityPlayer){
                if(ItemArtefact.isArtefactActive((EntityPlayer) caster, TalesItems.ring_poison_entangle)){
                    ((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.POISON,
                            (int)(getProperty(EFFECT_DURATION).floatValue() * modifiers.get(Sage.DURATION)),
                            (int) Math.min(getProperty(EFFECT_STRENGTH).intValue() * (modifiers.get(Sage.POTENCY) - 0.25f), 1)));
                }
            }

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
        float brightness = world.rand.nextFloat() * 0.25f;
        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(20 + world.rand.nextInt(8))
                .clr(brightness, brightness + 0.1f, 0).spawn(world);
        ParticleBuilder.create(ParticleBuilder.Type.LEAF).pos(x, y, z).vel(0, -0.01, 0).time(40 + world.rand.nextInt(10)).spawn(world);
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
