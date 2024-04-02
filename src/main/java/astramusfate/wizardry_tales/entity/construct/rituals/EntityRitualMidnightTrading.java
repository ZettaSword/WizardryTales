package astramusfate.wizardry_tales.entity.construct.rituals;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.entity.construct.EntityRitual;
import astramusfate.wizardry_tales.entity.living.EntityMidnightTrader;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityRitualMidnightTrading extends EntityRitual {

    public EntityRitualMidnightTrading(World world) {
        super(world);
    }

    @Override
    protected Element getElement() {
        return Element.NECROMANCY;
    }

    @Override
    public String getLocation() {
        return "u_necromancy";
    }

    @Override
    protected void tick() {
        
    }

    @Override
    protected void ritualEnd() {
        this.playSound(SoundEvents.ENTITY_VILLAGER_TRADING, 1, 1);
        if (!world.isRemote) {
            EntityMidnightTrader trader = new EntityMidnightTrader(world);
            trader.setLifetime(Solver.asTicks(60*5));
            trader.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            world.spawnEntity(trader);
        }

        if(world.isRemote){
            Vec3d target = new Vec3d(this.posX, 255, this.posZ);

            ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(ArcaneColor.colorByElement(Element.NECROMANCY).getRGB())
                    .pos(this.posX, this.posY, this.posZ).target(target)
                    .scale(5f).time(20).spawn(world);
        }
    }
}
