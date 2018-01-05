package ashjack.simukraftreloaded.creativeTab;

import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabSUK extends CreativeTabs{

	public CreativeTabSUK(String name) 
	{
		super(name);
	}
	
	@Override
	public Item getTabIconItem() 
	{
		return Item.getItemFromBlock(SimukraftReloadedBlocks.buildingConstructor);
	}

}
