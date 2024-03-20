package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.WizardryTales;
import com.google.common.collect.Lists;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.HashMap;
import java.util.Map;

public class PacketSyncLearning implements IMessage
{
    public Map<Spell, Integer> spells;

    public PacketSyncLearning(){}

    public PacketSyncLearning(Map<Spell, Integer> spells){
        this.spells = spells;
    }

    @Override public void toBytes(ByteBuf buf) {
        if (this.spells == null) this.spells = new HashMap<>();
        this.spells.put(Spells.magic_missile, 11);
        NBTTagCompound tag = new NBTTagCompound();
        NBTTagIntArray array = new NBTTagIntArray(Lists.newArrayList(spells.values()));
        tag.setTag("data", array);
        ByteBufUtils.writeTag(buf, tag);
        for (Spell spell : spells.keySet()){
            buf.writeInt(spell.networkID());
        }
    }

    @Override public void fromBytes(ByteBuf buf) {
        this.spells = new HashMap<>();
        NBTTagCompound tag = ByteBufUtils.readTag(buf);
        if (tag != null) {
            NBTTagIntArray array = (NBTTagIntArray) tag.getTag("data");
            for (int i = 0; i <= array.getIntArray().length; i++){
                this.spells.put(Spell.byNetworkID(buf.readInt()), array.getIntArray()[i]);
            }
        }
    }

    public static class PacketHandler implements IMessageHandler<PacketSyncLearning, IMessage>
    {

        @Override public IMessage onMessage(PacketSyncLearning message, MessageContext ctx)
        {
            if(ctx.side.isClient()) {
                Minecraft.getMinecraft().addScheduledTask(() -> WizardryTales.proxy.handleSpellsLearning(message));
            }

            return null;
        }
    }


}
