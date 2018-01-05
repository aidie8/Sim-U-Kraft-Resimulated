package ashjack.simukraftreloaded.client.Gui.folk;

import java.io.File;
import java.util.HashMap;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.client.Gui.blocks.GuiControlBox;
import ashjack.simukraftreloaded.common.CourierTask;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class GuiCourierTasks extends GuiScreen
{
    V3 controlBoxLocation;
    FolkData theFolk = null;
    HashMap tasks = new HashMap();
    String onPage = "main";
    CourierTask newtask = new CourierTask();
    private int mouseCount = 0;
    private EntityPlayer thePlayer = null;

    public GuiCourierTasks(V3 xyz, String folkname, EntityPlayer pl)
    {
        this.controlBoxLocation = xyz;
        theFolk = FolkData.getFolkByName(folkname);
        this.thePlayer = pl;
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void updateScreen()
    {
        //theGuiTextField1.updateCursorCounter();
    }

    @Override
    public void initGui()
    {
        initscreen();
    }

    private void initscreen()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, 5, 5, 50, 20, "Done"));

        if (onPage.contentEquals("main"))
        {
            buttonList.add(new GuiButton(1, 5, height - 20, 50, 20, "Add"));
            int idx = 2;
            int y = 30;

            for (int t = 0; t < SimukraftReloaded.theCourierTasks.size(); t++)
            {
                CourierTask ct = SimukraftReloaded.theCourierTasks.get(t);

                if (ct.folkname.contentEquals(theFolk.name))
                {
                    y = 30 + ((idx - 2) * 20);

                    if (y + 20 > height)
                    {
                        break;
                    }

                    buttonList.add(new GuiButton(idx, width - 50, y, 50, 20, "Delete"));
                    tasks.put(idx, t);
                    idx++;
                }
            }
        }
        else if (onPage.contentEquals("add"))
        {
            int x = 10, y = 40, idx = 2;

            for (int f = 0; f < SimukraftReloaded.theCourierPoints.size(); f++)
            {
                V3 cpoint = SimukraftReloaded.theCourierPoints.get(f);
                buttonList.add(new GuiButton(idx, x, y, 110, 20, cpoint.name));
                idx++;
                x += 110;

                if ((x + 110) > width)
                {
                    x = 10;
                    y += 20;
                }

                if ((y + 20) > (height - 50))
                {
                    break;
                }
            }

            GuiButton b;
            buttonList.add(b = new GuiButton(1, width - 160, height - 25, 150, 20, "Deliver back to depot"));
            b.enabled = false;
        }
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
        drawDefaultBackground();

        if (mouseCount < 10)
        {
            mouseCount++;
            Mouse.setGrabbed(false);
        }

        if (onPage.contentEquals("main"))
        {
            drawCenteredString(fontRendererObj, "Courier tasks for " + theFolk.name, width / 2, 17, 0xffffff);
            int idx = 2;
            int y;

            for (int t = 0; t < SimukraftReloaded.theCourierTasks.size(); t++)
            {
                CourierTask ct = SimukraftReloaded.theCourierTasks.get(t);

                if (ct.folkname.contentEquals(theFolk.name))
                {
                    y = 40 + ((idx - 2) * 20);

                    if (y + 20 > height)
                    {
                        break;
                    }

                    try   // nulls out after point removed and MC not restarted yet
                    {
                        fontRendererObj.drawString(ct.pickup.name, 5, y - 5, 0xffff8f);
                        fontRendererObj.drawString("->", width / 3, y - 5, 0xffff00);

                        if (ct.dropoff == null)
                        {
                            fontRendererObj.drawString("The Depot", width / 2, y - 5, 0xf0ff8f);
                        }
                        else
                        {
                            fontRendererObj.drawString(ct.dropoff.name, width / 2, y - 5, 0xf0ff8f);
                        }

                        idx++;
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
        else if (onPage.contentEquals("add"))
        {
            if (newtask.pickup.name.contentEquals(""))
            {
                drawCenteredString(fontRendererObj, "Choose a pick up point", width / 2, 17, 0xffffff);
            }
            else
            {
                drawCenteredString(fontRendererObj, "Pick up from " + newtask.pickup.name + " and drop off at...", width / 2, 17, 0xffffff);
            }
        }

        super.drawScreen(i, j, f);
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
            mc.displayGuiScreen(null);
            GuiControlBox ui = new GuiControlBox(this.controlBoxLocation, thePlayer);
            mc.displayGuiScreen(ui);
            return;
        }

        if (onPage.contentEquals("main"))
        {
            if (guibutton.id == 1)   // add button
            {
                onPage = "add";
                initscreen();
            }
            else if (guibutton.id >= 2)    ///one of the delete task buttons
            {
                int tidx = (Integer) tasks.get(guibutton.id);
                String fn = "ct" + tidx + theFolk.name.replace(" ", "");
                File file = new File(SimukraftReloaded.getSavesDataFolder() + "CourierTasks" + File.separator + fn + ".sk2");
                file.delete();
                SimukraftReloaded.theCourierTasks.remove(tidx);
                buttonList.remove(guibutton.id);
                initscreen();
            }
        }
        else if (onPage.contentEquals("add"))
        {
            if (newtask.pickup.name.contentEquals(""))
            {
                String name = guibutton.displayString.trim();
                V3 v = CourierTask.getCourierPoint(name);
                newtask.pickup.name = name;
                newtask.pickup.setVals(v);
                guibutton.enabled = false;
                GuiButton but = getButtonWithId(1); //back to depot button
                but.enabled = true;
            }
            else if (newtask.dropoff !=null && newtask.dropoff.name.contentEquals("")) 
            {
                if (guibutton.id == 1)
                {
                    newtask.dropoff.name = "Depot";
                    newtask.dropoff.setVals(this.controlBoxLocation);
                }
                else
                {
                    String name = guibutton.displayString.trim();
                    V3 v = CourierTask.getCourierPoint(name);
                    newtask.dropoff.name = name;
                    newtask.dropoff.setVals(v);
                }

                guibutton.enabled = false;
                newtask.folkname = theFolk.name;
                newtask.name = "Task " + (SimukraftReloaded.theCourierTasks.size() + 1) + ""; //name of task not used really
                SimukraftReloaded.theCourierTasks.add(newtask);
                onPage = "main";
                initscreen();
            }
        }
    }

    public GuiButton getButtonWithId(int id)
    {
        GuiButton retbut;

        for (int x = 0; x < buttonList.size(); x++)
        {
            retbut = (GuiButton) buttonList.get(x);

            if (retbut.id == id)
            {
                return retbut;
            }
        }

        return null;
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
            GuiControlBox ui = new GuiControlBox(this.controlBoxLocation, thePlayer);
            mc.displayGuiScreen(ui);
            return;
        }

        //((GuiButton)buttonList.get(0)).enabled = theGuiTextField1.getText().trim().length() > 0;
    }
}
