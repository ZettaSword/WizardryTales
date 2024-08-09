package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesEffects;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class Phasing extends SpellBuff {
    /** A {@code ResourceLocation} representing the shader file used when possessing an entity. */
    public static final ResourceLocation SHADER = new ResourceLocation(WizardryTales.MODID, "shaders/post/phasing.json");

    public Phasing() {
        super(WizardryTales.MODID, "phasing", 0f, 1.0f, 40/255f, () -> TalesEffects.phasing);
    }

    /**
     * Whether this spell requires a packet to be sent when it is cast. Returns true by default, but can be overridden
     * to return false <b>if</b> the spell's cast() method does not use any code that must be executed client-side (i.e.
     * particle spawning). This is not checked for continuous spells, because they never need to send packets.
     * <p></p>
     * <i>If in doubt, leave this method as is; it is purely an optimisation.</i>
     *
     * @return <b>false</b> if the spell code should only be run on the server and the client of the player casting
     * it<br>
     * <b>true</b> if the spell code should be run on the server and all clients in the dimension
     */
    @Override
    public boolean requiresPacket() {
        return true;
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        if (super.cast(world, caster, hand, ticksInUse, modifiers)) {
            if (caster.world.isRemote) {
                // Shaders and effects
                //Wizardry.proxy.playBlinkEffect(caster);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
