package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.SoulProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSyncMode implements IMessage
{
    public int mode;

    public PacketSyncMode(){}

    public PacketSyncMode(int cooldown){
        this.mode = cooldown;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(this.mode);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.mode = buf.readInt();
    }

    public static class PacketHandler implements IMessageHandler<PacketSyncMode, IMessage>
    {

        @Override public IMessage onMessage(PacketSyncMode message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSyncMode(message));
            }

            return null;
        }
    }


}
