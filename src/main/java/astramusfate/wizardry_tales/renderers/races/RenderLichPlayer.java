package astramusfate.wizardry_tales.renderers.races;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.entity.living.EntityLich;
import astramusfate.wizardry_tales.renderers.models.ModelLich;
import com.google.common.base.MoreObjects;
import electroblob.wizardry.spell.Mine;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Objects;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderLichPlayer {

	public static final ModelLich model = new ModelLich();
	private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");
	private static final ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");

	@SubscribeEvent
	@SuppressWarnings("unchecked") // Can't check it due to type erasure
	public static void onRenderPlayerPreEvent(RenderPlayerEvent.Pre event){

		EntityPlayer player = event.getEntityPlayer();
		boolean bool = Race.is(player, Race.undead);
		EntityLich skeleton = new EntityLich(player.world);
		skeleton.setOwnerId(player.getUniqueID());

		if(bool){
			// Reject your HUMANITY!
			Render<EntityLiving> renderer = (Render<EntityLiving>)event.getRenderer().getRenderManager().entityRenderMap.get(skeleton.getClass());
			float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getPartialRenderTick();
			skeleton.swingProgress = player.swingProgress;
			skeleton.prevSwingProgress = player.prevSwingProgress;
			skeleton.renderYawOffset = player.renderYawOffset;
			skeleton.prevRenderYawOffset = player.prevRenderYawOffset;
			skeleton.rotationYawHead = player.rotationYawHead;
			skeleton.prevRotationYawHead = player.prevRotationYawHead;
			skeleton.rotationPitch = player.rotationPitch;
			skeleton.prevRotationPitch = player.prevRotationPitch;
			skeleton.limbSwing = player.limbSwing;
			skeleton.limbSwingAmount = player.limbSwingAmount;
			skeleton.prevLimbSwingAmount = player.prevLimbSwingAmount;
			skeleton.setSneaking(player.isSneaking());
			addEquip(player, skeleton, EntityEquipmentSlot.HEAD);
			addEquip(player, skeleton, EntityEquipmentSlot.CHEST);
			addEquip(player, skeleton, EntityEquipmentSlot.LEGS);
			addEquip(player, skeleton, EntityEquipmentSlot.FEET);
			addEquip(player, skeleton, EntityEquipmentSlot.MAINHAND);
			addEquip(player, skeleton, EntityEquipmentSlot.OFFHAND);
			renderer.doRender(skeleton, event.getX(), event.getY(), event.getZ(), yaw, event.getPartialRenderTick());
			event.setCanceled(true);

		}
	}

	public static void addEquip(EntityPlayer player, EntityLich skeleton, EntityEquipmentSlot slot){
		skeleton.setItemStackToSlot(slot, player.getItemStackFromSlot(slot));
	}

	@SubscribeEvent
	public static void onRenderHandEvent(RenderHandEvent event){
		AbstractClientPlayer player = Minecraft.getMinecraft().player;
		boolean bool = Race.is(player, Race.undead);

		if(bool) {
			/*skeleton.rotationYawHead = Minecraft.getMinecraft().player.rotationYaw;

			if(Minecraft.getMinecraft().player.getHeldItemMainhand().isEmpty() || Minecraft.getMinecraft().player.getHeldItemOffhand().isEmpty()){
				event.setCanceled(true);

				boolean flag = Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)Minecraft.getMinecraft().getRenderViewEntity()).isPlayerSleeping();

				if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !flag &&
						!Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().playerController.isSpectator())
				{
					//ModelSkeleton model = new ModelSkeleton();
					//Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItem(skeleton, );
					GlStateManager.pushMatrix();
					Minecraft.getMinecraft().entityRenderer.enableLightmap();


					float f = player.getSwingProgress(event.getPartialTicks());
					boolean flag0 = true;
					boolean flag1 = true;
					if (player.isHandActive())
					{
						ItemStack itemstack = player.getActiveItemStack();

						if (itemstack.getItem() instanceof net.minecraft.item.ItemBow)
						{
							EnumHand enumhand1 = player.getActiveHand();
							flag0 = enumhand1 == EnumHand.MAIN_HAND;
							flag1 = !flag0;
						}
					}
					EnumHandSide enumhandside = flag0 ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
					float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * event.getPartialTicks();
					float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getPartialTicks();
					rotateArroundXAndY(f1, f2);
					setLightmap();
					rotateArm(event.getPartialTicks());
					GlStateManager.enableRescaleNormal();


					if (flag0)
					{
						float f3 = player.getActiveHand() == EnumHand.MAIN_HAND ? f : 0.0F;
						float f5 = player.getSwingProgress(event.getPartialTicks());
						//float f5 = 1.0F - ((player.swingProgress - player.prevSwingProgress) * event.getPartialTicks());
						//float f5 = 1.0F - (this.prevEquippedProgressMainHand + (this.equippedProgressMainHand - this.prevEquippedProgressMainHand) * partialTicks);
						renderArmFirstPerson(f5, f3, enumhandside);

					}

					if (flag1)
					{
						float f4 = player.getActiveHand() == EnumHand.OFF_HAND ? f : 0.0F;
						float f6 = player.getSwingProgress(event.getPartialTicks());
						renderArmFirstPerson(f6, f4, enumhandside);
					}

					//Minecraft.getMinecraft().entityRenderer.itemRenderer.renderItemInFirstPerson(event.getPartialTicks());


					GlStateManager.disableRescaleNormal();
					RenderHelper.disableStandardItemLighting();
					GlStateManager.popMatrix();
					Minecraft.getMinecraft().entityRenderer.disableLightmap();


				}
			}*/

			event.setCanceled(true);
			boolean flag = Minecraft.getMinecraft().getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase) Minecraft.getMinecraft().getRenderViewEntity()).isPlayerSleeping();

			if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && !flag &&
					!Minecraft.getMinecraft().gameSettings.hideGUI && !Minecraft.getMinecraft().playerController.isSpectator()
			&& !WizardryTales.hasRealRender) {
				Minecraft.getMinecraft().entityRenderer.enableLightmap();
				float partialTicks = event.getPartialTicks();
				float f = player.getSwingProgress(partialTicks);
				EnumHand enumhand = MoreObjects.firstNonNull(player.swingingHand, EnumHand.MAIN_HAND);
				float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
				float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * partialTicks;
				ItemRenderer itemRenderer = Minecraft.getMinecraft().getItemRenderer();
				flag = true;
				boolean flag1 = true;

				if (player.isHandActive()) {
					ItemStack itemstack = player.getActiveItemStack();

					if (itemstack.getItem() == Items.BOW) {
						EnumHand enumhand1 = player.getActiveHand();
						flag = enumhand1 == EnumHand.MAIN_HAND;
						flag1 = !flag;
					}
				}

				rotateAroundXAndY(f1, f2);
				setLightmap();
				rotateArm(partialTicks);
				GlStateManager.enableRescaleNormal();

				ItemStack itemStackMainHand = player.getHeldItemMainhand();
				ItemStack itemStackOffHand = player.getHeldItemOffhand();
				if (flag) {
					float f3 = enumhand == EnumHand.MAIN_HAND ? f : 0.0F;
					//float f5 = player.getSwingProgress(event.getPartialTicks());
					//float f5 = 1.0F - (player.prevSwingProgress + (player.swingProgress - player.prevSwingProgress) * partialTicks);
					float f5 = 1.0F - (itemRenderer.prevEquippedProgressMainHand  + (itemRenderer.equippedProgressMainHand - itemRenderer.prevEquippedProgressMainHand) * partialTicks);
					renderItemInFirstPerson(player, partialTicks, f1, EnumHand.MAIN_HAND, f3, itemStackMainHand, f5);
				}

				if (flag1) {
					float f4 = enumhand == EnumHand.OFF_HAND ? f : 0.0F;
					//float f6 = player.getSwingProgress(event.getPartialTicks());
					//float f6 = 1.0F - (player.prevSwingProgress + (player.swingProgress - player.prevSwingProgress) * partialTicks);
					float f6 = 1.0F - (itemRenderer.prevEquippedProgressOffHand + (itemRenderer.equippedProgressOffHand - itemRenderer.prevEquippedProgressOffHand) * partialTicks);
					renderItemInFirstPerson(player, partialTicks, f1, EnumHand.OFF_HAND, f4, itemStackOffHand, f6);
				}

				GlStateManager.disableRescaleNormal();
				RenderHelper.disableStandardItemLighting();
				Minecraft.getMinecraft().entityRenderer.disableLightmap();
			}
		}
	}

	private static void rotateAroundXAndY(float p_178101_1_, float p_178101_2_) {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(p_178101_1_, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(p_178101_2_, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	private static void setLightmap() {
		AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().player;
		int i = Minecraft.getMinecraft().world.getCombinedLight(new BlockPos(abstractclientplayer.posX, abstractclientplayer.posY + (double)abstractclientplayer.getEyeHeight(), abstractclientplayer.posZ), 0);
		float f = (float)(i & '\uffff');
		float f1 = (float)(i >> 16);
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f1);
	}

	private static void rotateArm(float p_187458_1_) {
		EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
		float f = entityplayersp.prevRenderArmPitch + (entityplayersp.renderArmPitch - entityplayersp.prevRenderArmPitch) * p_187458_1_;
		float f1 = entityplayersp.prevRenderArmYaw + (entityplayersp.renderArmYaw - entityplayersp.prevRenderArmYaw) * p_187458_1_;
		GlStateManager.rotate((entityplayersp.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((entityplayersp.rotationYaw - f1) * 0.1F, 0.0F, 1.0F, 0.0F);
	}

	public static void renderItemInFirstPerson(AbstractClientPlayer p_187457_1_, float p_187457_2_, float p_187457_3_, EnumHand p_187457_4_, float p_187457_5_, ItemStack p_187457_6_, float p_187457_7_) {
		boolean flag = p_187457_4_ == EnumHand.MAIN_HAND;
		EnumHandSide enumhandside = flag ? p_187457_1_.getPrimaryHand() : p_187457_1_.getPrimaryHand().opposite();
		ItemStack itemStackOffHand = p_187457_1_.getHeldItemOffhand();
		ItemStack itemStackMainHand = p_187457_1_.getHeldItemMainhand();
		GlStateManager.pushMatrix();
		if (p_187457_6_.isEmpty()) {
			if (flag && !p_187457_1_.isInvisible()) {
				renderArmFirstPerson(p_187457_7_, p_187457_5_, enumhandside);
			}
		} else if (p_187457_6_.getItem() instanceof ItemMap) {
			if (flag && itemStackOffHand.isEmpty()) {
				renderMapFirstPerson(p_187457_3_, p_187457_7_, p_187457_5_);
			} else {
				renderMapFirstPersonSide(p_187457_7_, enumhandside, p_187457_5_, p_187457_6_);
			}
		} else {
			boolean flag1 = enumhandside == EnumHandSide.RIGHT;
			float f5;
			float f6;
			if (p_187457_1_.isHandActive() && p_187457_1_.getItemInUseCount() > 0 && p_187457_1_.getActiveHand() == p_187457_4_) {
				int j = flag1 ? 1 : -1;
				switch (p_187457_6_.getItemUseAction()) {
					case NONE:
						transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case EAT:
					case DRINK:
						transformEatFirstPerson(p_187457_2_, enumhandside, p_187457_6_);
						transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case BLOCK:
						transformSideFirstPerson(enumhandside, p_187457_7_);
						break;
					case BOW:
						transformSideFirstPerson(enumhandside, p_187457_7_);
						GlStateManager.translate((float)j * -0.2785682F, 0.18344387F, 0.15731531F);
						GlStateManager.rotate(-13.935F, 1.0F, 0.0F, 0.0F);
						GlStateManager.rotate((float)j * 35.3F, 0.0F, 1.0F, 0.0F);
						GlStateManager.rotate((float)j * -9.785F, 0.0F, 0.0F, 1.0F);
						f5 = (float)p_187457_6_.getMaxItemUseDuration() - ((float)Minecraft.getMinecraft().player.getItemInUseCount() - p_187457_2_ + 1.0F);
						f6 = f5 / 20.0F;
						f6 = (f6 * f6 + f6 * 2.0F) / 3.0F;
						if (f6 > 1.0F) {
							f6 = 1.0F;
						}

						if (f6 > 0.1F) {
							float f7 = MathHelper.sin((f5 - 0.1F) * 1.3F);
							float f3 = f6 - 0.1F;
							float f4 = f7 * f3;
							GlStateManager.translate(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
						}

						GlStateManager.translate(f6 * 0.0F, f6 * 0.0F, f6 * 0.04F);
						GlStateManager.scale(1.0F, 1.0F, 1.0F + f6 * 0.2F);
						GlStateManager.rotate((float)j * 45.0F, 0.0F, -1.0F, 0.0F);
				}
			} else {
				float f = -0.4F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 3.1415927F);
				f5 = 0.2F * MathHelper.sin(MathHelper.sqrt(p_187457_5_) * 6.2831855F);
				f6 = -0.2F * MathHelper.sin(p_187457_5_ * 3.1415927F);
				int i = flag1 ? 1 : -1;
				GlStateManager.translate((float)i * f, f5, f6);
				transformSideFirstPerson(enumhandside, p_187457_7_);
				transformFirstPerson(enumhandside, p_187457_5_);
			}

			renderItemSide(p_187457_1_, p_187457_6_, flag1 ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag1);
		}

		GlStateManager.popMatrix();
	}


	private static void renderMapFirstPersonSide(float p_187465_1_, EnumHandSide p_187465_2_, float p_187465_3_, ItemStack p_187465_4_) {
		float f = p_187465_2_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		GlStateManager.translate(f * 0.125F, -0.125F, 0.0F);
		if (!Minecraft.getMinecraft().player.isInvisible()) {
			GlStateManager.pushMatrix();
			GlStateManager.rotate(f * 10.0F, 0.0F, 0.0F, 1.0F);
			renderArmFirstPerson(p_187465_1_, p_187465_3_, p_187465_2_);
			GlStateManager.popMatrix();
		}

		GlStateManager.pushMatrix();
		GlStateManager.translate(f * 0.51F, -0.08F + p_187465_1_ * -1.2F, -0.75F);
		float f1 = MathHelper.sqrt(p_187465_3_);
		float f2 = MathHelper.sin(f1 * 3.1415927F);
		float f3 = -0.5F * f2;
		float f4 = 0.4F * MathHelper.sin(f1 * 6.2831855F);
		float f5 = -0.3F * MathHelper.sin(p_187465_3_ * 3.1415927F);
		GlStateManager.translate(f * f3, f4 - 0.3F * f2, f5);
		GlStateManager.rotate(f2 * -45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * f2 * -30.0F, 0.0F, 1.0F, 0.0F);
		renderMapFirstPerson(p_187465_4_);
		GlStateManager.popMatrix();
	}

	private static void renderMapFirstPerson(float p_187463_1_, float p_187463_2_, float p_187463_3_) {
		float f = MathHelper.sqrt(p_187463_3_);
		float f1 = -0.2F * MathHelper.sin(p_187463_3_ * 3.1415927F);
		float f2 = -0.4F * MathHelper.sin(f * 3.1415927F);
		GlStateManager.translate(0.0F, -f1 / 2.0F, f2);
		float f3 = getMapAngleFromPitch(p_187463_1_);
		GlStateManager.translate(0.0F, 0.04F + p_187463_2_ * -1.2F + f3 * -0.5F, -0.72F);
		GlStateManager.rotate(f3 * -85.0F, 1.0F, 0.0F, 0.0F);
		renderArms();
		float f4 = MathHelper.sin(f * 3.1415927F);
		GlStateManager.rotate(f4 * 20.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(2.0F, 2.0F, 2.0F);
		ItemStack itemStackMainHand = Minecraft.getMinecraft().player.getHeldItemMainhand();
		renderMapFirstPerson(itemStackMainHand);
	}

	private static void renderMapFirstPerson(ItemStack p_187461_1_) {
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.scale(0.38F, 0.38F, 0.38F);
		GlStateManager.disableLighting();
		Minecraft.getMinecraft().getTextureManager().bindTexture(RES_MAP_BACKGROUND);
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		GlStateManager.translate(-0.5F, -0.5F, 0.0F);
		GlStateManager.scale(0.0078125F, 0.0078125F, 0.0078125F);
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
		bufferbuilder.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
		bufferbuilder.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
		bufferbuilder.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
		tessellator.draw();
		MapData mapdata = ((ItemMap)p_187461_1_.getItem()).getMapData(p_187461_1_, Minecraft.getMinecraft().world);
		if (mapdata != null) {
			Minecraft.getMinecraft().entityRenderer.getMapItemRenderer().renderMap(mapdata, false);
		}

		GlStateManager.enableLighting();
	}

	private static void renderArmFirstPerson(float p_187456_1_, float p_187456_2_, EnumHandSide p_187456_3_) {
		boolean flag = p_187456_3_ != EnumHandSide.LEFT;
		float f = flag ? 1.0F : -1.0F;
		float f1 = MathHelper.sqrt(p_187456_2_);
		float f2 = -0.3F * MathHelper.sin(f1 * 3.1415927F);
		float f3 = 0.4F * MathHelper.sin(f1 * 6.2831855F);
		float f4 = -0.4F * MathHelper.sin(p_187456_2_ * 3.1415927F);
		GlStateManager.translate(f * (f2 + 0.64000005F), f3 + -0.6F + p_187456_1_ * -0.6F, f4 + -0.71999997F);
		GlStateManager.rotate(f * 45.0F, 0.0F, 1.0F, 0.0F);
		float f5 = MathHelper.sin(p_187456_2_ * p_187456_2_ * 3.1415927F);
		float f6 = MathHelper.sin(f1 * 3.1415927F);
		GlStateManager.rotate(f * f6 * 70.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f * f5 * -20.0F, 0.0F, 0.0F, 1.0F);
		AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().player;
		Minecraft.getMinecraft().getTextureManager().bindTexture(SKELETON_TEXTURES);
		GlStateManager.translate(f * -1.0F, 3.6F, 3.5F);
		GlStateManager.rotate(f * 120.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.translate(f * 5.6F, 0.0F, 0.0F);
		GlStateManager.disableCull();
		if (flag) {
			renderRightArm(abstractclientplayer);
		} else {
			renderLeftArm(abstractclientplayer);
		}

		GlStateManager.enableCull();
	}

	private static float getMapAngleFromPitch(float p_178100_1_) {
		float f = 1.0F - p_178100_1_ / 45.0F + 0.1F;
		f = MathHelper.clamp(f, 0.0F, 1.0F);
		f = -MathHelper.cos(f * 3.1415927F) * 0.5F + 0.5F;
		return f;
	}

	public static void renderRightArm(AbstractClientPlayer clientPlayer)
	{
		float f = 1.0F;
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		float f1 = 0.0625F;
		ModelLich modelplayer = model;
		GlStateManager.enableBlend();
		modelplayer.swingProgress = 0.0F;
		modelplayer.isSneak = false;
		modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
		modelplayer.bipedRightArm.rotateAngleX = 0.0F;
		modelplayer.bipedRightArm.render(0.0625F);
		GlStateManager.disableBlend();
	}

	public static void renderLeftArm(AbstractClientPlayer clientPlayer)
	{
		float f = 1.0F;
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		float f1 = 0.0625F;
		ModelLich modelplayer = model;
		GlStateManager.enableBlend();
		modelplayer.isSneak = false;
		modelplayer.swingProgress = 0.0F;
		modelplayer.setRotationAngles(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F, clientPlayer);
		modelplayer.bipedLeftArm.rotateAngleX = 0.0F;
		modelplayer.bipedLeftArm.render(0.0625F);
		GlStateManager.disableBlend();
	}

	private static void transformFirstPerson(EnumHandSide p_187453_1_, float p_187453_2_) {
		int i = p_187453_1_ == EnumHandSide.RIGHT ? 1 : -1;
		float f = MathHelper.sin(p_187453_2_ * p_187453_2_ * 3.1415927F);
		GlStateManager.rotate((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
		float f1 = MathHelper.sin(MathHelper.sqrt(p_187453_2_) * 3.1415927F);
		GlStateManager.rotate((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.rotate(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
	}

	private static void transformSideFirstPerson(EnumHandSide p_187459_1_, float p_187459_2_) {
		int i = p_187459_1_ == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate((float)i * 0.56F, -0.52F + p_187459_2_ * -0.6F, -0.72F);
	}

	private static void transformEatFirstPerson(float p_187454_1_, EnumHandSide p_187454_2_, ItemStack p_187454_3_) {
		float f = (float)Minecraft.getMinecraft().player.getItemInUseCount() - p_187454_1_ + 1.0F;
		float f1 = f / (float)p_187454_3_.getMaxItemUseDuration();
		float f3;
		if (f1 < 0.8F) {
			f3 = MathHelper.abs(MathHelper.cos(f / 4.0F * 3.1415927F) * 0.1F);
			GlStateManager.translate(0.0F, f3, 0.0F);
		}

		f3 = 1.0F - (float)Math.pow((double)f1, 27.0);
		int i = p_187454_2_ == EnumHandSide.RIGHT ? 1 : -1;
		GlStateManager.translate(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
		GlStateManager.rotate((float)i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
	}

	public static void renderItemSide(EntityLivingBase p_187462_1_, ItemStack p_187462_2_, ItemCameraTransforms.TransformType p_187462_3_, boolean p_187462_4_) {
		if (!p_187462_2_.isEmpty()) {
			Item item = p_187462_2_.getItem();
			Block block = Block.getBlockFromItem(item);
			GlStateManager.pushMatrix();
			boolean flag = Minecraft.getMinecraft().getRenderItem().shouldRenderItemIn3D(p_187462_2_) && block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT;
			if (flag) {
				GlStateManager.depthMask(false);
			}
			Minecraft.getMinecraft().getRenderItem().renderItem(p_187462_2_, p_187462_1_, p_187462_3_, p_187462_4_);
			if (flag) {
				GlStateManager.depthMask(true);
			}

			GlStateManager.popMatrix();
		}

	}

	private static void renderArms() {
		if (!Minecraft.getMinecraft().player.isInvisible()) {
			GlStateManager.disableCull();
			GlStateManager.pushMatrix();
			GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
			renderArm(EnumHandSide.RIGHT);
			renderArm(EnumHandSide.LEFT);
			GlStateManager.popMatrix();
			GlStateManager.enableCull();
		}
	}

	private static void renderArm(EnumHandSide p_187455_1_) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(SKELETON_TEXTURES);
		GlStateManager.pushMatrix();
		float f = p_187455_1_ == EnumHandSide.RIGHT ? 1.0F : -1.0F;
		GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(f * -41.0F, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(f * 0.3F, -1.1F, 0.45F);
		if (p_187455_1_ == EnumHandSide.RIGHT) {
			renderRightArm(Minecraft.getMinecraft().player);
		} else {
			renderLeftArm(Minecraft.getMinecraft().player);
		}

		GlStateManager.popMatrix();
	}

}