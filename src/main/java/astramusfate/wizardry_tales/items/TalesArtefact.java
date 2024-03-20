package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import astramusfate.wizardry_tales.registry.TalesTabs;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.Spells;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

@Mod.EventBusSubscriber(modid = WizardryTales.MODID)
public class TalesArtefact extends ItemArtefact {

    private String description = "";
    public TalesArtefact(String name ,EnumRarity rarity, Type type) {
        super(rarity, type);
        this.setRegistryName(WizardryTales.MODID, name);
        this.setUnlocalizedName(WizardryTales.MODID + ":" + name);
        this.setCreativeTab(TalesTabs.Items);
        this.setMaxStackSize(1);
    }

    public TalesArtefact(String name, EnumRarity rarity, Type type, String description) {
        super(rarity, type);
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

    @SubscribeEvent
    public static void artefactsActivityEvent(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START && Solver.doEvery(event.player.ticksExisted, 0.25)) {

            EntityPlayer player = event.player;
            World world = player.world;
            List<ItemArtefact> artefacts = getActiveArtefacts(player);

            for (ItemArtefact artefact : artefacts) {

                if(artefact == TalesItems.ring_protector){
                        if(player.getHealth() < player.getMaxHealth() * 0.7f && !player.isPotionActive(MobEffects.RESISTANCE)){
                            if(!findMatchingWandAndCast(player, Spells.diamondflesh))
                                if(!findMatchingWandAndCast(player, Spells.ironflesh))
                                    findMatchingWandAndCast(player, Spells.oakflesh);
                    }
                }else if(artefact == TalesItems.ring_new_moon){
                    if(player.getHealth() != player.getMaxHealth() && !world.isDaytime()){
                        Alchemy.applyPotion(player, Solver.asTicks(5), MobEffects.REGENERATION);
                    }
                }
            }

        }
    }
}
