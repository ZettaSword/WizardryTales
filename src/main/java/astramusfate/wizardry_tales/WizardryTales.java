package astramusfate.wizardry_tales;

import astramusfate.wizardry_tales.api.Librarian;
import astramusfate.wizardry_tales.api.wizardry.TalesArtemis;
import astramusfate.wizardry_tales.data.PacketMagic;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.events.RaceListener;
import astramusfate.wizardry_tales.events.StatsListener;
import astramusfate.wizardry_tales.events.VisualEffects;
import astramusfate.wizardry_tales.registry.*;
import astramusfate.wizardry_tales.renderers.RenderTesterPerk;
import astramusfate.wizardry_tales.items.TalesWandUpgrade;
import astramusfate.wizardry_tales.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = WizardryTales.MODID, name = WizardryTales.NAME, version = WizardryTales.VERSION, dependencies =
        "required-after:ebwizardry@[4.3.11,);required-after:forge@[14.23.5.2847,);required-after:baubles@[1.5.2,);" +
                "required-after:geckolib3@[3.0.3,);required-after:patchouli@[1.0-23.6,);required-after:potioncore@[1.9,);before:vampirism@[1.3.7,)")
public class WizardryTales {
    public static final String MODID = "wizardry_tales";
    public static final String NAME = "Wizardry Tales";
    public static final String VERSION = "2.2.4";

    @SidedProxy(clientSide = "astramusfate.wizardry_tales.proxy.ClientProxy",
            serverSide = "astramusfate.wizardry_tales.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(WizardryTales.MODID)
    public static WizardryTales instance;

    public static Logger log;

    public static boolean canCompat(String id){
        return Loader.isModLoaded(id);
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        proxy.preInit(event);

        log = event.getModLog();
        ConfigManager.sync(MODID, Config.Type.INSTANCE);

        if(Tales.effects.enabled) {
            MinecraftForge.EVENT_BUS.register(new VisualEffects());
        }

        TalesLoot.preInit();
        TalesMaps.preInit();
        TalesBlocks.registerTileEntities();
        Librarian.preInitBookShelfModelTextures();

        TalesArtemis.init();
        if(TalesArtemis.enabled()){
            MinecraftForge.EVENT_BUS.register(new RaceListener());
        }
        MinecraftForge.EVENT_BUS.register(new StatsListener());


        proxy.registerResourceReloadListeners();
        proxy.registerKeyBindings();

        GeckoLib.initialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        proxy.registerParticles();

        RenderTesterPerk.updateMap();
        TalesWandUpgrade.init();

        PacketMagic.init();
        TalesRecipes.init();
        Librarian.InitBookshelfItems();

        TalesWorldGen.init();
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
        proxy.initialiseLayers();
        //ConfigManager.sync(MODID, Config.Type.INSTANCE);
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        proxy.serverStarting(event);
    }
}
