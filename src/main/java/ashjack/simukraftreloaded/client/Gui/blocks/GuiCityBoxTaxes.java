package ashjack.simukraftreloaded.client.Gui.blocks;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.client.Gui.other.GuiBeamPlayerTo;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCityBoxTaxes extends GuiScreen
{
	private int mouseCount = 0;
	private EntityPlayer playerWhoClickedIt = null;
	private GuiCityBox cityBoxGui;
	private int guiID = 0;
	
	int taxPercentage;
	
	 /** regular constructor */
    public GuiCityBoxTaxes(V3 location, EntityPlayer thePlayer, GuiCityBox gui)
    {
        playerWhoClickedIt = thePlayer;
        cityBoxGui=gui;
    }
	
	@Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, 5, 5, 50, 20, "Back"));
        
        buttonList.add(new GuiButton(1, width/2, height/2-40, 60, 20, String.valueOf(taxPercentage)));
        
    }
    
    @Override
    public void drawScreen(int i, int j, float f)
    {
        try
        {
            if (mouseCount < 10)
            {
                mouseCount++;
                Mouse.setGrabbed(false);
            }

            drawDefaultBackground();
            drawCenteredString(fontRendererObj, "Town Taxes", width / 2, 17, 0xffffff);
            
            super.drawScreen(i, j, f);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void actionPerformed(GuiButton guibutton)
    {
        if (!guibutton.enabled)
        {
            return;
        }

        if (guibutton.id == 0) // /cancel button
        {
            mc.currentScreen = cityBoxGui;
            return;
        }
    }
	
	 @Override
	    public void onGuiClosed()
	    {
	        Keyboard.enableRepeatEvents(false);
	        mc.setIngameFocus();
	    }

	    @Override
	    public void keyTyped(char c, int i)
	    {
	        if (i == 1) //escape
	        {
	            mc.displayGuiScreen(null);
	            mc.setIngameFocus();
	            return;
	        }
	    }
}
