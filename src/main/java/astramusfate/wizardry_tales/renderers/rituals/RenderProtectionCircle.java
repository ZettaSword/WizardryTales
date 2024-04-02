package astramusfate.wizardry_tales.renderers.rituals;

import astramusfate.wizardry_tales.api.Arcanist;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.entity.construct.EntityMagicConstruct;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class RenderProtectionCircle extends Render<EntityMagicConstruct> {

	private final ResourceLocation texture;
	private final float rotationSpeed;

	public RenderProtectionCircle(RenderManager renderManager, ResourceLocation texture, float rotationSpeed){
		super(renderManager);
		this.texture = texture;
		this.rotationSpeed = rotationSpeed;
	}

	@Override
	public void doRender(@Nonnull EntityMagicConstruct entity, double x, double y, double z, float entityYaw, float partialTicks){

		Arcanist.push();

		Arcanist.startCircleSettings();
		//GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		float yOffset = 0;

		GlStateManager.translate((float)x, (float)y + yOffset, (float)z);

		GlStateManager.enableRescaleNormal();

		GlStateManager.rotate(-90, 1, 0, 0);

		//Due to this ^

		if(rotationSpeed != 0) GlStateManager.rotate(entity.ticksExisted * rotationSpeed, 0, 0, 1);

		float s = DrawingUtils.smoothScaleFactor(entity.lifetime, entity.ticksExisted, partialTicks, 10, 10);
		float scale = entity.width;
		Arcanist.scale(scale);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();
		this.bindTexture(Arcanist.getCircle("construct0"));

		Arcanist.push(); // We do not need to have color mixed

		GlStateManager.color(255, 245, 196, 0.3f);
		Arcanist.drawCircle(tessellator, buffer);

		Arcanist.pop();

		GlStateManager.color(1f, 1f, 1f, s); // Color now be normal all time, due we not use color-required textures

		Arcanist.push(); // We also do not want to offset others circles position

		Arcanist.move(0, 0, 0.02f);
		float amplitude = 0.02f;
		Arcanist.move(0, 0, Arcanist.makeCurve(entity.ticksExisted, amplitude, 1f));
		this.bindTexture(Arcanist.getCircle("construct1"));
		Arcanist.drawCircle(tessellator, buffer);

		Arcanist.pop();


		// We are binding Usual Circle now (without a symbol)
		this.bindTexture(Arcanist.getCircle("u_healing"));

		GlStateManager.translate(0f, 0f, 0.1f);
		Arcanist.scale(0.2);
		Arcanist.drawCircle(tessellator, buffer);

		GlStateManager.translate(0f, 0f, 0.1f);
		Arcanist.scale(2);
		Arcanist.drawCircle(tessellator, buffer);

		GlStateManager.translate(0f, 0f, 0.05f);
		Arcanist.scale(1.5);
		Arcanist.drawCircle(tessellator, buffer);

		Arcanist.endCircleSettings();

		Arcanist.pop();
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMagicConstruct entity){
		return null;
	}

}
