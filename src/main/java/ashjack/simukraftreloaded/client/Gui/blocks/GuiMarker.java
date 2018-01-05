package ashjack.simukraftreloaded.client.Gui.blocks;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

import org.lwjgl.input.Mouse;

import cpw.mods.fml.common.registry.GameRegistry;
import ashjack.simukraftreloaded.blocks.BlockMarker;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class GuiMarker extends GuiScreen
{
    V3 location;
    String errorText = "";
    GuiTextField theGuiTextField1;
    EntityPlayer thePlayer = null;
    private int mouseCount = 0;

    ///normal constructor
    public GuiMarker(V3 location, EntityPlayer p)
    {
        this.location = location;
        this.thePlayer = p;
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void updateScreen()
    {
        if (theGuiTextField1 != null)
        {
            theGuiTextField1.updateCursorCounter();
        }
    }

    @Override
    public void initGui()
    {
        buttonList.clear();
        buttonList.add(new GuiButton(0, width / 2 - 100, height - 30, "Done"));
        buttonList.add(new GuiButton(1, width / 2 - 100, 100, "Copy structure/building"));
        buttonList.add(new GuiButton(2, width / 2 - 100, 160, "Set new Courier/Beaming point"));

        for (int x = 1; x <= 2; x++)
        {
            ((GuiButton)buttonList.get(x)).enabled = false;
        }

        if (BlockMarker.markers.size() == 3)
        {
            ((GuiButton)buttonList.get(1)).enabled = true;
        }
        else if (BlockMarker.markers.size() == 1)
        {
            ((GuiButton)buttonList.get(2)).enabled = true;
            /// courier drop point name
            theGuiTextField1 = new GuiTextField(fontRendererObj, width / 2 - (width / 3 / 2), 138, width / 3, 20);
            theGuiTextField1.setMaxStringLength(23);
        }
    }

    @Override
    public void drawScreen(int i, int j, float f)
    {
        if (mouseCount < 10)
        {
            mouseCount++;
            Mouse.setGrabbed(false);
        }

        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Sim-u-Markers", width / 2, 30, 0xffffff);
        drawCenteredString(fontRendererObj, "Markers can be used to make a copy of a building or you can use them to", width / 2, 40, 0xa0ffff);
        drawCenteredString(fontRendererObj, "mark out a mining or food-based farming area.", width / 2, 55, 0xa0ffff);
        drawCenteredString(fontRendererObj, "A single marker can be used to designate a new lumberjack area and more!", width / 2, 70, 0xa0ffff);
        drawCenteredString(fontRendererObj, "TIP: remove old markers after use, before marking a new area.", width / 2, 85, 0xa0ffff);
        drawCenteredString(fontRendererObj, errorText, width / 2, height - 50, 0xff0000);

        if (theGuiTextField1 != null)
        {
            theGuiTextField1.drawTextBox();
        }

        super.drawScreen(i, j, f);
    }

    @Override
    public void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.id == 0) ///cancel button
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
            return;
        }
        else if (guibutton.displayString.contentEquals("Copy structure/building"))
        {
            new ThreadFacsimile();
        }
        else if (guibutton.displayString.contentEquals("Set new Courier/Beaming point"))
        {
            String s = BlockMarker.markers.get(0).toString();
            String[] ss = s.split(",");
            String name = theGuiTextField1.getText().trim();

            if (name.length() == 0)
            {
                errorText = "Please type a name for this Courier/Beaming point";
                theGuiTextField1.isFocused();  // was GetisFocused()
                return;
            }

            V3 point = new V3(Double.parseDouble(ss[0]), Double.parseDouble(ss[1]),
                              Double.parseDouble(ss[2]), thePlayer.dimension);
            ArrayList<IInventory> chestInvs = Job.inventoriesFindClosest(point, 5);

            if (chestInvs.size() == 0)
            {
                errorText = "Error: Place at least one chest near the marker.";
                return;
            }

            point.name = name;

            for (int p = 0; p < SimukraftReloaded.theCourierPoints.size(); p++)
            {
                V3 epoint = SimukraftReloaded.theCourierPoints.get(p);

                if (epoint.name.contentEquals(name))
                {
                    errorText = "Error: The name must be unique, '" + name + "' is already used.";
                    return;
                }
            }

            SimukraftReloaded.theCourierPoints.add(point);
            errorText = "Courier/Beaming point '" + name + "' has been added.";
        }
    }

    @Override
    protected void mouseClicked(int i, int j, int k)
    {
        if (theGuiTextField1 != null)
        {
            theGuiTextField1.mouseClicked(i, j, k);
        }

        super.mouseClicked(i, j, k);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        if (i == 1)
        {
            mc.currentScreen = null;
            mc.setIngameFocus();
        }

        if (theGuiTextField1 != null)
        {
            theGuiTextField1.textboxKeyTyped(c, i);   //was textboxKeyTyped()
        }
    }

    
    public class ThreadFacsimile extends Thread
    {
        public ThreadFacsimile()
        {
            start();
        }

        public void run()
        {
            V3 cxyz = GuiMarker.this.location;
            V3 Lxyz = BlockMarker.markers.get(1).toV3();
            V3 Bxyz = BlockMarker.markers.get(2).toV3();
            V3 exyz = new V3(Math.floor(mc.thePlayer.posX), Math.floor(Lxyz.y+1d)
                             , Math.floor(mc.thePlayer.posZ), Bxyz.theDimension);
            int ftbCount = 0, ltrCount = 0;

            if (cxyz.x.intValue() == Lxyz.x.intValue())
            {
                ltrCount = Math.abs(Lxyz.z.intValue() - cxyz.z.intValue()) - 1;
            }
            else
            {
                ltrCount = Math.abs(Lxyz.x.intValue() - cxyz.x.intValue()) - 1;
            }

            if (cxyz.x.intValue() == Bxyz.x.intValue())
            {
                ftbCount = Math.abs(Bxyz.z.intValue() - cxyz.z.intValue()) - 1;
            }
            else
            {
                ftbCount = Math.abs(Bxyz.x.intValue() - cxyz.x.intValue()) - 1;
            }

            if (ftbCount == 0 || ltrCount == 0)
            {
                errorText = "ERROR: Markers not placed correctly, try again.";
                return;
            }

            int cx, cy, cz, ex, ey, ez, bx = 0, by = 0, bz = 0;
            cx = cxyz.x.intValue();
            cy = cxyz.y.intValue();
            cz = cxyz.z.intValue();
            ex = exyz.x.intValue();
            ey = exyz.y.intValue();
            ez = exyz.z.intValue();
            bx = ex;
            by = ey;
            bz = ez;  // b... is initial building xyz, adjusted below

            if (cz == ez)
            {
                if (cx > ex)
                {
                    bx = cx + 1;
                }
                else
                {
                    bx = cx - 1;
                }
            }
            else if (cx == ex)
            {
                if (cz > ez)
                {
                    bz = cz + 1;
                }
                else
                {
                    bz = cz - 1;
                }
            }
            else
            {
                errorText = "Please stand facing the primary marker with the rear marker in the distance.";
                SimukraftReloaded.sendChat("Could not copy building, Technical info:cx=" + cx + ", cz=" + cz + ", ex=" + ex + ", ez=" + ez);
                return;
            }

            int xo = 0, zo = 0;
            int iD = 0;
			int meta = 0;
            HashMap key = new HashMap();
            key.put("0:0", "A"); // air block
            ArrayList layerLines = new ArrayList();
            String layerLine;
            int ch = 66;
            boolean allAirBlocks = true;
            String keyString = "A=minecraft:air,0;";

            try
            {
                allDone:

                for (int l = 0; l < 200; l++)
                {
                    layerLine = "";

                    for (int ftb = 0; ftb < ftbCount; ftb++)
                    {
                        for (int ltr = 1; ltr <= ltrCount; ltr++)
                        {
                            if (cz == ez)
                            {
                                if (cx > ex)
                                {
                                    xo = ftb;
                                    zo = ltr;
                                }
                                else
                                {
                                    xo = -ftb;
                                    zo = -ltr;
                                }
                            }
                            else if (cx == ex)
                            {
                                if (cz > ez)
                                {
                                    xo = -ltr;
                                    zo = ftb;
                                }
                                else
                                {
                                    xo = ltr;
                                    zo = -ftb;
                                }
                            }

                            int xxx = bx + xo;
                            int yyy = (by + l) - 1;
                            int zzz = bz + zo;
                            Block theBlock = mc.getIntegratedServer().worldServerForDimension(thePlayer.dimension).getBlock(xxx, yyy, zzz);
                            iD = Block.getIdFromBlock(mc.getIntegratedServer().worldServerForDimension(thePlayer.dimension).getBlock(xxx, yyy, zzz));
                            meta = mc.getIntegratedServer().worldServerForDimension(thePlayer.dimension).getBlockMetadata(xxx, yyy, zzz);
                            String letter = "";

                            if(theBlock == SimukraftReloadedBlocks.controlBox)
                            {
                                letter = "$";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.livingBlock)
                            {
                            	letter = "!";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta > 7)
                            {
                            	letter = "0";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 0)
                            {
                            	letter = "0";
                            }
                            
                            else  if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 1)
                            {
                            	letter = "1";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 2)
                            {
                            	letter = "2";
                            }
                            
                            else  if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 3)
                            {
                            	letter = "3";
                            }
                            
                            else  if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 4)
                            {
                            	letter = "4";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 5)
                            {
                            	letter = "5";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 6)
                            {
                            	letter = "6";
                            }
                            
                            else if(theBlock == SimukraftReloadedBlocks.specialBlock && meta == 7)
                            {
                            	letter = "7";
                            }
                            
                            /*Light Blocks*/
                            /*else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 0)
                            {
                                letter = "À";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 1)
                            {
                                letter = "Æ";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 2)
                            {
                                letter = "Ç";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 3)
                            {
                                letter = "È";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 4)
                            {
                                letter = "Ì";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 5)
                            {
                                letter = "Ð";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 6)
                            {
                                letter = "Ñ";
                            }
                            else if (iD == Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox) && meta == 7)
                            {
                                letter = "Ò";
                            }*/
                            else
                            {
                                letter = (String) key.get(iD + ":" + meta);

                                if (key.get(iD + ":" + meta) == null)
                                {
                                    ch++;
                                    key.put(iD + ":" + meta, new Character((char)ch).toString());
                                    keyString += new Character((char)ch).toString() + "=" + GameRegistry.findUniqueIdentifierFor(theBlock).toString() + "," + meta + ";";
                                    letter = new Character((char)ch).toString();
                                }
                            }

                            layerLine += letter;

                            if (iD != 0)
                            {
                                allAirBlocks = false;
                            }
                        }
                    }

                    if (allAirBlocks)
                    {
                        break allDone;
                    }
                    else
                    {
                        layerLines.add(layerLine);
                    }

                    allAirBlocks = true;
                }

                if (layerLines.size() == 0)
                {
                    errorText = "Error, could not capture all blocks, try standing closer to marker and try again";
                    return;
                }

                /// captured building
                File check = new File(SimukraftReloaded.getSimukraftFolder() + "/buildings/");

                if (!check.exists())
                {
                	SimukraftReloaded.sendChat(SimukraftReloaded.getSimukraftFolder() + "/buildings/  folder is missing, The mod is not correctly installed, please copy the simukraft folder AND the zip file.");
                    return;
                }

                String f = String.valueOf(System.currentTimeMillis());
                int l = f.length();
                f = f.substring(l - 6);
                FileWriter fstream = new FileWriter(SimukraftReloaded.getSimukraftFolder() + "/buildings/other/My Build" + f + ".txt");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(ltrCount + "x" + ftbCount + "x" + layerLines.size() + "\r\n");
                out.write(keyString + "\r\n");

                for (int q = 0; q < layerLines.size(); q++)
                {
                    out.write(layerLines.get(q).toString() + "\r\n");
                }

                out.close();
                Thread.sleep(500);
                errorText = "Building copied and stored as 'My Build" + f + "' in Other buildings.";
                mc.theWorld.playSoundEffect(location.x, location.y, location.z, "ashjacksimukraftreloaded:computer", 1f, 1f);
                Building.initialiseAllBuildings();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
