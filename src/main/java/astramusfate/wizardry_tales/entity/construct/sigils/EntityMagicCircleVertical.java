package astramusfate.wizardry_tales.entity.construct.sigils;

import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import net.minecraft.world.World;

public class EntityMagicCircleVertical extends EntityMagicCircle {
    public EntityMagicCircleVertical(World world) {
        super(world);
    }

    public EntityMagicCircleVertical(World world, String location) {
        super(world);
        setLocation(location);
    }

    @Override
    public void chooseSize() {
        setSize(0.2f, this.height);
    }

    @Override
    protected boolean shouldScaleHeight() {
        return true;
    }

    @Override
    protected boolean shouldScaleWidth() {
        return false;
    }


}
