package ashjack.simukraftreloaded.client.Gui.other;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toServer.GenerateFolkPacket;
import cpw.mods.fml.common.FMLCommonHandler;

public class GuiRunMod extends GuiScreen
{
    public boolean running = true;
    private int mouseCount = 0;

    @Override
    public boolean doesGuiPauseGame()
    {
        return true;
    }

    public GuiRunMod()
    {
    }

    @Override
    public void initGui()
    {   System.out.println("Initializing GUI");
        buttonList.add(new GuiButton(0, (width / 2 - 75) , 40, "Do NOT run Sim-U-Kraft"));
        buttonList.add(new GuiButton(1, (width / 2 - 75) , 90, "Normal Mode"));
        buttonList.add(new GuiButton(2, (width / 2 - 75) , 140, "Creative Mode"));
        buttonList.add(new GuiButton(3, (width / 2 - 75) , 190, "Hardcore Mode"));
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
    	//System.out.println("Preparing to draw screen");
        try
        {
            if (mouseCount < 10)
            {
                mouseCount++;
                Mouse.setGrabbed(false);
            }

            drawDefaultBackground();
            //System.out.println("Drawing Strings");
            drawCenteredString(fontRendererObj, "Please choose the game mode for Sim-U-Kraft", width / 2, 20, 0xffffff);
            drawCenteredString(fontRendererObj, "This mode switches off Sim-U-kraft for this world", width / 2, 60, 0xffff00);
            drawCenteredString(fontRendererObj, "Ideal for beginners and experts. Not too challenging.", width / 2, 110, 0xffff00);
            drawCenteredString(fontRendererObj, "No money needed, everything free, no blocks required, be creative!", width / 2, 160, 0xffff00);
            drawCenteredString(fontRendererObj, "Builders require ALL blocks, harder gameplay", width / 2, 210, 0xffff00);
            
            
            
        }
        catch (Exception e)
        {
        	System.out.println("Caught Exception while drawing strings/screen");
        }

        super.drawScreen(i, j, f);
    }

    @Override
    protected void actionPerformed(GuiButton guibutton)
    {
    	if(Minecraft.getMinecraft().theWorld.isRemote)
    	{
    		if (guibutton.id == 0)   //do not run
    		{
    			// SimukraftReloaded.states.runMod = 0;
    			SimukraftReloaded.states.gameModeNumber = 0;
    			System.out.println("Turning off Sim-U-Kraft Reloaded");
    		}
    		else if (guibutton.id == 1)      // normal
    		{
    			// SimukraftReloaded.states.runMod = 1;
    			SimukraftReloaded.states.gameModeNumber = 0;
    			//ModSimukraft.proxy.getClientWorld().playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:welcome", 1.0f, 1.0f, false);
    			System.out.println("Playing Sim-U-Kraft Reloaded in normal mode");
    			PacketHandler.net.sendToServer(new GenerateFolkPacket(FMLCommonHandler.instance().getMinecraftServerInstance().getEntityWorld(), false));
    		}
    		else if (guibutton.id == 2)    //creative
    		{
    			// SimukraftReloaded.states.runMod = 1;
    			SimukraftReloaded.states.gameModeNumber = 1;
    			//ModSimukraft.proxy.getClientWorld().playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:welcome", 1.0f, 1.0f, false);
    		}
    		else if (guibutton.id == 3)    //hardcore
    		{
    			//  SimukraftReloaded.states.runMod = 1;
    			SimukraftReloaded.states.gameModeNumber = 2;
    			//ModSimukraft.proxy.getClientWorld().playSound(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:welcome", 1.0f, 1.0f, false);
    		}

    		SimukraftReloaded.states.saveStates();
    		this.running = false;
    		mc.currentScreen = null;
    		mc.setIngameFocus();
    	}
    }
}
