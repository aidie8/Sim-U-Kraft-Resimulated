package ashjack.simukraftreloaded.blocks.gases;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCarbonDioxide extends BlockGas
{

	public BlockCarbonDioxide() 
	{
		super();
		setRiseRate(5);
		setTickRandomly(true);
	    disableStats();
	    setHardness(0.0F);
	    //setCreativeTab(CreativeTabs.tabMisc);
	}
	
	  
}
