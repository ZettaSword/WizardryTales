package astramusfate.wizardry_tales.entity.construct;

import electroblob.wizardry.constants.Element;
import net.minecraft.world.World;

public abstract class EntityRitual extends EntityMagicScaled {
    public EntityRitual(World world) {
        super(world);
        setSize(3.0f*2, 0.2f);
        setLifetime(120);
        this.setNoGravity(true);
        this.noClip=true;
    }

    public EntityRitual(World world, int lifetime) {
        super(world);
        setSize(3.0f*2, 0.2f);
        setLifetime(lifetime);
        this.setNoGravity(true);
        this.noClip=true;
    }

    public EntityRitual(World world, float size, int lifetime) {
        super(world);
        setSize(size, 0.2f);
        setLifetime(lifetime);
        this.setNoGravity(true);
        this.noClip=true;
    }

    protected abstract Element getElement();
    public abstract String getLocation();

    public int getLife(){return this.ticksExisted;}

    @Override
    public boolean canRenderOnFire(){
        return false;
    }

    @Override
    public boolean canBeCollidedWith() {
        return false;
    }

    protected abstract void tick();
    /** When ritual ends, it's being cast. **/
    protected abstract void ritualEnd();

    @Override
    public void onUpdate() {
        super.onUpdate();
        tick();
    }

    @Override
    public void despawn() {
        ritualEnd();
        super.despawn();
    }
}