package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncRace implements IMessage
{
    public String race;

    public PacketSyncRace(){}

    public PacketSyncRace(String race){
        this.race = race;
    }

    @Override public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.race);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.race = ByteBufUtils.readUTF8String(buf);
    }

    public static class PacketHandler implements IMessageHandler<PacketSyncRace, IMessage>
    {

        @Override public IMessage onMessage(PacketSyncRace message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSyncRace(message));
            }

            return null;
        }
    }


}
