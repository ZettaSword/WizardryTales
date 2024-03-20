package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.data.Tales;
import astramusfate.wizardry_tales.events.RaceListener;
import com.artemis.artemislib.util.attributes.ArtemisLibAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;

public final class TalesArtemis {

	private TalesArtemis() {}

	public static final String MOD_ID = "artemislib";

	private static boolean artemisLibLoaded;

	public static void init() {
		artemisLibLoaded = Loader.isModLoaded(MOD_ID);
	}

	public static boolean enabled() {
		return Tales.compat.artemis_lib && artemisLibLoaded;
	}

	@Nullable
	public static IAttribute getHeightAttribute() {
		return enabled() ? ArtemisLibAttributes.ENTITY_HEIGHT : null;
	}

	@Nullable
	public static IAttribute getWidthAttribute() {
		return enabled() ? ArtemisLibAttributes.ENTITY_WIDTH : null;
	}

	public static void nullAttributes(EntityPlayer player){
		if(TalesArtemis.enabled()){
			for(IAttributeInstance instance : player.getAttributeMap().getAllAttributes()){
				if(instance.getModifier(RaceListener.id) != null) instance.removeModifier(RaceListener.id);
			}
			/*
			if(TalesArtemis.getHeightAttribute() != null && TalesArtemis.getWidthAttribute() != null){
				IAttributeInstance height =
						player.getAttributeMap().getAttributeInstance(TalesArtemis.getHeightAttribute());
				IAttributeInstance width =
						player.getAttributeMap().getAttributeInstance(TalesArtemis.getWidthAttribute());

				if(height.getModifier(RaceClient.id) != null) height.removeModifier(RaceClient.id);
				if(width.getModifier(RaceClient.id) != null) width.removeModifier(RaceClient.id);
			}*/
		}
	}

}
