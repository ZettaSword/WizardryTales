package astramusfate.wizardry_tales.compats;

import net.minecraft.entity.player.EntityPlayer;
import zettasword.player_mana.cap.ISoul;
import zettasword.player_mana.cap.SoulProvider;

import javax.annotation.Nullable;

public class ManaPlayerCompat {
    @Nullable
    public static ISoul getSoul(EntityPlayer player){
        return player.getCapability(SoulProvider.SOUL_CAP, null);
    }
}
