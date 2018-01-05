package ashjack.simukraftreloaded.client.Gui;

import net.minecraft.client.gui.GuiMainMenu;

public class SimukraftMenuWarning extends GuiMainMenu
{
	
	@Override
	public void initGui()
    {
		drawString(mc.fontRenderer, "YOU MUST RELOAD YOUR CLIENT BEFORE PLAYING ON ANOTHER WORLD", width / 2, height / 2, 0xff0000);
    }
}
