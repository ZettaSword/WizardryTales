package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.UUID;

public class Charm extends SpellRay {

	public static final IStoredVariable<UUID> UUID_CHARMED = IStoredVariable.StoredVariable.ofUUID("charmedBy", Persistence.DIMENSION_CHANGE).setSynced();

	/** The NBT tag name for storing the controlling entity's UUID in the target's tag compound. */
	public static final String NBT_KEY = "charmedToEntity";

	public Charm(){
		super(WizardryTales.MODID,"charm", SpellActions.POINT, false);
		this.soundValues(0.7f, 1, 0.4f);
		addProperties(DURATION);
		WizardData.registerStoredVariables(UUID_CHARMED);
	}
	
	@Override public boolean canBeCastBy(EntityLiving npc, boolean override) { return true; }
	@Override public boolean canBeCastBy(TileEntityDispenser dispenser) { return false; }
	
	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		if(EntityUtils.isLiving(target)){
				
			if(!canControl(target)){
				if(!world.isRemote){
					if(caster instanceof EntityPlayer){
						// Adds a message saying that the player/boss entity/wizard resisted mind control
						((EntityPlayer)caster).sendStatusMessage(new TextComponentTranslation("spell.resist", target.getName(),
								this.getNameForTranslationFormatted()), true);
					}
				}

			}else if(target instanceof EntityLivingBase){
				if(!world.isRemote) startControlling((EntityLivingBase) target, caster,
						this.getProperty(DURATION).intValue());
			}

			if(world.isRemote){
				
				for(int i=0; i<10; i++){
					ParticleBuilder.create(Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
					.clr(0.8f, 0.2f, 1.0f).spawn(world);
					ParticleBuilder.create(Type.DARK_MAGIC, world.rand, target.posX,
							target.posY + target.getEyeHeight(), target.posZ, 0.25, false)
					.clr(0.2f, 0.04f, 0.25f).spawn(world);
				}
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	protected boolean onBlockHit(World world, BlockPos pos, EnumFacing side, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	@Override
	protected boolean onMiss(World world, EntityLivingBase caster, Vec3d origin, Vec3d direction, int ticksInUse, SpellModifiers modifiers){
		return false;
	}

	/** Returns true if the given entity can be charmed (i.e. is not a npc or boss). */
	public static boolean canControl(Entity target){
		return target instanceof EntityLivingBase && target.isNonBoss()
				&& !Arrays.asList(Tales.toResourceLocations(Tales.spells.charm_blacklist))
				.contains(EntityList.getKey(target.getClass()));
	}

	public static void startControlling(EntityLivingBase target, EntityLivingBase controller, int duration){
		target.getEntityData().setUniqueId(NBT_KEY, controller.getUniqueID());
		if (target instanceof EntityPlayer){
			WizardData data = WizardData.get((EntityPlayer) target);
			if (data != null) {
				data.setVariable(UUID_CHARMED, controller.getUniqueID());
				data.sync();
			}
		}
		target.addPotionEffect(new PotionEffect(TalesEffects.charm, duration, 0));
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
	}

}