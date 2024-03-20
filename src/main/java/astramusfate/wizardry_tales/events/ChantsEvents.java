package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.data.ChantWorker;
import astramusfate.wizardry_tales.data.OldLexicon;
import electroblob.wizardry.constants.Element;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;

/** This class made for Spelling/Wild Magic **/
@Mod.EventBusSubscriber
public class ChantsEvents extends ChantWorker implements OldLexicon {

    /** Allowed - all forming words of spell
     *  Identifiers - they link spell effect to some object
     *  Effects - what spell does
     * **/

    private static final List<String> allowed = Arrays.asList("release", "conjure", "apply", "on");
    private static final List<String> identifiers = Arrays.asList(s_caster,
            s_others, s_block, s_items);
    private static final List<String> effects = Arrays.asList("freeze", "ignite");
/*
    public static void incantate(EntityPlayer player, Entity focal, List<String> words, Element element, boolean isServer){
        World world = player.getEntityWorld();
        Vec3d pos = player.getPositionVector();
        BlockPos blockPos = player.getPosition();
        Vec3d view = player.getLookVec().scale(5);
        Vec3d look = new Vec3d(pos.x + view.x, pos.y + 1 + view.y, pos.z + view.z);
        List<Entity> targets = new java.util.ArrayList<>(Collections.emptyList());
        List<BlockPos> blocks = new java.util.ArrayList<>(Collections.emptyList());
        ItemStack catalyst = Thief.getInHands(player, i -> i.getItem() instanceof ISpellCastingItem);
        if(catalyst == null) catalyst = new ItemStack(WizardryItems.magic_wand);
        SpellModifiers modifiers = SpellModifiers.fromNBT(catalyst.getTagCompound() != null && catalyst.hasTagCompound()
                ? catalyst.getTagCompound() : new NBTTagCompound());

        List<String> original = words;

        // First we filter all and apply changes to modifiers and targets
        filterAll(words, targets, blocks, player, focal, world, modifiers);

        if(getElementOfIncantation(words.get(0)) != Element.MAGIC) words.set(0, "");

        // We do actions!
        actions(words, original, player, focal, targets, blocks, isServer, element, modifiers);
    }

    public static void filterAll(List<String> words, List<Entity> targets, List<BlockPos> blocks, EntityPlayer player, Entity focal, World world, SpellModifiers modifiers){
        Collections.reverse(words); // Reverse all array to find all targets
        // This phase of Chanting is for defining different values
        String next = "";
        modifiers.set(Sage.CHANT_POWER, 1.0f, true);
        modifiers.set(Sage.CHANT_DURATION, 8.0f, true);
        modifiers.set(Sage.CHANT_RANGE, 10.0f, true);
        modifiers.set(Sage.CHANT_DELAY, 1.0f, true);

        for(String word : words){
            if(words.indexOf(word) + 1 < words.size()) next = words.get(words.indexOf(word) + 1); else next = "";
            // Due we reverted array - it's previous word actually
            processModifiers(word, next, modifiers);

            if ((findIn(next, global_find) && findIn(word, s_entities)) || findIn(word, ef_begone)) {
                List<Entity> list = EntityUtils.getEntitiesWithinRadius(modifiers.get(Sage.CHANT_RANGE),
                        focal.getPosition().getX(), focal.getPosition().getY(), focal.getPosition().getZ(), world, Entity.class);
                list.remove(focal);
                list.removeIf(e -> e == player);
                targets.addAll(list);
            }

            if (findIn(word, s_blocks)) {
                float range = modifiers.get(Sage.CHANT_RANGE);
                List<BlockPos> sphere = BlockUtils.getBlockSphere(new BlockPos(focal.getPosition()), range);
                for (BlockPos pos : sphere) {
                    if (!world.isAirBlock(pos) || containsRightAfter(words, "including air", 0)) {
                        blocks.add(pos);
                    }
                }
            }

            if(focal.getDistance(player) <= modifiers.get(Sage.CHANT_RANGE)) {
                if (findIn(word, s_caster)) {
                    targets.add(player);
                }

                if ((findIn(next, global_find) && findIn(word, "entity")) || findIn(word, s_someone)) {
                    RayTraceResult ray = Solver.standardEntityRayTrace(world, focal, modifiers.get(Sage.CHANT_RANGE), false,
                            e ->  (e == player || e == focal) || (e instanceof EntityLivingBase && ((EntityLivingBase)e).deathTime > 0));
                    if (ray != null && ray.typeOfHit == RayTraceResult.Type.ENTITY) {
                        targets.add(ray.entityHit);
                    }
                }

                if (findIn(word, s_block)) {
                    RayTraceResult ray = Solver.standardBlockRayTrace(world, focal, modifiers.get(Sage.CHANT_RANGE), true);
                    if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                        //BlockPos pos = ray.getBlockPos().offset(ray.sideHit);
                        blocks.add(ray.getBlockPos());
                    }
                }
            }
        }

        Collections.reverse(words); // Reverse once more so things have order! :)
        // Now doing this for filtering what we gathered
        String previous = "";
        for(String word : words){
            if(words.indexOf(word) + 1 < words.size()) next = words.get(words.indexOf(word) + 1); else next = "";

            if (findIn(previous, filter)) {
                if (findIn(word, filter_mob)) { // Remove if not this type of mob
                    targets.removeIf(e -> !(e instanceof IMob || e instanceof IRangedAttackMob));
                }

                if (findIn(word, filter_undead)) { // Remove if not this type of mob
                    targets.removeIf(e -> !(e instanceof EntityLivingBase && ((EntityLivingBase) e).isEntityUndead()));
                }

                if (findIn(word, filter_human)) { // Remove if not this type of mob
                    targets.removeIf(e -> !(e instanceof EntityPlayer || e instanceof EntityWizard || e instanceof EntityEvilWizard));
                }

                if(findIn(word, filter_living)){
                    targets.removeIf(e -> !(e instanceof EntityLivingBase));
                }

                if(findIn(word, filter_creature)){
                    targets.removeIf(e -> !(e instanceof EntityLiving));
                }

                if(findIn(word, filter_entity)){
                    targets.removeIf(e -> e instanceof EntityLivingBase);
                }
            }
            previous = word;
        }
        // Here we do actual casting using data collected
        // Take in mind we cast both Client and Server, and to change that use isServer boolean

    }

    public static void actions(List<String> words, List<String> original, EntityPlayer player, Entity focal, List<Entity> targets, List<BlockPos> blocks, boolean isServer, Element element, SpellModifiers mods){
        World world = player.getEntityWorld();
        Vec3d playerPos = focal.getPositionVector();
        Vec3d view = focal.getLookVec().scale(5);
        Vec3d look = new Vec3d(playerPos.x + view.x, playerPos.y + 1 + view.y, playerPos.z + view.z);
        BlockPos blockPos = player.getPosition();
        ISoul soul = Mana.getSoul(player);
        if(soul == null) return;

        // REMEMBER to remove words that cause that from Construct summoned, else it'll be bad
        if(!doForCaster(words, original, isServer, mods, player, world, element)) return;

        //if(targets.isEmpty()) targets.add(player);
        for (Entity target : targets) {
            doForTarget(words, original, isServer, mods, player, focal, target, world, element);
        }
        for (BlockPos pos : blocks){
            doForBlocks(words, original, pos, isServer, mods, player, focal, world, element);
        }
    }

    public static boolean doForCaster(List<String> words, List<String> original, boolean isServer, SpellModifiers mods, EntityPlayer player, World world, Element element){
        String previous = "";
        String next = "";
        Vec3d playerPos = player.getPositionVector();
        Vec3d view = player.getLookVec().scale(5);
        Vec3d look = new Vec3d(playerPos.x + view.x, playerPos.y + 1 + view.y, playerPos.z + view.z);

        for (String word : words) {
            if (words.indexOf(word) + 1 < words.size()) next = words.get(words.indexOf(word) + 1);
            else next = "";

            processModifiers(word, previous, mods);

            // These things are not based on Element at all, meaning they're Multi-elemental!
            if (findIn(previous, global_create) && findIn(word, global_magic)) {
                EntityMagicCircle entity = new EntityMagicCircle(world, element,
                        "u_" + element.func_176610_l());
                entity.setCaster(player);
                //entity.setPositionAndRotationDirect(look.x, look.y, look.z,
                //        player.rotationYawHead, player.rotationPitch);
                entity.setPosition(playerPos.x, playerPos.y, playerPos.z);
                entity.setSizeMultiplier(4 + mods.get(Sage.CHANT_RANGE));
                world.spawnEntity(entity);
            }

            if (findIn(previous, "cast") && findIn(word, global_magic)) {
                List<String> set = Lists.newArrayList();
                set.addAll(original);
                set.remove("cast");
                set.remove(global_magic);
                EntityCircleSpellcasting entity = new EntityCircleSpellcasting(world, set, element,
                        "u_" + element.func_176610_l());
                entity.setCaster(player);
                //entity.setPositionAndRotationDirect(look.x, look.y, look.z,
                //        player.rotationYawHead, player.rotationPitch);
                entity.setPosition(playerPos.x, playerPos.y, playerPos.z);
                entity.setSizeMultiplier(4 + mods.get(Sage.CHANT_RANGE));
                world.spawnEntity(entity);
                return false;
            }

            if (findIn(previous, global_create) && findIn(word, global_delay)) {
                List<String> set = Lists.newArrayList();
                set.addAll(original);
                set.remove(global_create);
                set.remove(global_delay);
                EntityCircleDelayedCast entity = new EntityCircleDelayedCast(world, set, element, element.func_176610_l());
                entity.setCaster(player);
                entity.setLifetime(Solver.asTicks(mods.get(Sage.CHANT_DELAY)));
                entity.setSizeMultiplier(4 + mods.get(Sage.CHANT_RANGE));
                entity.setPositionAndRotation(playerPos.x, playerPos.y, playerPos.z, player.rotationYawHead, player.rotationPitch);
                world.spawnEntity(entity);
                return false;
            }

            if(findIn(previous, global_create) && findIn(next, global_collector)){
                List<String> set = Lists.newArrayList();
                set.addAll(original);
                set.remove(global_create);
                set.remove(global_collector);
                EntityCircleManaCollector entity = new EntityCircleManaCollector(world, set, element, element.func_176610_l(),
                        (int) mods.get(Sage.CHANT_COUNT));
                entity.setCaster(player);
                entity.setLifetime(-1);
                //entity.setLifetime(Solver.asTicks(mods.get(Sage.CHANT_DELAY)));
                entity.setSizeMultiplier(4 + mods.get(Sage.CHANT_RANGE));
                entity.setPositionAndRotation(playerPos.x, playerPos.y, playerPos.z, player.rotationYawHead, player.rotationPitch);
                world.spawnEntity(entity);
                return false;
            }

            previous = word;
        }

        return true;
    }

    public static void doForTarget(List<String> words, List<String> original, boolean isServer, SpellModifiers mods, EntityPlayer player, Entity focal, Entity target, World world, Element element){
        String previous = "";
        String next = "";
        Vec3d focalPos = focal.getPositionVector();
        Vec3d view = focal.getLookVec().scale(5);
        Vec3d look = new Vec3d(focalPos.x + view.x, focalPos.y + 1 + view.y, focalPos.z + view.z);

        for (String word : words) {
            if (words.indexOf(word) + 1 < words.size()) next = words.get(words.indexOf(word) + 1);
            else next = "";

            processModifiers(word, previous, mods);

            EntityLivingBase targetLiv = null;
            EntityItem targetItem = null;
            if (target instanceof EntityLivingBase)
                targetLiv = (EntityLivingBase) target;
            if (target instanceof EntityItem) targetItem = (EntityItem) target;
            Random random = new Random();

            // Those things are limited to element
            switch (element) {
                case MAGIC:
                    if (findIn(word, ef_begone) && useMana(focal, 3, mods.get(Sage.CHANT_POWER))) {
                        if (targetLiv != null) {
                            EntityUtils.applyStandardKnockback(focal, targetLiv, (float) calc(0.5f, mods.get(Sage.CHANT_POWER)));
                        } else if (target != null) {
                            {
                                Selena.pushEntity(focal, target, (float) calc(0.5f, mods.get(Sage.CHANT_POWER)));
                            }
                        }

                        if (!isServer) {
                            Vec3d position = focal.getPositionVector();

                            for (int i = 0; i < 5; i++) {
                                ParticleBuilder.create(ParticleBuilder.Type.SPHERE)
                                        .pos(position.add(new Vec3d(0, 0.1, 0)))
                                        .scale(mods.get(Sage.CHANT_RANGE) * 0.8f)
                                        .clr(0.8f, 0.9f, 1).spawn(world);
                            }
                        }
                    }

                    if (findIn(word, ef_pull) && useMana(focal, 3, mods.get(Sage.CHANT_POWER))) {
                        if (targetLiv != null) {
                            EntityUtils.applyStandardKnockback(focal, targetLiv, (float)
                                    calc(-0.5f, mods.get(Sage.CHANT_POWER)));
                        } else if (target != null) {
                            Selena.pushEntity(focal, target, (float) calc(-0.5f, mods.get(Sage.CHANT_POWER)));
                        }
                    }

                    if (findIn(previous, global_apply) && findIn(word, global_effect) &&
                            useMana(focal, 2, mods.get(Sage.CHANT_POWER), 0.5f * mods.get(Sage.CHANT_DURATION))) {
                        Potion effect = null;
                        try {
                            effect = Potion.getPotionFromResourceLocation(next);
                        } catch (NullPointerException ignore) {
                        }

                        if (targetLiv != null && effect != null) {
                            targetLiv.addPotionEffect(new PotionEffect(effect,
                                    Solver.asTicks(mods.get(Sage.CHANT_DURATION)),
                                    (int) mods.get(Sage.CHANT_POWER) - 1));
                        }
                    }

                    if (findIn(previous, ef_exchange) && !world.isRemote) {
                        int exchange = 5;
                        try {
                            exchange = Integer.parseInt(word);
                        } catch (NumberFormatException ignore) {
                        }

                        ItemStack currentStack = player.inventory.getCurrentItem();
                        if (exchange > 0 && currentStack.getItem() instanceof IManaStoringItem
                                && useMana(focal, exchange)) {
                            ((IManaStoringItem) currentStack.getItem()).rechargeMana(currentStack, exchange * 10);
                        }
                    }

                    if (findIn(previous, "remove") && findIn(word,global_magic)){
                        if(target instanceof EntityMagicCircle){
                            ((EntityMagicCircle)target).setLifetime(Solver.asTicks(1.0));
                        }
                    }

                    break;
                case FIRE:
                    if (findIn(word, ef_ignite) && useMana(focal, 2, mods.get(Sage.CHANT_DURATION))) {
                        target.setFire((int) mods.get(Sage.CHANT_DURATION));

                        if (!isServer) {
                            for (int i = 0; i < 30; i++) {
                                ParticleBuilder.create(ParticleBuilder.Type.MAGIC_FIRE, random, focal.posX, focal.posY, focal.posZ,
                                        2, false)
                                        .time(35).gravity(true).spawn(world);
                            }
                        }
                    }
                    break;
                case ICE:
                    if (findIn(word, ef_freeze) && targetLiv != null &&
                            useMana(focal, 2, mods.get(Sage.CHANT_DURATION))) {
                        targetLiv.addPotionEffect(new PotionEffect(WizardryPotions.frost,
                                Solver.asTicks(mods.get(Sage.CHANT_DURATION)), 0));
                        if (!isServer) {
                            for (int i = 0; i < 30; i++) {
                                ParticleBuilder.create(ParticleBuilder.Type.ICE, random, focal.posX, focal.posY, focal.posZ,
                                        2, false)
                                        .time(35).gravity(true).spawn(world);
                            }
                        }
                    }
                    break;
                case LIGHTNING:
                    if (targetLiv != null) {
                        if (findIn(previous, global_create)) {
                            if (findIn(word, ef_spark) && useMana(focal, 2, mods.get(Sage.CHANT_POWER))) {
                                if (!isServer) {
                                    // Rather neatly, the entity can be set here and if it's null nothing will happen.
                                    ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(focal)
                                            .pos(targetLiv.getPositionVector().subtract(focal.getPositionVector()))
                                            .target(targetLiv).spawn(world);
                                    ParticleBuilder.spawnShockParticles(world, targetLiv.posX, targetLiv.posY + targetLiv.height / 2, targetLiv.posZ);
                                }

                                // This is a lot neater than it was, thanks to the damage type system.
                                if (MagicDamage.isEntityImmune(MagicDamage.DamageType.SHOCK, targetLiv)) {
                                    if (!world.isRemote) player.sendStatusMessage(
                                            new TextComponentTranslation("spell.resist",
                                                    targetLiv.getName(), Spells.arc.getNameForTranslationFormatted()), true);
                                } else {
                                    targetLiv.attackEntityFrom(MagicDamage.causeDirectMagicDamage(player, MagicDamage.DamageType.SHOCK),
                                            mods.get(Sage.CHANT_POWER));
                                }
                            }
                        }
                    }
                    break;
                case NECROMANCY:
                    break;
                case EARTH:

                    break;
                case SORCERY:
                    break;
                case HEALING:
                    if (targetLiv == null) break;
                    if (findIn(previous, global_open) && useMana(focal, 1, mods.get(Sage.CHANT_POWER), mods.get(Sage.CHANT_DURATION))) {
                        if (findIn(word, ef_wound)) {
                            targetLiv.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,
                                    Solver.asTicks(mods.get(Sage.CHANT_DURATION)), (int) mods.get(Sage.CHANT_POWER)));
                        }
                    }
                    if (findIn(word, ef_heal) && useMana(focal, 2, mods.get(Sage.CHANT_POWER))) {
                        if (targetLiv.getCreatureAttribute() != EnumCreatureAttribute.UNDEAD) {
                            if (targetLiv.getHealth() >= targetLiv.getMaxHealth()) break;
                            targetLiv.heal((float) calc(2.0f, mods.get(Sage.CHANT_POWER)));
                        } else {
                            targetLiv.setFire(2);
                            Sage.causeDamage(MagicDamage.DamageType.RADIANT, player, targetLiv,
                                    (float) calc(2.0f, mods.get(Sage.CHANT_POWER)));
                        }

                        if (!isServer) {
                            Vec3d position = targetLiv.getPositionVector();

                            for (int i = 0; i < 5; i++) {

                                float hue = world.rand.nextFloat() * 0.4f;
                                ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                                        .pos(focalPos.x, focalPos.y, focalPos.z).vel(0, 0.03, 0).time(50)
                                        .clr(1, 1 - hue, 0.6f + hue).spawn(world);
                            }

                            for (int i = 0; i < 15; i++) {

                                float hue = world.rand.nextFloat() * 0.4f;
                                ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
                                        .pos(position.x, position.y, position.z).vel(0, 0.03, 0).time(50)
                                        .clr(1, 1 - hue, 0.6f + hue).spawn(world);
                            }
                        }
                    }
            }

            previous = word;
        }
    }

    public static void doForBlocks(List<String> words, List<String> original, BlockPos pos, boolean isServer, SpellModifiers mods, EntityPlayer player, Entity focal, World world, Element element) {
        String previous = "";
        String next;
        Vec3d focalPos = focal.getPositionVector();
        Vec3d view = focal.getLookVec().scale(5);
        Vec3d look = new Vec3d(focalPos.x + view.x, focalPos.y + 1 + view.y, focalPos.z + view.z);
        BlockPos blockPos = focal.getPosition();
        for (String word : words) {
            if (words.indexOf(word) + 1 < words.size()) next = words.get(words.indexOf(word) + 1);
            else next = "";

            IBlockState state = player.world.getBlockState(pos);
            Block block = state.getBlock();
            Chunk chunk = world.getChunkFromBlockCoords(pos);

            switch (element) {
                case MAGIC:
                    break;
                case FIRE:
                    if (findIn(word, ef_ignite) && useMana(focal, 2)) {
                        BlockPos ignite = findAir(world, pos);
                        if (ignite != null && world.isAirBlock(ignite) && !world.isAirBlock(pos) && world.isBlockNormalCube(pos, false)) {

                            if (!world.isRemote && isServer && BlockUtils.canPlaceBlock(player, world, ignite)) {
                                world.setBlockState(ignite, Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                    break;
                case ICE:
                    break;
                case LIGHTNING:
                    break;
                case NECROMANCY:
                    break;
                case EARTH:
                    break;
                case SORCERY:
                    if (findIn(word, ef_lock)) {
                        if (toggleLock(world, pos, player)) {
                            BlockPos otherHalf = BlockUtils.getConnectedChest(world, pos);
                            if (otherHalf != null) toggleLock(world, otherHalf, player);
                            world.markAndNotifyBlock(pos, chunk, state, state, 3);
                            //world.updateObservingBlocksAt(pos, block);
                        }
                    }
                    break;
                case HEALING:
            }
            previous = word;
        }
    }
*/
    //@SubscribeEvent
    public static void incantationsEvent(ServerChatEvent event) {
        String msg = event.getMessage().toLowerCase();
        EntityPlayerMP player = event.getPlayer();
        Vec3d pos = player.getPositionVector();
        BlockPos blockPos = player.getPosition();
        Vec3d view = player.getLookVec().scale(5);
        Vec3d look = new Vec3d(pos.x + view.x, pos.y + 1 + view.y, pos.z + view.z);
        World world = player.world;
        msg = msg.replace(",", "").replace(".", "").replace("!", "")
                .replace("?","");
        List<String> words = Arrays.asList(msg.split(" "));

        if(words.isEmpty()) return;
        Element element = getElementOfIncantation(words.get(0));

        //incantate(player, player, words, element, true);
    }

