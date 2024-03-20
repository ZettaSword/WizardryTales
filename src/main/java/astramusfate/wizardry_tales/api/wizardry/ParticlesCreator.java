package astramusfate.wizardry_tales.api.wizardry;

import astramusfate.wizardry_tales.WizardryTales;
import electroblob.wizardry.Wizardry;
import net.minecraft.util.ResourceLocation;

public class ParticlesCreator {
    // This was originally an enum, but I think having 'Type' explicitly declared is quite nice so I've left it as a
    // nested class.
    public static class Type {
        /** 3D-rendered expanding ring.<p></p><b>Defaults:</b><<br>Lifetime: 6 ticks<br>Colour: white */
        public static final ResourceLocation RING = new ResourceLocation(WizardryTales.MODID,"ring");

    }
}
