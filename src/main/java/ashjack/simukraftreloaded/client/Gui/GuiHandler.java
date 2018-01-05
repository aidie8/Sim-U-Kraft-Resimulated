package ashjack.simukraftreloaded.client.Gui;

import ashjack.simukraftreloaded.core.References;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		switch(ID)
		{
			case References.GUI_FOLKINVENTORY:
				break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) 
	{
		switch(ID)
		{
			case References.GUI_FOLKINVENTORY:
				break;
		}
		return null;
	}
	
}
