package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.IIntelligentSpellCaster;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.SpellRay;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Steal extends SpellRay {

	public static final String LUCK_FACTOR = "luck_factor";

	public Steal(){
		super(WizardryTales.MODID,"steal", SpellActions.POINT, false);
		this.addProperties(LUCK_FACTOR);
	}

	@Override
	protected boolean onEntityHit(World world, Entity target, Vec3d hit, EntityLivingBase caster, Vec3d origin, int ticksInUse, SpellModifiers modifiers){
		
		if(target instanceof EntityLivingBase && caster != null){

			EntityLivingBase entity = (EntityLivingBase)target;
			PotionEffect potion = caster.isPotionActive(MobEffects.LUCK) ? caster.getActivePotionEffect(MobEffects.LUCK) : null;
			double luck = caster.getEntityAttribute(SharedMonsterAttributes.LUCK).getAttributeValue();
			int addition = potion == null ? (int) (luck * 5) : (int) ((luck) * 10);

			if (!world.isRemote && Solver.chance(Math.round((100 * getProperty(LUCK_FACTOR).floatValue()) + addition))){
				if (target instanceof EntityPlayer){
					EntityPlayer targetPlayer = (EntityPlayer) target;
					List<ItemStack> inventory = Lists.newArrayList(targetPlayer.inventory.armorInventory);
					inventory.add(targetPlayer.getHeldItemMainhand());
					inventory.add(targetPlayer.getHeldItemOffhand());
					inventory.addAll(targetPlayer.inventory.mainInventory);
                    Collections.shuffle(inventory);
					ItemStack stack = inventory.get(0);
					EntityItem item = entity.entityDropItem(stack.copy(), 0.0F);
					// Makes the item move towards the caster
					if (item != null) {
						item.motionX = (origin.x - caster.posX) / 20;
						item.motionZ = (origin.z - caster.posZ) / 20;
					}
					stack.shrink(stack.getCount());
					if (caster instanceof EntityPlayer){
						Aterna.translate((EntityPlayer) caster, "spell.wizardry_tales:steal.success", true);
					}
					return true;
				}else{
					List<ItemStack> inventory = Lists.newArrayList(entity.getHeldItemMainhand(), entity.getHeldItemOffhand());
					inventory.addAll(Lists.newArrayList(entity.getArmorInventoryList()));
					if (inventory.isEmpty()) return true;
					Collections.shuffle(inventory);
					ItemStack stack = inventory.get(0);
					EntityItem item = entity.entityDropItem(stack.copy(), 0.0F);
					// Makes the item move towards the caster
					if (item != null) {
						item.motionX = (origin.x - caster.posX) / 20;
						item.motionZ = (origin.z - caster.posZ) / 20;
					}
					stack.shrink(stack.getCount());
					if (caster instanceof EntityPlayer){
						Aterna.translate((EntityPlayer) caster, "spell.wizardry_tales:steal.success", true);
					}

					if (target instanceof EntityLiving){
						String text = "examplemod:no_such";
						ResourceLocation location = EntityList.getKey(target);
						if (location != null) text = location.toString();
						EntityLiving living = (EntityLiving) target;
						if ((target instanceof ISpellCaster && Tales.spells.steal_anger_wizards) ||
								Arrays.asList(Tales.spells.steal_anger_list).contains(text)){
							living.setRevengeTarget(caster);
						}
					}
					return true;
				}
			}

			if (caster instanceof EntityPlayer && !world.isRemote){
				Aterna.translate((EntityPlayer) caster, "spell.wizardry_tales:steal.fail", true);
			}

			if (target instanceof EntityLiving && !world.isRemote){
				String text = "examplemod:no_such";
				ResourceLocation location = EntityList.getKey(target);
				if (location != null) text = location.toString();
				EntityLiving living = (EntityLiving) target;
				if ((target instanceof ISpellCaster && Tales.spells.steal_anger_wizards) ||
						Arrays.asList(Tales.spells.steal_anger_list).contains(text)){
					living.setRevengeTarget(caster);
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
		return true;
	}

	@Override
	public boolean applicableForItem(Item item) {
		return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
	}

}
