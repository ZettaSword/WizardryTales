package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.data.Tales;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderCircles {

	//private static final ResourceLocation TEXTURE = new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_arcane.png");
	public static final ResourceLocation[] TEXTURES =
			new ResourceLocation[] {new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_arcane.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_fire.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_ice.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_lightning.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_necromancy.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_earth.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_sorcery.png"),
					new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_healing.png"),
			};



	// First person
	@SubscribeEvent
	public static void onRenderWorldLastEvent(RenderWorldLastEvent event){
		// Only render in first person
		if(!Tales.effects.rendered_circles) return;
		if(Minecraft.getMinecraft().gameSettings.thirdPersonView == 0){

			EntityPlayer player = Minecraft.getMinecraft().player;
			Spell spell = getCasting(player);
			boolean bool = spell.isContinuous;

			if(spell != Spells.none && bool){

				Arcanist.push();

				GlStateManager.enableBlend();
				GlStateManager.disableLighting();
				OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
				GlStateManager.depthMask(false);
				GlStateManager.disableCull();

				GlStateManager.translate(0, 1.2, 0);
				GlStateManager.rotate(-player.rotationYaw, 0, 1, 0);
				GlStateManager.rotate(player.rotationPitch, 1, 0, 0);

				Element element = spell.getElement();
				if (element == null) element = Element.MAGIC;
				Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES[element.ordinal()]);

				Arcanist.push();

				GlStateManager.translate(0, 0, 1.2);
				GlStateManager.rotate(player.ticksExisted * -2, 0, 0, 1);
				GlStateManager.scale(1.1, 1.1, 1.1);

				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				buffer.pos(-0.5, 0.5, -0.5).tex(0, 0).endVertex();
				buffer.pos(0.5, 0.5, -0.5).tex(1, 0).endVertex();
				buffer.pos(0.5, -0.5, -0.5).tex(1, 1).endVertex();
				buffer.pos(-0.5, -0.5, -0.5).tex(0, 1).endVertex();

				tessellator.draw();

				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

				buffer.pos(-0.5, 0.5, -0.5).tex(0, 0).endVertex();
				buffer.pos(-0.5, -0.5, -0.5).tex(0, 1).endVertex();
				buffer.pos(0.5, -0.5, -0.5).tex(1, 1).endVertex();
				buffer.pos(0.5, 0.5, -0.5).tex(1, 0).endVertex();

				tessellator.draw();

				Arcanist.pop();

				GlStateManager.disableBlend();
				GlStateManager.enableLighting();
				GlStateManager.depthMask(true);
				GlStateManager.enableCull();

				Arcanist.pop();

			}
		}
	}

	// Third person
	@SubscribeEvent
	public static void onRenderPlayerEvent(RenderPlayerEvent.Post event){
		if(!Tales.effects.rendered_circles) return;

		EntityPlayer player = event.getEntityPlayer();
		Vec3d look = player.getLookVec().scale(2.0);
		Spell spell = getCasting(player);
		boolean bool = spell.isContinuous;

		if(spell != Spells.none && bool){

			Arcanist.push();

			GlStateManager.enableBlend();
			GlStateManager.disableLighting();
			OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
			GlStateManager.depthMask(false);
			GlStateManager.disableCull();

			GlStateManager.translate(event.getX() + look.x, event.getY() + look.y, event.getZ() + look.z);

			GlStateManager.rotate(180, 0, 1, 0);
			GlStateManager.rotate(-player.renderYawOffset, 0, 1, 0);

			Element element = spell.getElement();
			if (element == null) element = Element.MAGIC;
			Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURES[element.ordinal()]);
			GlStateManager.color(1f,1f,1f,1f);

			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();

			GlStateManager.translate(0, 1.4, 0);
			GlStateManager.rotate(player.ticksExisted * -2, 0, 0, 1);
			double scale = 0.8;
			GlStateManager.scale(scale, scale, scale);

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			buffer.pos(-0.5, 0.5, -0.5).tex(0, 0).endVertex();
			buffer.pos(0.5, 0.5, -0.5).tex(1, 0).endVertex();
			buffer.pos(0.5, -0.5, -0.5).tex(1, 1).endVertex();
			buffer.pos(-0.5, -0.5, -0.5).tex(0, 1).endVertex();

			tessellator.draw();

			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			buffer.pos(-0.5, 0.5, -0.5).tex(0, 0).endVertex();
			buffer.pos(-0.5, -0.5, -0.5).tex(0, 1).endVertex();
			buffer.pos(0.5, -0.5, -0.5).tex(1, 1).endVertex();
			buffer.pos(0.5, 0.5, -0.5).tex(1, 0).endVertex();

			tessellator.draw();

			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			GlStateManager.depthMask(true);
			GlStateManager.enableCull();

			Arcanist.pop();

		}
	}

	// The reason this is a boolean check is that actually returning a spell presents a problem: players can cast two
	// continuous spells at once, one via commands and one via an item, so which do you choose? Since the main point was
	// to check for specific spells, it seems more useful to do it this way.
	public static Spell getCasting(EntityLivingBase caster){

		if(caster instanceof EntityPlayer){

			WizardData data = WizardData.get((EntityPlayer)caster);

			if(data != null && data.currentlyCasting() != Spells.none) return data.currentlyCasting();

			if(caster.isHandActive() && caster.getItemInUseMaxCount() >= 0){

				ItemStack stack = caster.getHeldItem(caster.getActiveHand());

				if(stack.getItem() instanceof ISpellCastingItem && ((ISpellCastingItem)stack.getItem()).getCurrentSpell(stack) != Spells.none
				&& caster.getItemInUseCount() > ((ISpellCastingItem)stack.getItem()).getCurrentSpell(stack).getChargeup()){
					// Don't do this, it interferes with stuff! We effectively already tested this with caster.isHandActive() anyway
//						&& ((ISpellCastingItem)stack.getItem()).canCast(stack, spell, (EntityPlayer)caster,
//						EnumHand.MAIN_HAND, 0, new SpellModifiers())){
					return ((ISpellCastingItem)stack.getItem()).getCurrentSpell(stack);
				}
			}

		}else if(caster instanceof ISpellCaster){
			if(((ISpellCaster)caster).getContinuousSpell() != Spells.none) return ((ISpellCaster)caster).getContinuousSpell();
		}

		return Spells.none;
	}


}
