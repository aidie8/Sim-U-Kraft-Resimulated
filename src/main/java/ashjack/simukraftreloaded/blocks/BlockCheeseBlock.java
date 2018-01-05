package ashjack.simukraftreloaded.blocks;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCheeseBlock extends Block {
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    
	public BlockCheeseBlock() {
		super(Material.cactus);
		setBlockName("SUKcheeseBlock");
		setStepSound(Block.soundTypeCloth);
		setHardness(0.1F);
		setResistance(0.5f);
		setBlockTextureName(ModSimukraft.modid + ":" + "cheeseBlock");
		this.setCreativeTab(SimukraftReloadedTabs.SUKTab);
	}

	  @SideOnly(Side.CLIENT)
	  @Override
	    public void registerBlockIcons(IIconRegister iconRegister)
	    {
	        icons = new IIcon[1];
	        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:cheeseblock");
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public IIcon getIcon(int side, int meta)      // getBlockTextureFromSideAndMetadata
	    {
	        return icons[0];
	    }
	    
	    /*@Override
	    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
	    {
	    	Block b = SimukraftReloadedBlocks.blockCheese;
	    	SimukraftReloaded.sendChat(GameRegistry.findUniqueIdentifierFor(SimukraftReloadedBlocks.blockCheese).toString());
	    	return true;
	    }*/
}
