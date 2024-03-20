package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCastingRingCooldown implements IMessage
{
    public int cooldown;

    public PacketCastingRingCooldown(){}

    public PacketCastingRingCooldown(int cooldown){
        this.cooldown = cooldown;
    }

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(this.cooldown);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.cooldown = buf.readInt();
    }

    public static class PacketHandler implements IMessageHandler<PacketCastingRingCooldown, IMessage>
    {

        @Override public IMessage onMessage(PacketCastingRingCooldown message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleCastingRingCooldown(message));
            }

            return null;
        }
    }


}
