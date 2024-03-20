package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.data.cap.ISoul;
import astramusfate.wizardry_tales.data.cap.SoulProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketAbilityMode implements IMessage
{
    public int ability;

    public PacketAbilityMode(){this.ability=1;}
    public PacketAbilityMode(int ability){this.ability=ability;}

    @Override public void toBytes(ByteBuf buf) {
        buf.writeInt(ability);
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.ability=buf.readInt();
    }

    public static class PacketHandler implements IMessageHandler<PacketAbilityMode, IMessage>
    {

        @Override public IMessage onMessage(PacketAbilityMode message, MessageContext ctx)
        {
            if(ctx.side.isServer()) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    ISoul soul = player.getCapability(SoulProvider.SOUL_CAP, null);
                    if (soul != null) {
                        int mode = message.ability;
                        soul.setMode(player, mode);
                        Aterna.message(player, mode == 1 ? TextFormatting.DARK_GREEN + "You started using ability!"
                                : TextFormatting.DARK_GREEN + "You stopped using ability!");
                    }
                });
            }

                return null;
        }
    }


}
