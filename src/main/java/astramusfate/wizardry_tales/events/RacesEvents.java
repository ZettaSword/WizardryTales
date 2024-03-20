package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Grim;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.api.wizardry.ParticlesCreator;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.data.EventsBase;
import com.google.common.collect.Lists;
import electroblob.wizardry.block.BlockCrystalOre;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeForest;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class RacesEvents extends EventsBase {

    @SubscribeEvent
    public static void onRaceBlockClick(PlayerInteractEvent.RightClickBlock event){
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.getPosition();
        Vec3d vec = event.getHitVec() == null ? new Vec3d(pos) : event.getHitVec();
        BlockPos eventPos = new BlockPos(vec);
        IBlockState state = world.getBlockState(eventPos);
        String race = Race.get(player);

        rightClick(world, player, event.getItemStack(), eventPos, pos, state, race);
    }

    @SubscribeEvent
    public static void onRaceItemClick(PlayerInteractEvent.RightClickItem event){
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.getPosition();
        IBlockState state = world.getBlockState(pos);
        String race = Race.get(player);

        rightClick(world, player, event.getItemStack(), null, pos, state, race);
    }

    //This is only client side
    @SubscribeEvent
    public static void onRaceEmptyClick(PlayerInteractEvent.RightClickEmpty event){
        EntityPlayer player = event.getEntityPlayer();
        World world = player.world;
        BlockPos pos = player.getPosition();
        IBlockState state = world.getBlockState(pos);
        String race = Race.get(player);

        rightClick(world, player, event.getItemStack(), pos, pos, state, race);
    }

    public static void rightClick(World world, EntityPlayer player, ItemStack stack, @Nullable BlockPos eventPos, BlockPos pos, IBlockState state, String race){
        if(!stack.isEmpty()) {
            if (race.equals("dwarf")) {
                if (stack.getItem() instanceof ItemPickaxe && !player.getCooldownTracker().hasCooldown(stack.getItem())) {
                    float f = player.getBrightness();

                    if (f <= 0.5F && !world.canSeeSky(new BlockPos(pos.getX(),
                            pos.getY() + (double) player.getEyeHeight(), pos.getZ()))) {
                        Alchemy.applyPotion(player, Solver.asTicks(15), 0, MobEffects.HASTE);
                    }

                    List<BlockPos> blocks = Grim.findBlocks(pos, 6,
                            b -> world.getBlockState(b).getBlock() instanceof BlockOre
                                    || world.getBlockState(b).getBlock() instanceof BlockCrystalOre);

                    List<BlockPos> path = Grim.findBlocks(pos, 6, b -> world.getBlockState(b).getBlock() instanceof BlockStone);

                    if(!blocks.isEmpty()){
                        if(!player.capabilities.isCreativeMode)
                            stack.damageItem(1, player);
                        player.getCooldownTracker().setCooldown(stack.getItem(), Solver.asTicks(3.0));
                    }

                    if (world.isRemote) {
                        Vec3d vec = player.getPositionVector().addVector(0, player.getEyeHeight() - 0.3, 0);

                        if(blocks.size() >= 5){
                            blocks.sort(Comparator.comparingDouble(c -> c.getDistance(pos.getX(), pos.getY(), pos.getZ())));
                            final int[] remove = {0};
                            blocks.removeIf(e -> { remove[0]++; return remove[0] > 5;} );
                        }

                        int color = ArcaneColor.byElement(Element.EARTH);

                        // Highlighting ore
                        for (BlockPos ore : blocks) {
                            Grim.highlightBlock(world, ore, color);

                            Vec3d target = new Vec3d(ore.getX() + 0.5, ore.getY() + 0.5, ore.getZ() + 0.5);

                            ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(color)
                                    .pos(vec).scale(2f).time(120).target(target).spawn(world);
                        }

                        color = ArcaneColor.byElement(Element.FIRE);

                        // Highlighting stone
                        for (BlockPos pathBlock : path){
                            Grim.highlightBlock(world, pathBlock, color);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void racesTick(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;
            World world = player.world;
            BlockPos pos = player.getPosition();
            String race = Race.get(player);

            if (player instanceof EntityPlayerMP && Solver.doEvery(event.player.ticksExisted, 5)) {


                if (race.equals(Race.elf)) {
                    //Alchemy.applyPotion(player, Solver.asTicks(10), 1, TalesEffects.elf);// We need to change player size
                    Biome biome = world.getBiome(pos);
                    float temperature = biome.getTemperature(pos);
                    if (temperature >= 2.0) {
                        Alchemy.applyPotion(player, Solver.asTicks(20), 1, MobEffects.WEAKNESS);
                    }
                    if (biome instanceof BiomeForest) {
                        Alchemy.applyPotion(player, Solver.asTicks(20), 0, MobEffects.SPEED);
                    }
                }

                if (race.equals(Race.dwarf)) {
                    if (!world.isRemote) {

                        float f = player.getBrightness();

                        if (f <= 0.5F && !world.canSeeSky(new BlockPos(player.posX,
                                player.posY + (double) player.getEyeHeight(), player.posZ))) {
                            Alchemy.applyPotionHide(player, Solver.asTicks(20), 0, MobEffects.NIGHT_VISION);
                        }
                    }
                }
            }

            if(race.equals("slime") && player.getAir() < 300) player.setAir(300);
        }
    }

    @SubscribeEvent
    public static void onRaceJump(LivingEvent.LivingJumpEvent event){
        if(event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            String race = Race.get(player);
            if(race.equals(Race.elf) && player.isSneaking()){
                player.motionY += 3 * 0.1F;
            }
        }
    }

    @SubscribeEvent
    public static void onRaceBowLoose(ArrowLooseEvent event){
        EntityPlayer player = event.getEntityPlayer();
        String race = Race.get(player);

        if(race.equals(Race.elf) && event.getCharge() >= 45){
            event.setCharge(100);
        }
    }

    /** I made it so, that it will modify the speed gained overall **/
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRaceBlockBreakSpeed(PlayerEvent.BreakSpeed event){
        EntityPlayer player = event.getEntityPlayer();
        String race = Race.get(player);

        if (race.equals(Race.dwarf)) event.setNewSpeed(event.getOriginalSpeed() * 1.25f);
    }

    @SubscribeEvent
    public static void onRaceAnvil(AnvilRepairEvent event){
        EntityPlayer player = event.getEntityPlayer();
        String race = Race.get(player);

        if(race.equals(Race.dwarf)){
            event.setBreakChance(0.0f);
        }
    }

    @SubscribeEvent
    public static void onRaceHurtEvent(LivingHurtEvent event){
        List<MagicDamage.DamageType> list = Lists.newArrayList(MagicDamage.DamageType.values());
        list.removeIf(e -> e.name().equals(event.getSource().damageType));
        MagicDamage.DamageType damageType = list.isEmpty() ? null : list.get(0);
        boolean isMagic = (event.getSource() == DamageSource.MAGIC || damageType != null);

        if(event.getSource().getTrueSource() instanceof EntityPlayer &&
                Objects.equals(Race.get(((EntityPlayer) event.getSource().getTrueSource())), Race.elf)
                && isMagic){
            event.setAmount(event.getAmount() * 1.25f);
        }

        if(event.getEntityLiving() instanceof EntityPlayer){
            EntityPlayer player = (EntityPlayer) event.getEntityLiving();
            String race = Race.get(player);
            if(race.equals(Race.dwarf) && !isMagic){
                event.setAmount(event.getAmount() * 0.8f);
            }
        }
    }


    @SubscribeEvent
    public static void onSpellcastRace(SpellCastEvent.Pre event){
        if(event.getCaster() == null) return;

        if(event.getCaster() instanceof EntityPlayer) {
            String race = Race.get((EntityPlayer) event.getCaster());
            SpellModifiers mods = event.getModifiers();
            if (race.equals(Race.elf)) {
                mods.set(WizardryItems.blast_upgrade, mods.get(WizardryItems.blast_upgrade) * 1.25f, false);
                mods.set(WizardryItems.range_upgrade, mods.get(WizardryItems.range_upgrade) * 1.25f, false);
                if (event.getSpell().getElement() == Element.LIGHTNING || event.getSpell().getElement() == Element.EARTH) {
                    mods.set(SpellModifiers.POTENCY, mods.get(SpellModifiers.POTENCY) * 1.1f, false);
                    if (event.getCaster().world.isRemote){
                        EntityPlayer player = (EntityPlayer) event.getCaster();
                        double radius = 1;
                        for (double t = 0; t <= 2*Math.PI*radius; t += 0.05) {
                            BlockPos location = player.getPosition();
                            double x = (radius * Math.cos(t)) + location.getX();
                            double z = (location.getZ() + radius * Math.sin(t));
                            Vec3d particle = new Vec3d(x + 0.5F, location.getY() + 1, z + 0.5F);
                            ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(particle).time(12)
                                    .clr(ArcaneColor.byElement(event.getSpell().getElement())).spawn(player.world);
                        }
                    }
                }
            }

            if(race.equals(Race.dwarf)){
                mods.set(SpellModifiers.COST, mods.get(SpellModifiers.COST) * 1.5f, false);
                mods.set(WizardryItems.cooldown_upgrade, mods.get(WizardryItems.cooldown_upgrade) * 1.5f, false);

                if (event.getSpell().getElement() == Element.FIRE || event.getSpell().getElement() == Element.EARTH) {
                    mods.set(SpellModifiers.POTENCY, mods.get(SpellModifiers.POTENCY) * 1.3f, false);
                }
            }
        }

        if(event.getCaster().isPotionActive(WizardryPotions.arcane_jammer)){
            event.setCanceled(true);

            if(event.getSource() == SpellCastEvent.Source.WAND || event.getSource() == SpellCastEvent.Source.SCROLL){
                event.getCaster().setActiveHand(EnumHand.MAIN_HAND);
            }

            // Because we're using a seed that should be consistent, we can do client-side stuff!
            event.getWorld().playSound(event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ,
                    WizardrySounds.MISC_SPELL_FAIL, WizardrySounds.SPELLS, 1, 1, false);

            if(event.getWorld().isRemote){

                Vec3d centre = event.getCaster().getPositionEyes(1).add(event.getCaster().getLookVec());

                for(int i = 0; i < 5; i++){
                    double x = centre.x + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                    double y = centre.y + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                    double z = centre.z + 0.5f * (event.getWorld().rand.nextFloat() - 0.5f);
                    event.getWorld().spawnParticle(EnumParticleTypes.SMOKE_LARGE, x, y, z, 0, 0, 0);
                }
            }

        }
    }
}
