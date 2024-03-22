package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.entity.living.EntityTenebria;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class ItemTenebriaCookie extends ItemFood {
    public ItemTenebriaCookie() {
        super(2, 0.1F, false);
        this.setUnlocalizedName("cookie");
        this.setRegistryName(WizardryTales.MODID, "tenebria_cookie");
    }

    @Override
    protected void onFoodEaten(ItemStack stack, World world, EntityPlayer player) {
        super.onFoodEaten(stack, world, player);
        Alchemy.applyPotionHide(player, Solver.asTicks(10),0, MobEffects.POISON);
        Alchemy.applyPotionHide(player, Solver.asTicks(10),0, MobEffects.REGENERATION);
        Alchemy.applyPotionHide(player, Solver.asTicks(10),1, MobEffects.NAUSEA);
        Alchemy.applyPotionHide(player, Solver.asTicks(15),0, MobEffects.BLINDNESS);
        if (!world.isRemote){
            Aterna.dialogue(player, TextFormatting.DARK_PURPLE,"Tenebria", TextFormatting.DARK_PURPLE + ":D");
            EntityTenebria tenebria = new EntityTenebria(world);
            // Try and find a nearby floor space
            BlockPos pos = BlockUtils.findNearbyFloorSpace(player, 10, 10*2);
            if (pos == null) pos = player.getPosition();
            tenebria.setPosition(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
            world.spawnEntity(tenebria);
        }
    }
}
