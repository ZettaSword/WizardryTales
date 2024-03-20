package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Alchemy;
import astramusfate.wizardry_tales.api.Aterna;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Wizard;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class FocusedTeleportation extends Spell {

    public FocusedTeleportation() {
        super(WizardryTales.MODID, "focused_teleportation", SpellActions.IMBUE, false);
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        if(caster != null) {
            ItemStack scroll = caster.getHeldItemOffhand();
            if(EnumHand.OFF_HAND == hand) scroll = caster.getHeldItemMainhand();
            NBTTagCompound nbt = scroll.getTagCompound();
            if(scroll.isEmpty() || nbt == null || !nbt.hasKey("bound")) return false;
            if(nbt.getBoolean("bound")){
                if(nbt.getInteger("id") == world.provider.getDimension()){
                    caster.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
                    if(world.isRemote) {
                        Wizard.conjureCircle(world,Element.SORCERY, caster.getPositionVector());
                        Wizard.castParticles(world,Element.SORCERY, caster.getPositionVector());
                    }
                    double range = 5.0D;
                    double casterMod = 0.0D;
                    range-=Math.min((1-modifiers.get(SpellModifiers.POTENCY)) * 5 - casterMod, 0);
                    caster.setPositionAndUpdate(nbt.getDouble("x") + Solver.range(range), nbt.getDouble("y"), nbt.getDouble("z") + Solver.range(range));
                    if(world.isRemote) {
                        Wizard.conjureCircle(world,Element.SORCERY, new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")));
                        Wizard.castParticles(world, Element.SORCERY, new Vec3d(nbt.getDouble("x"), nbt.getDouble("y"), nbt.getDouble("z")));
                    }
                    caster.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0f, 1.0f);
                    Alchemy.applyPotionHide(caster,50, 0, MobEffects.BLINDNESS);
                    Alchemy.applyPotionHide(caster, Solver.asTicks(10), 0, MobEffects.NAUSEA);
                    caster.getCooldownTracker().setCooldown(scroll.getItem(), 20);
                    return true;
                } else{
                    if(!world.isRemote) Aterna.message(caster, TextFormatting.ITALIC + "" + TextFormatting.GRAY + I18n.format("wizardry_tales:wrong_dim"));
                }
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
