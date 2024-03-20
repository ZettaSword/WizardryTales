package astramusfate.wizardry_tales.entity.projectile;

import astramusfate.wizardry_tales.api.Tenebria;
import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.entity.construct.sigils.chanting.EntityCustomSigil;
import astramusfate.wizardry_tales.events.SpellCreation;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.projectile.EntityMagicProjectile;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.*;
import electroblob.wizardry.util.MagicDamage.DamageType;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * This class was copied from EntityArrow in the 1.7.10 update as part of the overhaul and major cleanup of the code for
 * the projectiles. It provides a unifying superclass for all <b>directed</b> projectiles (i.e. not spherical stuff like
 * snowballs), namely magic missile, ice shard, force arrow, lightning arrow and dart. All spherical projectiles should
 * extend {@link EntityMagicProjectile}.
 * <p></p>
 * This class handles saving of the damage multiplier and all shared logic. Methods are provided which are triggered at
 * useful points during the entity update cycle as well as a few getters for various properties. Override any of these
 * to change the behaviour (no need to call super for any of them).
 * 
 * @since Wizardry 1.0
 * @author Electroblob
 */
public abstract class EntityCustomArrow extends Entity implements IProjectile, IEntityAdditionalSpawnData {

	public static final double LAUNCH_Y_OFFSET = 0.1;
	public static final int SEEKING_TIME = 15;

	private int blockX = -1;
	private int blockY = -1;
	private int blockZ = -1;
	/** The block the arrow is stuck in */
	private IBlockState stuckInBlock;
	/** The metadata of the block the arrow is stuck in */
	private int inData;
	private boolean inGround;
	/** Seems to be some sort of timer for animating an arrow. */
	public int arrowShake;
	/** The owner of this arrow. */
	private WeakReference<EntityLivingBase> caster;
	/**
	 * The UUID of the caster. Note that this is only for loading purposes; during normal updates the actual entity
	 * instance is stored (so that getEntityByUUID is not called constantly), so this will not always be synced (this is
	 * why it is private).
	 */
	private UUID casterUUID;
	int ticksInGround;
	int ticksInAir;

	int lifetime;
	/** The amount of knockback an arrow applies when it hits a mob. */
	private int knockbackStrength;
	/** The damage multiplier for the projectile. */
	public float damageMultiplier = 1.0f;

	/** Element of Arrow */
	public Element element = Element.MAGIC;

	public List<String> words = Lists.newArrayList();
	public int amount = -1;

	/** Creates a new projectile in the given world. */
	public EntityCustomArrow(World world){
		super(world);
		this.setSize(0.5F, 0.5F);
	}
	
	// Initialiser methods
	
