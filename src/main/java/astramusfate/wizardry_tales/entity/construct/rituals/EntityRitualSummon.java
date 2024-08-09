package astramusfate.wizardry_tales.entity.construct.rituals;

import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.entity.construct.EntityRitual;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class EntityRitualSummon extends EntityRitual {

    public EntityRitualSummon(World world) {
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
    protected void ritualEnd() {
        this.playSound(WizardrySounds.BLOCK_LECTERN_LOCATE_SPELL, 1, 1);
        if (!world.isRemote) {
            Entity entity = getSummon();
            prepareSummon(entity);
            world.spawnEntity(entity);
        }

        if(world.isRemote){
            clientParticlesAtEnd();
        }
    }

    /** Get entity to summon. You can do all stuff except for changing position.
     *  If you want to change position yourself - override setSpecificPosition method.
     *  This class will take care of spawning entity though.
     *  This all is on server side.
     *  **/
    public abstract Entity getSummon();

    /** Prepare entity to be summoned. Set position and all other stuff. **/
    public void prepareSummon(Entity entity){
        entity.setPositionAndUpdate(this.posX, this.posY, this.posZ);
    }

    public void clientParticlesAtEnd(){
        Vec3d target = new Vec3d(this.posX, 255, this.posZ);

        ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(ArcaneColor.colorByElement(Element.NECROMANCY).getRGB())
                .pos(this.posX, this.posY, this.posZ).target(target)
                .scale(5f).time(20).spawn(world);
    }

}
