package astramusfate.wizardry_tales.renderers.artefacts;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.items.TalesArtefact;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.util.RayTracer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

/** This part of code is taken and modified from LagGoggles mod, licensed by GNU GENERAL PUBLIC LICENSE
 Version 3 (which my mod currently is using by the way) **/
@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class RenderTenebriaCrown {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onDraw(RenderWorldLastEvent event){
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;
        List<ItemArtefact> artefacts = ItemArtefact.getActiveArtefacts(player);
        artefacts.removeIf(a -> a != TalesItems.tenebria_crown);
        if (artefacts.isEmpty()) return; // Making sure there is Tenebria crown artefact is worn or in active slots!

        float partialTicks = event.getPartialTicks();
        double pX = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
        double pY = player.prevPosY + (player.posY - player.prevPosY) * partialTicks;
        double pZ = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;

        /* Prepare */
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        GL11.glPushMatrix();
        GL11.glTranslated(-pX,-pY,-pZ);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glHint( GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST );

        RayTraceResult rayTrace = Arcanist.rayTraceEntity(player.world, player,
                player.getAttributeMap().getAttributeInstance(EntityPlayer.REACH_DISTANCE).getAttributeValue(), false,
                e -> {String text = "examplemod:no_such";
                    ResourceLocation location = EntityList.getKey(e);
                    if (location != null) text = location.toString();
                    return Arrays.asList(Tales.addon.tenebriaCrownBlacklist).contains(text);});
        if(rayTrace != null && rayTrace.entityHit != null) {
            drawEntityTags(rayTrace.entityHit, partialTicks);
        }

        /* Restore settings */
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glPopMatrix();
        GL11.glPopAttrib();
    }

    private static synchronized void drawEntityTags(Entity entity, float partialTicks){
        /* ENTITIES */
        RenderManager RENDER_MANAGER = Minecraft.getMinecraft().getRenderManager();
        FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
        GL11.glPushMatrix();

        double pXe;
        double pYe;
        double pZe;

        if(entity.isDead) {
            pXe = entity.posX;
            pYe = entity.posY;
            pZe = entity.posZ;
        }else{
            pXe = entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks;
            pYe = entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks;
            pZe = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks;
        }

        GL11.glTranslated(pXe, pYe + 2.3D, pZe);
        GL11.glRotated(-RENDER_MANAGER.playerViewY, 0.0, 1.0, 0.0);
        GL11.glRotated(RENDER_MANAGER.playerViewX, 1.0, 0.0, 0.0);
        /* Flip it! */
        GL11.glScaled(-0.015, -0.015, 0.015);
        // Rendering Entity id

        GL11.glColor4d(ArcaneColor.VIOLET.getRed()/255F,ArcaneColor.VIOLET.getGreen()/255F,ArcaneColor.VIOLET.getBlue()/255F, 0.6);

        GL11.glBegin(GL11.GL_QUADS);
        String text = "";
        ResourceLocation location = EntityList.getKey(entity);
        if (location != null) text = location.toString();
        if(entity.isDead){
            text = text + " (" + entity.getName() + ")";
        }
        int width_plus_2 = FONT_RENDERER.getStringWidth(text) + 2;
        int height_div_2_plus_1 = (FONT_RENDERER.FONT_HEIGHT/2) + 1;
        GL11.glVertex3d(0           ,-height_div_2_plus_1,0);
        GL11.glVertex3d(0           , height_div_2_plus_1 - 1,0);
        GL11.glVertex3d(width_plus_2, height_div_2_plus_1 - 1,0);
        GL11.glVertex3d(width_plus_2,-height_div_2_plus_1,0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4d(0,0,0,1);

        FONT_RENDERER.drawString(text, 1, -FONT_RENDERER.FONT_HEIGHT/2, 0xc0c0c0);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Rendering entity health.

        GL11.glColor4d(ArcaneColor.VIOLET.getRed()/255F,ArcaneColor.VIOLET.getGreen()/255F,ArcaneColor.VIOLET.getBlue()/255F, 0.6);
        GL11.glTranslated(0, 10, 0);
        GL11.glBegin(GL11.GL_QUADS);
        text = "Not Alive.";
        if (entity instanceof EntityLivingBase && !entity.isDead){
            text = "HP: " + Math.round(((EntityLivingBase) entity).getHealth()) + "/" + Math.round(((EntityLivingBase) entity).getMaxHealth());
        }
        width_plus_2 = FONT_RENDERER.getStringWidth(text) + 2;
        height_div_2_plus_1 = (FONT_RENDERER.FONT_HEIGHT/2) + 1;
        GL11.glVertex3d(0           ,-height_div_2_plus_1,0);
        GL11.glVertex3d(0           , height_div_2_plus_1 - 1,0);
        GL11.glVertex3d(width_plus_2, height_div_2_plus_1 - 1,0);
        GL11.glVertex3d(width_plus_2,-height_div_2_plus_1,0);
        GL11.glEnd();

        GL11.glEnable(GL11.GL_TEXTURE_2D);

        GL11.glColor4d(1.0,1.0,1.0,1);

        FONT_RENDERER.drawString(text, 1, -FONT_RENDERER.FONT_HEIGHT/2, 0xc0c0c0);

        GL11.glColor4d(0,0,0,1);

        GL11.glDisable(GL11.GL_TEXTURE_2D);

        // Rendering entity lifetime.

        GL11.glColor4d(ArcaneColor.VIOLET.getRed()/255F,ArcaneColor.VIOLET.getGreen()/255F,ArcaneColor.VIOLET.getBlue()/255F, 0.6);
        GL11.glTranslated(0, 10, 0);
        GL11.glBegin(GL11.GL_QUADS);
        text = "";
        NBTTagCompound nbt = new NBTTagCompound();
        nbt = entity.writeToNBT(nbt);
        if ((entity.getEntityData().hasKey("lifetime") || nbt.hasKey("lifetime")) && !entity.isDead){
            if (entity.getEntityData().hasKey("lifetime")){
                int time = (int)(Math.round(Solver.asSeconds(entity.getEntityData().getInteger("lifetime") - entity.ticksExisted)));
                if (time < 0) time = -1;
                text = "Lifetime: " + time;
            } else if (nbt.hasKey("lifetime")) {
                int time = (int)(Math.round(Solver.asSeconds(nbt.getInteger("lifetime") - entity.ticksExisted)));
                if (time < 0) time = -1;
                text = "Lifetime: " + time;
            }
        }
        if (!text.isEmpty()) {
            width_plus_2 = FONT_RENDERER.getStringWidth(text) + 2;
            height_div_2_plus_1 = (FONT_RENDERER.FONT_HEIGHT / 2) + 1;
            GL11.glVertex3d(0, -height_div_2_plus_1, 0);
            GL11.glVertex3d(0, height_div_2_plus_1 - 1, 0);
            GL11.glVertex3d(width_plus_2, height_div_2_plus_1 - 1, 0);
            GL11.glVertex3d(width_plus_2, -height_div_2_plus_1, 0);
            GL11.glEnd();

            GL11.glEnable(GL11.GL_TEXTURE_2D);

            GL11.glColor4d(1.0, 1.0, 1.0, 1);

            FONT_RENDERER.drawString(text, 1, -FONT_RENDERER.FONT_HEIGHT / 2, 0xc0c0c0);

            GL11.glColor4d(0, 0, 0, 1);

            GL11.glDisable(GL11.GL_TEXTURE_2D);
        }else{
            GL11.glEnd();
        }

        GL11.glPopMatrix();
    }
}
