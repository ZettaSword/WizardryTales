package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Allocate extends SpellAreaEffect {


    public static final String MINIMUM_TELEPORT_DISTANCE = "minimum_teleport_distance";
    public static final String MAXIMUM_TELEPORT_DISTANCE = "maximum_teleport_distance";

    public Allocate() {
        super(WizardryTales.MODID, "allocate", SpellActions.SUMMON, false);
        this.addProperties(MINIMUM_TELEPORT_DISTANCE, MAXIMUM_TELEPORT_DISTANCE);
    }

    @Override
    protected boolean affectEntity(World world, Vec3d origin, @Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
        if (target != null){
            double minRadius = TalesSpells.allocate.getProperty("minimum_teleport_distance").doubleValue();
            double maxRadius = TalesSpells.allocate.getProperty("maximum_teleport_distance").doubleValue();
            double radius = (minRadius + world.rand.nextDouble() * maxRadius-minRadius);

            return teleport(target, world, radius);
        }
        return false;
    }

    // Extracted as a separate method for external use
    public static boolean teleport(EntityLivingBase entity, World world, double radius){

        float angle = world.rand.nextFloat() * (float)Math.PI * 2;

        int x = MathHelper.floor(entity.posX + MathHelper.sin(angle) * radius);
        int z = MathHelper.floor(entity.posZ - MathHelper.cos(angle) * radius);
        Integer y = BlockUtils.getNearestFloor(world,
                new BlockPos(x, (int)entity.posY, z), (int)radius);

        if(world.isRemote){

            for(int i=0; i<10; i++){
                double dx1 = entity.posX;
                double dy1 = entity.posY + entity.height * world.rand.nextFloat();
                double dz1 = entity.posZ;
                world.spawnParticle(EnumParticleTypes.PORTAL, dx1, dy1, dz1, world.rand.nextDouble() - 0.5,
                        world.rand.nextDouble() - 0.5, world.rand.nextDouble() - 0.5);
            }

            if(entity instanceof EntityPlayer) Wizardry.proxy.playBlinkEffect((EntityPlayer)entity);
        }

        if(y != null){

            // This means stuff like snow layers is ignored, meaning when on snow-covered ground the target does
            // not teleport 1 block above the ground.
            if(!world.getBlockState(new BlockPos(x, y, z)).getMaterial().blocksMovement()){
                y--;
            }

            if(world.getBlockState(new BlockPos(x, y + 1, z)).getMaterial().blocksMovement()
                    || world.getBlockState(new BlockPos(x, y + 2, z)).getMaterial().blocksMovement()){
                return false;
            }

            if(!world.isRemote){
                entity.setPositionAndUpdate(x + 0.5, y + 1, z + 0.5);
            }
        }

        return true;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
