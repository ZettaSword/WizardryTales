package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSoulMana implements IMessage
{
    public double mana;

    public PacketSoulMana(){}

    public PacketSoulMana(double mana){
        this.mana = mana;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.mana);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.mana = buf.readDouble();
    }

    public static class PacketHandler implements IMessageHandler<PacketSoulMana, IMessage>
    {

        @Override public IMessage onMessage(PacketSoulMana message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSoulMana(message));
            }

            return null;
        }
    }


}
