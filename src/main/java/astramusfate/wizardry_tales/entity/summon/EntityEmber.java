package astramusfate.wizardry_tales.entity.summon;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.entity.ai.EntityAICasterHurtByTarget;
import astramusfate.wizardry_tales.entity.ai.EntityAICasterHurtTarget;
import astramusfate.wizardry_tales.spells.TalesSpells;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class EntityEmber extends EntitySummonedCreature {

	private static final DataParameter<Boolean> SPAWN_PARTICLES = EntityDataManager.createKey(EntityEmber.class, DataSerializers.BOOLEAN);


	/** Data parameter that tracks whether the remnant is currently attacking (charging). */
	private static final DataParameter<Boolean> ATTACKING = EntityDataManager.createKey(EntityEmber.class, DataSerializers.BOOLEAN);

	@Nullable
	private BlockPos boundOrigin;

	public EntityEmber(World world){
		super(world);
		this.setSize(0.8f, 0.8f);
		this.moveHelper = new AIMoveControl(this);
		this.experienceValue = 0;
	}

	// Field implementations
	private int lifetime = -1;
	private UUID casterUUID;

	// Setter + getter implementations
	@Override public int getLifetime(){ return lifetime; }
	@Override public void setLifetime(int lifetime){ this.lifetime = lifetime; }
	@Override public UUID getOwnerId(){ return casterUUID; }


	@Nullable
	@Override
	public Entity getOwner(){
		return getCaster(); // Delegate to getCaster
	}

	@Override public void setOwnerId(UUID uuid){ this.casterUUID = uuid; }

	//@Nullable
	//@Override
	//public UUID func_184753_b() {
	//	return casterUUID;
	//}

	@Override
	protected void entityInit(){
		super.entityInit();
		this.dataManager.register(ATTACKING, false);
		this.dataManager.register(SPAWN_PARTICLES, true);
	}

	@Override
	protected void initEntityAI(){
		super.initEntityAI();
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(4, new AIChargeAttack());
		//this.tasks.addTask(7, new EntityAIFollowCaster(this, this, 1.0F, 10.0f, 5.0f));
		//this.tasks.addTask(9, new EntityAIWatchClosest(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLivingBase.class, 8.0F));
		this.tasks.addTask(9, new AIMoveRandom());

		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
		this.targetTasks.addTask(1, new EntityAICasterHurtByTarget(this, this));
		this.targetTasks.addTask(2, new EntityAICasterHurtTarget(this, this));
		this.targetTasks.addTask(3, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class,
				0, true, false, this.getTargetSelector()));
	}

	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(16);
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(4);
	}

	// Recommended overrides

	@Override protected int getExperiencePoints(@Nonnull EntityPlayer player){ return 0; }
	@Override protected boolean canDropLoot(){ return false; }
	@Override protected Item getDropItem(){ return null; }
	@Override public boolean canPickUpLoot(){ return false; }

	@Nullable
	@Override
	public IEntityLivingData onInitialSpawn(@Nonnull DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata){
		this.setBoundOrigin(new BlockPos(this));
		return super.onInitialSpawn(difficulty, livingdata);
	}

	public Element getElement(){
		return Element.FIRE;
	}

	public boolean isAttacking(){
		return this.dataManager.get(ATTACKING);
	}

	public void setAttacking(boolean attacking){
		this.dataManager.set(ATTACKING, attacking);
	}

	@Nullable
	public BlockPos getBoundOrigin(){
		return this.boundOrigin;
	}

	public void setBoundOrigin(@Nullable BlockPos boundOriginIn){
		this.boundOrigin = boundOriginIn;
	}

	@Override
	public float getBlockPathWeight(@Nonnull BlockPos pos){
		// Goddamnit Minecraft, stop imposing obscure spawning restrictions
		return 1; // This won't affect pathfinding since remnants are flying mobs anyway
	}

	@Override
	protected float applyPotionDamageCalculations(@Nonnull DamageSource source, float damage){
		damage = super.applyPotionDamageCalculations(source, damage);
		if(source.isMagicDamage()) damage *= 0.25f; // Remnants are 75% resistant to magic damage
		if(source.isFireDamage() || source.damageType.equals(MagicDamage.DamageType.FIRE.name())) damage = 0f;
		return damage;
	}

	@Override
	public void onUpdate(){

		// Use the same trick as EntityVex to fly through stuff
		this.noClip = true;
		super.onUpdate();
		this.noClip = false;
		this.updateDelegate();

		this.setNoGravity(true);

		if(world.isRemote){

			Vec3d centre = this.getPositionVector().add(new Vec3d(0, height/2, 0));

			int[] colours = BlockReceptacle.PARTICLE_COLOURS.get(this.getElement());

			if(rand.nextInt(10) == 0){
				ParticleBuilder.create(ParticleBuilder.Type.FLASH).entity(this).pos(0, height/2, 0).scale(width).time(48).clr(colours[0]).spawn(world);
			}

			double r = width/3;

			double x = r * (rand.nextDouble() * 2 - 1);
			double y = r * (rand.nextDouble() * 2 - 1);
			double z = r * (rand.nextDouble() * 2 - 1);

			if(this.deathTime > 0){
				// Spew out particles on death
				for(int i = 0; i < 8; i++){
					ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE, rand, centre.x + x, centre.y + y, centre.z + z, 0.1, true)
							.scale(2).time(12).clr(colours[1]).fade(colours[2]).spawn(world);
				}
			}else{
				ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE).pos(centre.x + x, centre.y + y, centre.z + z)
						.vel(x * -0.03, 0.02, z * -0.03).time(24 + rand.nextInt(8)).clr(colours[1]).fade(colours[2]).spawn(world);
			}
		}

		if(Solver.doEvery(this.ticksExisted, 2) && this.getCaster() != null && !world.isRemote){
		    this.setBoundOrigin(this.getCaster().getPosition());
        }

	}

	// Implementations

	@Override
	public void setRevengeTarget(EntityLivingBase entity){
		if(this.shouldRevengeTarget(entity)) super.setRevengeTarget(entity);
	}

	@Override
	public void onSpawn(){
		if(this.dataManager.get(SPAWN_PARTICLES)) this.spawnParticleEffect();
	}

	@Override
	public void onDespawn(){
		this.spawnParticleEffect();
	}

	private void spawnParticleEffect(){
		if(this.world.isRemote){
			for(int i = 0; i < 15; i++){
				this.world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX + this.rand.nextFloat() - 0.5f,
						this.posY + this.rand.nextFloat() * 2, this.posZ + this.rand.nextFloat() - 0.5f, 0, 0, 0);
			}
		}
	}

	@Override
	public boolean hasParticleEffect(){
		return true;
	}

	@Override
	public boolean hasRangedAttack() {
		return false;
	}

	@Override
	public boolean hasAnimation(){
		return this.dataManager.get(SPAWN_PARTICLES) || this.ticksExisted > 20;
	}

	public void hideParticles(){
		this.dataManager.set(SPAWN_PARTICLES, false);
	}

	@Override
	protected boolean processInteract(@Nonnull EntityPlayer player, @Nonnull EnumHand hand){
		return this.interactDelegate(player, hand) || super.processInteract(player, hand);
	}


	@Override
	protected SoundEvent getAmbientSound(){
		return WizardrySounds.ENTITY_REMNANT_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound(){
		return WizardrySounds.ENTITY_REMNANT_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(@Nonnull DamageSource source){
		return WizardrySounds.ENTITY_REMNANT_HURT;
	}

	@Override protected ResourceLocation getLootTable(){ return null; }

	@Override
	public boolean attackEntityAsMob(@Nonnull Entity entityIn) {
		if(EntityUtils.isLiving(entityIn)) {
			if (!MagicDamage.isEntityImmune(MagicDamage.DamageType.FIRE, entityIn)) {
				int i = 0;
				i += EnchantmentHelper.getKnockbackModifier(this);

				boolean flag = entityIn.attackEntityFrom(MagicDamage.causeDirectMagicDamage(this.getCaster(), MagicDamage.DamageType.FIRE),
						TalesSpells.summon_ember.getProperty(Spell.DIRECT_DAMAGE).floatValue());

				if (flag) {
					entityIn.setFire(TalesSpells.summon_ember.getProperty(Spell.BURN_DURATION).intValue());
					this.setAttacking(false);

					if (i > 0) {
						((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
						this.motionX *= 0.6D;
						this.motionZ *= 0.6D;
					}

					int j = EnchantmentHelper.getFireAspectModifier(this);

					if (j > 0) {
						entityIn.setFire(j * 4);
					}

					if (entityIn instanceof EntityPlayer) {
						EntityPlayer entityplayer = (EntityPlayer) entityIn;
						ItemStack itemstack = this.getHeldItemMainhand();
						ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

						if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
							float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

							if (this.rand.nextFloat() < f1) {
								entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
								this.world.setEntityState(entityplayer, (byte) 30);
							}
						}
					}

					this.applyEnchantments(this, entityIn);
				}

				return flag;
			}
		}
		return false;
	}

	@Override
	public void readEntityFromNBT(@Nonnull NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		this.readNBTDelegate(nbt);
		if(nbt.hasKey("BoundOrigin")) boundOrigin = NBTUtil.getPosFromTag(nbt.getCompoundTag("BoundOrigin"));
	}

	@Override
	public void writeEntityToNBT(@Nonnull NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		this.writeNBTDelegate(nbt);
		if(boundOrigin != null) nbt.setTag("BoundOrigin", NBTUtil.createPosTag(boundOrigin));
	}

	// This vanilla method has nothing to do with the custom despawn() method.
	@Override protected boolean canDespawn(){
		return getCaster() == null && getOwnerId() == null;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName(){
		if(getCaster() != null){
			return new TextComponentTranslation(NAMEPLATE_TRANSLATION_KEY, getCaster().getName(),
					new TextComponentTranslation("entity." + this.getEntityString() + ".name"));
		}else{
			return super.getDisplayName();
		}
	}

	@Override
	public boolean hasCustomName(){
		// If this returns true, the renderer will show the nameplate when looking directly at the entity
		return Wizardry.settings.summonedCreatureNames && getCaster() != null;
	}

	// AI classes (copied from EntityVex)

	class AIChargeAttack extends EntityAIBase {

		public AIChargeAttack(){
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute(){
			if(EntityEmber.this.getAttackTarget() != null && !EntityEmber.this.getMoveHelper().isUpdating() && EntityEmber.this.rand.nextInt(7) == 0){
				return EntityEmber.this.getDistanceSq(EntityEmber.this.getAttackTarget()) > 4.0D;
			}else{
				return false;
			}
		}

		@Override
		public boolean shouldContinueExecuting(){
			return EntityEmber.this.getMoveHelper().isUpdating() && EntityEmber.this.isAttacking() && EntityEmber.this.getAttackTarget() != null && EntityEmber.this.getAttackTarget().isEntityAlive();
		}

		@Override
		public void startExecuting(){
			EntityLivingBase entitylivingbase = EntityEmber.this.getAttackTarget();
			if(entitylivingbase == null) return;
			Vec3d vec3d = entitylivingbase.getPositionEyes(1.0F);
			EntityEmber.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
			EntityEmber.this.setAttacking(true);
//			EntityRemnant.this.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
		}

		@Override
		public void resetTask(){
			EntityEmber.this.setAttacking(false);
		}

		@Override
		public void updateTask(){

			EntityLivingBase living = EntityEmber.this.getAttackTarget();

			if(living == null) return;

			if(EntityEmber.this.getEntityBoundingBox().intersects(living.getEntityBoundingBox())){
				EntityEmber.this.attackEntityAsMob(living);
			}else{
				double d0 = EntityEmber.this.getDistanceSq(living);

				if(d0 < 9.0D){
					Vec3d vec3d = living.getPositionEyes(1.0F);
					EntityEmber.this.moveHelper.setMoveTo(vec3d.x, vec3d.y, vec3d.z, 1.0D);
				}
			}
		}
	}

	class AIMoveControl extends EntityMoveHelper {

		public AIMoveControl(EntityEmber host){
			super(host);
		}

		@Override
		public void onUpdateMoveHelper(){

			if(this.action == Action.MOVE_TO){

				double d0 = this.posX - EntityEmber.this.posX;
				double d1 = this.posY - EntityEmber.this.posY;
				double d2 = this.posZ - EntityEmber.this.posZ;
				double d3 = d0 * d0 + d1 * d1 + d2 * d2;

				d3 = MathHelper.sqrt(d3);

				if(d3 < EntityEmber.this.getEntityBoundingBox().getAverageEdgeLength()){

					this.action = Action.WAIT;
					EntityEmber.this.motionX *= 0.5D;
					EntityEmber.this.motionY *= 0.5D;
					EntityEmber.this.motionZ *= 0.5D;

				}else{

					EntityEmber.this.motionX += d0 / d3 * 0.05D * this.speed;
					EntityEmber.this.motionY += d1 / d3 * 0.05D * this.speed;
					EntityEmber.this.motionZ += d2 / d3 * 0.05D * this.speed;

					if(EntityEmber.this.getAttackTarget() == null){
						EntityEmber.this.rotationYaw = -((float)MathHelper.atan2(EntityEmber.this.motionX, EntityEmber.this.motionZ)) * (180F / (float)Math.PI);
					}else{
						double d4 = EntityEmber.this.getAttackTarget().posX - EntityEmber.this.posX;
						double d5 = EntityEmber.this.getAttackTarget().posZ - EntityEmber.this.posZ;
						EntityEmber.this.rotationYaw = -((float)MathHelper.atan2(d4, d5)) * (180F / (float)Math.PI);
					}

					EntityEmber.this.renderYawOffset = EntityEmber.this.rotationYaw;
				}
			}
		}
	}

	class AIMoveRandom extends EntityAIBase {

		public AIMoveRandom(){
			this.setMutexBits(1);
		}

		@Override
		public boolean shouldExecute(){
			return !EntityEmber.this.getMoveHelper().isUpdating() && EntityEmber.this.rand.nextInt(7) == 0;
		}

		@Override
		public boolean shouldContinueExecuting(){
			return false;
		}

		@Override
		public void updateTask(){

			BlockPos blockpos = EntityEmber.this.getBoundOrigin();

			if(blockpos == null){
				blockpos = new BlockPos(EntityEmber.this);
			}

			for(int i = 0; i < 3; ++i){
				BlockPos blockpos1 = blockpos.add(EntityEmber.this.rand.nextInt(15) - 7, EntityEmber.this.rand.nextInt(11) - 5, EntityEmber.this.rand.nextInt(15) - 7);

				if(EntityEmber.this.world.isAirBlock(blockpos1)){
					EntityEmber.this.moveHelper.setMoveTo((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 0.25D);

					if(EntityEmber.this.getAttackTarget() == null){
						EntityEmber.this.getLookHelper().setLookPosition((double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.5D, (double)blockpos1.getZ() + 0.5D, 180.0F, 20.0F);
					}

					break;
				}
			}
		}
	}

}
