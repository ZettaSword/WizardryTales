package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.data.Tales;
import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.entity.ExtendedCreature;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

public final class TalesVampirism {

	private TalesVampirism() {}

	public static final String MOD_ID = "vampirism";

	private static boolean vampirismLoaded;

	public static void init() {
		vampirismLoaded = Loader.isModLoaded(MOD_ID);
	}

	public static boolean enabled() {
		return Tales.compat.vampirism && vampirismLoaded;
	}

	public static boolean isVampireSafe(EntityLivingBase player) {
		return Tales.compat.vampirism && vampirismLoaded && Helper.isVampire(player);
	}

	public static boolean isVampire(EntityPlayer player) {
		return Helper.isVampire(player);
	}
	public static void drinkBlood(EntityPlayer player, int blood, float saturation){
		VampirePlayer.get(player).drinkBlood(blood, saturation);
	}
	public static void turnVampire(Entity entity){
		if (entity instanceof EntityCreature) {
			IExtendedCreatureVampirism creature = ExtendedCreature.get((EntityCreature) entity);
			creature.makeVampire();
		}

		if (entity instanceof EntityPlayer) {
			VampirePlayer.get((EntityPlayer) entity).onSanguinareFinished();
		}
	}
}
