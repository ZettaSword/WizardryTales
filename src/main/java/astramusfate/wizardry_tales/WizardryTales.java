package astramusfate.wizardry_tales;

import astramusfate.wizardry_tales.api.Librarian;
import astramusfate.wizardry_tales.api.wizardry.TalesArtemis;
import astramusfate.wizardry_tales.api.wizardry.TalesVampirism;
import astramusfate.wizardry_tales.compats.BotaniaRecipes;
import astramusfate.wizardry_tales.data.PacketMagic;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.events.RaceListener;
import astramusfate.wizardry_tales.events.StatsListener;
import astramusfate.wizardry_tales.events.VampirismEvents;
import astramusfate.wizardry_tales.events.VisualEffects;
import astramusfate.wizardry_tales.registry.*;
import astramusfate.wizardry_tales.renderers.RenderTesterPerk;
import astramusfate.wizardry_tales.items.TalesWandUpgrade;
import astramusfate.wizardry_tales.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
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

@Mod(modid = WizardryTales.MODID, name = WizardryTales.NAME, version = WizardryTales.VERSION, dependencies = WizardryTales.DEPENDENCIES)
public class WizardryTales {
    public static final String MODID = "wizardry_tales";
    public static final String NAME = "Wizardry Tales";
    public static final String VERSION = "2.2.7";

    public static final String DEPENDENCIES = "required-after:ebwizardry@[4.3.11,);" +
            "required-after:forge@[14.23.5.2847,);" +
            "required-after:baubles@[1.5.2,);" +
            "required-after:geckolib3@[3.0.3,);" +
            "required-after:patchouli@[1.0-23.6,);" +
            "required-after:potioncore@[1.9,);" +
            "after:botania@[r1.10-364,);" +
            "after:player_mana@[1.0.0,);" +
            "required-after:artemislib@[1.0.6,);" +
            "after:vampirism;"+
            "after:rfp2;" +
            "after:realrender;";

    @SidedProxy(clientSide = "astramusfate.wizardry_tales.proxy.ClientProxy",
            serverSide = "astramusfate.wizardry_tales.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(WizardryTales.MODID)
    public static WizardryTales instance;

    public static boolean hasPlayerMana =false;
    /** Defines if we should hide Lich and others due to first person mods **/
    public static boolean shouldHideHead = false;
    public static boolean hasRealRender = false;
    public static boolean hasAncientSpellcraft = false;

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

        hasPlayerMana =Loader.isModLoaded("player_mana");

        shouldHideHead = Loader.isModLoaded("rfp2") || Loader.isModLoaded("realrender");
        hasRealRender = Loader.isModLoaded("realrender");
        hasAncientSpellcraft = Loader.isModLoaded("ancientspellcraft");

        TalesLoot.preInit();
        TalesBlocks.registerTileEntities();
        Librarian.preInitBookShelfModelTextures();

        TalesArtemis.init();
        if(TalesArtemis.enabled()){
            MinecraftForge.EVENT_BUS.register(new RaceListener());
        }

        TalesVampirism.init();
        if(TalesVampirism.enabled()){
            MinecraftForge.EVENT_BUS.register(new VampirismEvents());
        }

        MinecraftForge.EVENT_BUS.register(new StatsListener());


        proxy.registerResourceReloadListeners();
        proxy.registerKeyBindings();
        proxy.registerExtraHandbookContent();

        GeckoLib.initialize();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        TalesMaps.preInit();
        proxy.init(event);
        proxy.registerParticles();

        RenderTesterPerk.updateMap();
        TalesWandUpgrade.init();

        PacketMagic.init();
        TalesRecipes.init();
        Librarian.InitBookshelfItems();

        if(canCompat("botania") && Tales.compat.botania){
            BotaniaRecipes.init();
        }


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

    /** Returns False if we should hide head, returns True if we should NOT hide head, and instead show it. **/
    public static boolean isShowingHead(){
        return !WizardryTales.shouldHideHead || Minecraft.getMinecraft().gameSettings.thirdPersonView != 0;
    }
}
