package astramusfate.wizardry_tales.items.rituals;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.registry.TalesTabs;
import com.google.common.collect.Lists;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.ParticleBuilder.Type;
import net.minecraft.client.resources.I18n;
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
import net.minecraftforge.common.IRarity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public abstract class ItemRitual extends Item {
	public Element element;
	public ItemRitual(String name, Element element){
		super();
		this.setCreativeTab(TalesTabs.Rituals);
		this.setMaxStackSize(16);
		this.setRegistryName(WizardryTales.MODID, "ritual_" + name);
		this.setUnlocalizedName(WizardryTales.MODID + ":ritual_" + name);
		this.element = element;
	}

	public ItemRitual(String modId, String name, Element element){
		super();
		this.setCreativeTab(TalesTabs.Rituals);
		this.setMaxStackSize(16);
		this.setRegistryName(modId, "ritual_" + name);
		this.setUnlocalizedName(modId + ":ritual_" + name);
		this.element = element;
	}

	public void setElement(Element element){
		this.element = element;
	}

	/** Defines if ritual can be cast, if it requires as cost singular items, it's appropriate to use {@link Thief#hasItems(EntityPlayer, List)}} **/
	abstract boolean canCastRitual(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack);

	/** Casts ritual. If not succeed, then doesn't consume cost [look below]. **/
	abstract boolean castRitual(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack);
	/** If ritual is succeeded, consumes cost of ritual, if it's singular items, it's appropriate to use {@link Thief#consumeItems(EntityPlayer, List)}} **/
	abstract void consumeRitualCost(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack);

	/** How much ticks player should wait till player be able to use rituals again. **/
	abstract int cooldownTime();
	/** How much ticks player should charge up ritual to activate it. **/
	abstract int chargeUpTime();

	/** This method defines if item should be traded or not. **/
	public abstract boolean canBeTraded();
	/** This method defines cost of trade for Midnight Trader and others if ritual is traded of course! **/
	abstract IRarity getItemRarity();

	abstract List<Item> getIngredients();



	private void usage(ItemStack stack, EntityPlayer player){
		//EntityUtils.playSoundAtPlayer(player, WizardrySounds, 1, 1);
		if (castRitual(player.world, player, stack)){
			consumeRitualCost(player.world, player, stack);
			if (!player.isCreative()) stack.shrink(1);
			player.getCooldownTracker().setCooldown(this, cooldownTime());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<String> tooltip, @Nonnull ITooltipFlag flagIn){
		tooltip.add(I18n.format("item." + WizardryTales.MODID + ":" + Objects.requireNonNull(this.getRegistryName()).getResourcePath() + ".desc"));
	}

	@Nonnull
	@Override
	public EnumAction getItemUseAction(@Nonnull ItemStack stack){
		return EnumAction.BLOCK;
	}

	@Override
	public int getMaxItemUseDuration(@Nonnull ItemStack stack){
		return chargeUpTime();
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand){

		ItemStack stack = player.getHeldItem(hand);

		ISoul soul = Mana.getSoul(player);
		if (soul != null){
			if(player.capabilities.isCreativeMode){
				usage(stack, player);
			}else{
				player.setActiveHand(hand);
			}

			return new ActionResult<>(EnumActionResult.SUCCESS, stack);

		}else{
			return new ActionResult<>(EnumActionResult.FAIL, stack);
		}
	}

	@Override
	public void onUsingTick(@Nonnull ItemStack stack, EntityLivingBase player, int count){
		if(player.world.isRemote){
			float f = count/(float)getMaxItemUseDuration(stack);
			Vec3d pos = player.getPositionEyes(0).subtract(0, 0.2, 0).add(player.getLookVec().scale(0.6));
			Vec3d delta = new Vec3d(0, 0.2 * f, 0).rotatePitch(count * 0.5f).rotateYaw((float)Math.toRadians(90 - player.rotationYawHead));
			ParticleBuilder.create(Type.DUST).pos(pos.add(delta)).vel(delta.scale(0.2)).time(12 + player.world.rand.nextInt(6))
					.clr(ArcaneColor.colorByElement(this.element).darker().getRGB())
					.fade(this.element == Element.MAGIC ?
							ArcaneColor.MAGIC_FADE.getRGB() : ArcaneColor.chooseOld(this.element).getRGB())
					.spawn(player.world);
		}
	}

	@Nonnull
	@Override
	public ItemStack onItemUseFinish(@Nonnull ItemStack stack, @Nonnull World world, @Nonnull EntityLivingBase entity){

		if(entity instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)entity;
			if (player.capabilities.isCreativeMode || canCastRitual(world, player, stack)){
				usage(stack, player);
			}
		}

		return stack;
	}

	@Nonnull
	@Override
	public IRarity getForgeRarity(@Nonnull ItemStack stack) {
		return getItemRarity();
	}

	public List<Item> createStack(Item item, int count){
		List<Item> items = Lists.newArrayList();
		for (int i = 0; i < count; i++){
			items.add(item);
		}
		return items;
	}
}