	/** Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
	 * aims it in the direction they are looking with the given speed. */
	public void aim(EntityLivingBase caster, float speed){
		
		this.setCaster(caster);
		
		this.setLocationAndAngles(caster.posX, caster.posY + (double)caster.getEyeHeight() - LAUNCH_Y_OFFSET,
				caster.posZ, caster.rotationYaw, caster.rotationPitch);
		
		this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= 0.10000000149011612D;
		this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		
		this.setPosition(this.posX, this.posY, this.posZ);
		
		// yOffset was set to 0 here, but that has been replaced by getYOffset(), which returns 0 in Entity anyway.
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI)
				* MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		
		this.shoot(this.motionX, this.motionY, this.motionZ, speed * 1.5F, 1.0F);
	}

	/** Sets the shooter of the projectile to the given caster, positions the projectile at the given caster's eyes and
	 * aims it at the given target with the given speed. The trajectory will be altered slightly by a random amount
	 * determined by the aimingError parameter. For reference, skeletons set this to 10 on easy, 6 on normal and 2 on hard
	 * difficulty. */
	public void aim(EntityLivingBase caster, Entity target, float speed, float aimingError){
		
		this.setCaster(caster);

		this.posY = caster.posY + (double)caster.getEyeHeight() - LAUNCH_Y_OFFSET;
		double dx = target.posX - caster.posX;
		double dy = this.doGravity() ? target.posY + (double)(target.height / 3.0f) - this.posY
				: target.posY + (double)(target.height / 2.0f) - this.posY;
		double dz = target.posZ - caster.posZ;
		double horizontalDistance = (double)MathHelper.sqrt(dx * dx + dz * dz);

		if(horizontalDistance >= 1.0E-7D){
			float yaw = (float)(Math.atan2(dz, dx) * 180.0d / Math.PI) - 90.0f;
			float pitch = (float)(-(Math.atan2(dy, horizontalDistance) * 180.0d / Math.PI));
			double dxNormalised = dx / horizontalDistance;
			double dzNormalised = dz / horizontalDistance;
			this.setLocationAndAngles(caster.posX + dxNormalised, this.posY, caster.posZ + dzNormalised, yaw, pitch);
			// yOffset was set to 0 here, but that has been replaced by getYOffset(), which returns 0 in Entity anyway.

			// Depends on the horizontal distance between the two entities and accounts for bullet drop,
			// but of course if gravity is ignored this should be 0 since there is no bullet drop.
			float bulletDropCompensation = this.doGravity() ? (float)horizontalDistance * 0.2f : 0;
			this.shoot(dx, dy + (double)bulletDropCompensation, dz, speed, aimingError);
		}
	}
	
	// Property getters (to be overridden by subclasses)

	/** Returns the maximum flight time in ticks before this projectile disappears, or -1 if it can continue
	 * indefinitely until it hits something. This should be constant. */
	public int getLifetime(){return this.lifetime;}

	public void setLifetime(int lifetime){this.lifetime = lifetime;}

	public float getDamageMultiplier(){return this.damageMultiplier;}
	public void setDamageMultiplier(float damageMultiplier){this.damageMultiplier = damageMultiplier;}

	/** Override this to specify the damage type dealt. Defaults to {@link DamageType#MAGIC}. */
	public DamageType getDamageType(){
		return Sage.getTypeByElement(element);
	}

	public Element getElement(){return element;}

	public void setElement(Element element){this.element = element;}

	/** Override this to disable gravity. Returns true by default. */
	public boolean doGravity(){
		return true;
	}

	/**
	 * Override this to disable deceleration (generally speaking, this isn't noticeable unless gravity is turned off).
	 * Returns true by default.
	 */
	public boolean doDeceleration(){
		return true;
	}

	/**
	 * Override this to allow the projectile to pass through mobs intact (the onEntityHit method will still be called
	 * and damage will still be applied). Returns false by default.
	 */
	public boolean doOverpenetration(){
		return false;
	}

	/**
	 * Returns the seeking strength of this projectile, or the maximum distance from a target the projectile can be
	 * heading for that will make it curve towards that target. By default, this is 2 if the caster is wearing a ring
	 * of attraction, otherwise it is 0.
	 */
	public float getSeekingStrength(){
		return getCaster() instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer)getCaster(),
				WizardryItems.ring_seeking) ? 2 : 0;
	}

	// Setters and getters

	/** Sets the amount of knockback the projectile applies when it hits a mob. */
	public void setKnockbackStrength(int knockback){
		this.knockbackStrength = knockback;
	}
	
	/**
	 * Returns the EntityLivingBase that created this construct, or null if it no longer exists. Cases where the entity
	 * may no longer exist are: entity died or was deleted, mob despawned, player logged out, entity teleported to
	 * another dimension, or this construct simply had no caster in the first place.
	 */
	public EntityLivingBase getCaster(){
		return caster == null ? null : caster.get();
	}

	public void setCaster(EntityLivingBase entity){
		caster = new WeakReference<>(entity);
	}
	
	// Methods triggered during the update cycle

	/** Called each tick when the projectile is in a block. Defaults to setDead(), but can be overridden to change the
	 * behaviour. */
	protected void tickInGround(){
		this.setDead();
	}

	/** Called each tick when the projectile is in the air. Override to add particles and such like. */
	public void tickInAir(){

		if(this.world.isRemote){

			if(Wizardry.tisTheSeason){

				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(0.8f, 0.15f, 0.15f)
						.time(20 + rand.nextInt(10)).spawn(world);

				ParticleBuilder.create(ParticleBuilder.Type.SNOW).pos(posX, posY, posZ).spawn(world);

				if(this.ticksExisted > 1){ // Don't spawn particles behind where it started!
					double x = posX - motionX / 2;
					double y = posY - motionY / 2;
					double z = posZ - motionZ / 2;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, x, y, z, 0.03, true).clr(0.15f, 0.7f, 0.15f)
							.time(20 + rand.nextInt(10)).spawn(world);
				}

			}else{

				ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, posX, posY, posZ, 0.03, true).clr(
								ArcaneColor.byElement(element)).fade(0.7f, 0, 1)
						.time(20 + rand.nextInt(10)).spawn(world);

				if(this.ticksExisted > 1){ // Don't spawn particles behind where it started!
					double x = posX - motionX / 2;
					double y = posY - motionY / 2;
					double z = posZ - motionZ / 2;
					ParticleBuilder.create(ParticleBuilder.Type.SPARKLE, rand, x, y, z, 0.03, true).clr(
									ArcaneColor.byElement(element)).fade(0.7f, 0, 1)
							.time(20 + rand.nextInt(10)).spawn(world);
				}
			}
		}
	}

	/** Called when the projectile hits an entity. Override to add potion effects and such like. */
	protected void onEntityHit(EntityLivingBase target) {
		if (words.isEmpty()){
			target.setFire(10);
		}
		SpellCreation.createSpell(words, this, target, !world.isRemote);

		if(this.world.isRemote) ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(posX, posY, posZ).clr(
				ArcaneColor.byElement(element)).spawn(world);
	}

	/** Called when the projectile hits a block. Override to add sound effects and such like. 
	 * @param hit A vector representing the exact coordinates of the hit; use this to centre particle effects, for
	 * example. */
	public void onBlockHit(RayTraceResult hit){
		String shape = "me";
		boolean attackAlly = false;
		boolean canAlly = false;

		String previous = "";
		for (int i = 0; i < words.size(); i++) {
			String next = i + 1 < words.size() ? words.get(i+1) : "";
			String next2 = i + 2 < words.size() ? words.get(i+2) : "";
			String next3 = i + 3 < words.size() ? words.get(i+3) : "";
			String word = words.get(i);

			// No numbers
			if (SpellCreation.findIn(word, Lexicon.par_shape)) shape = SpellCreation.findShape(next, next2, next3);

			if (SpellCreation.findIn(next, "ally allies allied")){
				if (SpellCreation.findIn(word, "allow")) canAlly=true;
				if (SpellCreation.findIn(word, "deny")) canAlly=false;
				if (SpellCreation.findIn(word, "attack")) attackAlly = !SpellCreation.findIn(previous, "not");
			}

			previous = word;
		}

		if (SpellCreation.findIn(shape, Lexicon.shape_sigil) && (hit.sideHit == EnumFacing.UP)) {
			words.remove(Lexicon.par_shape);
			words.remove(Lexicon.shape_sigil);
			EntityCustomSigil entity = new EntityCustomSigil(world, words);
			entity.setLocation(element.func_176610_l());
			if (!attackAlly) entity.setCaster(this.getCaster());
			entity.setLifetime(Solver.asTicks(lifetime));
			if (canAlly) entity.onlyAllies();
			BlockPos spellBlock = new BlockPos(hit.hitVec.x, hit.hitVec.y, hit.hitVec.z);
			entity.setPosition(spellBlock.getX() + 0.5, spellBlock.getY(), spellBlock.getZ() + 0.5);
			entity.setSizeMultiplier((float) 2);
			if(world.getEntitiesWithinAABB(entity.getClass(), entity.getEntityBoundingBox()).isEmpty())
				Tenebria.create(world, entity);
		}

		if(this.world.isRemote){
			// Gets a position slightly away from the block hit so the particle doesn't get cut in half by the block face
			Vec3d vec = hit.hitVec.add(new Vec3d(hit.sideHit.getDirectionVec()).scale(0.15));
			ParticleBuilder.create(ParticleBuilder.Type.FLASH).pos(vec).clr(ArcaneColor.byElement(element)
			).fade(0.85f, 0.5f, 0.8f).spawn(world);
		}
	}

	@Override
	public void onUpdate(){

		super.onUpdate();

		// Projectile disappears after its lifetime (if it has one) has elapsed
		if(getLifetime() >=0 && this.ticksExisted > getLifetime()){
			this.setDead();
		}

		if(this.getCaster() == null && this.casterUUID != null){
			Entity entity = EntityUtils.getEntityByUUID(world, casterUUID);
			if(entity instanceof EntityLivingBase){
				this.caster = new WeakReference<>((EntityLivingBase)entity);
			}
		}

		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F){
			float f = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D
					/ Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f) * 180.0D
					/ Math.PI);
		}

		BlockPos blockpos = new BlockPos(this.blockX, this.blockY, this.blockZ);
		IBlockState iblockstate = this.world.getBlockState(blockpos);

		if(iblockstate.getMaterial() != Material.AIR){
			AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

			if(axisalignedbb != Block.NULL_AABB
					&& Objects.requireNonNull(axisalignedbb).offset(blockpos).contains(new Vec3d(this.posX, this.posY, this.posZ))){
				this.inGround = true;
			}
		}

		if(this.arrowShake > 0){
			--this.arrowShake;
		}

		// When the arrow is in the ground
		if(this.inGround){
			++this.ticksInGround;
			this.tickInGround();
		}
		// When the arrow is in the air
		else{

			this.tickInAir();

			this.ticksInGround = 0;
			++this.ticksInAir;
			
			// Does a ray trace to determine whether the projectile will hit a block in the next tick
			
			Vec3d vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
			Vec3d vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			RayTraceResult raytraceresult = this.world.rayTraceBlocks(vec3d1, vec3d, false, true, false);
			vec3d1 = new Vec3d(this.posX, this.posY, this.posZ);
			vec3d = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);

			if(raytraceresult != null){
				vec3d = new Vec3d(raytraceresult.hitVec.x, raytraceresult.hitVec.y,
						raytraceresult.hitVec.z);
			}
			
			// Uses bounding boxes to determine whether the projectile will hit an entity in the next tick, and if so
			// overwrites the block hit with an entity

			Entity entity = null;
			List<?> list = this.world.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox()
					.expand(this.motionX, this.motionY, this.motionZ).grow(1.0D, 1.0D, 1.0D));
			double d0 = 0.0D;
			int i;
			float f1;

			for(i = 0; i < list.size(); ++i){
				Entity entity1 = (Entity)list.get(i);

				if(entity1.canBeCollidedWith() && (entity1 != this.getCaster() || this.ticksInAir >= 5)){
					f1 = 0.3F;
					AxisAlignedBB axisalignedbb1 = entity1.getEntityBoundingBox().grow((double)f1, (double)f1,
							(double)f1);
					RayTraceResult RayTraceResult1 = axisalignedbb1.calculateIntercept(vec3d1, vec3d);

					if(RayTraceResult1 != null){
						double d1 = vec3d1.distanceTo(RayTraceResult1.hitVec);

						if(d1 < d0 || d0 == 0.0D){
							entity = entity1;
							d0 = d1;
						}
					}
				}
			}

			if(entity != null){
				raytraceresult = new RayTraceResult(entity);
			}

			// Players that are considered invulnerable to the caster allow the projectile to pass straight through
			// them.
			if(raytraceresult != null && raytraceresult.entityHit != null
					&& raytraceresult.entityHit instanceof EntityPlayer){
				EntityPlayer entityplayer = (EntityPlayer)raytraceresult.entityHit;

				if(entityplayer.capabilities.disableDamage || this.getCaster() instanceof EntityPlayer
						&& !((EntityPlayer)this.getCaster()).canAttackPlayer(entityplayer)){
					raytraceresult = null;
				}
			}

			// If the arrow hits something
			if(raytraceresult != null){
				// If the arrow hits an entity
				if(raytraceresult.entityHit != null){
					if(raytraceresult.entityHit instanceof EntityLivingBase){
						EntityLivingBase entityHit = (EntityLivingBase)raytraceresult.entityHit;

						this.onEntityHit(entityHit);

						if(this.knockbackStrength > 0){
							float f4 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

							if(f4 > 0.0F){
								raytraceresult.entityHit.addVelocity(
										this.motionX * (double)this.knockbackStrength * 0.6000000238418579D
												/ (double)f4,
										0.1D, this.motionZ * (double)this.knockbackStrength * 0.6000000238418579D
												/ (double)f4);
							}
						}

						// Thorns enchantment
						if(this.getCaster() != null){
							EnchantmentHelper.applyThornEnchantments(entityHit, this.getCaster());
							EnchantmentHelper.applyArthropodEnchantments(this.getCaster(), entityHit);
						}

						if(this.getCaster() != null && raytraceresult.entityHit != this.getCaster()
								&& raytraceresult.entityHit instanceof EntityPlayer
								&& this.getCaster() instanceof EntityPlayerMP){
							((EntityPlayerMP)this.getCaster()).connection
									.sendPacket(new SPacketChangeGameState(6, 0.0F));
						}
					}

					if(!(raytraceresult.entityHit instanceof EntityEnderman) && !this.doOverpenetration()){
						this.setDead();
					}
				}
				// If the arrow hits a block
				else{
					this.blockX = raytraceresult.getBlockPos().getX();
					this.blockY = raytraceresult.getBlockPos().getY();
					this.blockZ = raytraceresult.getBlockPos().getZ();
					this.stuckInBlock = this.world.getBlockState(raytraceresult.getBlockPos());
					this.motionX = (double)((float)(raytraceresult.hitVec.x - this.posX));
					this.motionY = (double)((float)(raytraceresult.hitVec.y - this.posY));
					this.motionZ = (double)((float)(raytraceresult.hitVec.z - this.posZ));
					// f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ *
					// this.motionZ);
					// this.posX -= this.motionX / (double)f2 * 0.05000000074505806D;
					// this.posY -= this.motionY / (double)f2 * 0.05000000074505806D;
					// this.posZ -= this.motionZ / (double)f2 * 0.05000000074505806D;
					// this.playSound("random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;

					this.onBlockHit(raytraceresult);

					if(this.stuckInBlock.getMaterial() != Material.AIR){
						this.stuckInBlock.getBlock().onEntityCollidedWithBlock(this.world, raytraceresult.getBlockPos(),
								this.stuckInBlock, this);
					}
				}
			}

			// Seeking
			if(getSeekingStrength() > 0){

				Vec3d velocity = new Vec3d(motionX, motionY, motionZ);

				RayTraceResult hit = RayTracer.rayTrace(world, this.getPositionVector(),
						this.getPositionVector().add(velocity.scale(SEEKING_TIME)), getSeekingStrength(), false,
						true, false, EntityLivingBase.class, RayTracer.ignoreEntityFilter(null));

				if(hit != null && hit.entityHit != null){

					if(AllyDesignationSystem.isValidTarget(getCaster(), hit.entityHit)){

						Vec3d direction = new Vec3d(hit.entityHit.posX, hit.entityHit.posY + hit.entityHit.height/2,
								hit.entityHit.posZ).subtract(this.getPositionVector()).normalize().scale(velocity.lengthVector());

						motionX = motionX + 2 * (direction.x - motionX) / SEEKING_TIME;
						motionY = motionY + 2 * (direction.y - motionY) / SEEKING_TIME;
						motionZ = motionZ + 2 * (direction.z - motionZ) / SEEKING_TIME;
					}
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			// f2 = MathHelper.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

			// for (this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f2) * 180.0D / Math.PI);
			// this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F)
			// {
			// ;
			// }

			while(this.rotationPitch - this.prevRotationPitch >= 180.0F){
				this.prevRotationPitch += 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw < -180.0F){
				this.prevRotationYaw -= 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw >= 180.0F){
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

			float f3 = 0.99F;

			if(this.isInWater()){
				for(int l = 0; l < 4; ++l){
					float f4 = 0.25F;
					this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)f4,
							this.posY - this.motionY * (double)f4, this.posZ - this.motionZ * (double)f4, this.motionX,
							this.motionY, this.motionZ);
				}

				f3 = 0.8F;
			}

			if(this.isWet()){
				this.extinguish();
			}

			if(this.doDeceleration()){
				this.motionX *= (double)f3;
				this.motionY *= (double)f3;
				this.motionZ *= (double)f3;
			}

			if(this.doGravity()) this.motionY -= 0.05;

			this.setPosition(this.posX, this.posY, this.posZ);
			this.doBlockCollisions();
		}
	}

	@Override
	public void shoot(double x, double y, double z, float speed, float randomness){
		float f2 = MathHelper.sqrt(x * x + y * y + z * z);
		x /= (double)f2;
		y /= (double)f2;
		z /= (double)f2;
		x += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)randomness;
		y += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)randomness;
		z += this.rand.nextGaussian() * (double)(this.rand.nextBoolean() ? -1 : 1) * 0.007499999832361937D * (double)randomness;
		x *= (double)speed;
		y *= (double)speed;
		z *= (double)speed;
		this.motionX = x;
		this.motionY = y;
		this.motionZ = z;
		float f3 = MathHelper.sqrt(x * x + z * z);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(x, z) * 180.0D / Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(y, (double)f3) * 180.0D / Math.PI);
		this.ticksInGround = 0;
	}

	// There was an override for setPositionAndRotationDirect here, but it was exactly the same as the superclass
	// method (in Entity), so it was removed since it was redundant.

	/** Sets the velocity to the args. Args: x, y, z. THIS IS CLIENT SIDE ONLY! DO NOT USE IN COMMON OR SERVER CODE! */
	@Override
	@SideOnly(Side.CLIENT)
	public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_){
		this.motionX = p_70016_1_;
		this.motionY = p_70016_3_;
		this.motionZ = p_70016_5_;

		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F){
			float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(p_70016_3_, (double)f) * 180.0D / Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}
	}
	
	// Data reading and writing

	@Override
	public void writeEntityToNBT(NBTTagCompound tag){
		tag.setShort("xTile", (short)this.blockX);
		tag.setShort("yTile", (short)this.blockY);
		tag.setShort("zTile", (short)this.blockZ);
		tag.setShort("life", (short)this.ticksInGround);
		if(this.stuckInBlock != null){
			ResourceLocation resourcelocation = Block.REGISTRY.getNameForObject(this.stuckInBlock.getBlock());
			tag.setString("inTile", resourcelocation == null ? "" : resourcelocation.toString());
		}
		tag.setByte("inData", (byte)this.inData);
		tag.setByte("shake", (byte)this.arrowShake);
		tag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
		tag.setFloat("damageMultiplier", this.damageMultiplier);
		tag.setInteger("element", this.element.ordinal());
		tag.setInteger("lifetime", this.lifetime);
		if(this.getCaster() != null){
			tag.setUniqueId("casterUUID", this.getCaster().getUniqueID());
		}
		tag.setInteger("amount", amount);
		NBTExtras.storeTagSafely(tag, "words", NBTExtras.listToNBT(words, NBTTagString::new));
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound tag){
		this.blockX = tag.getShort("xTile");
		this.blockY = tag.getShort("yTile");
		this.blockZ = tag.getShort("zTile");
		this.ticksInGround = tag.getShort("life");
		// Commented out for now because there's some funny stuff going on with blockstates and id.
		// this.stuckInBlock = Block.getBlockById(tag.getByte("inTile") & 255);
		this.inData = tag.getByte("inData") & 255;
		this.arrowShake = tag.getByte("shake") & 255;
		this.inGround = tag.getByte("inGround") == 1;
		this.damageMultiplier = tag.getFloat("damageMultiplier");
		this.element = Element.values()[tag.getInteger("element")];
		this.lifetime = tag.getInteger("lifetime");
		casterUUID = tag.getUniqueId("casterUUID");

		this.amount = tag.getInteger("amount");
		this.words = (List<String>)NBTExtras.NBTToList(tag.getTagList("words", Constants.NBT.TAG_STRING),
				NBTTagString::getString);
	}
	
	@Override
	public void writeSpawnData(ByteBuf buffer){
		buffer.writeInt(this.element.ordinal());
		buffer.writeInt(this.lifetime);
		if(this.getCaster() != null) buffer.writeInt(this.getCaster().getEntityId());
		buffer.writeInt(amount);
		for(String word : words){
			ByteBufUtils.writeUTF8String(buffer, word);
		}
	}

	@Override
	public void readSpawnData(ByteBuf buffer){
		this.element = Element.values()[buffer.readInt()];
		this.lifetime = buffer.readInt();
		if(buffer.isReadable()) this.caster = new WeakReference<>(
				(EntityLivingBase)this.world.getEntityByID(buffer.readInt()));

		amount = buffer.readInt();
		if(amount > -1) {
			for (int i = 0; i < amount; i++) {
				words.add(i, ByteBufUtils.readUTF8String(buffer));
			}
		}
	}

	// Miscellaneous overrides
	
	@Override
	protected boolean canTriggerWalking(){
		return false;
	}
	
	@Override
	public boolean canBeAttackedWithItem(){
		return false;
	}

	@SideOnly(Side.CLIENT)
	public float getShadowSize(){
		return 0.0F;
	}
	
	@Nonnull
	@Override
	public SoundCategory getSoundCategory(){
		return WizardrySounds.SPELLS;
	}

	@Override
	protected void entityInit(){}
}