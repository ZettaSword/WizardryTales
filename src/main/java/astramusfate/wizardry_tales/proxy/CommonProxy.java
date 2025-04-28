package astramusfate.wizardry_tales.proxy;

import astramusfate.wizardry_tales.data.cap.*;
import astramusfate.wizardry_tales.data.commands.CommandAddMana;
import astramusfate.wizardry_tales.data.commands.CommandCheckMana;
import astramusfate.wizardry_tales.data.commands.CommandSetMana;
import astramusfate.wizardry_tales.data.commands.CommandSetMaxMana;
import astramusfate.wizardry_tales.data.packets.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        CapabilityManager.INSTANCE.register(ISoul.class, new SoulStorage(), Soul::new);
        //RuneMagic.regServer();
        //MinecraftForge.EVENT_BUS.register(new CapabilityHandler());
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event) {

    }

    public void serverStarting(FMLServerStartingEvent event) {
       event.registerServerCommand(new CommandCheckMana());
       event.registerServerCommand(new CommandSetMana());
       event.registerServerCommand(new CommandSetMaxMana());
       event.registerServerCommand(new CommandAddMana());
    }

    public void initialiseLayers(){}

    public void handleSoulMana(PacketSoulMana message){}
    public void handleSoulStat(PacketSyncStat message){}
    public void handleSoulMaxMana(PacketSoulMaxMana message){}
    public void handleCastingRingCooldown(PacketCastingRingCooldown message){}
    public void handleDataToAll(PacketDataToAll message){}
    public void handleSpellsLearning(PacketSyncLearning message){}
    public void handleLearnSpell(PacketLearnSpell message){}

    public void handleSyncMode(PacketSyncMode message){}
    public void handleSyncRace(PacketSyncRace message){}
    public void handleAbilitySwitch(PacketAbilityMode message){}

    public void registerResourceReloadListeners(){}

    public void registerKeyBindings(){}
    public void registerParticles(){}
    public void registerExtraHandbookContent() {}

    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.getServerHandler().player;
    }
}
