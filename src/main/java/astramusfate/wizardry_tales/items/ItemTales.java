package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.Wizardry;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemTales extends Item {

    public ItemTales(int count) {
        super();
        setMaxStackSize(count);
        setCreativeTab(TalesTabs.Items);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World world, @Nonnull List<String> list, @Nonnull ITooltipFlag tooltipFlag) {
        super.addInformation(stack, world, list, tooltipFlag);
        list.add(Wizardry.proxy.translate("item." + Objects.requireNonNull(this.getRegistryName()) + ".desc"));
    }
}
