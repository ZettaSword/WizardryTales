package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.registry.TalesTabs;
import baubles.api.BaubleType;
import baubles.api.IBauble;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public abstract class TalesBauble extends Item implements IBauble {

    private String description = "";
    public TalesBauble(String name) {
        super();
        this.setRegistryName(WizardryTales.MODID, name);
        this.setUnlocalizedName(WizardryTales.MODID + ":" + name);
        this.setCreativeTab(TalesTabs.Items);
        this.setMaxStackSize(1);
    }

    public TalesBauble(String name, String description) {
        this.description=description;
        this.setRegistryName(WizardryTales.MODID, name);
        this.setUnlocalizedName(WizardryTales.MODID + ":" + name);
        this.setCreativeTab(TalesTabs.Items);
        this.setMaxStackSize(1);
    }

    public boolean canBeTraded(){return true;}

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, net.minecraft.client.util.ITooltipFlag advanced){
        if(!description.equals("")) {
            tooltip.add(I18n.format(description));
        }else{
            tooltip.add(I18n.format(getUnlocalizedName() + ".desc"));
        }
    }

    protected abstract BaubleType type();

    @Override
    public BaubleType getBaubleType(ItemStack itemStack) {
        return type();
    }
}
