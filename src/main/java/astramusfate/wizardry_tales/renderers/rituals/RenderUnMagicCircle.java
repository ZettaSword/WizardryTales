package astramusfate.wizardry_tales.renderers.rituals;

import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class RenderUnMagicCircle extends Render<EntityMagicConstruct> {

	private final ResourceLocation texture;
	private final float rotationSpeed;

	public RenderUnMagicCircle(RenderManager renderManager, ResourceLocation texture, float rotationSpeed){
		super(renderManager);
		this.texture = texture;
		this.rotationSpeed = rotationSpeed;
	}

	@Override
	public void doRender(@Nonnull EntityMagicConstruct entity, double x, double y, double z, float entityYaw, float partialTicks){

		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.disableLighting();
		OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float yOffset = 0;

		GlStateManager.translate((float)x, (float)y + yOffset, (float)z);

		this.bindTexture(texture);
		GlStateManager.enableRescaleNormal();

		GlStateManager.rotate(-90, 1, 0, 0);

		// Healing aura rotates slowly
		if(rotationSpeed != 0) GlStateManager.rotate(entity.ticksExisted * rotationSpeed, 0, 0, 1);

		float s = DrawingUtils.smoothScaleFactor(entity.lifetime, entity.ticksExisted, partialTicks, 10, 10);
		GlStateManager.color(1f, 1, 1f, s);
		float scale = entity.width;
		GlStateManager.scale(scale, scale, scale);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
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
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicConstruct entity){
		return null;
	}

}
