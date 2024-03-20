package astramusfate.wizardry_tales.items;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.registry.TalesTabs;
import com.google.common.collect.Sets;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber
public abstract class MagicWeapon extends ItemTool implements ISpellCastingItem {
    protected static final UUID RANGE_MODIFIER = UUID.fromString("b641a364-dae7-4501-9276-991fe276887a");
    public static final Set<Block> AXE_EFFECTIVE = Sets.newHashSet(Blocks.PLANKS, Blocks.BOOKSHELF, Blocks.LOG, Blocks.LOG2, Blocks.CHEST, Blocks.PUMPKIN, Blocks.LIT_PUMPKIN, Blocks.MELON_BLOCK, Blocks.LADDER, Blocks.WOODEN_BUTTON, Blocks.WOODEN_PRESSURE_PLATE);
    public static final Set<Block> EMPTY_EFFECTIVE = Sets.newHashSet();


    public MagicWeapon(float damage, float speed, Set<Block> effective, String name, @Nonnull ToolMaterial material) {
        super(damage, speed, material, effective);
        this.setCreativeTab(TalesTabs.Items);
        this.setRegistryName(WizardryTales.MODID, name);
        this.setUnlocalizedName(WizardryTales.MODID + ":" + name);
        this.maxStackSize=1;
    }

    @Nonnull
    @Override
    public Spell getCurrentSpell(ItemStack stack) {
        return Spells.none;
    }

    @Override
    public boolean showSpellHUD(EntityPlayer player, ItemStack stack) {
        return false;
    }

    @Override
    public boolean canCast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers){
        return false;
    }

    @Override
    public boolean cast(ItemStack stack, Spell spell, EntityPlayer caster, EnumHand hand, int castingTick, SpellModifiers modifiers) {
        return false;
    }

    public abstract void magicAttack(World world, ItemStack stack, EntityLivingBase attacker, EntityLivingBase target);

    @SubscribeEvent
    public static void MagicSwordsAttack(LivingDamageEvent event){
        if(event.getSource().getTrueSource() instanceof EntityPlayer){
            EntityPlayer attacker = (EntityPlayer) event.getSource().getTrueSource();
            EnumHand hand = getMagicSword(attacker);
            if(hand != null){
                ItemStack stack = attacker.getHeldItem(hand);
                MagicWeapon sword = (MagicWeapon)stack.getItem();
                sword.magicAttack(attacker.world, stack, attacker, event.getEntityLiving());
            }
        }
    }

    private static EnumHand getMagicSword(EntityLivingBase player){
        ItemStack left = player.getHeldItemOffhand();
        ItemStack right = player.getHeldItemMainhand();
        if(right.getItem() instanceof MagicWeapon) return EnumHand.MAIN_HAND;
        if(left.getItem() instanceof MagicWeapon) return EnumHand.OFF_HAND;
        return null;
    }


}
