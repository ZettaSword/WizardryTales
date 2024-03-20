package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.api.classes.IChestManaCollector;
import astramusfate.wizardry_tales.events.SpellCreation;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EntityCircleAreaOnceCast extends EntityCircleWords implements IChestManaCollector {
    public Predicate<Entity> filter = Objects::nonNull;
    public EntityCircleAreaOnceCast(World world){
        super(world);
    }

    public EntityCircleAreaOnceCast(World world, List<String> words) {
        super(world, words);
    }

    @Override
    protected boolean shouldScaleHeight(){
        return false;
    }

    @Override
    public void onUpdate(){
        super.onUpdate();
        if (words == null) {
            return;
        }
        List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width/2, this.posX, this.posY,
                this.posZ, this.world);

        if (targets.isEmpty()) {
            return;
        }

        for(EntityLivingBase target : targets) {
            if (this.isValidTarget(target) && filter.test(target)) {
                if (this.getCaster() instanceof EntityPlayerMP) {
                    SpellCreation.createSpell(words, this, target, true);
                }
                if (this.world.isRemote) {
                    SpellCreation.createSpell(words, this, target, false);
                }
            }
        }


        if(this.world.isRemote && this.rand.nextInt(15) == 0){
            double radius = (0.5 + rand.nextDouble() * 0.3) * width/2;
            float angle = rand.nextFloat() * (float)Math.PI * 2;
            Wizard.castParticlesWithoutRange(world, Element.MAGIC,new Vec3d(this.posX + radius * MathHelper.cos(angle), this.posY + 0.1,
                    this.posZ + radius * MathHelper.sin(angle)), 1);
        }

        if (!this.world.isRemote) {
            this.setDead();
        }
    }

    @Override
    public float getChestArea() {
        return this.width/2;
    }
}
