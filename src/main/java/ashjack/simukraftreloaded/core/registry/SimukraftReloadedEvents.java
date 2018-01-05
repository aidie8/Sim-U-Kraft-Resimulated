package ashjack.simukraftreloaded.core.registry;

import ashjack.simukraftreloaded.core.UpdateChecker;
import cpw.mods.fml.common.FMLCommonHandler;

public class SimukraftReloadedEvents 
{
	public static void loadEvents()
	{
		FMLCommonHandler.instance().bus().register(new UpdateChecker()); 
	}
}
