package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.api.Sage;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.data.ChantWorker;
import astramusfate.wizardry_tales.data.PacketMagic;
import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.events.TalesControlHandler;
import electroblob.wizardry.command.CommandCastSpell;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.packet.PacketCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCastingRing implements IMessage
{
    /** EntityID of the caster */
    public int casterID;
    /** ID of the spell being cast */
    public int spellID;
    /** SpellModifiers for the spell */
    public SpellModifiers modifiers;

    public PacketCastingRing(){}

    public PacketCastingRing(int casterID, Spell spell, SpellModifiers modifiers){
        this.casterID = casterID;
        this.spellID = spell.networkID();
        this.modifiers = modifiers;
    }

    @Override
    public void fromBytes(ByteBuf buf){
        // The order is important
        this.casterID = buf.readInt();
        this.spellID = buf.readInt();
        this.modifiers = new SpellModifiers();
        this.modifiers.read(buf);
    }

    @Override
    public void toBytes(ByteBuf buf){

        buf.writeInt(casterID);
        buf.writeInt(spellID);
        this.modifiers.write(buf);
    }

    public static class PacketHandler implements IMessageHandler<PacketCastingRing, IMessage>
    {

        @Override public IMessage onMessage(PacketCastingRing message, MessageContext ctx)
        {
            if(ctx.side.isServer()) {

                final EntityPlayerMP player = ctx.getServerHandler().player;

                player.getServerWorld().addScheduledTask(() -> {
                    World world = player.world;
                    if(!(world.getEntityByID(message.casterID) instanceof EntityPlayer)) return;
                    EntityPlayer caster = (EntityPlayer) world.getEntityByID(message.casterID);
                    if(caster == null) return;
                    ItemStack wand = Thief.getWandInUse(player);
                    if(wand == null) return;
                    ISoul soul = Mana.getSoul(player);
                    if(soul == null) return;
                    Spell spell = Spell.byNetworkID(message.spellID);
                    if (WizardData.get(player) != null && !WizardData.get(player).hasSpellBeenDiscovered(spell)) return;
                    SpellModifiers modifiers = message.modifiers;

                    int duration = CommandCastSpell.DEFAULT_CASTING_DURATION;
                    int seconds = duration/20;

                    // Mana cost
                    int cost = (int)(spell.getCost() * modifiers.get(SpellModifiers.COST) + 0.1f); // Weird floaty rounding
                    if(spell.isContinuous) cost = cost * seconds;

                    cost*= Tales.mp.casting_ring_cost;

                    // If anything stops the spell working at this point, nothing else happens.
                    if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(SpellCastEvent.Source.OTHER, spell, caster, modifiers)))
                        return;

                    if(spell.isContinuous){

                        WizardData data = WizardData.get(caster);

                        // Events/packets for continuous spell casting via commands are dealt with in WizardData.

                        if(data != null){
                            if(data.isCasting()){
                                data.stopCastingContinuousSpell(); // I think on balance this is quite a nice feature to leave in
                            }else{
                                if(!ChantWorker.useMana(player, cost) || soul.getCooldown() > 0) return;
                                data.startCastingContinuousSpell(spell, modifiers, duration);
                                soul.setCooldown(player, Tales.mp.casting_ring_cooldown);
                            }
                        }

                    }else{
                        if(!ChantWorker.useMana(player, cost) || soul.getCooldown() > 0) return;
                        soul.setCooldown(player, Tales.mp.casting_ring_cooldown);

                        if(spell.cast(caster.world, caster, EnumHand.MAIN_HAND, 0, modifiers)){

                            MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(SpellCastEvent.Source.OTHER, spell, caster, modifiers));

                            if(spell.requiresPacket()){
                                // Sends a packet to all players in dimension to tell them to spawn particles.
                                // Only sent if the spell succeeded, because if the spell failed, you wouldn't
                                // need to spawn any particles!
                                IMessage msg = new PacketCastSpell.Message(caster.getEntityId(), null, spell, modifiers);
                                WizardryPacketHandler.net.sendToDimension(msg, caster.world.provider.getDimension());
                            }
                        }
                    }
                });
            }

            return null;
        }
    }


}
