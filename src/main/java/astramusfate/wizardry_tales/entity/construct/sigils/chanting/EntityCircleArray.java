package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.classes.IChestManaCollector;
import astramusfate.wizardry_tales.data.ChantWorker;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.data.chanting.SpellParams;
import astramusfate.wizardry_tales.events.SpellCreation;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class EntityCircleArray extends EntityCircleWords implements IChestManaCollector {
    public Predicate<Entity> filter = Objects::nonNull;
    public EntityCircleArray(World world){
        super(world);
    }

    public EntityCircleArray(World world, List<String> words) {
        super(world, words);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.isDead) return;
        if(Solver.doEvery(this, 5) && getCaster() != null && getCaster() instanceof EntityPlayer){
            if (SpellCreation.useMana(this, 1)) {
                if (words == null) {
                    return;
                }
                List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(width/2, this.posX, this.posY,
                        this.posZ, this.world);

                if (targets.isEmpty()) {
                    return;
                }

                for(Entity target : targets) {
                    if (isValidTarget(target) && filter.test(target)){
                        if (this.getCaster() instanceof EntityPlayerMP) {
                            SpellCreation.createSpell(words, this.getCaster(), target, true);
                        }
                        if (this.world.isRemote) {
                            SpellCreation.createSpell(words, this.getCaster(), target, false);
                        }
                    }
                }
            }else{
                this.setDead();
            }
        }
    }

    @Override
    public float getChestArea() {
        return this.width/2;
    }
}
