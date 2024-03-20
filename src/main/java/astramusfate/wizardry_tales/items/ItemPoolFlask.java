package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemPoolFlask extends Item {

	public enum Size {

		SMALL(Tales.mp.mana_flask_small, 25, EnumRarity.COMMON),
		MEDIUM(Tales.mp.mana_flask_medium, 40, EnumRarity.COMMON),
		LARGE(Tales.mp.mana_flask_large, 60, EnumRarity.RARE);

		public final int capacity;
		public final int useDuration;
		public final EnumRarity rarity;

		Size(int capacity, int useDuration, EnumRarity rarity){
			this.capacity = capacity;
			this.useDuration = useDuration;
			this.rarity = rarity;
		}
	}

	public final Size size;

	public ItemPoolFlask(Size size){
		super();
		this.size = size;
		this.setCreativeTab(TalesTabs.Items);
		this.setMaxStackSize(16);
	}

	@Nonnull
	@Override
	public EnumRarity getForgeRarity(@Nonnull ItemStack stack){
		return size.rarity;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn){
		Wizardry.proxy.addMultiLineDescription(tooltip, "item." + WizardryTales.MODID + ":pool_flask.desc", size.capacity);
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack stack){
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack){
		return size.useDuration;
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand){

		ItemStack flask = player.getHeldItem(hand);

		ISoul soul = Mana.getSoul(player);
		if (soul != null){
			if(player.capabilities.isCreativeMode){
				findAndChargeItem(flask, player);

			}else{
				player.setActiveHand(hand);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, flask);

		}else{
			return new ActionResult<>(EnumActionResult.FAIL, flask);
		}
	}

	@Override
	public void onUsingTick(@Nonnull ItemStack stack, EntityLivingBase player, int count){
		if(player.world.isRemote){
			float f = count/(float)getMaxItemUseDuration(stack);
			Vec3d pos = player.getPositionEyes(0).subtract(0, 0.2, 0).add(player.getLookVec().scale(0.6));
			Vec3d delta = new Vec3d(0, 0.2 * f, 0).rotatePitch(count * 0.5f).rotateYaw((float)Math.toRadians(90 - player.rotationYawHead));
			ParticleBuilder.create(Type.DUST).pos(pos.add(delta)).vel(delta.scale(0.2)).time(12 + player.world.rand.nextInt(6))
					.clr(1, 1, 0.65f).fade(0.7f, 0, 1).spawn(player.world);
		}
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity){

		if(entity instanceof EntityPlayer && Mana.getSoul((EntityPlayer) entity) != null){
			findAndChargeItem(stack, (EntityPlayer)entity);
		}

		return stack;
	}

	private void findAndChargeItem(ItemStack stack, EntityPlayer player){
		ISoul soul = Mana.getSoul(player);
		if (soul == null) return;
		if (soul.getMP() < soul.getMaxMP()) {
			soul.addMana(player, size.capacity);

			EntityUtils.playSoundAtPlayer(player, WizardrySounds.ITEM_MANA_FLASK_USE, 1, 1);
			EntityUtils.playSoundAtPlayer(player, WizardrySounds.ITEM_MANA_FLASK_RECHARGE, 0.7f, 1.1f);

			if (!player.isCreative()) stack.shrink(1);
			player.getCooldownTracker().setCooldown(this, 20);
		}
	}

}
