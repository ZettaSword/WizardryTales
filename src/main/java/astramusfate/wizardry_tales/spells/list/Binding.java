package astramusfate.wizardry_tales.spells.list;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.registry.TalesItems;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class Binding extends Spell {

    public Binding() {
        super(WizardryTales.MODID, "binding", SpellActions.IMBUE, false);
    }

    @Override
    public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
        ItemStack bind = caster.getHeldItem(revertHand(hand)).isEmpty() ? caster.getHeldItem(hand) : caster.getHeldItem(revertHand(hand));
        NBTTagCompound tag = getTagSafe(bind);
        if(bind.hasTagCompound() && !tag.hasKey("owner")) {

            if(!world.isRemote){
                tag.setString("owner", caster.getDisplayNameString());
                bind.setTagCompound(tag);
            }

            if(world.isRemote){
                for(int i = 0; i<15;i++) {
                    double x = caster.posX + Solver.randFloat(-1.5f, 1.5f);
                    double y = caster.posY + caster.getEyeHeight() + Solver.randFloat(-1.5f, -1.25f);//+ Solver.randFloat(-1.5f, 0.5f);
                    double z = caster.posZ + Solver.randFloat(-1.5f, 1.5f);
                    ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0xFFD700).spawn(world); }
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
        if(caster != null) {
            ItemStack bind = caster.getHeldItem(revertHand(hand)).isEmpty() ? caster.getHeldItem(hand) : caster.getHeldItem(revertHand(hand));
            NBTTagCompound tag = getTagSafe(bind);
            if(bind.hasTagCompound() && !tag.hasKey("owner")) {
                if(!world.isRemote){ tag.setString("owner", caster.getDisplayName().toString()); bind.setTagCompound(tag);}

                if(world.isRemote){
                    for(int i = 0; i<15;i++) {
                        double x = caster.posX + Solver.randFloat(-1.5f, 1.5f);
                        double y = caster.posY + caster.getEyeHeight() + Solver.randFloat(-1.5f, -1.25f);//+ Solver.randFloat(-1.5f, 0.5f);
                        double z = caster.posZ + Solver.randFloat(-1.5f, 1.5f);
                        ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, -0.1, 0).time(15).clr(0xFFD700).spawn(world); }
                }
                return true;
            }
        }
        return false;
    }

    public EnumHand revertHand(EnumHand hand){
        return hand == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    @Nonnull
    public NBTTagCompound getTagSafe(ItemStack stack){
        if(!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        assert stack.getTagCompound() != null;
        return stack.getTagCompound().copy();
    }

    @Override
    public boolean applicableForItem(Item item) {
        return item == TalesItems.tales_book || item == TalesItems.tales_scroll;
    }
}