   // @SubscribeEvent
    public static void incantationClientEvent(ClientChatEvent event){
        String msg = event.getMessage().toLowerCase();
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Vec3d pos = player.getPositionVector();
        BlockPos blockPos = player.getPosition();
        Vec3d view = player.getLookVec().scale(5);
        Vec3d look = new Vec3d(pos.x + view.x, pos.y + 1 + view.y, pos.z + view.z);
        World world = player.world;
        msg = msg.replace(",", "").replace(".", "").replace("!", "")
                .replace("?","");
        List<String> words = Arrays.asList(msg.split(" "));

        if(words.isEmpty()) return;
        Element element = getElementOfIncantation(words.get(0));

        //incantate(player, player, words, element, false);
    }

    private static Element getElementOfIncantation(String element){
        if(findIn(element, e_fire)) return Element.FIRE;
        if(findIn(element, e_ice)) return Element.ICE;
        if(findIn(element, e_earth)) return Element.EARTH;
        if(findIn(element, e_lightning)) return Element.LIGHTNING;
        if(findIn(element, e_healing)) return Element.HEALING;
        if(findIn(element, e_necromancy)) return Element.NECROMANCY;
        if(findIn(element, e_sorcery)) return Element.SORCERY;

        return Element.MAGIC;
    }
}