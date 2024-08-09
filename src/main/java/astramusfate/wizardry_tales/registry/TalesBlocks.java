package astramusfate.wizardry_tales.registry;

import astramusfate.wizardry_tales.WizardryTales;
import astramusfate.wizardry_tales.blocks.BlockBloomingFlower;
import astramusfate.wizardry_tales.blocks.BlockConjuredAir;
import astramusfate.wizardry_tales.blocks.tile.TileEntityBlooming;
import astramusfate.wizardry_tales.blocks.tile.TileEntityRevertingBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(WizardryTales.MODID)
@Mod.EventBusSubscriber
public final class TalesBlocks {

	private TalesBlocks(){} // No instances!

	// Found a very nice way of registering things using arrays, which might make @ObjectHolder actually useful.
	// http://www.minecraftforge.net/forum/topic/49497-1112-is-using-registryevent-this-way-ok/

	// Anything set to use the material 'air' will not render, even with a TESR!

	// setSoundType should be public, but in this particular version it isn't... which is a bit of a pain.

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder(){ return null; }

	@ObjectHolder("alchemy_receptacle")
	public static final Block alchemy_receptacle = placeholder();

	@ObjectHolder("alchemy_table")
	public static final Block alchemy_table = placeholder();

	@ObjectHolder("blooming_flower")
	public static final Block blooming_flower = placeholder();

	@ObjectHolder("conjured_air")
	public static final Block conjured_air = placeholder();

	/**
	 * Sets both the registry and unlocalised names of the given block, then registers it with the given registry. Use
	 * this instead of {@link Block#setRegistryName(String)} and {@link Block#setUnlocalizedName(String)} during
	 * construction, for convenience and consistency.
	 *
	 * @param name The name of the block, without the mod ID or the .name stuff. The registry name will be
	 *        {@code ebwizardry:[name]}. The unlocalised name will be {@code tile.ebwizardry:[name].name}.
	 * @param block The block to register.
	 */
	public static void registerBlock(IForgeRegistry<Block> registry, String name, Block block){
		block.setRegistryName(WizardryTales.MODID, name);
		block.setUnlocalizedName(WizardryTales.MODID + ":" + name);
		registry.register(block);
	}

	@SubscribeEvent
	public static void registerTalesBlocks(RegistryEvent.Register<Block> event) {
		IForgeRegistry<Block> reg = event.getRegistry();
		//BlockBookshelf.initBookProperties();

		//registerBlock("alchemy_receptacle", new BlockAlchemyReceptacle());
		//registerBlock("alchemy_table", new BlockAlchemyTable());
		registerBlock(reg,"blooming_flower", new BlockBloomingFlower(Material.PLANTS));
		registerBlock(reg, "conjured_air", new BlockConjuredAir());
	}

	@SideOnly(Side.CLIENT)
	public static void initRenders() {
		setRender(blooming_flower);
	}

	@SideOnly(Side.CLIENT)
	private static void setRender(Block block) {
		if(block.getRegistryName() != null) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(
					Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
	}

	/** Called from the preInit method in the main mod class to register all the tile entities. */
	public static void registerTileEntities(){
		GameRegistry.registerTileEntity(TileEntityBlooming.class, new ResourceLocation(WizardryTales.MODID, "blooming_flower"));
		GameRegistry.registerTileEntity(TileEntityRevertingBlock.class, new ResourceLocation(WizardryTales.MODID, "reverting_tile"));
		//GameRegistry.registerTileEntity(TileEntityAlchemyReceptacle.class, 		new ResourceLocation(WizardryTales.MODID, "alchemy_receptacle"));
		//GameRegistry.registerTileEntity(TileEntityAlchemyTable.class, 	new ResourceLocation(WizardryTales.MODID, "alchemy_table"));
	}
}