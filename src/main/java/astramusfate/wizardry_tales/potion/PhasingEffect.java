package astramusfate.wizardry_tales.potion;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import electroblob.wizardry.potion.PotionMagicEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

/** It's known Phasing has some glitches depending on minecraft rendering, which is fixed by Yung's Better Caves mod for unknown reason. **/
@Mod.EventBusSubscriber
public class PhasingEffect extends PotionMagicEffect {


    public PhasingEffect() {
        super(true,
                Color.RED.getRGB(), new ResourceLocation(WizardryTales.MODID, "textures/potions/repel.png"));
            this.setPotionName("potion." + WizardryTales.MODID + ":repel");
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase entity, int amplifier) {
        //entity.motionX = entity.getLookVec().x * (entity.moveForward);
        //entity.motionY = entity.getLookVec().y * (entity.moveForward);
        //entity.motionZ = entity.getLookVec().z * (entity.moveForward);
        if (entity instanceof EntityPlayer){
            entity.noClip = true;
            EntityPlayer player = ((EntityPlayer) entity);
            player.onGround=false;
            player.capabilities.allowFlying = true;
            player.capabilities.isFlying = true;
        }


        //if (Math.abs(entity.motionY) > 0) entity.motionY*=1.2;
        /*
        World world = entity.getEntityWorld();
        BlockPos centre = entity.getPosition();
        List<BlockPos> blocks =Lists.newArrayList(centre.east(), centre.south(), centre.north(), centre.west());
        boolean phase = false;
        for (BlockPos pos : blocks){
            IBlockState state = world.getBlockState(pos);
            if (state.getMaterial().isSolid()){
                phase = true;
                break;
            }
        }
        if (phase){
            entity.noClip = true;
            entity.motionY = 0.0F;
        }else{
            entity.noClip = false;
        }
         */
    }

    @SubscribeEvent
    public static void onPotionExpiryEvent(PotionEvent.PotionExpiryEvent event){
        onEffectEnd(event.getPotionEffect(), event.getEntity());
    }

    @SubscribeEvent
    public static void onPotionExpiryEvent(PotionEvent.PotionRemoveEvent event){
        onEffectEnd(event.getPotionEffect(), event.getEntity());
    }

    private static void onEffectEnd(PotionEffect effect, Entity entity){
        if(effect != null && effect.getPotion() == TalesEffects.phasing){
            entity.noClip = false;
            entity.onGround = true;

            if (entity instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) entity;
                if (!player.capabilities.isCreativeMode) {
                    player.capabilities.allowFlying = false;
                    player.capabilities.isFlying = false;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onLivingAttackEvent(LivingAttackEvent event){
        if(event.getSource() != null){
            // Prevents all blockable damage while transience is active
            if(event.getEntityLiving().isPotionActive(TalesEffects.phasing)
                    && event.getSource() != DamageSource.OUT_OF_WORLD){
                event.setCanceled(true);
            }
            // Prevents transient entities from causing any damage
            if(event.getSource().getTrueSource() instanceof EntityLivingBase
                    && ((EntityLivingBase)event.getSource().getTrueSource()).isPotionActive(TalesEffects.phasing)){
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerInteractEvent(PlayerInteractEvent event){
        // Prevents transient players from interacting with the world in any way
        if(event.isCancelable() && event.getEntityPlayer().isPotionActive(TalesEffects.phasing)){
            event.setCanceled(true);
        }
    }
}
