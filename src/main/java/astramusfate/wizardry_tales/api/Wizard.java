package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.entity.construct.sigils.EntityMagicCircleVertical;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.particle.ParticleEnchantmentTable;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Wizard {

    public static void conjureCircle(World world, Element element, Vec3d pos){
        EntityMagicCircle entity = new EntityMagicCircle(world);
        entity.setLocation(element == null ? "u_magic" : "u_" + element.func_176610_l());
        entity.setPosition(pos.x, pos.y + Tales.effects.y_offset, pos.z);
        entity.lifetime = Solver.asTicks(1);
        entity.chooseSize();
        entity.setSizeMultiplier((float) Tales.effects.circle_size);
        if(!world.isRemote) world.spawnEntity(entity);
    }

    public static void conjureCircle(World world, String location, Vec3d pos){
        EntityMagicCircle entity = getCircle(world, location, pos);
        if(!world.isRemote) world.spawnEntity(entity);
    }

    public static EntityMagicCircle getCircle(World world, String location, Vec3d pos){
        EntityMagicCircle entity = new EntityMagicCircle(world);
        entity.setLocation(location);
        entity.setPosition(pos.x, pos.y + Tales.effects.y_offset, pos.z);
        entity.lifetime = Solver.asTicks(1);
        entity.chooseSize();
        entity.setSizeMultiplier((float) Tales.effects.circle_size);
        return entity;
    }

    public static EntityMagicCircleVertical getVerticalCircle(World world, String location, Vec3d pos, Entity rotations){
        EntityMagicCircleVertical entity = new EntityMagicCircleVertical(world);
        entity.setLocation(location);
        entity.lifetime = Solver.asTicks(1);
        Vec3d view = rotations.getLookVec().scale(Tales.effects.look_distance);
        Vec3d look = new Vec3d(pos.x + view.x, pos.y + Tales.effects.look_y_offset
                + view.y, pos.z + view.z);
        entity.chooseSize();
        entity.setSizeMultiplier((float) Tales.effects.vertical_circle_size);
        entity.setPositionAndRotation(look.x,look.y,look.z,rotations.rotationYaw, rotations.rotationPitch);
        return entity;
    }

    public static void conjureVerticalCircle(World world, Element element, Vec3d pos, Entity rotations){
        EntityMagicCircleVertical entity = new EntityMagicCircleVertical(world);
        entity.setLocation(element == null ? "u_magic" : "u_" + element.func_176610_l());
        entity.lifetime = Solver.asTicks(1);
        Vec3d view = rotations.getLookVec().scale(Tales.effects.look_distance);
        Vec3d look = new Vec3d(pos.x + view.x, pos.y + Tales.effects.look_y_offset
                + view.y, pos.z + view.z);
        entity.chooseSize();
        entity.setSizeMultiplier((float) Tales.effects.vertical_circle_size);
        entity.setPositionAndRotation(look.x,look.y,look.z,rotations.rotationYaw, rotations.rotationPitch);
        if(!world.isRemote) world.spawnEntity(entity);
    }

    public static void conjureVerticalCircle(World world, String location, Vec3d pos, Entity rotations){
        EntityMagicCircleVertical entity = getVerticalCircle(world, location, pos, rotations);
        if(!world.isRemote) world.spawnEntity(entity);
    }

    public static void castParticles(World world, Element element, Vec3d pos){
        castParticles(world, element, pos, 18);
    }

    public static void castParticles(World world, Element element, Vec3d pos, int count){
        if(world.isRemote){
            for(int i = 0; i < count; i++) {
                double x = pos.x + Solver.range(2);
                double y = pos.y + Solver.range(2);
                double z = pos.z + Solver.range(2);
                ResourceLocation type = ParticleBuilder.Type.SPARKLE;
                switch (element) {
                    case FIRE:
                        ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(1f, 1f, 1f).spawn(world);
                        break;
                    case ICE:
                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
                        break;
                    case LIGHTNING:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(x,y,z).spawn(world);
                        break;
                    case NECROMANCY:
                        ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x800080).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x9400D3).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x4B0082).vel(0, -0.1, 0).spawn(world);
                        break;
                    case EARTH:
                        ParticleBuilder.create(ParticleBuilder.Type.LEAF).collide(true).pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
                        break;
                    case SORCERY:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0.2f, 0.6f, 1).spawn(world);

                        break;
                    case HEALING:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0.8f, 1, 0.5f).spawn(world);
                        break;

                    default: ParticleBuilder.create(type).collide(true).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(1f, 1f, 1f).spawn(world);
                        break;
                }

            }
        }
    }

    public static void castParticlesWithoutRange(World world, Element element, Vec3d pos, int count){
        if(world.isRemote){
            for(int i = 0; i < count; i++) {
                double x = pos.x;
                double y = pos.y;
                double z = pos.z;
                ResourceLocation type = ParticleBuilder.Type.SPARKLE;
                switch (element) {
                    case FIRE:
                        ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(1f, 1f, 1f).spawn(world);
                        break;
                    case ICE:
                        ParticleBuilder.create(ParticleBuilder.Type.SNOW).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
                        break;
                    case LIGHTNING:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARK).pos(x,y,z).spawn(world);
                        break;
                    case NECROMANCY:
                        ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x800080).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.DARK_MAGIC).pos(x, y, z).clr(0x9400D3).spawn(world);
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).time(12 + world.rand.nextInt(8)).clr(0x4B0082).vel(0, -0.1, 0).spawn(world);
                        break;
                    case EARTH:
                        ParticleBuilder.create(ParticleBuilder.Type.LEAF).collide(true).pos(x, y, z).vel(0, -0.1, 0).time(15).spawn(world);
                        break;
                    case SORCERY:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0.2f, 0.6f, 1).spawn(world);

                        break;
                    case HEALING:
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).collide(true)
                                .pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0.8f, 1, 0.5f).spawn(world);
                        break;

                    default: ParticleBuilder.create(type).collide(true).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(1f, 1f, 1f).spawn(world);
                        break;
                }

            }
        }
    }


    public static void castParticles(World world, EnumParticleTypes types, Vec3d pos, Vec3d motion, int count){
        if(world.isRemote){
            for(int i = 0; i < count; i++) {
                double x = pos.x + Solver.range(2);
                double y = pos.y + Solver.range(2);
                double z = pos.z + Solver.range(2);
                world.spawnParticle(types, x, y, z, motion.x, motion.y,motion.z);
            }
        }
    }

    public static void castBuff(World world, Entity entity, int color){
        castBuff(world, entity, color, 18);
    }

    public static void castBuff(World world, Entity entity, int color, int particleCount){
        if (world.isRemote) {
            for (int i = 0; i < particleCount; i++) {
                double x = entity.posX + world.rand.nextDouble() * 2 - 1;
                double y = entity.posY + entity.getEyeHeight() - 0.5 + world.rand.nextDouble();
                double z = entity.posZ + world.rand.nextDouble() * 2 - 1;
                ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).clr(color).spawn(world);
            }

            ParticleBuilder.create(ParticleBuilder.Type.BUFF).entity(entity).clr(color).spawn(world);
        }
    }
}
