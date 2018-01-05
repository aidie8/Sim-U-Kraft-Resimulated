package ashjack.simukraftreloaded.core.registry;

import ashjack.simukraftreloaded.creativeTab.CreativeTabSUK;
import net.minecraft.creativetab.CreativeTabs;

public class SimukraftReloadedTabs 
{
	public static CreativeTabs SUKTab;
	
	public static void loadCreativeTabs()
	{
		SUKTab = new CreativeTabSUK("SUK");
	}
}
