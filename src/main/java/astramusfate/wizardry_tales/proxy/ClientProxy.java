package astramusfate.wizardry_tales.proxy;


import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.wizardry.ParticlesCreator;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.SoulProvider;
import astramusfate.wizardry_tales.data.packets.*;
import astramusfate.wizardry_tales.entity.construct.ParticleRing;
import astramusfate.wizardry_tales.renderers.GuiTalesSpellDisplay;
import astramusfate.wizardry_tales.renderers.layers.*;
import astramusfate.wizardry_tales.registry.TalesBlocks;
import astramusfate.wizardry_tales.registry.TalesEntities;
import electroblob.wizardry.client.gui.handbook.GuiWizardHandbook;
import electroblob.wizardry.client.particle.*;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends CommonProxy {
    public static final KeyBinding CAST_SPELL = new KeyBinding("key." + WizardryTales.MODID + ".cast_spell", KeyConflictContext.IN_GAME, Keyboard.KEY_G, "key.categories." + WizardryTales.MODID);
    public static final KeyBinding ABILITY_1 = new KeyBinding("key." + WizardryTales.MODID + ".ability_1", KeyConflictContext.IN_GAME, Keyboard.KEY_NUMPAD1, "key.categories." + WizardryTales.MODID);

    public static final KeyBinding MANUAL_CHANT = new KeyBinding("key." + WizardryTales.MODID + ".manual_chant", KeyConflictContext.IN_GAME, Keyboard.KEY_R, "key.categories." + WizardryTales.MODID);
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        TalesEntities.RegisterRenderers();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        TalesBlocks.initRenders();

    }

    @Override
    public void handleSoulMana(PacketSoulMana message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null) {
            soul.setMP(message.mana);
        }
    }

    @Override
    public void handleSoulStat(PacketSyncStat message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null) {
            soul.setStat(player, message.id, message.stat);
        }
    }

    @Override
    public void handleSoulMaxMana(PacketSoulMaxMana message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null) {
            soul.setMaxMP(message.maxMana);
        }
    }

    @Override
    public void handleCastingRingCooldown(PacketCastingRingCooldown message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            soul.setCooldown(player, message.cooldown);
        }
    }

    @Override
    public void handleDataToAll(PacketDataToAll message) {

    }

    @Override
    public void handleSpellsLearning(PacketSyncLearning message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            if (Tales.addon.learning) soul.setLearnedSpells(player, message.spells);
        }
    }

    @Override
    public void handleLearnSpell(PacketLearnSpell message) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            if (Tales.addon.learning) soul.learnSpell(player, message.spell);
        }
    }

    @Override
    public void handleSyncMode(PacketSyncMode message){
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            soul.setMode(player, message.mode);
        }
    }

    @Override
    public void handleSyncRace(PacketSyncRace message){
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            soul.setRace(player, message.race);
        }
    }

    public void handleAbilitySwitch(PacketAbilityMode message){
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if(soul != null){
            soul.setMode(player, message.ability);
        }
    }


    @Override
    public void initialiseLayers() {
        TalesLayerTiledOverlay.initialiseLayers(LayerTangled::new);
        TalesLayerTiledOverlay.initialiseLayers(LayerLeafDisguise::new);
        TalesLayerTiledOverlay.initialiseLayers(LayerBurningDisease::new);
        TalesLayerTiledOverlay.initialiseLayers(LayerStormArmor::new);
    }

    @Override
    public void registerResourceReloadListeners(){
        IResourceManager manager = Minecraft.getMinecraft().getResourceManager();
        if(manager instanceof IReloadableResourceManager){
            ((IReloadableResourceManager)manager).registerReloadListener(GuiTalesSpellDisplay::loadSkins);
        }
    }

    @Override
    public void registerKeyBindings(){
        ClientRegistry.registerKeyBinding(CAST_SPELL);
        ClientRegistry.registerKeyBinding(ABILITY_1);
        ClientRegistry.registerKeyBinding(MANUAL_CHANT);
    }

    @Override
    public void registerParticles(){
        // I'll be a good programmer and use the API method rather than the one above. Lead by example, as they say...
        ParticleWizardry.registerParticle(ParticlesCreator.Type.RING, ParticleRing::new);
    }

    @Override
    public void registerExtraHandbookContent() {
        GuiWizardHandbook.registerAddonHandbookContent(WizardryTales.MODID);
    }

    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
    }
}
