package astramusfate.wizardry_tales.entity.construct.sigils.chanting;

import astramusfate.wizardry_tales.entity.construct.EntityMagicCircle;
import astramusfate.wizardry_tales.events.SpellCreation;
import com.google.common.collect.Lists;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.NBTExtras;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nonnull;
import java.util.List;

public abstract class EntityCircleWords extends EntityMagicCircle {
    public List<String> words = Lists.newArrayList();
    public int amount = -1;


    public EntityCircleWords(World world){
        super(world);
    }

    public EntityCircleWords(World world, List<String> words) {
        super(world);
        setWords(words);
    }

    public void setWords(List<String> words) {
        this.words = words;
        this.amount = words.size() - 1;
    }

    @Override
    protected void writeEntityToNBT(@Nonnull NBTTagCompound tag) {
        super.writeEntityToNBT(tag);
        tag.setInteger("amount", amount);
        NBTExtras.storeTagSafely(tag, "words", NBTExtras.listToNBT(words, NBTTagString::new));
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound tag) {
        super.readEntityFromNBT(tag);
        this.amount = tag.getInteger("amount");
        this.words = (List<String>)NBTExtras.NBTToList(tag.getTagList("words", Constants.NBT.TAG_STRING),
                NBTTagString::getString);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        super.writeSpawnData(data);
        data.writeInt(amount);
        for(String word : words){
            ByteBufUtils.writeUTF8String(data, word);
        }
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        super.readSpawnData(data);
        amount = data.readInt();
        if(amount > -1) {
            for (int i = 0; i < amount; i++) {
                words.add(i, ByteBufUtils.readUTF8String(data));
            }
        }
    }
}
