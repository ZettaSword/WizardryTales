package astramusfate.wizardry_tales.entity.living;

import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.ai.EntityAIAttackSpellSmart;
import astramusfate.wizardry_tales.spells.TalesSpells;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityVampire extends EntityMob implements ISpellCaster, IEntityAdditionalSpawnData {
    protected Predicate<Entity> targetSelector;
    private Spell continuousSpell;
    private int spellCounter;
    public int textureIndex = 0;

    public EntityVampire(World world) {
        super(world);
        this.tasks.addTask(0, new EntityAIAttackSpellSmart<>(this, 0.5D,
                5,14.0f, 30, 50));
    }

    @Override
    public boolean attackEntityAsMob(@Nonnull Entity entity) {
        float f = (float)this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        int i = 0;
        if (entity instanceof EntityLivingBase) {
            f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase)entity).getCreatureAttribute());
            i += EnchantmentHelper.getKnockbackModifier(this);
        }

        boolean flag = entity.attackEntityFrom(DamageSource.causeMobDamage(this), f);
        if (flag) {
            Wizard.playHeartEffect(world, this.getPositionVector(), true);
            this.heal(f);
            if (i > 0 && entity instanceof EntityLivingBase) {
                this.motionX *= 0.6;
                this.motionZ *= 0.6;
            }

            int j = EnchantmentHelper.getFireAspectModifier(this);
            if (j > 0) {
                entity.setFire(j * 4);
            }

            if (entity instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer)entity;
                ItemStack itemstack = this.getHeldItemMainhand();
                ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;
                if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
                    float f1 = 0.25F + (float)EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;
                    if (this.rand.nextFloat() < f1) {
                        entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
                        this.world.setEntityState(entityplayer, (byte)30);
                    }
                }
            }

            this.applyEnchantments(this, entity);
        }

        return flag;
    }

    @Override
    protected void initEntityAI() {

        this.tasks.addTask(0, new EntityAISwimming(this));
        //this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, true));
        this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0, true));
        this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
        this.tasks.addTask(5, new EntityAIFleeSun(this, 1.5D));
        this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.6D));
        this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
        this.tasks.addTask(7, new EntityAIWander(this, 0.6D));

        this.targetSelector = entity -> {

            // If the target is valid and not invisible...
            if(entity != null && !entity.isInvisible()
                    && AllyDesignationSystem.isValidTarget(EntityVampire.this, entity)){

                // ... and is a player, a summoned creature, another (non-evil) wizard ...
                if(entity instanceof EntityPlayer
                        || (entity instanceof ISummonedCreature || entity instanceof EntityWizard
                        // ... or in the whitelist ...
                        || Arrays.asList(Wizardry.settings.summonedCreatureTargetsWhitelist)
                        .contains(EntityList.getKey(entity.getClass())))
                        // ... and isn't in the blacklist ...
                        && !Arrays.asList(Wizardry.settings.summonedCreatureTargetsBlacklist)
                        .contains(EntityList.getKey(entity.getClass()))){
                    // ... it can be attacked.
                    return true;
                }
            }

            return false;
        };

        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        this.targetTasks.addTask(0, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
                0, false, true, this.targetSelector));
    }

    @Override
    protected void applyEntityAttributes(){
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40);
    }

    @Nonnull
    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.UNDEAD;
    }

    @Nonnull
    @Override
    public List<Spell> getSpells() {
        // First she tries to lure her target.
        List<Spell> spells = Lists.newArrayList(TalesSpells.charm);
        if (getHealth() < getMaxHealth() * 0.8) {
            // Her base spells.
            spells.add(TalesSpells.necro_attack);
            spells.add(Spells.ice_spikes);
            spells.add(Spells.darkness_orb);
            spells.add(Spells.decay);
            if (getHealth() < getMaxHealth() * 0.5) {
                spells.add(TalesSpells.vile_blood);
                spells.add(Spells.summon_ice_giant);
                spells.add(Spells.summon_skeleton_legion);
                spells.add(Spells.summon_wither_skeleton);
                spells.add(Spells.wither_skull);
                if (getHealth() < getMaxHealth() * 0.25) {
                    spells.add(Spells.ice_age);
                }
            } else {
                spells.add(Spells.freeze);
            }
        }
        return spells;
    }


    @Nonnull
    @Override
    public SpellModifiers getModifiers() {
        SpellModifiers mods = ISpellCaster.super.getModifiers();
        if (getHealth() < getMaxHealth() * 0.5){
            mods.set(Sage.POTENCY, mods.get(Sage.POTENCY) * 1.5F, false);
            mods.set(Sage.BLAST, mods.get(Sage.BLAST) * 2F, false);
            mods.set(Sage.RANGE, mods.get(Sage.RANGE) * 2F, false);
        }
        return mods;
    }

    @Override
    public void setContinuousSpell(Spell spell) {
        this.continuousSpell = spell;
    }

    @Override
    public Spell getContinuousSpell() {
        return this.continuousSpell;
    }

    @Override
    public void setSpellCounter(int count) {
        spellCounter = count;
    }

    @Override
    public int getSpellCounter() {
        return spellCounter;
    }

    @Override
    public boolean getCanSpawnHere(){
        if (super.getCanSpawnHere()){
            BlockPos blockpos = new BlockPos(MathHelper.floor(this.posX), 0, MathHelper.floor(this.posZ));
            Biome biome = this.world.getBiome(blockpos);
            return Arrays.stream(Tales.toResourceLocations(Tales.entities.vampireBiomeWhitelist))
                    .anyMatch(e -> biome.getRegistryName() == e);
        }
        return false;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt) {
        super.writeEntityToNBT(nbt);
        nbt.setInteger("skin", this.textureIndex);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt) {
        super.readEntityFromNBT(nbt);
        this.textureIndex = nbt.getInteger("skin");
    }

    @Override
    public void writeSpawnData(ByteBuf data){
        data.writeInt(textureIndex);
    }

    @Override
    public void readSpawnData(ByteBuf data){
        textureIndex = data.readInt();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {

        livingdata = super.onInitialSpawn(difficulty, livingdata);

        textureIndex = this.rand.nextInt(6);
        // Umbrellas
        if (ForgeRegistries.ITEMS.containsKey(new ResourceLocation("vampiresneedumbrellas", "umbrellaIron"))){
            ItemStack stack = new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(
                    new ResourceLocation("vampiresneedumbrellas", "umbrellaIron"))));
            this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
        }

        return livingdata;
    }

    @Override
    public void onLivingUpdate() {
        if (this.world.isDaytime() && !this.world.isRemote && this.shouldBurnInDay()) {
            float f = this.getBrightness();
            if (f > 0.5F && this.rand.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.canSeeSky(new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ))) {
                boolean flag = true;
                ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
                if (!itemstack.isEmpty()) {
                    if (itemstack.isItemStackDamageable()) {
                        itemstack.setItemDamage(itemstack.getItemDamage() + this.rand.nextInt(2));
                        if (itemstack.getItemDamage() >= itemstack.getMaxDamage()) {
                            this.renderBrokenItemStack(itemstack);
                            this.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    this.setFire(8);
                }
            }
        }

        super.onLivingUpdate();
    }

    protected boolean shouldBurnInDay() {
        return true;
    }
}
