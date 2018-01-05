package ashjack.simukraftreloaded.client.Gui.blocks;

import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.client.Gui.other.GuiBeamPlayerTo;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;

public class GuiCityBox extends GuiScreen
{
	private int mouseCount = 0;
	
	public boolean isSetup = false;

    /** location of this control box */
    public V3 location;
    /** reference to the building object this control box is attached to */
    public Building theBuilding = null;
    /** hold a reference to a folk that is passed in */
    public FolkData theFolk = null;
    private EntityPlayer playerWhoClickedIt = null;
    GuiCityBoxTaxes taxesGui = null;
    
    private GuiTextField villageNamer;
    
    /** regular constructor */
    public GuiCityBox(V3 location, EntityPlayer thePlayer)
    {
        this.location = location.clone();
        Building.loadAllBuildings(); //re-load the buildings client side
        theBuilding = Building.getBuilding(location);
        playerWhoClickedIt = thePlayer;
    }
    
    @Override
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void initGui()
    {
    	if(isSetup)
    	{
        	buttonList.clear();
        	buttonList.add(new GuiButton(0, 5, 5, 50, 20, "Done"));
        	buttonList.add(new GuiButton(30, width - 110, height - 30, 100, 20, "Beam me to.."));
        
        	buttonList.add(new GuiButton(1, width/2 -50, height/2 -70 , 100, 20, "Taxes and Rent"));
        	buttonList.add(new GuiButton(3, width/2 -50, height/2 -30 , 100, 20, "Working Hours"));
        	buttonList.add(new GuiButton(4, width/2 -50, height/2 +10 , 100, 20, "Information"));
    	}
    	
    	else
    	{
    		buttonList.clear();
    		buttonList.add(new GuiButton(0, 5, 5, 50, 20, "Done"));
    		
    		buttonList.add(new GuiButton(5, width/2 -50, height/2 -70 , 100, 20, "Declare New Village"));
    	}
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
            drawCenteredString(fontRendererObj, "Town Control Panel", width / 2, 17, 0xffffff);
                     
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
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        
        else if (guibutton.id == 30)   //Beam me to...
        {
            GuiScreen ui = new GuiBeamPlayerTo(playerWhoClickedIt);
            mc.displayGuiScreen(null);
            mc.displayGuiScreen(ui);
        }
        
        else if (guibutton.displayString.contentEquals("Taxes and Rent"))
        {
        	taxesGui = new GuiCityBoxTaxes(this.location, this.playerWhoClickedIt, this);
	        mc.displayGuiScreen(taxesGui);
        }
        
        else if (guibutton.displayString.contentEquals("Rent"))
        {
        	taxesGui = new GuiCityBoxTaxes(this.location, this.playerWhoClickedIt, this);
	        mc.displayGuiScreen(taxesGui);
        }
        
        else if (guibutton.displayString.contentEquals("Working Hours"))
        {
        	taxesGui = new GuiCityBoxTaxes(this.location, this.playerWhoClickedIt, this);
	        mc.displayGuiScreen(taxesGui);
        }
        
        else if (guibutton.displayString.contentEquals("Information"))
        {
        	taxesGui = new GuiCityBoxTaxes(this.location, this.playerWhoClickedIt, this);
	        mc.displayGuiScreen(taxesGui);
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
