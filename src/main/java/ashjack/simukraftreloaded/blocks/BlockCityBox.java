package ashjack.simukraftreloaded.blocks;

import java.util.Random;

import ashjack.simukraftreloaded.client.Gui.blocks.GuiBankATM;
import ashjack.simukraftreloaded.client.Gui.blocks.GuiCityBox;
import ashjack.simukraftreloaded.client.Gui.blocks.GuiControlBox;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockCityBox extends Block
{
	@SideOnly(Side.CLIENT)
    private IIcon[] icons;
	
	public BlockCityBox() 
	 {
		super(Material.wood);
		setStepSound(Block.soundTypeWood);
        setHardness(10F);
        setResistance(1.0F);
        setBlockName("SUKcitybox");
        //this.setCreativeTab(CreativeTabs.tabMisc);
	}
	 
	 @SideOnly(Side.CLIENT)
	  @Override
	    public void registerBlockIcons(IIconRegister iconRegister)
	    {
	        icons = new IIcon[1];
	        icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:blockPath");
	    }

	    @Override
	    @SideOnly(Side.CLIENT)
	    public IIcon getIcon(int side, int meta)      // getBlockTextureFromSideAndMetadata
	    {
	        return icons[0];
	    }
	    
	    
	    @Override
	    @SideOnly(Side.CLIENT)
	    public boolean onBlockActivated(World world, int i, int j, int k, EntityPlayer entityplayer,
	                                    int par6, float par7, float par8, float par9)
	    {
	        world.playSoundEffect(i, j, k, "ashjacksimukraftreloaded:computer", 1f, 1f);
	        GuiCityBox ui = null;
	        Minecraft mc = Minecraft.getMinecraft();
	        mc.setIngameNotInFocus();
	        
	        ui = new GuiCityBox(new V3((double)i, (double)j, (double)k, entityplayer.dimension), entityplayer);
	        mc.displayGuiScreen(ui);

	        return true;
	    }

	    @Override
	    public int quantityDropped(Random random)
	    {
	        return 0;   // no recipe and no drop
	    }

}
