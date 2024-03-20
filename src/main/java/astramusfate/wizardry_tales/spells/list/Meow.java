package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Selena;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellBuff;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import java.util.List;

public class Meow extends Spell {
    public static final String NBT_KEY = "fearedEntity";
    public static final String DURATION = "duration";

    public Meow() {
        super(WizardryTales.MODID, "meow", SpellActions.POINT_DOWN, false);
        this.addProperties(RANGE, DURATION);
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        if(caster != null) {
            List<EntityCreeper> creepers = Selena.getAround(world, getProperty(RANGE).intValue() * modifiers.get(SpellModifiers.POTENCY), caster.getPosition(), EntityCreeper.class);
            if(creepers.isEmpty()) return false;
            for (EntityCreeper creep : creepers) {
                int bonusAmplifier = SpellBuff.getStandardBonusAmplifier(modifiers.get(SpellModifiers.POTENCY));

                NBTTagCompound entityNBT = creep.getEntityData();
                entityNBT.setUniqueId(NBT_KEY, caster.getUniqueID());

                Alchemy.applyPotionHide(creep,
                        (int) (getProperty(DURATION).floatValue() * modifiers.get(WizardryItems.duration_upgrade)),
                        bonusAmplifier, WizardryPotions.fear);

                Wizard.conjureCircle(world, Element.EARTH, creep.getPositionVector());
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
