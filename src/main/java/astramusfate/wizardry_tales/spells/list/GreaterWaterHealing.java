package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellAreaEffect;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GreaterWaterHealing extends SpellAreaEffect {
    public GreaterWaterHealing() {
        super(WizardryTales.MODID, "great_water_healing", SpellActions.POINT_UP, false);
        this.addProperties(DURATION, "regen_power");
        this.targetAllies(true);
    }

    /**
     * Called to do something to each entity within the spell's area of effect.
     *
     * @param world       The world in which the spell was cast.
     * @param origin      The position the spell was cast from.
     * @param caster      The entity that cast the spell, or null if it was cast from a position.
     * @param target      The entity to do something to.
     * @param targetCount The number of targets that have already been affected, useful for spells with a target limit.
     *                    Targets will be called in order of distance from the caster/origin,
     * @param ticksInUse  The number of ticks the spell has already been cast for.
     * @param modifiers   The modifiers the spell was cast with.
     * @return True if whatever was done to the entity was successful, false if not.
     */
    @Override
    protected boolean affectEntity(World world, Vec3d origin, @Nullable EntityLivingBase caster, EntityLivingBase target, int targetCount, int ticksInUse, SpellModifiers modifiers) {
        if(target != null) {
            Aterna.playSound(world, target.getPosition(), WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND);
            target.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, (int)(getProperty(DURATION).intValue() * modifiers.get(SpellModifiers.POTENCY)), getProperty("regen_power").intValue()-1));
            if(world.isRemote){
                Wizard.conjureCircle(world, Element.ICE, target.getPositionVector());
            }
            return true;
        }
        return false;
    }

    /**
     * Called from the constructor to initialise this spell's sounds. By default, this creates and returns a 1-element
     * array containing a single sound event called {@code spell.[unlocalised name]}. Override this to add a custom
     * sound array, perhaps using one of the convenience methods (see below).
     *
     * @return An array of sound events played by this spell.
     * @see Spell#createSoundWithSuffix(String)
     * @see Spell#createContinuousSpellSounds
     * @see Spell#playSound(World, double, double, double, int, int, SpellModifiers, String...)
     */
    @Override
    protected SoundEvent[] createSounds() {
        return new SoundEvent[]{WizardrySounds.BLOCK_ARCANE_WORKBENCH_SPELLBIND};
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
