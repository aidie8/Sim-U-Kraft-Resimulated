package ashjack.simukraftreloaded.client.Gui.blocks;

import java.util.ArrayList;

import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.blocks.functionality.PathBox;
import ashjack.simukraftreloaded.client.Gui.folk.GuiEmployFolk;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;


/////NOTE : This box was never implimented, was going to make paths and bridges

public class GuiPathBox extends GuiScreen
{
    ArrayList<FolkData> theWorkers = new ArrayList<FolkData>();
    PathBox thePathBox = null;
    private GuiTextField tfSize;
    private int mouseCount = 0;
    private int page = 0;

    public GuiPathBox(PathBox pathBlock, ArrayList<FolkData> folks)
    {
        this.thePathBox = pathBlock;
        theWorkers = folks; /// is null before employing someone
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void updateScreen()
    {
        if (tfSize != null)
        {
            tfSize.updateCursorCounter();
        }
    }

    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 30, "Done"));

        if (thePathBox == null)
        {
            return;
        }

        if (thePathBox.marker1XYZ == null)
        {
            return;
        }

        if (page == 0)
        {
            if (theWorkers == null || theWorkers.size() == 0)
            {
                buttonList.add(new GuiButton(1, width / 2 - 100, 40, "Hire Path Builder"));
            }
            else
            {
                buttonList.add(new GuiButton(1, width / 2 - 100, 40, "Fire " + theWorkers.get(0).name));
                buttonList.add(new GuiButton(2, width / 2 - 100, 60, "Choose path type"));
            }
        }
        else if (page == 1)
        {
            buttonList.add(new GuiButton(1, 10, 20, "Wooden Bridge"));
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
            drawCenteredString(fontRendererObj, "Path Constructor", width / 2, 17, 0xffffff);

            try
            {
                if (thePathBox.marker1XYZ == null)
                {
                    drawCenteredString(fontRendererObj, "Error: No marker placed - place a marker down first",
                                       width / 2, 27, 0xff0000);
                }
            }
            catch (Exception e)
            {
                drawCenteredString(fontRendererObj, "Error: No marker placed - place a marker down first",
                                   width / 2, 27, 0xff0000);
            }

            super.drawScreen(i, j, f);
        }
        catch (Exception e)
        {
            e.printStackTrace();   //get a NPE for first few ticks on drawbackground
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
        else if (guibutton.displayString.contentEquals("Hire Path Builder"))
        {
            GuiEmployFolk ui = new GuiEmployFolk(thePathBox, Vocation.PATHBUILDER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.startsWith("Fire "))
        {
            for (int i = 0; i < theWorkers.size(); i++)
            {
                FolkData folk = theWorkers.get(i);
                folk.selfFire();
            }

            guibutton.enabled = false;
            mc.currentScreen = null;
            mc.setIngameFocus();
        }
        else if (guibutton.displayString.contentEquals("Choose path type"))
        {
            page = 1;
            initGui();
        }
        else if (page == 1)
        {
            thePathBox.pathType = guibutton.displayString;
            SimukraftReloaded.sendChat("Path constructor set to " + guibutton.displayString);
            mc.currentScreen = null;
            mc.setIngameFocus();
        }
    }

    @Override
    public void keyTyped(char c, int i)
    {
        if (i == 1)
        {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
            return;
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        super.mouseClicked(i, j, k);
    }
}
