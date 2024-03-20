package astramusfate.wizardry_tales.data.packets;

import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.events.SpellCreation;
import astramusfate.wizardry_tales.events.SpellcastingHandler;
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Arrays;
import java.util.List;

public class PacketIncantate implements IMessage
{

    public PacketIncantate(){}

    @Override
    public void fromBytes(ByteBuf buf){
    }

    @Override
    public void toBytes(ByteBuf buf){
    }

    public static class PacketHandler implements IMessageHandler<PacketIncantate, IMessage>
    {

        @Override public IMessage onMessage(PacketIncantate message, MessageContext ctx)
        {
            if(ctx.side.isServer()) {
                final EntityPlayerMP player = ctx.getServerHandler().player;
                player.getServerWorld().addScheduledTask(() -> {
                    List<ItemStack> stacks = SpellcastingHandler.getChantedItems(player);
                    for (ItemStack stack : stacks) {
                        if (stack != null && SpellcastingHandler.checkCondition(stack, Lexicon.condition_manual)
                                && !player.getCooldownTracker().hasCooldown(stack.getItem())){
                            NBTTagCompound tag = stack.getTagCompound();
                            if (tag != null) {
                                if (tag.hasKey("spell") && tag.hasKey("condition")
                                        && SpellCreation.findIn(tag.getString("condition"), Lexicon.condition_manual)) {
                                    player.getCooldownTracker().setCooldown(stack.getItem(), 20);

                                    String msg = SpellCreation.getMsg(tag.getString("spell"));
                                    List<String> words = SpellCreation.getSpell(msg);
                                    String[] spell = words.toArray(new String[0]);
                                    List<String> set = Lists.newArrayList();
                                    try {
                                        set.addAll(Arrays.asList(spell));
                                        set.remove(Lexicon.par_shape);
                                        set.remove(Lexicon.shape_inscribe);
                                    } catch (Exception e) {
                                        Aterna.messageBar(player, "Problem when casting!");
                                    }

                                    SpellCreation.createSpell(set, player, player, true);
                                }
                            }
                        }
                    }
                });
            }

            return null;
        }
    }


}
