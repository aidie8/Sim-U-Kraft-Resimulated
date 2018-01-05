package ashjack.simukraftreloaded.blocks.gases;

import net.minecraft.creativetab.CreativeTabs;

public class BlockSulphurDioxide extends BlockGas
{
	public BlockSulphurDioxide() 
	{
		super();
		setRiseRate(5);
		setTickRandomly(true);
	    disableStats();
	    setHardness(0.0F);
	    //setCreativeTab(CreativeTabs.tabMisc);
	}
}
