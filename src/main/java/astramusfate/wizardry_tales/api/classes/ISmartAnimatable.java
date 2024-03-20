package astramusfate.wizardry_tales.api.classes;

import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.controller.AnimationController;

import java.util.UUID;

public interface ISmartAnimatable extends IAnimatable {

    default AnimationController getDeathAnimationController(UUID uuid){
        if(this.getFactory().getOrCreateAnimationData(uuid.hashCode())
                .getAnimationControllers().containsKey("controller_death")){
            return this.getFactory().getOrCreateAnimationData(uuid.hashCode())
                    .getAnimationControllers().get("controller_death");
        }
        return null;
    }

    default AnimationController getAnimationController(UUID uuid, String name){
        if(this.getFactory().getOrCreateAnimationData(uuid.hashCode())
                .getAnimationControllers().containsKey(name)){
            return this.getFactory().getOrCreateAnimationData(uuid.hashCode())
                    .getAnimationControllers().get(name);
        }
        return null;
    }

    default boolean isIdle(AnimationController controller){
        return controller.getCurrentAnimation() == null;
    }

    default boolean isIdle(AnimationController controller, boolean bool){
        return controller.getCurrentAnimation() == null && bool;
    }

    /** Return value entity needs to die, usually is 40 **/
    int getTimeToDie();

    /// DEATH STUFF EXAMPLE
    /*
    @Override
    protected void onDeathUpdate() {
        // Do nothing here!
    }

    @Override
    public int getTimeToDie() {
        return 40;
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (getHealth() <= 0.0F) {
            if (getDeathAnimationController(this.getUniqueID()) != null) {
                AnimationController death = getDeathAnimationController(this.getUniqueID());
                if(death.getAnimationState() == AnimationState.Stopped) {
                    onDeathUpdate();
                }
            }else {
                onDeathUpdate();
            }
        }
    }

    private void onDeathUpdate() {
        ++this.deathTime;

        if (this.deathTime == getTimeToDie()) {
            if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
                int i = this.getExperiencePoints(this.attackingPlayer);
                i = net.minecraftforge.event.ForgeEventFactory.getExperienceDrop(this, this.attackingPlayer, i);
                while (i > 0) {
                    int j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
                }
            }

            this.setDead();

            for (int k = 0; k < 20; ++k) {
                double d2 = this.rand.nextGaussian() * 0.02D;
                double d0 = this.rand.nextGaussian() * 0.02D;
                double d1 = this.rand.nextGaussian() * 0.02D;
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_NORMAL, this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, this.posY + (double) (this.rand.nextFloat() * this.height), this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, d2, d0, d1);
            }
        }
    }

    */
    // DEATH STUFF
}
