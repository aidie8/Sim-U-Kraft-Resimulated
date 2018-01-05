package ashjack.simukraftreloaded.blocks.gases;

import net.minecraft.creativetab.CreativeTabs;

public class BlockRadiationGas extends BlockGas
{
	public BlockRadiationGas() 
	{
		super();
		setRiseRate(3);
		setTickRandomly(true);
	    disableStats();
	    setHardness(0.0F);
	    //setCreativeTab(CreativeTabs.tabMisc);
	}
}
