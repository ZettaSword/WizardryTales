package astramusfate.wizardry_tales.entity.construct.rituals;

import astramusfate.wizardry_tales.entity.construct.EntityMagicScaled;
import astramusfate.wizardry_tales.entity.construct.EntityRitual;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.ICustomHitbox;
import electroblob.wizardry.entity.construct.EntityScaledConstruct;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class EntityHouseProtectionAura extends EntityRitual implements ICustomHitbox {

	private static final float BOUNCINESS = 0.2f;

	public EntityHouseProtectionAura(World world){
		super(world);
		setSize(5 * 2, 1);
		this.lifetime = -1;
	}

	@Override
	protected Element getElement() {
		return Element.HEALING;
	}

	@Override
	public String getLocation() {
		return "u_healing";
	}

	@Override
	public EnumActionResult applyPlayerInteraction(EntityPlayer player, Vec3d vec, EnumHand hand){
		// Permanent constructs can now be dispelled by sneak-right-clicking
		if(lifetime == -1 && getCaster() == player && player.isSneaking()){
			this.lifetime=10;
			this.despawn();
			return EnumActionResult.SUCCESS;
		}

		return super.applyPlayerInteraction(player, vec, hand);
	}


	/** Returns true if the given entity is completely inside this forcefield (the surface counts as outside). */
	public boolean contains(Entity entity){
		return contains(entity.getEntityBoundingBox());
	}


	/** Returns true if the given bounding box is completely inside this forcefield (the surface counts as outside). */
	public boolean contains(AxisAlignedBB box){
		return Arrays.stream(GeometryUtils.getVertices(box)).allMatch(this::contains);
	}

	@Override
	public boolean contains(Vec3d vec){
		return vec.distanceTo(this.getPositionVector()) < 16; // The surface counts as outside
	}

	@Override
	public Vec3d calculateIntercept(Vec3d origin, Vec3d endpoint, float fuzziness){

		// We want the intercept between the line and a sphere
		// First we need to find the point where the line is closest to the centre
		// Then we can use a bit of geometry to find the intercept

		// Find the closest point to the centre
		// http://mathworld.wolfram.com/Point-LineDistance3-Dimensional.html
		Vec3d line = endpoint.subtract(origin);
		double t = -origin.subtract(this.getPositionVector()).dotProduct(line) / line.lengthSquared();
		Vec3d closestPoint = origin.add(line.scale(t));
		// Now calculate the distance from that point to the centre (squared because that's all we need)
		double dsquared = closestPoint.squareDistanceTo(this.getPositionVector());
		double rsquared = Math.pow(16 + fuzziness, 2);
		// If the minimum distance is outside the radius (plus fuzziness) then there is no intercept
		if(dsquared > rsquared) return null;
		// Now do pythagoras to find the other side of the triangle, which is the distance along the line from
		// the closest point to the edge of the sphere, and go that far back towards the origin - and that's it!
		return closestPoint.subtract(line.normalize().scale(MathHelper.sqrt(rsquared - dsquared)));
	}

	@Override
	public boolean canRenderOnFire(){
		return false;
	}

	@Override
	protected void tick() {

		if(this.ticksExisted % 25 == 1){
			this.playSound(WizardrySounds.ENTITY_HEAL_AURA_AMBIENT, 0.05f, 1.0f);
		}

		super.onUpdate();

		if(!this.world.isRemote){
			double radius = 32;

			List<EntityLivingBase> targets = EntityUtils.getLivingWithinRadius(radius, posX, posY, posZ, world);

			for(EntityLivingBase target : targets){

				if(this.isValidTarget(target) && target instanceof IMob){

					Vec3d currentPos = Arrays.stream(GeometryUtils.getVertices(target.getEntityBoundingBox()))
							.min(Comparator.comparingDouble(v -> v.distanceTo(this.getPositionVector())))
							.orElse(target.getPositionVector()); // This will never happen, it's just here to make the compiler happy

					double currentDistance = target.getDistance(this);

					// Estimate the target's position next tick
					// We have to assume the same vertex is closest or the velocity will be wrong
					Vec3d nextTickPos = currentPos.add(new Vec3d(target.motionX, target.motionY, target.motionZ));
					double nextTickDistance = nextTickPos.distanceTo(this.getPositionVector());

					boolean flag;

					if(EntityUtils.isLiving(target)){
						// Non-allied living entities shouldn't be inside at all
						flag = nextTickDistance <= radius;
					}else{
						// Non-living entities will bounce off if they hit the forcefield within the next tick...
						flag = (currentDistance > radius && nextTickDistance <= radius) // ...from the outside...
								|| (currentDistance < radius && nextTickDistance >= radius); // ...or from the inside
					}

					if(flag){
						Vec3d targetRelativePos = currentPos.subtract(this.getPositionVector());

						double nudgeVelocity = this.contains(target) ? -0.1 : 0.1;
						if(EntityUtils.isLiving(target)) nudgeVelocity = 0.25;
						Vec3d extraVelocity = targetRelativePos.normalize().scale(nudgeVelocity);

						// ...make it bounce off!
						target.motionX = target.motionX * -BOUNCINESS + extraVelocity.x;
						target.motionY = target.motionY * -BOUNCINESS + extraVelocity.y;
						target.motionZ = target.motionZ * -BOUNCINESS + extraVelocity.z;

						// Prevents the forcefield bouncing things into the floor
						if(target.onGround && target.motionY < 0) target.motionY = 0.1;

						// How far the target needs to move towards the centre (negative means away from the centre)
						double distanceTowardsCentre = -(targetRelativePos.lengthVector() - radius) - (radius - nextTickDistance);
						Vec3d targetNewPos = target.getPositionVector().add(targetRelativePos.normalize().scale(distanceTowardsCentre));
						target.setPosition(targetNewPos.x, targetNewPos.y, targetNewPos.z);

						world.playSound(target.posX, target.posY, target.posZ, WizardrySounds.ENTITY_FORCEFIELD_DEFLECT,
								WizardrySounds.SPELLS, 0.3f, 1.3f, false);

						if(!world.isRemote){
							// Player motion is handled on that player's client so needs packets
							if(target instanceof EntityPlayerMP){
								((EntityPlayerMP)target).connection.sendPacket(new SPacketEntityVelocity(target));
							}

						}else{

							Vec3d relativeImpactPos = targetRelativePos.normalize().scale(radius);

							float yaw = (float)Math.atan2(relativeImpactPos.x, -relativeImpactPos.z);
							float pitch = (float)Math.asin(relativeImpactPos.y/ radius);

							ParticleBuilder.create(Type.FLASH).pos(this.getPositionVector().add(relativeImpactPos))
									.time(6).face((float)(yaw * 180/Math.PI), (float)(pitch * 180/Math.PI))
									.clr(0.9f, 0.95f, 1).spawn(world);

							for(int i = 0; i < 12; i++){

								float yaw1 = yaw + 0.3f * (rand.nextFloat() - 0.5f) - (float)Math.PI/2;
								float pitch1 = pitch + 0.3f * (rand.nextFloat() - 0.5f);

								float brightness = rand.nextFloat();

								double r = radius + 0.05;
								double x = this.posX + r * MathHelper.cos(yaw1) * MathHelper.cos(pitch1);
								double y = this.posY + r * MathHelper.sin(pitch1);
								double z = this.posZ + r * MathHelper.sin(yaw1) * MathHelper.cos(pitch1);

								ParticleBuilder.create(Type.DUST).pos(x, y, z).time(6 + rand.nextInt(6))
										.face((float)(yaw1 * 180/Math.PI) + 90, (float)(pitch1 * 180/Math.PI)).scale(1.5f)
										.clr(0.7f + 0.3f * brightness, 0.85f + 0.15f * brightness, 1).spawn(world);
							}
						}
					}
				}
			}
		}else{
			for(int i=1; i<3; i++){
				float brightness = 0.5f + (rand.nextFloat() * 0.5f);
				double radius = rand.nextDouble() * (width/2);
				float angle = rand.nextFloat() * (float)Math.PI * 2;
				ParticleBuilder.create(Type.SPARKLE)
						.pos(this.posX + radius * MathHelper.cos(angle), this.posY, this.posZ + radius * MathHelper.sin(angle))
						.vel(0, 0.05, 0)
						.time(48 + this.rand.nextInt(12))
						.clr(1.0f, 1.0f, brightness)
						.spawn(world);
			}
		}
	}

	@Override
	protected void ritualEnd() {

	}

}
