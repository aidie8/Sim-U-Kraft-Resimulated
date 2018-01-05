package ashjack.simukraftreloaded.client.Gui.blocks;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.client.Gui.folk.GuiEmployFolk;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.folk.FolkData;

public class GuiMining extends GuiScreen
{
    ArrayList<FolkData> theWorkers = new ArrayList<FolkData>();
    MiningBox theMiningBox = null;
    private GuiTextField tfSize;
    private int mouseCount = 0;

    public GuiMining(MiningBox miningBlock, ArrayList<FolkData> folks)
    {
        this.theMiningBox = miningBlock;
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

        if (theMiningBox == null)
        {
            //drawCenteredString(fontRenderer, "Error: Problem with mining box, please replace it",
            //	width / 2, 27,0xff0000);
            return;
        }

        if (theWorkers == null || theWorkers.size() == 0)
        {
            buttonList.add(new GuiButton(1, width / 2 - 100, 40, "Hire Miner"));
        }
        else
        {
            buttonList.add(new GuiButton(1, width / 2 - 100, 40, "Fire " + theWorkers.get(0).name));
        }

        String i = "";
        String j = "";

        if (theMiningBox.discards == 0)
        {
            i = "Keep all block types";
        }
        else if (theMiningBox.discards == 1)
        {
            i = "Discard Dirt blocks";
        }
        else if (theMiningBox.discards == 2)
        {
            i = "Discard Dirt and Stone";
        }
        else if (theMiningBox.discards == 3)
        {
            i = "Discard Dirt and Sand";
        }
        else if (theMiningBox.discards == 4)
        {
            i = "Discard Dirt, sand and Stone";
        }

        if (theMiningBox.addGlassCover)
        {
            j = "Cover with glass (put glass in chest)";
        }
        else
        {
            j = "Leave mine open";
        }

        GuiButton gb = null;

        if (GameMode.gameMode != GameMode.GAMEMODES.HARDCORE)
        {
            buttonList.add(new GuiButton(2, width / 2 - 100, 120, i));
            buttonList.add(gb = new GuiButton(3, width / 2 - 100, 160, j));
        }

        //// if horizontal mine...
        if (theMiningBox.marker1XYZ != null && theMiningBox.marker2XYZ == null)
        {
            tfSize = new GuiTextField(fontRendererObj, width / 2 - 25, height - 50, 50, 15);
            tfSize.setText(theMiningBox.size + "");
            tfSize.setFocused(true);
            tfSize.setMaxStringLength(3);

            if (gb != null)
            {
                gb.enabled = false;
            }
        }
    }

    private void extraButtons()
    {
        if (GameMode.gameMode == GameMode.GAMEMODES.HARDCORE)
        {
            return;
        }

        String i = "";
        String j = "";

        if (theMiningBox.discards == 0)
        {
            i = "Keep all block types";
        }
        else if (theMiningBox.discards == 1)
        {
            i = "Discard Dirt blocks";
        }
        else if (theMiningBox.discards == 2)
        {
            i = "Discard Dirt and Stone";
        }
        else if (theMiningBox.discards == 3)
        {
            i = "Discard Dirt and Sand";
        }
        else if (theMiningBox.discards == 4)
        {
            i = "Discard Dirt, sand and Stone";
        }

        if (theMiningBox.addGlassCover)
        {
            j = "Cover with glass (put glass in chest)";
        }
        else
        {
            j = "Leave mine open";
        }

        buttonList.add(new GuiButton(2, width / 2 - 100, 120, i));
        buttonList.add(new GuiButton(3, width / 2 - 100, 140, j));
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
            drawCenteredString(fontRendererObj, "Mining", width / 2, 17, 0xffffff);

            try
            {
                if (theMiningBox.marker1XYZ == null)
                {
                    drawCenteredString(fontRendererObj, "Error: No markers placed - 3 markers are needed to mine vertically, 1 marker for horizontal.",
                                       width / 2, 27, 0xff0000);
                }
            }
            catch (Exception e)
            {
                drawCenteredString(fontRendererObj, "Error: Please place markers BEFORE the mining box",
                                   width / 2, 27, 0xff0000);
            }

            if (theWorkers != null)
            {
                if (theWorkers.size() > 0)
                {
                    try
                    {
                        String others = "";

                        if (theWorkers.size() > 1)
                        {
                            others = " and " + (theWorkers.size() - 1) + " other folks";
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

            if (theMiningBox != null)
            {
                try
                {
                    if (theMiningBox.marker1XYZ != null && theMiningBox.marker2XYZ == null)
                    {
                        drawCenteredString(fontRendererObj, "Size of Horizontal mine", width / 2, height - 60, 0xffffaa);
                        tfSize.drawTextBox();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
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
        else if (guibutton.displayString.contentEquals("Hire Miner"))
        {
            GuiEmployFolk ui = new GuiEmployFolk(theMiningBox, Vocation.MINER);
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
        else if (guibutton.id == 2)     // discard block types toggle
        {
            theMiningBox.discards++;

            if (theMiningBox.discards > 4)
            {
                theMiningBox.discards = 0;
            }

            String i = "";

            if (theMiningBox.discards == 0)
            {
                i = "Keep all block types";
            }
            else if (theMiningBox.discards == 1)
            {
                i = "Discard Dirt blocks";
            }
            else if (theMiningBox.discards == 2)
            {
                i = "Discard Dirt and Stone";
            }
            else if (theMiningBox.discards == 3)
            {
                i = "Discard Dirt and Sand";
            }
            else if (theMiningBox.discards == 4)
            {
                i = "Discard Dirt, sand and Stone";
            }

            guibutton.displayString = i;
        }
        else if (guibutton.id == 3)     //add glass cover toggle
        {
            theMiningBox.addGlassCover = !theMiningBox.addGlassCover;
            String j = "";

            if (theMiningBox.addGlassCover)
            {
                j = "Cover with glass (put glass in chest)";
            }
            else
            {
                j = "Leave mine open";
            }

            guibutton.displayString = j;
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
        else
        {
            if (tfSize != null)
            {
                tfSize.textboxKeyTyped(c, i);
                int s = 3;

                try
                {
                    s = Integer.parseInt(tfSize.getText());
                }
                catch (Exception e) {}

                theMiningBox.size = s;
            }
        }
    }

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        if (tfSize != null)
        {
            tfSize.mouseClicked(i, j, k);
        }

        super.mouseClicked(i, j, k);
    }
}
