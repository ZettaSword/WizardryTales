package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketLearnSpell implements IMessage
{
    public Spell spell;

    public PacketLearnSpell(){}

    public PacketLearnSpell(Spell spell){
        this.spell = spell;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeDouble(spell.networkID());
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.spell = Spell.byNetworkID(buf.readInt());
    }

    public static class PacketHandler implements IMessageHandler<PacketLearnSpell, IMessage>
    {

        @Override public IMessage onMessage(PacketLearnSpell message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleLearnSpell(message));
            }

            return null;
        }
    }


}
