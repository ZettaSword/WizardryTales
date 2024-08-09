package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.entity.living.EntityLich;
import astramusfate.wizardry_tales.renderers.models.ModelLich;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerElytra;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

@SideOnly(Side.CLIENT)
public class RenderLich extends RenderLivingBase<EntityLich> {
    private static final ResourceLocation SKELETON_TEXTURES = new ResourceLocation("textures/entity/skeleton/skeleton.png");

    public RenderLich(RenderManager p_i46143_1_) {
        super(p_i46143_1_, new ModelLich(), 0.5F);
        this.addLayer(new LayerHeldItem(this));
        this.addLayer(new LayerBipedArmor(this) {
            protected void initArmor() {
                this.modelLeggings = new ModelLich(0.5F, true);
                this.modelArmor = new ModelLich(1.0F, true);
            }
        });
        this.addLayer(new LayerElytra(this));
    }

    public void transformHeldFull3DItemLayer() {
        GlStateManager.translate(0.09375F, 0.1875F, 0.0F);
    }

    protected ResourceLocation getEntityTexture(@Nonnull EntityLich p_110775_1_) {
        return SKELETON_TEXTURES;
    }

    @Override
    public void doRender(EntityLich p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
        double d0 = p_76986_4_;
        if (p_76986_1_.isSneaking()) {
            d0 = p_76986_4_ - 0.125;
        }
        this.setModelVisibilities(p_76986_1_);
        super.doRender(p_76986_1_, p_76986_2_, d0, p_76986_6_, p_76986_8_, p_76986_9_);
    }

    private void setModelVisibilities(EntityLich p_177137_1_) {
        ModelLich modelplayer = (ModelLich) this.getMainModel();
        if (Minecraft.getMinecraft().playerController.isSpectator()) {
            modelplayer.setVisible(false);
            modelplayer.bipedHead.showModel = WizardryTales.isShowingHead();
        }else {
            Entity owner = p_177137_1_.getOwner();
            ItemStack itemstack, itemstack1;
            if (!(owner instanceof EntityLivingBase)) {
                itemstack = p_177137_1_.getHeldItemMainhand();
                itemstack1 = p_177137_1_.getHeldItemOffhand();
            }else{
                itemstack = ((EntityLivingBase) owner).getHeldItemMainhand();
                itemstack1 = ((EntityLivingBase) owner).getHeldItemOffhand();
            }
            modelplayer.setVisible(true);
            modelplayer.bipedHead.showModel = WizardryTales.isShowingHead();
            modelplayer.isSneak = p_177137_1_.isSneaking();
            ModelBiped.ArmPose modelbiped$armpose = ModelBiped.ArmPose.EMPTY;
            ModelBiped.ArmPose modelbiped$armpose1 = ModelBiped.ArmPose.EMPTY;
            EnumAction enumaction1;
            if (!itemstack.isEmpty()) {
                modelbiped$armpose = ModelBiped.ArmPose.ITEM;
                if (p_177137_1_.getItemInUseCount() > 0) {
                    enumaction1 = itemstack.getItemUseAction();
                    if (enumaction1 == EnumAction.BLOCK) {
                        modelbiped$armpose = ModelBiped.ArmPose.BLOCK;
                    } else if (enumaction1 == EnumAction.BOW) {
                        modelbiped$armpose = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (!itemstack1.isEmpty()) {
                modelbiped$armpose1 = ModelBiped.ArmPose.ITEM;
                if (p_177137_1_.getItemInUseCount() > 0) {
                    enumaction1 = itemstack1.getItemUseAction();
                    if (enumaction1 == EnumAction.BLOCK) {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BLOCK;
                    } else if (enumaction1 == EnumAction.BOW) {
                        modelbiped$armpose1 = ModelBiped.ArmPose.BOW_AND_ARROW;
                    }
                }
            }

            if (owner instanceof EntityPlayer){
                if (((EntityPlayer) owner).getPrimaryHand() == EnumHandSide.RIGHT) {
                    modelplayer.rightArmPose = modelbiped$armpose;
                    modelplayer.leftArmPose = modelbiped$armpose1;
                } else {
                    modelplayer.rightArmPose = modelbiped$armpose1;
                    modelplayer.leftArmPose = modelbiped$armpose;
                }
            }else{
                if (p_177137_1_.getPrimaryHand() == EnumHandSide.RIGHT) {
                    modelplayer.rightArmPose = modelbiped$armpose;
                    modelplayer.leftArmPose = modelbiped$armpose1;
                } else {
                    modelplayer.rightArmPose = modelbiped$armpose1;
                    modelplayer.leftArmPose = modelbiped$armpose;
                }
            }
        }
    }

    protected void applyRotations(EntityLich p_77043_1_, float p_77043_2_, float p_77043_3_, float p_77043_4_) {
        if (p_77043_1_.isEntityAlive() && p_77043_1_.isPlayerSleeping()) {
            EntityPlayer player = p_77043_1_.getOwner() instanceof EntityPlayer ? (EntityPlayer) p_77043_1_.getOwner() : null;
            if (player != null) GlStateManager.rotate(player.getBedOrientationInDegrees(), 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.getDeathMaxRotation(p_77043_1_), 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
        } else if (p_77043_1_.isElytraFlying()) {
            super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
            float f = (float)p_77043_1_.getTicksElytraFlying() + p_77043_4_;
            float f1 = MathHelper.clamp(f * f / 100.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f1 * (-90.0F - p_77043_1_.rotationPitch), 1.0F, 0.0F, 0.0F);
            Vec3d vec3d = p_77043_1_.getLook(p_77043_4_);
            double d0 = p_77043_1_.motionX * p_77043_1_.motionX + p_77043_1_.motionZ * p_77043_1_.motionZ;
            double d1 = vec3d.x * vec3d.x + vec3d.z * vec3d.z;
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (p_77043_1_.motionX * vec3d.x + p_77043_1_.motionZ * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
                double d3 = p_77043_1_.motionX * vec3d.z - p_77043_1_.motionZ * vec3d.x;
                GlStateManager.rotate((float)(Math.signum(d3) * Math.acos(d2)) * 180.0F / 3.1415927F, 0.0F, 1.0F, 0.0F);
            }
        } else {
            super.applyRotations(p_77043_1_, p_77043_2_, p_77043_3_, p_77043_4_);
        }

    }


}