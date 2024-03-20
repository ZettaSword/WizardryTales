package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSoulMaxMana implements IMessage
{
    public double maxMana;

    public PacketSoulMaxMana(){}

    public PacketSoulMaxMana(double maxMana){
        this.maxMana = maxMana;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.maxMana);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.maxMana = buf.readDouble();
    }

    public static class PacketHandler implements IMessageHandler<PacketSoulMaxMana, IMessage>
    {

        @Override public IMessage onMessage(PacketSoulMaxMana message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSoulMaxMana(message));
            }

            return null;
        }
    }


}
