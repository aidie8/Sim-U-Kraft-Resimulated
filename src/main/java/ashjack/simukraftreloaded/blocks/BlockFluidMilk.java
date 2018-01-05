package ashjack.simukraftreloaded.blocks;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

public class BlockFluidMilk extends BlockFluidClassic {
	private IIcon icons[];
	
	public BlockFluidMilk() {
		super(SimukraftReloadedBlocks.SUKfluidMilk, Material.water);
		setBlockName("fluidMilk");
		//ModSimukraft.SUKfluidMilk.setBlock(id); // Set the fluids block ID to this block.
		this.setCreativeTab(SimukraftReloadedTabs.SUKTab);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
    public void registerBlockIcons(IIconRegister iconRegister) {
    	icons=new IIcon[2];
    	icons[0] = iconRegister.registerIcon("ashjacksimukraftreloaded:milk_still");
    	icons[1] = iconRegister.registerIcon("ashjacksimukraftreloaded:milk_flow");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
    	if (meta>=1) {
    		return icons[1];
    	} else {
    		return icons[0];
    	}
    }

   
    

}
