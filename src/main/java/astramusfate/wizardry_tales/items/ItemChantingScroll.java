package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.classes.IInscribed;
import astramusfate.wizardry_tales.data.Lexicon;
import astramusfate.wizardry_tales.events.SpellCreation;
import astramusfate.wizardry_tales.registry.TalesTabs;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ItemChantingScroll extends Item implements IInscribed {

    public ItemChantingScroll(){
        super();
        setMaxStackSize(16);
        setCreativeTab(TalesTabs.Items);
        this.setRegistryName(new ResourceLocation(WizardryTales.MODID, "chanting_scroll"));
        this.setUnlocalizedName(new ResourceLocation(WizardryTales.MODID, "chanting_scroll").toString());
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, EntityPlayer player, @Nonnull EnumHand hand){
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getTagCompound() == null) return new ActionResult<>(EnumActionResult.PASS, stack);
        NBTTagCompound tag = stack.getTagCompound();

        if (!tag.hasKey("spell")) return new ActionResult<>(EnumActionResult.FAIL, stack);

        if(!player.isCreative()){
            stack.shrink(1);
        }

        player.getCooldownTracker().setCooldown(this, 20);

        String msg = SpellCreation.getMsg(tag.getString("spell"));
        List<String> words = SpellCreation.getSpell(msg);
        String[] spell = words.toArray(new String[0]);
        List<String> set = Lists.newArrayList();
        try {
            set.addAll(Arrays.asList(spell));
            set.remove(Lexicon.par_shape);
            set.remove(Lexicon.shape_inscribe);
        }catch (Exception e){
            Aterna.messageBar(player, "Problem when casting!");
        }
        SpellCreation.createSpell(set, player, player, !world.isRemote);

        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @Override
    public boolean applyConditions() {
        return false;
    }

    @Override
    public boolean canApplyCondition(String condition) {
        return false;
    }

    @Override
    public boolean applyParameters() {
        return false;
    }
}
