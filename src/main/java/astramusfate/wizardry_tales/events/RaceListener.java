package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.wizardry.Race;
import astramusfate.wizardry_tales.api.wizardry.TalesArtemis;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Tales;
import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.UUID;

public class RaceListener extends EventsBase {

    public static final UUID id = UUID.fromString("27739b22-46dd-48af-910f-d858e01e1f48");

    @SubscribeEvent
    public static void racesTicking(TickEvent.PlayerTickEvent event) {
        if (Solver.doEvery(event.player, 5)) {
            EntityPlayer player = event.player;
            tick(player);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void racesLogin(EntityJoinWorldEvent event) {
        if (event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            tick(player);
        }
    }

    @SubscribeEvent
    public void racesQuit(PlayerEvent.PlayerLoggedOutEvent event){
        EntityPlayer player = event.player;
        tick(player);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void racesClone(net.minecraftforge.event.entity.player.PlayerEvent.Clone event){
        tick(event.getEntityPlayer());
    }

    public static void tick(EntityPlayer player){
        String race = Race.get(player);
        double transition = Tales.races.transition;

        IAttributeInstance height = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_HEIGHT);
        IAttributeInstance width = player.getAttributeMap().getAttributeInstance(ArtemisLibAttributes.ENTITY_WIDTH);

        IAttributeInstance health = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MAX_HEALTH);
        IAttributeInstance attack = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ATTACK_DAMAGE);
        IAttributeInstance armor = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.ARMOR);
        IAttributeInstance knockback = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
        IAttributeInstance speed = player.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);

        switch (race) {
            case "elf":
                checkAttributes(player, health, "race health", 10, Solver.ADD);
                checkAttributes(speed, "race speed", 0.1, Solver.MULTIPLY);
                checkAttributes(attack, "race attack", -0.3, Solver.MULTIPLY);

                checkAttributes(height, "race height", 0.1, Solver.MULTIPLY);
                break;
            case "dwarf":
                checkAttributes(player, health, "race health", -2, Solver.ADD);
                checkAttributes(speed, "race speed", -0.1, Solver.MULTIPLY);
                checkAttributes(attack, "race attack", 0.25, Solver.MULTIPLY);

                checkAttributes(height, "race height", -0.2, Solver.MULTIPLY);
                break;
            case "slime":
                checkAttributes(health, "race health", -4, Solver.ADD, transition);

                if(player.isSneaking()) {
                    checkAttributes(height, "race height", -0.5D, Solver.MULTIPLY, transition);
                    checkAttributes(width, "race width", -0.5D, Solver.MULTIPLY, transition);
                }
                else if(isAir(player)) {
                    if(transition != 1.0D) {
                        if (player.motionY > 0) {
                            checkAttributes(height, "race height", -0.5 * Math.min(player.motionY, 1.0D),
                                    Solver.MULTIPLY, transition);
                        }

                        if(player.onGround){
                            checkAttributes(height, "race height", 0.0D, Solver.MULTIPLY, transition);
                        }else if(player.motionY < 0.3D) {
                            checkAttributes(height, "race height",
                                    -0.5 * Math.max(player.motionY, -1.0D), Solver.MULTIPLY, transition);
                        }

                    }else{
                        checkAttributes(height, "race height", 0.0D, Solver.MULTIPLY);
                    }
                    /*
                    if (player.collidedHorizontally){
                        checkAttributes(height, "race height", 0.0D, Solver.MULTIPLY, 20);
                    }*/
                    checkAttributes(width, "race width", 0.0D, Solver.MULTIPLY);
                }

                checkAttributes(knockback, "race knockback", -0.5, Solver.ADD);
                checkAttributes(attack, "race attack", -0.5, Solver.MULTIPLY);
                checkAttributes(speed, "race speed", 0.1, Solver.MULTIPLY);
                break;

            default:
                TalesArtemis.nullAttributes(player);
                break;
        }
    }

    public static boolean isAir(EntityPlayer player){
       return  player.world.getBlockState(player.getPosition().up(1)).getBlock() == Blocks.AIR;
    }

    public static AttributeModifier newMod(String name, double amount, int operation){
        return new AttributeModifier(id, name, amount, operation);
    }

    /** Specifically to force update on client. Eh. **/
    public static void checkAttributes(EntityPlayer player, IAttributeInstance check, String name,  double value, int operation){
        AttributeModifier mod = newMod(name, value, operation);
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
            if (check.getAttribute() == SharedMonsterAttributes.MAX_HEALTH){
                player.setHealth(player.getHealth() -1);
                player.setHealth(player.getHealth());
            }
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() != value){
                    check.removeModifier(id);
                    check.applyModifier(mod);
                    if (check.getAttribute() == SharedMonsterAttributes.MAX_HEALTH){
                        player.setHealth(player.getHealth() -1);
                        player.setHealth(player.getHealth());
                    }
                }
            }
        }
    }

    public static void checkAttributes(IAttributeInstance check, String name,  double value, int operation){
        AttributeModifier mod = newMod(name, value, operation);
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() != value){
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }
            }
        }
    }

    public static void checkAttributes(IAttributeInstance check, String name,  double value, int operation, double speed){
        AttributeModifier mod = newMod(name, value, operation);
        double difference;
        if(!check.hasModifier(mod)){
            check.applyModifier(mod);
        } else{
            AttributeModifier par = check.getModifier(id);
            if(par != null){
                if(par.getAmount() > value){
                    difference=par.getAmount() - value;
                    mod = newMod(name, par.getAmount() - difference/speed, operation).setSaved(true);
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }else if(par.getAmount() < value){
                    difference=value - par.getAmount();
                    mod = newMod(name, par.getAmount() + difference/speed, operation).setSaved(true);
                    check.removeModifier(id);
                    check.applyModifier(mod);
                }
            }
        }
    }
}
