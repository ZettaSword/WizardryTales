package astramusfate.wizardry_tales.events;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.data.EventsBase;
import astramusfate.wizardry_tales.data.Tales;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.SpellType;
import electroblob.wizardry.entity.living.EntitySummonedCreature;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class VisualEffects extends EventsBase {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void OnSummon(EntityJoinWorldEvent event) {
        int summon = 0;
        if(event.getEntity() instanceof EntitySummonedCreature) summon = 1;
        if(event.getEntity() instanceof ISummonedCreature) summon = 2;
        if(summon == 0) return;
        if (Tales.effects.onSummon && !event.getEntity().getEntityData().hasKey("spawned")) {
            World world = event.getWorld();
            try {
                if (summon == 1 && ((EntitySummonedCreature) event.getEntity()).getOwner() != null) {
                    event.getEntity().getEntityData().setBoolean("spawned", true);
                    Wizard.conjureCircle(world, Element.NECROMANCY, event.getEntity().getPositionVector());
                } else if (summon == 2 && ((ISummonedCreature) event.getEntity()).getOwner() != null) {
                    event.getEntity().getEntityData().setBoolean("spawned", true);
                    Wizard.conjureCircle(world, Element.NECROMANCY, event.getEntity().getPositionVector());
                }
            } catch (Exception ignore){}
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void OnCast(SpellCastEvent.Post event) {
        if(event.getCaster() != null && event.getWorld().isRemote) {
            try {
                if ((!event.getSpell().isContinuous && event.getSpell().getChargeup() <= 0) && Tales.effects.long_spelled)
                    return;
                Wizard.conjureCircle(event.getWorld(), event.getSpell().getElement(), event.getCaster().getPositionVector());

            } catch (Exception ignore){}
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void OnCastVertical(SpellCastEvent.Post event) {
        if(event.getCaster() != null && event.getWorld().isRemote) {
            try {
                if ((!event.getSpell().isContinuous && event.getSpell().getChargeup() <= 0) && Tales.effects.vertical_long_spelled)
                    return;
                if(event.getSpell().getType() == SpellType.BUFF) return;
                Wizard.conjureVerticalCircle(event.getWorld(), event.getSpell().getElement(), event.getCaster().getPositionVector(), event.getCaster());

            } catch (Exception ignore){}
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void OnCastClient(SpellCastEvent.Post event) {
        if(event.getCaster() != null && event.getWorld().isRemote) {
            try {
            Wizard.castParticles(event.getWorld(), event.getSpell().getElement(), event.getCaster().getPositionVector());
            } catch (Exception ignore){}
        }
    }

    @SubscribeEvent
    public void tickCast(SpellCastEvent.Tick event){
        if(event.getCaster() != null){
            if(Solver.doEvery(event.getCount(), 2.0f)){
                try {
                    if ((!event.getSpell().isContinuous && event.getSpell().getChargeup() <= 0) && Tales.effects.long_spelled)
                        return;
                    Wizard.conjureCircle(event.getWorld(), event.getSpell().getElement(), event.getCaster().getPositionVector());
                } catch (Exception ignore){}
            }
        }
    }

    @SubscribeEvent
    public void castingProcess(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.START) {
            EntityPlayer player = event.player;

            if (Solver.doEvery(player, 2.0)) {
                try {
                    if(player.isHandActive() && player.getActiveItemStack().getItem() instanceof ISpellCastingItem){
                        ItemStack stack = player.getActiveItemStack();
                        ISpellCastingItem casting = (ISpellCastingItem) stack.getItem();
                        Spell spell = casting.getCurrentSpell(stack);
                        if(spell != Spells.none && player.getItemInUseCount() < spell.getChargeup()){
                            Wizard.conjureCircle(player.world, spell.getElement(), player.getPositionVector());
                        }
                    }
                } catch (Exception ignore){}
            }

            if(Solver.doEvery(player, 0.5)){
                try {
                    if(player.isHandActive() && player.getActiveItemStack().getItem() instanceof ISpellCastingItem){
                        ItemStack stack = player.getActiveItemStack();
                        ISpellCastingItem casting = (ISpellCastingItem) stack.getItem();
                        Spell spell = casting.getCurrentSpell(stack);
                        if(spell != Spells.none && player.getItemInUseCount() < spell.getChargeup()){
                            Wizard.castParticles(player.world, spell.getElement(), player.getPositionVector(), 9);
                        }
                    }
                } catch (Exception ignore){}
            }
        }
    }
}
