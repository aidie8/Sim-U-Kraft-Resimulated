package ashjack.simukraftreloaded.client.Gui.blocks;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.blocks.BlockMarker;
import ashjack.simukraftreloaded.client.Gui.folk.GuiCourierTasks;
import ashjack.simukraftreloaded.client.Gui.folk.GuiEmployFolk;
import ashjack.simukraftreloaded.client.Gui.folk.GuiMerchant;
import ashjack.simukraftreloaded.client.Gui.folk.GuiShowEmployees;
import ashjack.simukraftreloaded.client.Gui.other.GuiBeamPlayerTo;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.packetsNEW.PacketHandler;
import ashjack.simukraftreloaded.packetsNEW.toServer.DemolishBuildingPacket;
import ashjack.simukraftreloaded.packetsNEW.toServer.LoadBuildingPacket;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class GuiControlBox extends GuiScreen
{
    private int mouseCount = 0;

    /** location of this control box */
    public V3 location;
    /** reference to the building object this control box is attached to */
    public Building theBuilding = null;
    /** hold a reference to a folk that is passed in */
    public FolkData theFolk = null;
    /** used to limit amount of people that can be employed at this building (workplaces only) */
    public int employeeCount = 0; // used to limit amount of people that can be employed if this is a workplace building
    private HashMap employees = new HashMap();
    private EntityPlayer playerWhoClickedIt = null;

    /** regular constructor */
    public GuiControlBox(V3 location, EntityPlayer thePlayer)
    {
        this.location = location.clone();
        Building.loadAllBuildings(); //re-load the buildings client side
        theBuilding = Building.getBuilding(location);
        playerWhoClickedIt = thePlayer;
    }

    /** constructor called after choosing to hire staff */
    public GuiControlBox(V3 location, FolkData folk)     //called after choosing hired staff
    {
        this.location = location;
        Building.loadAllBuildings(); //re-load the buildings client side
        theBuilding = Building.getBuilding(location);
        theFolk = folk;

        if (SimukraftReloaded.isDayTime())
        {
            theFolk.gotoXYZ(location, null);
        }
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
        buttonList.clear();
        buttonList.add(new GuiButton(0, 5, 5, 50, 20, "Done"));

        if (theBuilding == null)
        {
            buttonList.add(new GuiButton(1, 10, height - 30, 100, 20, "Fix House"));
            return;
        }

        if (theBuilding.blockLocations != null && theBuilding.blockLocations.size() > 0
                && theBuilding.buildingComplete)
        {
            buttonList.add(new GuiButton(1000, width - 110, 5, 100, 20, "Demolish!"));
            buttonList.add(new GuiButton(1001, width - 110,25,100,20,"Rotate Stairs"));
        }

        buttonList.add(new GuiButton(21, width - 110, height - 30, 100, 20, "Show Employees"));
        buttonList.add(new GuiButton(30, width - 110, height - 50, 100, 20, "Beam me to.."));

        if (theBuilding.type.contentEquals("commercial") || theBuilding.type.contentEquals("industrial") || theBuilding.type.contentEquals("special"))
        {
            int down = 70;
            int idx = 2;    ///display list of employees and their fire buttons
            employeeCount = 0;
            employees.clear();

            for (int fc = 0; fc < SimukraftReloaded.theFolks.size(); fc++)
            {
                FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(fc);

                if (theBuilding.primaryXYZ.isSameCoordsAs(folk.employedAt, true, true))
                {
                	buttonList.add(new GuiButton(idx, width - 140, down - 6, 130, 20, "Fire " + folk.name));
                    
                    employees.put((idx + 100), folk.name);

                    if (theBuilding.displayName.contains("Depot"))
                    {
                        buttonList.add(new GuiButton((idx + 100), width - 190, down - 6, 50, 20, "Tasks"));
                    }

                    down += 20;
                    employeeCount++;
                    idx++;
                }
            }
        }

        ////commercial buildings
        if (theBuilding.type.contentEquals("commercial"))
        {
            if (theBuilding.displayName.contains("Bakery"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Baker"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            if (theBuilding.displayName.contains("Grocery Store"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Grocer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            if (theBuilding.displayName.contains("Butchers"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Butcher"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }
            if (theBuilding.displayName.contains("Burgers"))
            {
            	ArrayList<FolkData> employees=FolkData.getFolksByEmployedAt(theBuilding.primaryXYZ);
            	Boolean flag=false;
            	
            	GuiButton b1;
                buttonList.add(b1 = new GuiButton(1, 10, height - 30, 100, 20, "Hire Manager"));
                for(FolkData folk:employees) {
                	if(folk.employedAt !=null && folk.employedAt.isSameCoordsAs(theBuilding.primaryXYZ, true, true)
                			&& folk.vocation==Vocation.BURGERSMANAGER) {
                		flag=true; break;
                	}
                		
                }
                if (flag) {b1.enabled=false;}
                
                GuiButton b2; flag=false;
                buttonList.add(b2 = new GuiButton(2, 10, height - 50, 100, 20, "Hire Fry Cook"));
                for(FolkData folk:employees) {
                	if(folk.employedAt !=null && folk.employedAt.isSameCoordsAs(theBuilding.primaryXYZ, true, true)
                			&& folk.vocation==Vocation.BURGERSFRYCOOK) {
                		flag=true; break;
                	}
                		
                }
                if (flag) {b2.enabled=false;}
                
                GuiButton b3; flag=false;
                buttonList.add(b3 = new GuiButton(3, 10, height - 70, 100, 20, "Hire Waiter"));
                for(FolkData folk:employees) {
                	if(folk.employedAt !=null && folk.employedAt.isSameCoordsAs(theBuilding.primaryXYZ, true, true)
                			&& folk.vocation==Vocation.BURGERSWAITER) {
                		flag=true; break;
                	}
                		
                }
                if (flag) {b3.enabled=false;}

            }
            
        }

        /////industrial buildings
        if (theBuilding.type.contentEquals("industrial"))
        {
            ////lumbermill control panel
            if (theBuilding.displayName.contains("Lumbermill"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Lumberjack"));

                if (employeeCount > 4)
                {
                    b.enabled = false;
                }

                if (BlockMarker.markers.size() == 1)
                {
                    buttonList.add(new GuiButton(20, width / 2 + 100, height - 30, 100, 20, "Set Lumber area"));
                }
            }

            //// Builder's merchant
            if (theBuilding.displayName.contains("Builders Merchant"))
            {
                GuiButton b;
                GuiButton b2;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Merchant"));
                buttonList.add(b2 = new GuiButton(25, 10, height - 50, 100, 20, "Buy/Sell"));

                if (!SimukraftReloaded.isDayTime() || employeeCount == 0)
                {
                    b2.enabled = false;
                }

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            //barracks control panel
            if (theBuilding.displayName.contains("Barracks"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Train Soldier"));

                if (employeeCount > 9)
                {
                    b.enabled = false;
                }
            }

            //sheep farm control panel
            if (theBuilding.displayName.contains("Sheep Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Shepherd"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }
            
            if (theBuilding.displayName.contains("Egg Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Egg Farmer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            //cattle farm control panel
            if (theBuilding.displayName.contains("Cattle Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Cattle farmer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            //pig farm control panel
            if (theBuilding.displayName.contains("Pig Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Pig farmer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            //chicken farm control panel
            if (theBuilding.displayName.contains("Chicken Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Chicken farmer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            /////depot
            if (theBuilding.displayName.contains("Depot"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Courier"));

                if (employeeCount > 3)
                {
                    b.enabled = false;
                }
            }

            /// glass factory
            if (theBuilding.displayName.contains("Glass Factory"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Glass maker"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }
            
            if (theBuilding.displayName.contains("Brick Factory"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Brick maker"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }

            //fishing dock
            if (theBuilding.displayName.contains("Fishing Dock"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Fisherman"));

                if (employeeCount > 1)
                {
                    b.enabled = false;
                }
            }
            
            //dairy farm
            if (theBuilding.displayName.contains("Dairy Farm"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Dairy farmer"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }
            
            //Cheese Factory
            if (theBuilding.displayName.contains("Cheese Factory"))
            {
                GuiButton b;
                buttonList.add(b = new GuiButton(1, 10, height - 30, 100, 20, "Hire Cheesemaker"));

                if (employeeCount > 0)
                {
                    b.enabled = false;
                }
            }



            int down = 70;
            int idx = 2;

            for (int fc = 0; fc < SimukraftReloaded.theFolks.size(); fc++)
            {
                FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(fc);

                if (theBuilding.primaryXYZ.isSameCoordsAs(folk.employedAt, true, true))
                {
                    if (theBuilding.displayName.contains("Barracks"))
                    {
                        buttonList.add(new GuiButton(idx, width - 210, down - 6, 200, 20, "Dismiss " + folk.name));
                    }
                    else if (theBuilding.displayName.contains("Burgers")) {
                    	buttonList.add(new GuiButton(idx,width-210,down-6,200,20,"Fire "+folk.vocation.toString()));
                    }
                    else
                    {
                        buttonList.add(new GuiButton(idx, width - 140, down - 6, 130, 20, "Fire " + folk.name));
                    }

                    down += 20;
                }
            }
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
            drawCenteredString(fontRendererObj, "Building Control Panel", width / 2, 17, 0xffffff);

            if (theBuilding != null)
            {
                String author = "";

                if (theBuilding.author != null && !theBuilding.author.contentEquals(""))
                {
                    author = " by " + theBuilding.author;
                }

                String isComplete = "Under construction";

                if (theBuilding.buildingComplete)
                {
                    isComplete = "Active Building";
                }

                fontRendererObj.drawString("Building : " + theBuilding.displayNameWithoutPK + author, 5, 37, 0xffff80);
                fontRendererObj.drawString("Type : " + theBuilding.type + " (" + isComplete + ")", 5, 47, 0xffff80);

                if (theBuilding.type.contentEquals("residential"))
                {
                    String s = "";

                    if (theBuilding.tennants.size() > 1 || theBuilding.tennants.size() == 0)
                    {
                        s = "s";
                    }

                    fontRendererObj.drawString(theBuilding.tennants.size() + " Resident" + s + " :", 5, 57, 0xffff80);
                    int down = 70;

                    for (int t = 0; t < theBuilding.tennants.size(); t++)
                    {
                        String folkname = theBuilding.tennants.get(t);
                        fontRendererObj.drawString(folkname , 20, down, 0xffffa0);
                        down += 20;
                    }
                }
                else if (theBuilding.type.contentEquals("industrial") || theBuilding.type.contentEquals("commercial") || theBuilding.type.contentEquals("special"))
                {
                    fontRendererObj.drawString("Employees :", 5, 57, 0xffff80);
                    int down = 70;

                    for (int fc = 0; fc < SimukraftReloaded.theFolks.size(); fc++)
                    {
                        FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(fc);

                        if (theBuilding.primaryXYZ.isSameCoordsAs(folk.employedAt, true, true))
                        {
                            fontRendererObj.drawString(folk.name + " (" + folk.age + ") - "
                                                    + folk.vocation.toString() , 20, down, 0xffffa0);
                            down += 20;
                        }
                    }
                }
                else if (theBuilding.type.contentEquals("other"))
                {
                }
            }
            else
            {
                fontRendererObj.drawString("ERROR: lost info on this building (" + this.location.toString() + ")", 5, 77, 0xff0000);
                fontRendererObj.drawString("only click 'fix house' below if this Building IS a house", 5, 97, 0xff0000);
            }

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
        else if (guibutton.displayString.contentEquals("Hire Lumberjack"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.LUMBERJACK);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Baker"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BAKER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Train Soldier"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.SOLDIER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Shepherd"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.SHEPHERD);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Grocer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.GROCER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Courier"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.COURIER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Merchant"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.MERCHANT);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Butcher"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BUTCHER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Egg Farmer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.EGGFARMER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Pig farmer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.PIGFARMER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Cattle farmer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.CATTLEFARMER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Chicken farmer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.CHICKENFARMER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Glass maker"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.GLASSMAKER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Brick maker"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BRICKMAKER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Fisherman"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.FISHERMAN);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Dairy farmer"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.DAIRYFARMER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Cheesemaker"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.CHEESEMAKER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Manager"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BURGERSMANAGER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Fry Cook"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BURGERSFRYCOOK);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contentEquals("Hire Waiter"))
        {
            mc.currentScreen = null;
            GuiEmployFolk ui = new GuiEmployFolk(this.location, "", Vocation.BURGERSWAITER);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.displayString.contains("Fire ") || guibutton.displayString.contains("Dismiss "))
        {
            String fname = guibutton.displayString.substring(guibutton.displayString.indexOf(" ")).trim();
            guibutton.enabled = false;

            if (theBuilding.displayName.contains("Depot"))
            {
                for (int x = 0; x < buttonList.size(); x++)
                {
                    GuiButton but = (GuiButton) buttonList.get(x);

                    if (but.yPosition == guibutton.yPosition)
                    {
                        but.enabled = false;
                    }
                }
            }

            FolkData folk = FolkData.getFolkByName(fname);

            if (folk != null)
            {
                folk.selfFire();
            }
        }
        else if (guibutton.displayString.contentEquals("Fix House"))
        {
            Building b;
            SimukraftReloaded.theBuildings.add(b = new Building("Repaired House", "residential", location, location, true));
            b.buildingComplete = true;
            b.capacity = -1;
            b.author = "Satscape";
            theBuilding = b;
            Building.saveAllBuildings();
        }
        else if (guibutton.displayString.contentEquals("Set Lumber area"))
        {
            theBuilding.lumbermillMarker = BlockMarker.markers.get(0).toV3();
            guibutton.enabled = false;
        }
        else if (guibutton.id == 21)     // show employees button pressed
        {
            GuiScreen gui = new GuiShowEmployees();
            mc.displayGuiScreen(null);
            mc.displayGuiScreen(gui);
        }
        else if (guibutton.displayString.contentEquals("Tasks"))
        {
            String folkname = (String) employees.get(guibutton.id);
            GuiScreen gui = new GuiCourierTasks(this.location, folkname, playerWhoClickedIt);
            mc.displayGuiScreen(null);
            mc.displayGuiScreen(gui);
            //// Builder's merchant buysell button
        }
        else if (guibutton.displayString.contentEquals("Buy/Sell"))
        {
            GuiScreen ui = new GuiMerchant();
            mc.displayGuiScreen(null);
            mc.displayGuiScreen(ui);
        }
        else if (guibutton.id == 30)   //Beam me to...
        {
            GuiScreen ui = new GuiBeamPlayerTo(playerWhoClickedIt);
            mc.displayGuiScreen(null);
            mc.displayGuiScreen(ui);
        } 
        else if (guibutton.displayString.contentEquals("Rotate Stairs")) {
        	rotateStairs();
        }
        else if (guibutton.displayString.contentEquals("Demolish!"))
        {
            World theWorld = playerWhoClickedIt.worldObj;
            int bindex = 0;

            for (int i = 0; i < SimukraftReloaded.theBuildings.size(); i++)
            {
                Building build = SimukraftReloaded.theBuildings.get(i);

                try {
	                if (build.primaryXYZ.isSameCoordsAs(theBuilding.primaryXYZ, true, true))
	                {
	                    bindex = i;
	                    FolkData theFolk = FolkData.getFolkByEmployedAt(theBuilding.primaryXYZ);
	
	                    if (theFolk != null)
	                    {
	                        theFolk.selfFire();
	                    }
	                }
                }catch(Exception e) {}
            }

            SimukraftReloaded.demolishWorld = theWorld;

            //Send the packet
           // if(theWorld.isRemote)
           // {
            	PacketHandler.net.sendToServer(new DemolishBuildingPacket(theBuilding, theBuilding.blockLocations));
           // }
            
            	//BOOKMARK
            	
            for (V3 blockLoc : theBuilding.blockLocations)
            {            
                mc.theWorld.spawnParticle("explode", blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(), 0, 0.3f, 0);
                mc.theWorld.spawnParticle("flame", blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(), 0, 0.4f, 0);
                
            }

            theWorld.playSoundAtEntity(playerWhoClickedIt, "random.explode", 1.0f, 1.0f);
            SimukraftReloaded.theBuildings.remove(bindex);
            mc.displayGuiScreen(null);
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

        //((GuiButton)buttonList.get(0)).enabled = theGuiTextField1.getText().trim().length() > 0;
    }
    
    // Stairs, torches, redstone torches, beds, pistons, signs, ladders, buttons, fence gates
    private void rotateStairs()
    {
        World theWorld = mc.getIntegratedServer().worldServerForDimension(theBuilding.primaryXYZ.theDimension);
        theWorld.playSoundEffect(theBuilding.primaryXYZ.x,theBuilding.primaryXYZ.y,theBuilding.primaryXYZ.z, "ashjacksimukraftreloaded:computer", 1f, 2f);

        for (V3 blockLoc : theBuilding.blockLocations)
        {
            ItemStack is = new ItemStack(theWorld.getBlock(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue()), 1,
                                         theWorld.getBlockMetadata(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue()));

            if (Block.getBlockFromItem(is.getItem()) == Blocks.acacia_stairs
            		|| Block.getBlockFromItem(is.getItem()) == Blocks.birch_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.dark_oak_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.spruce_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.stone_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.oak_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.brick_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.stone_brick_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.nether_brick_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.sandstone_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.stone_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.jungle_stairs
            		||Block.getBlockFromItem(is.getItem()) == Blocks.quartz_stairs)
                        //static method of BlockStairs
            {
                int newmeta = is.getItemDamage(); // 0 > 2 > 1 > 3 > 0

                if (newmeta == 0)
                {
                    newmeta = 2;
                }
                else if (newmeta == 1)
                {
                    newmeta = 3;
                }
                else if (newmeta == 2)
                {
                    newmeta = 1;
                }
                else if (newmeta == 3)
                {
                    newmeta = 0;
                }

                theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),
                                                    newmeta, 0x03);
            }//end of stairs
            //// torches and redstone torches
            else if (Block.getBlockFromItem(is.getItem())==Blocks.torch || Block.getBlockFromItem(is.getItem())==Blocks.redstone_torch || Block.getBlockFromItem(is.getItem())==Blocks.redstone_torch) {
            	int newmeta = is.getItemDamage();
            	if (newmeta==1) {
            		newmeta=3;
            	} else if(newmeta==3) {
            		newmeta=2;
            	} else if (newmeta==2) {
            		newmeta=4;
            	} else if (newmeta==4) {
            		newmeta=1;
            	}
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x03);
            
            // beds
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.bed) {
            	int newmeta = is.getItemDamage();
            	newmeta++;
            	if (newmeta==4) { newmeta=0; }
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x02);

            //pistons	
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.piston || Block.getBlockFromItem(is.getItem())==Blocks.piston_extension
            	|| Block.getBlockFromItem(is.getItem())==Blocks.piston_head) {
            	int newmeta = is.getItemDamage();
            	if (newmeta==2) { newmeta=5; }
            	else if(newmeta==5) { newmeta=3; }
            	else if(newmeta==3) { newmeta=4; }
            	else if(newmeta==4) { newmeta=2; }
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x03);

            //signs in floor or attached
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.wall_sign) {
            	int newmeta = is.getItemDamage();
            	if (newmeta==0) {newmeta=4;}
            	else if(newmeta==4) {newmeta=8;}
            	else if(newmeta==8) {newmeta=12;}
            	else if(newmeta==12) {newmeta=0;}
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x02);
            
            //signs on wall, ladders
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.wall_sign || Block.getBlockFromItem(is.getItem())==Blocks.ladder) {
            	int newmeta = is.getItemDamage();
            	if (newmeta==2) {newmeta=5;}
            	else if(newmeta==5) {newmeta=3;}
            	else if(newmeta==3) {newmeta=4;}
            	else if(newmeta==4) {newmeta=2;}
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x03);
            
            //buttons
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.stone_button || Block.getBlockFromItem(is.getItem())==Blocks.wooden_button) {
            	int newmeta = is.getItemDamage();
            	if (newmeta==1) {newmeta=3;}
            	else if(newmeta==3) {newmeta=2;}
            	else if(newmeta==2) {newmeta=4;}
            	else if(newmeta==4) {newmeta=1;}
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x03);
            
            // fence gates
            } else if(Block.getBlockFromItem(is.getItem())==Blocks.fence_gate) {
            	int newmeta = is.getItemDamage();
            	newmeta++;
            	if(newmeta>3) {newmeta=0;}
            	theWorld.setBlockMetadataWithNotify(blockLoc.x.intValue(), blockLoc.y.intValue(), blockLoc.z.intValue(),newmeta, 0x03);
            
            }
        }
    }

    
}
