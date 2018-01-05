package ashjack.simukraftreloaded.client.Gui.blocks;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.common.jobs.JobTerraformer;
import ashjack.simukraftreloaded.common.jobs.JobTerraformer.TerraformerType;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;

public class GuiTerraform extends GuiScreen
{
    FolkData theFolk;
    GuiTextField tfRadius;
    private int mouseCount = 0;

    public GuiTerraform(FolkData folk)
    {
        theFolk = folk;
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 30, "Cancel Request"));
        buttonList.add(new GuiButton(1, width / 2 - 200, 30, 200, 20, "'Sealand' (water to land)"));
        buttonList.add(new GuiButton(2, width / 2 - 200, 50, 200, 20, "'Nature' (plants saplings)"));
        buttonList.add(new GuiButton(3, width / 2 - 200, 70, 200, 20, "'Lawnmower' (cuts all long grass)"));
        buttonList.add(new GuiButton(4, width / 2 - 200, 90, 200, 20, "'Flattenizer' (flatten area)"));
        buttonList.add(new GuiButton(5, width / 2 - 200, 110, 200, 20, "'Value Pack' (single layer of dirt)"));
        buttonList.add(new GuiButton(6, width / 2 , 30, 200, 20, "'Glacial' (Freeze water, add snow)"));
        buttonList.add(new GuiButton(7, width / 2 , 50, 200, 20, "'Moisturizer' (Adds water to lava)"));
        buttonList.add(new GuiButton(8, width / 2 , 70, 200, 20, "'Thermalizer' (Collects lava)"));
        buttonList.add(new GuiButton(9, width / 2 , 90, 200, 20, "'De-icer' (Removes snow)"));
        
        tfRadius = new GuiTextField(fontRendererObj, width / 2 - 50, height - 55, 100, 20);
        tfRadius.setMaxStringLength(5);
        tfRadius.setText("30");
    }

    private String errorText = "";
    public void drawScreen(int i, int j, float f)
    {
        if (mouseCount < 10)
        {
            mouseCount++;
            Mouse.setGrabbed(false);
        }

        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Terraforming - Please choose a Terraforming theme", width / 2, 17, 0xffffff);
        drawCenteredString(fontRendererObj, "Radius (1 to 60)", width / 2, height - 70, 0xffffff);
        drawCenteredString(fontRendererObj, errorText, width / 2, height - 80, 0xff8080);
        tfRadius.drawTextBox();
        super.drawScreen(i, j, f);
    }

    public void updateScreen()
    {
        tfRadius.updateCursorCounter();
    }

    public void actionPerformed(GuiButton guibutton)
    {
        JobTerraformer theJob;

        try
        {
            theJob = (JobTerraformer) theFolk.theirJob;
        }
        catch (Exception e)
        {
            SimukraftReloaded.sendChat("Error: You must hire a terraformer, not a builder");
            return;
        }

        if (guibutton.id == 0) ///cancel button
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }

        if (Integer.parseInt(tfRadius.getText().trim()) > 60)
        {
            errorText = "ERROR: the radius must be 60 or less";
            return;
        }
        else if (guibutton.id == 1)   ///water > dirt option
        {
            theFolk.terraformerType = TerraformerType.WATERTODIRT;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 2)    // nature - plant trees and flowers
        {
            theFolk.terraformerType = TerraformerType.NATURE;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 3)  // lawnmower
        {
            theFolk.terraformerType = TerraformerType.LAWNMOWER;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 4)  // landscaper (flatten)
        {
            theFolk.terraformerType = TerraformerType.FLATTENIZER;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 5)  // value pack - single layer of dirt
        {
            theFolk.terraformerType = TerraformerType.VALUEPACK;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 6)  // glacial - snow and ice
        {
            theFolk.terraformerType = TerraformerType.GLACIAL;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 7)  // moisterizer - lava to cobble
        {
            theFolk.terraformerType = TerraformerType.MOISTURIZER;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 8)  // thermalizer - collect lava, put in buckets
        {
            theFolk.terraformerType = TerraformerType.THERMALIZER;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.id == 9)  // deicer, removes snow blocks
        {
            theFolk.terraformerType = TerraformerType.DEICER;
            theFolk.terraformerRadius = Integer.parseInt(tfRadius.getText().trim());
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
    }

    protected void mouseClicked(int i, int j, int k)
    {
        tfRadius.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
    }

    protected void keyTyped(char c, int i)
    {
        if (i == 1)  //escape and dont save
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }

        if ((i >= 2 && i <= 11) || i == 14)
        {
            try
            {
                if (tfRadius.isFocused())      //was isFocused()
                {
                    tfRadius.textboxKeyTyped(c, i);
                }
            }
            catch (Exception e)
            {
                tfRadius.textboxKeyTyped(c, i);
            }
        }
    }
}
