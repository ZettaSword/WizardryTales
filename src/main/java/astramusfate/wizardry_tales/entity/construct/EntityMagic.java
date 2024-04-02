package astramusfate.wizardry_tales.entity.construct;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.chanting.SpellParams;
import astramusfate.wizardry_tales.events.SpellCreation;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class EntityMagic extends Entity implements IEntityOwnable, IEntityAdditionalSpawnData {

    /** The UUID of the caster. As of Wizardry 4.3, this <b>is</b> synced, and rather than storing the caster
     * instance via a weak reference, it is fetched from the UUID each time it is needed in*/
    private UUID casterUUID;

    /** The time in ticks this magical construct lasts for; defaults to 600 (30 seconds). If this is -1 the construct
     * doesn't despawn. */
    public int lifetime = 600;

    /** The damage multiplier for this construct, determined by the wand with which it was cast. */
    public float damageMultiplier = 1.0f;

    /** Can affect only Allies? **/
    public boolean onlyAllies = false;

    /** Has Owner? **/
    public boolean hasOwner = true;
    public EntityMagic(World world){
        super(world);
        this.height = 1.0f;
        this.width = 1.0f;
        this.noClip = true;
    }

    public void onlyAllies(){
        onlyAllies =true;
    }

    public void setLifetime(int ticks){
        this.lifetime=ticks;
    }

    public int getLifetime(){
        return this.lifetime;
    }

    // Overrides the original to stop the entity moving when it intersects stuff. The default arrow does this to allow
    // it to stick in blocks.
    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport){
        this.setPosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    public void onUpdate(){
        super.onUpdate();

        if(this.ticksExisted > lifetime && lifetime > 0){
            this.despawn();
        }
    }

    @Nonnull
    @Override
    public EnumActionResult applyPlayerInteraction(@Nonnull EntityPlayer player, @Nonnull Vec3d vec, @Nonnull EnumHand hand){

        // Permanent constructs can now be dispelled by sneak-right-clicking
        if(lifetime < 0 && getCaster() == player && player.isSneaking() && player.getHeldItem(hand).getItem() instanceof ISpellCastingItem){
            this.despawn();
            return EnumActionResult.SUCCESS;
        }

        return super.applyPlayerInteraction(player, vec, hand);
    }

    /**
     * Defaults to just setDead() in EntityMagicConstruct, but is provided to allow subclasses to override this e.g.
     * bubble uses it to dismount the entity inside it and play the 'pop' sound before calling super(). You should
     * always call super() when overriding this method, in case it changes. There is no need, therefore, to call
     * setDead() when overriding.
     */
    public void despawn(){
        this.setDead();
    }

    @Override
    protected void entityInit(){
        // We could leave this unimplemented, but since the majority of subclasses don't use it, let's make it optional
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound nbttagcompound){
        if(casterUUID != null){
            nbttagcompound.setUniqueId("casterUUID", casterUUID);
        }
        nbttagcompound.setInteger("lifetime", lifetime);
        nbttagcompound.setFloat("damageMultiplier", damageMultiplier);
        nbttagcompound.setBoolean("canAllies", onlyAllies);
        nbttagcompound.setBoolean("hasOwner", hasOwner);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound){
        if(nbttagcompound.hasUniqueId("casterUUID")) casterUUID = nbttagcompound.getUniqueId("casterUUID");
        lifetime = nbttagcompound.getInteger("lifetime");
        damageMultiplier = nbttagcompound.getFloat("damageMultiplier");
        onlyAllies = nbttagcompound.getBoolean("canAllies");
        hasOwner = nbttagcompound.getBoolean("hasOwner");
    }
    @Override
    public void writeSpawnData(ByteBuf data){
        data.writeInt(lifetime);
        data.writeInt(getCaster() == null ? -1 : getCaster().getEntityId());
        data.writeBoolean(onlyAllies);
        data.writeBoolean(hasOwner);
    }

    @Override
    public void readSpawnData(ByteBuf data){

        lifetime = data.readInt();

        int id = data.readInt();

        if(id == -1){
            setCaster(null);
        }else{
            Entity entity = world.getEntityByID(id);
            if(entity instanceof EntityLivingBase){
                setCaster((EntityLivingBase)entity);
            }else{
                Wizardry.logger.warn("Construct caster with ID in spawn data not found");
            }
        }

        onlyAllies = data.readBoolean();
        hasOwner = data.readBoolean();
    }

    @Nullable
    @Override
    public UUID getOwnerId(){
        return casterUUID;
    }

    @Nullable
    @Override
    public Entity getOwner(){
        return getCaster(); // Delegate to getCaster
    }

    /**
     * Returns the EntityLivingBase that created this construct, or null if it no longer exists. Cases where the entity
     * may no longer exist are: entity died or was deleted, mob despawned, player logged out, entity teleported to
     * another dimension, or this construct simply had no caster in the first place.
     */
    @Nullable
    public EntityLivingBase getCaster(){ // Kept despite the above method because it returns an EntityLivingBase

        Entity entity = EntityUtils.getEntityByUUID(world, getOwnerId());

        if(entity != null && !(entity instanceof EntityLivingBase)){ // Should never happen
            Wizardry.logger.warn("{} has a non-living owner!", this);
            entity = null;
        }

        return (EntityLivingBase)entity;
    }

    public void setCaster(@Nullable EntityLivingBase caster){
        this.casterUUID = caster == null ? null : caster.getUniqueID();
    }

    /**
     * Shorthand for {@link AllyDesignationSystem#isValidTarget(Entity, Entity)}, with the owner of this construct as the
     * attacker. Also allows subclasses to override it if they wish to do so.
     */
    public boolean isValidTarget(Entity target){
        return AllyDesignationSystem.isValidTarget(this.getCaster(), target);
    }

    @Nonnull
    @Override
    public SoundCategory getSoundCategory(){
        return WizardrySounds.SPELLS;
    }

    @Override
    public boolean canRenderOnFire(){
        return false;
    }

    @Override
    public boolean isPushedByWater(){
        return false;
    }

    /** Handles actions of spell:
     * <br>1) Handle targets externally, if you need.<br/>
     * <br>2) Use this<br/>**/
    public void createMagic(List<String> spell, Entity focal, @Nullable Entity target, boolean isServer, @Nullable String original) {
        World world = focal.world;

        SpellParams mods = new SpellParams();
        // Setting up parameters
        mods.world = world;
        mods.target = target;
        mods.focal = focal;
        mods.isServer = isServer;
        mods.original = original;

        EntityLivingBase caster = getCaster();

        mods.set = Lists.newArrayList();
        mods.set.addAll(spell);
        mods.stopCast = false;

        String previous = "";
        try {
            for (int i = 0; i < spell.size(); i++) {
                String next = SpellCreation.getWord(spell, i, 1), next2 = SpellCreation.getWord(spell, i, 2),
                        next3 = SpellCreation.getWord(spell, i, 3), next4 = SpellCreation.getWord(spell, i, 4), word = spell.get(i);

                HashMap<String, String> keys = new HashMap<>();
                keys.put("word", word);
                keys.put("next", next);
                keys.put("next2", next2);
                keys.put("next3", next3);
                keys.put("next4", next4);
                keys.put("previous", previous);
                mods.keys=keys;

                // ? Raycast Entity && Raycast Block
                SpellCreation.raycast(world, caster, mods.shape.val(), mods);

                if (mods.stopCast) break;

                // ? Position changes, etc.
                SpellCreation.calcMods(caster, mods);

                if (mods.pos == null){
                    if (!mods.isRay)
                        mods.pos = focal.getPositionVector();
                    else return;
                }
                mods.pos.add(mods.change);

                //if (mods.pos == null) mods.pos = focal.getPositionVector();

                // ? If we apply cool shapes like Sigil, we could return!
                SpellCreation.shaping(caster, mods);
                if (mods.stopCast) break;

                // Spell-casting
                SpellCreation.applyActions(caster, mods);

                // We remove this for a reason, to make Shapes be better.
                try {
                    if (next.isEmpty()) mods.set.remove(word);
                    mods.set.remove(previous);
                }catch (Exception ignore){}
                previous = word;
            }
        }catch (Exception exception){
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");
            exception.printStackTrace();
            WizardryTales.log.error("---- [Wizardry Tales] - problem occurred when Chanting! ----");

            if (caster instanceof EntityPlayer)
                Aterna.messageBar((EntityPlayer) caster, "Spell is broken!");

            if (Tales.chanting.debug)
                throw new RuntimeException(exception);
        }

        SpellCreation.attachCastingVisual(world, caster, mods);
    }
}
