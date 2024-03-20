package astramusfate.wizardry_tales.renderers;


import astramusfate.wizardry_tales.api.Arcanist;
import astramusfate.wizardry_tales.data.Tales;
import com.google.common.collect.ImmutableMap;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.util.Box;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class RenderTesterPerk {
    private static final Map<String, String> testers = new HashMap<String, String>(){
    };
    private static final Map<String, String> testers_UUID = new HashMap<>();

    private static final Map<String, Box<String>> MAP = ImmutableMap.<String, Box<String>>builder()
            .put("4b29263e-007b-48ef-b3e6-ce86cca989e9", new Box<>("u_magic")) // Electroblob's
            .put("eb587ced-6a74-49fa-8705-f663c1849973", new Box<>("u_fire")) // ZettaSword
            .put("3d3e053a-6abb-4a5a-8bc5-31b05948b44d", new Box<>("u_necromancy")) // LastAshenLord
            .put("f9845e54-1168-4052-acb5-d0d38a71e1e2", new Box<>("u_necromancy")) // RichardStroh39
            .put("7319cf69-b44a-4cc3-b5ec-996d617e0a93", new Box<>("u_earth")) // Hat Bot
            .put("1e6d4b62-d8a4-41c4-9d38-28bcb8c55f43", new Box<>("u_fire")) // Hat Bot2
            .put("theismarius", new Box<>("u_fire"))
            .put("Tolki3n", new Box<>("u_necromancy"))
            .put("deadinpeace", new Box<>("u_necromancy"))
            .put("OhneSprud3l", new Box<>("u_necromancy"))
            .put("Galv_End", new Box<>("u_necromancy"))
            .put("SaltyyCoffee", new Box<>("u_necromancy"))
            .put("Konras", new Box<>("u_necromancy"))
            .put("WerelWolfs", new Box<>("vampires_2"))
            .put("Jaden", new Box<>("u_lightning"))
            .build();

    /** Returns the given player's donor perk element, or null if they are not a donor (may also be null for donors if
     * they have disabled the perk). */
    public static String getPerk(EntityPlayer player){
        Box<String> box = MAP.get(player.getUniqueID().toString());
        if (box == null){
            box = MAP.get(player.getName());
        }
        return box == null ? null : box.get();
    }

    // No first person in here due it would be annoying to see circle above your head ._.

    public static void updateMap(){
        testers.put("theismarius", "u_fire");
        testers.put("Tolki3n", "u_necromancy");
        testers.put("deadinpeace", "u_necromancy");
        testers.put("OhneSprud3l", "u_necromancy");
        testers.put("Galv_End", "u_necromancy");
        testers.put("SaltyyCoffee", "u_necromancy");
        testers.put("Konras", "u_necromancy");
        testers.put("WerelWolfs", "vampires_2");
        testers.put("Jaden", "u_lightning");

        // RichardStroh39
        testers_UUID.put("f9845e54-1168-4052-acb5-d0d38a71e1e2", "u_necromancy");

        // LastAshenLord
        testers_UUID.put("3d3e053a-6abb-4a5a-8bc5-31b05948b44d", "u_necromancy");
        // ZettaSword
        testers_UUID.put("eb587ced-6a74-49fa-8705-f663c1849973", "u_fire");

        // Hat Bot
        testers_UUID.put("7319cf69-b44a-4cc3-b5ec-996d617e0a93", "u_earth");
        // Hat Bot 2
        testers_UUID.put("1e6d4b62-d8a4-41c4-9d38-28bcb8c55f43", "u_fire");
    }

    public static String getElement(String name){
        if(testers.containsKey(name)) return testers.get(name);
        if(testers_UUID.containsKey(name))return testers_UUID.get(name);
        return "magic";
    }

    // Third person
    @SideOnly(Side.CLIENT)
    @SubscribeEvent()
    public static void onRenderPlayerPostEvent(RenderPlayerEvent.Post event){
        EntityPlayer player = event.getEntityPlayer();

        if(getPerk(player) != null){
            Arcanist.push();

            GlStateManager.enableBlend();
            GlStateManager.disableLighting();
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            GlStateManager.depthMask(false);
            GlStateManager.disableCull();

            //GlStateManager.enableBlend();
            //GlStateManager.disableLighting();

            GlStateManager.translate(event.getX(), event.getY() + player.getEyeHeight()+0.38, event.getZ());

            // GlStateManager.rotate(-entityplayer.rotationYawHead, 0, 1, 0);
            // GlStateManager.rotate(180, 1, 0, 0);

        Minecraft.getMinecraft().renderEngine.bindTexture(Arcanist.getCircle(getPerk(player)));
            GlStateManager.color(1f,1f,1f,1f);
            GlStateManager.enableRescaleNormal();

            GlStateManager.rotate(-90, 1, 0, 0);
            GlStateManager.rotate(player.ticksExisted * -2, 0, 0, 1);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();

            //GlStateManager.translate(0, , 0);
            float scale = 0.35f;
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
            GlStateManager.depthMask(true);
            GlStateManager.enableCull();
            //GlStateManager.disableRescaleNormal();
            Arcanist.pop();
        }
    }
}
