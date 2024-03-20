package astramusfate.wizardry_tales.data;

import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.classes.IChestManaCollector;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.SoulProvider;
import astramusfate.wizardry_tales.data.chanting.SpellParam;
import astramusfate.wizardry_tales.entity.construct.EntityMagic;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.spell.ArcaneLock;
import electroblob.wizardry.util.*;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

import static electroblob.wizardry.spell.ArcaneLock.NBT_KEY;

public class ChantWorker extends EventsBase implements Lexicon {

    public static boolean useMana(Entity focal, double cost, boolean addProgress){
        if (cost < 0) cost *= -1;
        if (!Tales.mp.manaPool) return true;
        cost*= Tales.mp.chant_multiplier;
        if (focal instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) focal;
            return collectMana(player, cost, addProgress);
        } else {
            if (focal instanceof EntityMagic) {
                EntityMagic magic = (EntityMagic) focal;
                if (magic.getCaster() instanceof EntityPlayer) {
                    if (chestUseMana(magic, cost)) return true;
                    EntityPlayer player = (EntityPlayer) magic.getCaster();
                    return collectMana(player, cost, addProgress);
                }
            }
        }

        return false;
    }

    /** Requires to have Caster. **/
    public static boolean chestUseMana(Entity magic, double cost){
        if (magic == null) return false;
        if (!(magic instanceof IChestManaCollector)) return false;
        float range = ((IChestManaCollector) magic).getChestArea();
        List<BlockPos> positions = BlockUtils.getBlockSphere(magic.getPosition(), range);
        boolean result = false;
        if (positions.isEmpty()) return false;

        for (BlockPos pos : positions){
            TileEntity tile = magic.world.getTileEntity(pos);
            if (result) break;
            if (tile == null) continue;
            if (tile instanceof TileEntityChest && tile.getTileData().hasUniqueId(ArcaneLock.NBT_KEY)){
                TileEntityChest chest = (TileEntityChest) tile;
                NBTTagCompound tag = new NBTTagCompound();
                chest.writeToNBT(tag);
                NonNullList<ItemStack> stacks = NonNullList.create();
                ItemStackHelper.loadAllItems(tag, stacks);
                for (ItemStack stack : stacks){
                    if (stack.getItem() instanceof IManaStoringItem){
                        IManaStoringItem mana = (IManaStoringItem) stack.getItem();
                        if (mana.getManaCapacity(stack) >= (int) Math.round(cost)) {
                            mana.consumeMana(stack, (int) Math.round(cost), null);
                            magic.world.markAndNotifyBlock(pos,
                                    null,
                                    magic.world.getBlockState(pos),
                                    magic.world.getBlockState(pos), 3);
                            result=true;
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean useMana(Entity focal, double cost){
        return useMana(focal, cost, false);
    }

    public static double calc(double result, double with){
        return result * with;
    }

    public static double calc(double result, double with, double pow){
        return result * Math.pow(with, pow);
    }


    public static boolean useMana(Entity focal, double cost, float... modifiers){
        for(float modifier : modifiers) {
            cost = cost * modifier;
        }
        return useMana(focal, cost);
    }

    public static boolean collectMana(EntityPlayer player, double cost, boolean progress){
        if (!Tales.mp.manaPool) return true;
        ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
        if (soul == null) return false;
        if (player.isCreative()) return true;

        if (soul.getMP() >= cost) {
            soul.addMana(player, -1 * cost);
            if (progress){
                double maxMana = soul.getMaxMP();
                double value = (maxMana + Math.max(Tales.mp.progression * (Tales.mp.progression_multiplier *
                        (cost / maxMana)), Tales.mp.progression));
                soul.setMaxMP(player, Math.min(value, Tales.mp.max));
            }
            return true;
        }

        if (!player.world.isRemote) Aterna.translate(player, true, "mana.not_enough");
        return false;
    }

    public static boolean collectMana(EntityPlayer player, double cost){
        return collectMana(player, cost, false);
    }

    public static boolean useMana(EntityPlayer player, ItemStack stack, int cost){
        if(stack.getItem() instanceof IManaStoringItem) {
            IManaStoringItem mana = (IManaStoringItem) stack.getItem();
            if (mana.getMana(stack) >= cost) {
                mana.consumeMana(stack, cost, player);
                return true;
            }
        }
        return false;
    }

    public static boolean optional(List<String> words, String first, String second){
        return (contains(words, first) || contains(words, second));
    }

    public static boolean contains(List<String> words, String matches){
        String[] to_match = matches.split(" ");
        List<Boolean> contained = new ArrayList<>(Collections.emptyList());
        for(String match : to_match){
            contained.add(words.contains(match));
        }
        return contained.stream().allMatch(bool -> bool);
    }

    public static boolean containsAll(List<String> words, List<String> matching){
        List<Boolean> contained = new ArrayList<>(Collections.emptyList());
        for (String matches : matching) {
            String[] to_match = matches.split(" ");
            for (String match : to_match) {
                contained.add(words.contains(match));
            }
        }
        return contained.stream().allMatch(bool -> bool);
    }

    public static boolean containsAny(List<String> words, List<String> matching){
        List<Boolean> contained = new ArrayList<>(Collections.emptyList());
        for (String matches : matching) {
            String[] to_match = matches.split(" ");
            for (String match : to_match) {
                contained.add(words.contains(match));
            }
        }
        return contained.stream().anyMatch(bool -> bool);
    }

    /** Smarter version of contains(...), which checks only X words after first match **/
    public static boolean containsRightAfter(List<String> words, String matches, int tolerance){
        String[] to_match = matches.split(" ");
        List<Boolean> contained = new ArrayList<>(Collections.emptyList());
        int count = -1;
        for(String match : to_match){
            if(words.contains(match)){
                count = tolerance + 1;
            }else{
                if(count > 0) count--;
            }
            if(count == 0) break;

            contained.add(words.contains(match));
        }
        return contained.stream().allMatch(bool -> bool);
    }

    /** Smarter version of containsRightAfter(...), to which i give already split words **/
    @SuppressWarnings("ConstantConditions")
    public static boolean containsRightAfter(List<String> words, int tolerance, String... matches){
        List<Boolean> contained = new ArrayList<>(Collections.emptyList());
        boolean matched = false;
        int count = -1;
        for(String part : matches) {
            String[] to_match = part.split(" ");
            for (String match : to_match) {
                contained.add(words.contains(match));
                if(words.contains(match)){
                    matched = true;
                }
            }
            if (matched) {
                count = tolerance + 1;
            } else {
                if (count > 0) count--; // IntelIJ, it's not constant value, because we are in the loop! LOOP!
            }
            if (count == 0) break;
        }
        return contained.stream().allMatch(bool -> bool);
    }

    /** Removes two words from all words **/
    @SuppressWarnings("All")
    public static List<String> removeIfFind(List<String> words, String matches){
        String next;
        List<String> finality = words;
        for (int i = 0; i < words.size(); i++){
            String word = words.get(i);
            if (i + 1 < words.size()) next = words.get(i+1); else next = "";

            if(findIn(matches, word) && findIn(matches, next)){
                finality.remove(i);
                finality.remove(i+1);
            }
        }
        return finality;
    }

    public static boolean findIn(String match, String matches){
        String[] to_match = matches.split(" ");
        for(String matchy : to_match){
            if (matchy.equals(match)) {
                return true;
            }
        }
        return false;
    }

    public static boolean findInSmart(String see, String matches){
        String[] to_match = matches.split(" ");
        String[] to_see = see.split(" ");
        for (String match : to_see) {
            for (String matchy : to_match) {
                if (matchy.equals(match)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean findAny(String match, String... matches){
        for (String matching : matches) {
            String[] to_match = matching.split(" ");
            for (String matchy : to_match) {
                if (matchy.equals(match)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean findIn(String match, String... matches){
        int count = 0;
        for (String matching : matches) {
            String[] to_match = matching.split(" ");
            for (String matchy : to_match) {
                if (matchy.equals(match)) {
                    count++;
                }
            }
        }
        return count == matches.length;
    }

    /** Method made for processing the Modifiers of Chant **/
    public static void processParameters(String word, String previous, String next, List<SpellParam> params){
        // Processing the parameters...
        boolean error = false;
        float number = 1.0F;
        try {
            number = Float.parseFloat(word);
        } catch (NumberFormatException ignore){error = true;}

        if(error){
            try {
                number = Float.parseFloat(next);
                error=false;
            } catch (NumberFormatException ignore){}
        }

        if(error) return;

        for(SpellParam param : params){
            if(findIn(param.name(), previous)){
                param.setNumber(number);
            }
        }

        // ...We processed the parameter, now starting spell cast....
    }


    /**
     * Highest-level particle spawning method, only called client-side. 'Normal' subclasses should not need to override
     * this method; by default it spawns a line of particles, applying jitter and then calling
     *  at each point. Override to replace this with
     * an entirely custom particle effect - this is done by a few spells in the main mod to spawn beam-type particles.
     * @param world The world in which to spawn the particles.
     * @param origin A vector representing the start point of the line of particles.
     * @param direction A normalised vector representing the direction of the line of particles.
     * @param distance The length of the line of particles, already set to the appropriate distance based on the spell's
     */
    // The caster argument is only really useful for spawning targeted particles continuously
    public static void spawnParticleRay(World world,EnumParticleTypes type, Vec3d origin, Vec3d direction, double distance){

        Vec3d velocity = direction.scale(0);

        for(double d = 0.85D; d <= distance; d += 0.85D){
            double x = origin.x + d*direction.x + 0.1D * (world.rand.nextDouble()*2 - 1);
            double y = origin.y + d*direction.y + 0.1D * (world.rand.nextDouble()*2 - 1);
            double z = origin.z + d*direction.z + 0.1D * (world.rand.nextDouble()*2 - 1);
            spawnParticle(world, type, x, y, z);
        }
    }

    /**
     * Called at each point along the spell trajectory to spawn one or more particles at that point. Only called
     * client-side. Does nothing by default.
     * @param world The world in which to spawn the particle.
     * @param x The x-coordinate to spawn the particle at, with jitter already applied.
     * @param y The y-coordinate to spawn the particle at, with jitter already applied.
     * @param z The z-coordinate to spawn the particle at, with jitter already applied.
     */
    public static void spawnParticle(World world,EnumParticleTypes type, double x, double y, double z){
        world.spawnParticle(type, x, y, z, 0, 0, 0);
    }

    public static boolean toggleLock(World world, BlockPos pos, EntityPlayer player){

        TileEntity tileentity = world.getTileEntity(pos);

        if(tileentity != null){
            if(tileentity.getTileData().hasUniqueId(NBT_KEY)){
                // Unlocking
                if (world.getPlayerEntityByUUID(Objects.requireNonNull(tileentity.getTileData().getUniqueId(NBT_KEY))) == player
                && useMana(player, 10)) {
                    NBTExtras.removeUniqueId(tileentity.getTileData(), NBT_KEY);
                    world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 3);
                    return true;
                }
            }else{
                if (useMana(player, 10)) {
                    // Locking
                    tileentity.getTileData().setUniqueId(NBT_KEY, player.getUniqueID());
                    world.markAndNotifyBlock(pos, null, world.getBlockState(pos), world.getBlockState(pos), 3);
                    return true;
                }
            }
        }

        return false;
    }

    /** Searches air in 4 directions to ignite/froze/etc. I give priority to UP facing first, due logic **/
    @Nullable
    public static BlockPos findAir(World world, BlockPos pos){
        for(EnumFacing face : EnumFacing.values()){
            if(world.getBlockState(pos.offset(face)).getBlock() instanceof BlockAir){
                return pos.offset(face);
            }
        }
        return null;
    }

    public static boolean canBypassLocks(EntityPlayer player){
        if(!player.isCreative()) return false;
        if(Wizardry.settings.creativeBypassesArcaneLock) return true;
        MinecraftServer server = player.world.getMinecraftServer();
        return server != null && EntityUtils.isPlayerOp(player, server);
    }
}
