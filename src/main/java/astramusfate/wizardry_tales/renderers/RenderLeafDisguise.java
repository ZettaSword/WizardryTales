package astramusfate.wizardry_tales.renderers;

import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.registry.TalesEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod.EventBusSubscriber()
public class RenderLeafDisguise {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderLeafDisguiseEventStart(RenderPlayerEvent.Pre event){
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        if(world.isRemote) {
            boolean hide = Alchemy.hasPotion(event.getEntityPlayer(), TalesEffects.leaf_disguise) && player.isSneaking();
            if (hide) {
                GlStateManager.pushMatrix();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE); // GhoOOoOostly OooOOOo00oOOo
                GlStateManager.color(1f, 1f, 1f, 0.5f);
            }
        }
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderLeafDisguiseEventEnd(RenderPlayerEvent.Post event){
        EntityPlayer player = event.getEntityPlayer();
        World world = player.getEntityWorld();
        if(world.isRemote) {
            boolean hide = Alchemy.hasPotion(event.getEntityPlayer(), TalesEffects.leaf_disguise) && player.isSneaking();
            if (hide) {
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.popMatrix();
            }
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void onRenderHandsEvent(RenderHandEvent event){
        EntityPlayer player = Minecraft.getMinecraft().player;
        World world = player.getEntityWorld();
        if(world.isRemote) {
            boolean hide = Alchemy.hasPotion(player, TalesEffects.leaf_disguise) && player.isSneaking();
            if (hide) {
                GlStateManager.pushMatrix();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE); // GhoOOoOostly OooOOOo00oOOo
                GlStateManager.color(1f, 1f, 1f, 0.75f);
                //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
                GlStateManager.popMatrix();
            }
        }
    }


}

