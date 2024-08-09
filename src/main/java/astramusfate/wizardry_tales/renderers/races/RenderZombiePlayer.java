package astramusfate.wizardry_tales.renderers.races;

import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.data.Tales;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderZombiePlayer {
	private static final ResourceLocation ZOMBIE_TEXTURES = new ResourceLocation("textures/entity/zombie/zombie.png");


	@SubscribeEvent
	@SuppressWarnings("unchecked") // Can't check it due to type erasure
	public static void onRenderPlayerPreEvent(RenderPlayerEvent.Pre event){

		EntityPlayer player = event.getEntityPlayer();
		boolean bool = !Race.is(player, Race.undead)
				&& player.isPotionActive(WizardryPotions.curse_of_undeath) && Tales.addon.curse_undead_visual;
		EntityZombie zombie = new EntityZombie(player.world);

		if(bool){
			// Regret your HUMANITY!
			Render<EntityLiving> renderer = (Render<EntityLiving>)event.getRenderer().getRenderManager().entityRenderMap.get(zombie.getClass());
			float yaw = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * event.getPartialRenderTick();
			zombie.swingProgress = player.swingProgress;
			zombie.prevSwingProgress = player.prevSwingProgress;
			zombie.renderYawOffset = player.renderYawOffset;
			zombie.prevRenderYawOffset = player.prevRenderYawOffset;
			zombie.rotationYawHead = player.rotationYawHead;
			zombie.prevRotationYawHead = player.prevRotationYawHead;
			zombie.rotationPitch = player.rotationPitch;
			zombie.prevRotationPitch = player.prevRotationPitch;
			zombie.limbSwing = player.limbSwing;
			zombie.limbSwingAmount = player.limbSwingAmount;
			zombie.prevLimbSwingAmount = player.prevLimbSwingAmount;
			addEquip(player, zombie, EntityEquipmentSlot.HEAD);
			addEquip(player, zombie, EntityEquipmentSlot.CHEST);
			addEquip(player, zombie, EntityEquipmentSlot.LEGS);
			addEquip(player, zombie, EntityEquipmentSlot.FEET);
			addEquip(player, zombie, EntityEquipmentSlot.MAINHAND);
			addEquip(player, zombie, EntityEquipmentSlot.OFFHAND);
			renderer.doRender(zombie, event.getX(), event.getY(), event.getZ(), yaw, event.getPartialRenderTick());
			event.setCanceled(true);
		}
	}

	public static void addEquip(EntityPlayer player, EntityZombie skeleton, EntityEquipmentSlot slot){
		skeleton.setItemStackToSlot(slot, player.getItemStackFromSlot(slot));
	}

	@SubscribeEvent
	public static void onRenderHandEvent(RenderHandEvent event){
		EntityPlayer player = Minecraft.getMinecraft().player;
		boolean bool = !Race.is(player, Race.undead)
				&& player.isPotionActive(WizardryPotions.curse_of_undeath) && Tales.addon.curse_undead_visual;
		EntityZombie zombie = new EntityZombie(player.world);

		if(bool){
			zombie.rotationYawHead = Minecraft.getMinecraft().player.rotationYaw;
		}
	}
}
