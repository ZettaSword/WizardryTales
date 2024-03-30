package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.ai.EntityAIFollowFriend;
import astramusfate.wizardry_tales.entity.ai.EntityAIFriendHurtByTarget;
import astramusfate.wizardry_tales.entity.ai.EntityAIFriendHurtTarget;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIAttackRanged;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class Befriend extends SpellRay {
    public Befriend() {
        super(WizardryTales.MODID, "befriend", SpellActions.POINT, false);
        //ignoreUncollidables(false);
    }

    @Override
    protected boolean onEntityHit(World world, Entity target, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        if(caster != null && target instanceof EntityCreature && ((EntityCreature) target).getMaxHealth() <= (caster.getMaxHealth() * 2)){
            EntityCreature living = (EntityCreature) target;
            /**if(target instanceof IShearable && !world.isRemote) {
                IShearable sheep = (IShearable) target;
                if (sheep.isShearable(caster.getActiveItemStack(), world, target.getPosition())) {
                    List<ItemStack> drops = sheep.onSheared(caster.getActiveItemStack(), world, target.getPosition(), 1);
                    Random rand = new Random();

                    for (ItemStack stack : drops) {
                        EntityItem ent = target.entityDropItem(stack, 1.0F);
                        if (ent != null) {
                            ent.motionY += rand.nextFloat() * 0.05F;
                            ent.motionX += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                            ent.motionZ += (rand.nextFloat() - rand.nextFloat()) * 0.1F;
                        }
                    }
                }
            }**/
            //EntityUtils.applyStandardKnockback(caster, (EntityLivingBase) target, 0.4F);
            //Selena.pushOne((EntityLivingBase) target, 0.4f);
            living.getEntityData().setUniqueId("friend", caster.getUniqueID());
            //NBTExtras.storeTagSafely(living.getEntityData(), "friend", NBTUtil.createUUIDTag(caster.getUniqueID()));
            boolean friends = false;
            if (!living.tasks.taskEntries.isEmpty()){
                living.tasks.taskEntries.removeIf(e -> (e.action instanceof EntityAINearestAttackableTarget));
                living.tasks.addTask(3, new EntityAIFollowFriend(living, 1.5, 2, 8));
                friends = true;
            }

            if (!living.targetTasks.taskEntries.isEmpty()) {
                living.targetTasks.taskEntries.removeIf(e -> (e.action instanceof EntityAINearestAttackableTarget));
                living.targetTasks.addTask(1, new EntityAIFriendHurtByTarget(living));
                living.targetTasks.addTask(2, new EntityAIFriendHurtTarget(living));
                friends = true;
            }

            return friends;
        }
        return false;
    }

    @Override
    protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, @Nullable EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    protected boolean onMiss(World world, @Nullable EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers) {
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
