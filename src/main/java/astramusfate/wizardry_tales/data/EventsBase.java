package astramusfate.wizardry_tales.data;

import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

/** Extend this to your EventHandler, to get some things from it! **/
public class EventsBase {

    /** Cancel events faster! **/
    public static void cancel(Event event){
        if(event.isCancelable()){
            event.setCanceled(true);
            return;
        }
        if(event.hasResult()){
            event.setResult(Event.Result.DENY);
        }
    }

    /** Made for Living Damage Event to multiple at something **/
    public static void multipleAmount(LivingDamageEvent event, float multiple){
        event.setAmount(event.getAmount() * multiple);
    }

    /** Made for canceling events, when Boolean match (if false returned) **/
    public static boolean promise(Event event, boolean bool){
        if(!bool) {
            cancel(event);
        }
        return bool;
    }
}
