package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

public class PacketDataToAll implements IMessage
{
    public PacketDataToAll(){}

    @Override public void toBytes(ByteBuf buf) {
    }

    @Override public void fromBytes(ByteBuf buf) {
    }

    public static class PacketHandler implements IMessageHandler<PacketDataToAll, IMessage>
    {

        @Override public IMessage onMessage(PacketDataToAll message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleDataToAll(message));
            }

            return null;
        }
    }


}
