package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.Mana;
import astramusfate.wizardry_tales.data.cap.StatIds;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncStatToServer implements IMessage
{
    public int id;
    public int stat;

    public PacketSyncStatToServer(){}

    public PacketSyncStatToServer(int id, int stat){
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

    public static class PacketHandler implements IMessageHandler<PacketSyncStatToServer, IMessage>
    {

        @Override public IMessage onMessage(PacketSyncStatToServer message, MessageContext ctx)
        {
            if(ctx.side.isServer()) {
                final EntityPlayerMP player = ctx.getServerHandler().player;

                player.getServerWorld().addScheduledTask(() -> {
                  ISoul soul = Mana.getSoul(player);
                  if (soul != null){
                      soul.setStat(player, StatIds.status, message.stat);
                  }
                });
            }

            return null;
        }
    }


}
