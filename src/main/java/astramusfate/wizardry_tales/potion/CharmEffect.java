package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.spells.list.Charm;
import astramusfate.wizardry_tales.spells.list.PermanentMindControl;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.potion.PotionMagicEffect;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.MindControl;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

@Mod.EventBusSubscriber
public class CharmEffect extends PotionMagicEffect {
    public CharmEffect() {
        super(true,
                Color.MAGENTA.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/charm.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":charm");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (entity instanceof EntityLiving) {
            NBTTagCompound entityNBT = entity.getEntityData();
            if (entityNBT.hasUniqueId(Charm.NBT_KEY)) {

                Entity caster = EntityUtils.getEntityByUUID(entity.world, entityNBT.getUniqueId(Charm.NBT_KEY));
                if (caster instanceof EntityLivingBase && !entity.world.isRemote) {
                    //((EntityLiving) entity).getNavigator().tryMoveToEntityLiving(caster, 2.0F);
                    moveToTarget(entity, (EntityLivingBase) caster);
                    ((EntityLiving) entity).setAttackTarget(null);
                }
            }
        }

        if (entity instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) entity;
            WizardData data = WizardData.get(player);
            if (data != null) {
                Entity caster = EntityUtils.getEntityByUUID(player.world, data.getVariable(Charm.UUID_CHARMED));
                if (caster instanceof EntityLivingBase) {
                    moveToTarget(player, (EntityLivingBase) caster);
                }
            }
        }
    }

    /** The following method is method from Ice & Fire, and same applies to updateRotations method below. **/
    public static void moveToTarget(EntityLivingBase move, EntityLivingBase to){
        if (move.collidedHorizontally) {
            move.setJumping(true);
        }
        move.motionX += (Math.signum(to.posX - move.posX) * 0.25D - move.motionX) * 0.1;
        move.motionY += (Math.signum(to.posY - move.posY + 1) * 0.25D - move.motionY) * 0.1;
        move.motionZ += (Math.signum(to.posZ - move.posZ) * 0.25D - move.motionZ) * 0.1;
        double d0 = to.posX - move.posX;
        double d2 = to.posZ - move.posZ;
        double d1 = to.posY - move.posY + 0.25;
        if (move.isRiding()) {
            move.dismountRidingEntity();
        }
        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
        float f = (float) (Math.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
        float f1 = (float) (-(Math.atan2(d1, d3) * (180D / Math.PI)));
        move.rotationPitch = updateRotation(move.rotationPitch, f1, 30F);
        move.rotationYaw = updateRotation(move.rotationYaw, f, 30F);
    }

    public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = MathHelper.wrapDegrees(targetAngle - angle);
        if (f > maxIncrease) {
            f = maxIncrease;
        }
        if (f < -maxIncrease) {
            f = -maxIncrease;
        }
        return angle + f;
    }
    

    @SubscribeEvent
    public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event){
        onEffectEnd(event.getPotionEffect(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPotionExpiryEvent(PotionEvent.PotionRemoveEvent event){
        onEffectEnd(event.getPotionEffect(), event.getEntity());
    }

    private static void onEffectEnd(PotionEffect effect, Entity entity) {
        if (effect != null && effect.getPotion() == TalesEffects.charm && entity instanceof EntityLiving) {
            ((EntityLiving) entity).setAttackTarget(null); // End effect
            ((EntityLiving) entity).setRevengeTarget(null); // End effect
        }
    }

    @SubscribeEvent
    public static void onLivingSetAttackTargetEvent(LivingSetAttackTargetEvent event){
        // The != null check prevents infinite loops with mind trick
        if(event.getTarget() != null && event.getEntityLiving() instanceof EntityLiving &&
        event.getEntityLiving().isPotionActive(TalesEffects.charm)){
            ((EntityLiving) event.getEntityLiving()).setAttackTarget(null);
        }

    }
}
