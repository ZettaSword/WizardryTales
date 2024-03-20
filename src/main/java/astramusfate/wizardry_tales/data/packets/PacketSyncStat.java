package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncStat implements IMessage
{
    public int id;
    public int stat;

    public PacketSyncStat(){}

    public PacketSyncStat(int id, int stat){
        this.id = id; this.stat = stat;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(this.id);
        buf.writeInt(this.stat);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.id = buf.readInt();
        this.stat = buf.readInt();
    }

    public static class PacketHandler implements IMessageHandler<PacketSyncStat, IMessage>
    {

        @Override public IMessage onMessage(PacketSyncStat message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSoulStat(message));
            }

            return null;
        }
    }


}
