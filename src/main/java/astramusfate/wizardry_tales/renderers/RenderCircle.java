package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.entity.construct.EntityMagicScaled;
import electroblob.wizardry.client.DrawingUtils;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class RenderCircle extends Render<EntityMagicCircle> {

	private final float rotationSpeed;

	public RenderCircle(RenderManager renderManager, float rotationSpeed){
		super(renderManager);
		this.rotationSpeed = rotationSpeed;
	}

	@Override
	public void doRender(@Nonnull EntityMagicCircle entity, double x, double y, double z, float entityYaw, float partialTicks){

		Arcanist.push();

		Arcanist.startCircleSettings();
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float yOffset = 0;

		GlStateManager.translate((float)x, (float)y + yOffset, (float)z);

		this.bindTexture(Arcanist.getCircle(entity.getLocation()));
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

		Arcanist.endCircleSettings();
		Arcanist.pop();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicCircle entity){
		return null;
	}

}
