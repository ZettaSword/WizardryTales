package astramusfate.wizardry_tales.items.rituals;

import astramusfate.wizardry_tales.api.Solver;
import astramusfate.wizardry_tales.api.Tenebria;
import astramusfate.wizardry_tales.api.Thief;
import astramusfate.wizardry_tales.api.wizardry.ArcaneColor;
import astramusfate.wizardry_tales.registry.TalesItems;
import com.google.common.collect.Lists;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.construct.EntityFireRing;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IRarity;

import javax.annotation.Nonnull;
import java.util.List;

public class RitualRingOfFire extends ItemRitual {
    public RitualRingOfFire(String name){
        super(name, Element.FIRE);
    }

    @Override
    boolean canCastRitual(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack) {
        return Thief.hasItems(player, getIngredients());
    }

    @Override
    boolean castRitual(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack) {
        BlockPos spellBlock = player.getPosition();
        EntityFireRing entity = new EntityFireRing(world);
        entity.lifetime = Solver.asTicks(120);
        entity.damageMultiplier = 1.2F;
        entity.setCaster(player);
        entity.setSizeMultiplier(2);

        entity.setPositionAndRotation(spellBlock.getX() + 0.5, spellBlock.getY(),
                spellBlock.getZ() + 0.5, player.rotationYaw, player.rotationPitch);

        if (world.isRemote){
            ParticleBuilder.create(ParticleBuilder.Type.BEAM)
                    .pos(spellBlock.getX() + 0.5, world.getActualHeight(),
                            spellBlock.getZ() + 0.5).target(spellBlock.getX() + 0.5, spellBlock.getY(),
                            spellBlock.getZ() + 0.5).scale(9)
                    .clr(ArcaneColor.chooseOld(this.element).getRGB()).time(20).spawn(world);
        }
        Tenebria.create(world, entity);

        world.playSound(player.posX, player.posY, player.posZ, WizardrySounds.ENTITY_ZOMBIE_SPAWNER_SPAWN,
                SoundCategory.PLAYERS, 0.7f, 1.0f, true);

        return true;
    }

    @Override
    void consumeRitualCost(@Nonnull World world, EntityPlayer player, @Nonnull ItemStack stack) {
        Thief.consumeItems(player, getIngredients());
    }

    @Override
    int cooldownTime() {
        return 40;
    }

    @Override
    int chargeUpTime() {
        return 80;
    }

    @Override
    public boolean canBeTraded() {
        return true;
    }

    @Override
    IRarity getItemRarity() {
        return EnumRarity.COMMON;
    }

    @Override
    List<Item> getIngredients() {
        return Lists.newArrayList(TalesItems.mana_dust, Items.COAL, Items.IRON_INGOT);
    }

}
