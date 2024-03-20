package astramusfate.wizardry_tales.renderers;


import astramusfate.wizardry_tales.api.Arcanist;
import electroblob.wizardry.registry.WizardryPotions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderWard {
    private static final ResourceLocation TEXTURE = Arcanist.getCircle("u_healing");

    // No first person in here because you can never see the wings on your back!

    // Third person
    @SubscribeEvent
    public static void onRenderPlayerEvent(RenderPlayerEvent.Post event){

        EntityPlayer player = event.getEntityPlayer();

        if(player.isPotionActive(WizardryPotions.ward) && !player.isInvisible()){

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

            GlStateManager.translate(event.getX(), event.getY() + player.getEyeHeight()+0.35, event.getZ());

            // GlStateManager.rotate(-entityplayer.rotationYawHead, 0, 1, 0);
            // GlStateManager.rotate(180, 1, 0, 0);

            Minecraft.getMinecraft().renderEngine.bindTexture(TEXTURE);
            GlStateManager.color(1f,1f,1f,1f);
            GlStateManager.rotate(-90, 1, 0, 0);
            GlStateManager.rotate(player.ticksExisted * -2, 0, 0, 1);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            //GlStateManager.translate(0, , 0);
            float scale = 0.5f;
            GlStateManager.scale(scale, scale, scale);
            //GlStateManager.rotate(-200 - 20 * MathHelper.sin((player.ticksExisted + event.getPartialRenderTick()) * 0.3f), 0, 1, 0);

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            buffer.pos(-0.5, 0.5, 0.01).tex(0, 0).endVertex();
            buffer.pos(0.5, 0.5, 0.01).tex(1, 0).endVertex();
            buffer.pos(0.5, -0.5, 0.01).tex(1, 1).endVertex();
            buffer.pos(-0.5, -0.5, 0.01).tex(0, 1).endVertex();

            tessellator.draw();

            buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

            buffer.pos(-0.5, 0.5, 0.01).tex(0, 0).endVertex();
            buffer.pos(-0.5, -0.5, 0.01).tex(0, 1).endVertex();
            buffer.pos(0.5, -0.5, 0.01).tex(1, 1).endVertex();
            buffer.pos(0.5, 0.5, 0.01).tex(1, 0).endVertex();

            tessellator.draw();

            GlStateManager.disableBlend();
            GlStateManager.enableLighting();
            //GlStateManager.disableRescaleNormal();
            GlStateManager.popMatrix();
        }
    }
}
