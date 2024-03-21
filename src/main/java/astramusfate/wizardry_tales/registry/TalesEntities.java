package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.entity.EntityManaBomb;
import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.entity.construct.EntityPoisonousGas;
import astramusfate.wizardry_tales.entity.construct.EntityRedGas;
import astramusfate.wizardry_tales.entity.living.*;
import astramusfate.wizardry_tales.entity.projectile.EntityChantingArrow;
import astramusfate.wizardry_tales.renderers.*;
import astramusfate.wizardry_tales.entity.construct.sigils.EntityMagicCircleVertical;
import astramusfate.wizardry_tales.entity.construct.sigils.chanting.*;
import astramusfate.wizardry_tales.entity.summon.EntityEmber;
import electroblob.wizardry.client.renderer.entity.RenderProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Arrays;
import java.util.stream.Collectors;


@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class TalesEntities {
    enum TrackingType {

        LIVING(80, 3, true),
        PROJECTILE(64, 10, true),
        CONSTRUCT(160, 10, false);

        int range;
        int interval;
        boolean trackVelocity;

        TrackingType(int range, int interval, boolean trackVelocity){
            this.range = range;
            this.interval = interval;
            this.trackVelocity = trackVelocity;
        }
    }

    private static int id = 0;

    @SubscribeEvent
    public static void register(RegistryEvent.Register<EntityEntry> event) {
        IForgeRegistry<EntityEntry> reg = event.getRegistry();

        // ! Animated
        reg.register(createEntry(EntityMushroom.class, "mushroom", TrackingType.LIVING)
                .egg(0x871105, 0xebd8d1)
                .spawn(EnumCreatureType.MONSTER, Tales.entities.mushroomSpawnRate, 1, 4, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.mushroomBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntityBigMushroom.class, "big_mushroom", TrackingType.LIVING)
                .egg(0x871105, 0xebd8d1)
                .spawn(EnumCreatureType.MONSTER, Tales.entities.big_mushroomSpawnRate, 1, 1, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.big_mushroomBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntityEnvenomedBlade.class, "envenomed_blade", TrackingType.LIVING)
                .egg(0x348748, 0x48871f)
                .spawn(EnumCreatureType.MONSTER, Tales.entities.envenomed_bladeSpawnRate, 1, 1, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.envenomed_bladeBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntityPoisonousGas.class, 	"poisonous_gas", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityRedGas.class, 	"red_gas", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityChantingArrow.class, 	"chanting_arrow", TrackingType.PROJECTILE).build());
        reg.register(createEntry(EntityManaBomb.class, 	"mana_bomb", TrackingType.PROJECTILE).build());
        reg.register(createEntry(EntityEmber.class, "ember", TrackingType.LIVING).build());

        registerCircles(reg);

        // Not Animated:
        reg.register(createEntry(EntityMidnightTrader.class, "midnight_trader", TrackingType.LIVING).egg(0x934DFF, 0x929193).build());
        reg.register(createEntry(EntityMidnightGuard.class, "midnight_guard", TrackingType.LIVING).egg(0x934DFF, 0x929193).build());
        reg.register(createEntry(EntityTenebria.class, "tenebria", TrackingType.LIVING).build());

        reg.register(createEntry(EntityEarthWolf.class, "earth_wolf", TrackingType.LIVING).egg(0x895F21, 0x70AA4D)
                // For reference: 5, 1, 1 are the parameters for the witch in vanilla
                .spawn(EnumCreatureType.CREATURE, Tales.entities.earthWolfSpawnRate, 2, 5, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.elementalWolfsBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntityThunderWolf.class, "thunder_wolf", TrackingType.LIVING).egg(0x050954, 0x00011A)
                // For reference: 5, 1, 1 are the parameters for the witch in vanilla
                .spawn(EnumCreatureType.CREATURE, Tales.entities.thunderWolfSpawnRate, 2, 5, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.elementalWolfsBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntitySkeletonWolf.class, "skeleton_wolf", TrackingType.LIVING).egg(0xB4B4B4, 0xBA91C5).build());


        reg.register(createEntry(EntityLightningSpider.class, "lightning_spider", TrackingType.LIVING).egg(0x35424b, 0x27b9d9)
                // For reference: 5, 1, 1 are the parameters for the witch in vanilla
                .spawn(EnumCreatureType.MONSTER, Tales.entities.lightningSpiderSpawnRate, 1, 2, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.spidersSpawnBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        reg.register(createEntry(EntitySpellWitch.class, "spell_witch", TrackingType.LIVING).egg(0x764B80, 0x62DFE7)
                // For reference: 5, 1, 1 are the parameters for the witch in vanilla
                .spawn(EnumCreatureType.MONSTER, Tales.entities.spellWitchSpawnRate, 1, 1, ForgeRegistries.BIOMES.getValuesCollection().stream()
                        .filter(b -> Arrays.asList(Tales.toResourceLocations(Tales.entities.spellWitchBiomeWhitelist)).contains(b.getRegistryName()))
                        .collect(Collectors.toSet())).build());

        //registerRituals(reg);

        //reg.register(createEntry(EntityMagicCircle.class, "magic_circle", TrackingType.CONSTRUCT).build());

        // Remember to make an client proxy registering
        //reg.register(createEntry(EntityManaStabilizer.class, "stabilize_mana", TrackingType.CONSTRUCT).build());
    }

    private static void registerCircles(IForgeRegistry<EntityEntry> reg){
        reg.register(createEntry(EntityMagicCircle.class, "circle", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityMagicCircleVertical.class, "vertical_circle", TrackingType.CONSTRUCT).build());

        reg.register(createEntry(EntityCircleManaCollector.class, "circle_collector", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityCircleAreaOnceCast.class, "circle_area_once", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityCircleTeleportation.class, "circle_teleport", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityCustomSigil.class, "circle_sigil", TrackingType.CONSTRUCT).build());
        reg.register(createEntry(EntityCircleArray.class, "circle_array", TrackingType.CONSTRUCT).build());
        //reg.register(createEntry(EntityMagicCircle.class, "vertical_circle", TrackingType.CONSTRUCT).build());
    }

    @SideOnly(Side.CLIENT)
    public static void RegisterRenderers(){
        // RenderingRegistry.registerEntityRenderingHandler(EntityManaStabilizer.class,
        //        manager -> new RenderCircle()(manager, new ResourceLocation(ZettaiMagic.MODID, "textures/entity/stabilize_mana.png"), 5.0f, false));
        // Slime Minion!
        registerNothing(EntityPoisonousGas.class);
        registerNothing(EntityRedGas.class);
        registerNothing(EntityEmber.class);
        registerNothing(EntityChantingArrow.class);
        RenderingRegistry.registerEntityRenderingHandler(EntityManaBomb.class,
                manager -> new RenderProjectile(manager, 0.6f,
                        new ResourceLocation(WizardryTales.MODID, "textures/items/mana_bomb.png"), false));

        registerRender(EntityMushroom.class, RenderMushroom::new);
        registerRender(EntityBigMushroom.class, RenderBigMushroom::new);
        registerRender(EntityEnvenomedBlade.class, RenderEnvenomedBlade::new);
        registerRender(EntityTenebria.class, RenderTenebria::new);

        // Not animated:
        RenderingRegistry.registerEntityRenderingHandler(EntityLightningSpider.class, RenderLightningSpider::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySpellWitch.class, RenderSpellWitch::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMidnightTrader.class, RenderMidnightTrader::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityMidnightGuard.class, RenderMidnightGuard::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonWolf.class, RenderSkeletonWolf::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityEarthWolf.class, RenderEarthWolf::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThunderWolf.class, RenderThunderWolf::new);

        registerCirclesRenderers();
    }

    public static <T extends Entity> void registerNothing(Class<T> entityClass){
        RenderingRegistry.registerEntityRenderingHandler(entityClass, RenderNothing::new);
    }

    public static <T extends Entity> void registerRender(Class<T> entityClass, IRenderFactory<? super T> renderFactory){
        RenderingRegistry.registerEntityRenderingHandler(entityClass, renderFactory);
    }

    @SideOnly(Side.CLIENT)
    private static void registerCirclesRenderers(){
        RenderingRegistry.registerEntityRenderingHandler(EntityMagicCircle.class, manager -> new RenderCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityMagicCircleVertical.class, manager -> new RenderVerticalCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityCircleManaCollector.class, manager -> new RenderCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityCircleAreaOnceCast.class, manager -> new RenderCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityCircleTeleportation.class, manager -> new RenderCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityCustomSigil.class, manager -> new RenderCircle(manager,
                0.5f));

        RenderingRegistry.registerEntityRenderingHandler(EntityCircleArray.class, manager -> new RenderCircle(manager,
                0.5f));
    }

    private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name, TrackingType tracking){
        return createEntry(entityClass, name).tracker(tracking.range, tracking.interval, tracking.trackVelocity);
    }

    private static <T extends Entity> EntityEntryBuilder<T> createEntry(Class<T> entityClass, String name){
        ResourceLocation registryName = new ResourceLocation(WizardryTales.MODID, name);
        return EntityEntryBuilder.<T>create().entity(entityClass).id(registryName, id++).name(registryName.toString());
    }
}
