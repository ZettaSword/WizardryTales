package astramusfate.wizardry_tales.api;

import astramusfate.wizardry_tales.WizardryTales;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

/** Arcanist - Class to help with different rendering, such as shapes render, circles render, and etc.
 * <br><br/> scale() - multiplies current scale on given scale; So if: scale(0.5); scale(1.5); Then it will will scale [0.5*1.5];
 * <br><br/> GlStateManager.translate(given) - pluses to current xyz given xyz; [x+given.x, y+given.y, z+given.z];
 * **/
public class Arcanist {

    public static void startCircleSettings(){
        GlStateManager.enableBlend();
        GlStateManager.disableLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
        //GlStateManager.depthMask(false);
        GlStateManager.disableCull();
    }

    public static void endCircleSettings(){
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
       // GlStateManager.depthMask(true);
        GlStateManager.enableCull();
    }

    public static void alpha(float alpha){
        GlStateManager.color(1f, 1, 1f, alpha);
    }

    public static void drawCircle(Tessellator tessellator, BufferBuilder buffer){
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
    }

    public static void drawCircleCurved(Tessellator tessellator, BufferBuilder buffer){
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
    }

    public static void drawCircleOnlyCeil(Tessellator tessellator, BufferBuilder buffer){
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

        buffer.pos(-0.5, 0.5, 0.01).tex(0, 0).endVertex();
        buffer.pos(0.5, 0.5, 0.01).tex(1, 0).endVertex();
        buffer.pos(0.5, -0.5, 0.01).tex(1, 1).endVertex();
        buffer.pos(-0.5, -0.5, 0.01).tex(0, 1).endVertex();

        tessellator.draw();
    }

    public static void move(double x, double y, double z){
        GlStateManager.translate(x,y,z);
    }

    public static void move(float x, float y, float z){
        GlStateManager.translate(x,y,z);
    }

    /** Allows to make object go up & down, or so on **/
    public static double makeCurve(int ticks, float amplitude, float frequency){
        frequency /= 100f;
       return Math.sin(ticks * Math.PI * frequency) * amplitude;
    }

    /** Pushes Matrix **/
    public static void push(){
        GlStateManager.pushMatrix();
    }

    /** Pops Matrix **/
    public static void pop(){
        GlStateManager.popMatrix();
    }

    /** Scales whole [xyz] of object **/
    public static void scale(double scale){
        GlStateManager.scale(scale,scale,scale);
    }

    /** Rotates object by [xyz] axis **/
    public static void rotate(float angle, float x, float y, float z){
        GlStateManager.rotate(angle, x, y, z);
    }

    /** Returns Resource Location of [Circle] **/
    public static ResourceLocation getCircle(String name){
        return new ResourceLocation(WizardryTales.MODID, "textures/entity/sigils/circle_" + name +".png");
    }

    /** Returns Resource Location of [Circle] for modded **/
    public static ResourceLocation getCircle(String modid,String name){
        return new ResourceLocation(modid, "textures/entity/sigils/circle_" + name +".png");
    }

    /** Creates a shade, so entity/layer will render like obvious, without Glowing! **/
    public static void shade(Entity entity){
        int j = entity.getBrightnessForRender();
        int k = j % 65536;
        int l = j / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) k, (float) l);
    }
}
