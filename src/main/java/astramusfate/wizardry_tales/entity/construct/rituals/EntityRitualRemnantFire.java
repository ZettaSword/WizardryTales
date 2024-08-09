package astramusfate.wizardry_tales.entity.construct.rituals;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityRemnant;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class EntityRitualRemnantFire extends EntityRitualSummon {
    public EntityRitualRemnantFire(World world) {
        super(world);
    }

    @Override
    protected Element getElement() {
        return Element.FIRE;
    }

    @Override
    public String getLocation() {
        return "fire";
    }

    @Override
    protected void tick() {
    }

    @Override
    public Entity getSummon() {
        EntityRemnant remnant = new EntityRemnant(world);
        remnant.setElement(Element.FIRE);
        return remnant;
    }
}
