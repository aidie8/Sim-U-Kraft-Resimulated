package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class JobBrickMaker extends Job implements Serializable
{
    private static final long serialVersionUID = 1177111222904279141L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient V3 blockOfClay = null;
    private transient ArrayList<IInventory> factoryChests = new ArrayList<IInventory>();
    private transient TileEntityFurnace factoryFurnace = null;

    public JobBrickMaker() {}

    public JobBrickMaker(FolkData folk)
    {
        theFolk = folk;

        if (theStage == null)
        {
            theStage = Stage.IDLE;
        }

        if (theFolk == null)
        {
            return;   // is null when first employing, this is for next day(s)
        }

        if (theFolk.destination == null)
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }

    public enum Stage
    {
        IDLE, SCANFORCLAY, GOTOCLAYBLOCK, COLLECTCLAY, RETURNCLAY, USEFURNACE, CANTWORK;
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        super.onUpdateGoingToWork(theFolk);

        if (theStage == Stage.IDLE)
        {
            runDelay = 2000;
            theStage = Stage.SCANFORCLAY;
            return;
        }
        else if (theStage == Stage.COLLECTCLAY ||
                 theStage == Stage.GOTOCLAYBLOCK ||
                 theStage == Stage.SCANFORCLAY)
        {
            runDelay = 500 / 2;
        }
        else
        {
            runDelay = 2000;
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        if (factoryFurnace == null)
        {
            factoryFurnace = findFurnace(theFolk.employedAt);
        }

        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
        }
        else if (theStage == Stage.SCANFORCLAY)
        {
            stageScanForClay();
        }
        else if (theStage == Stage.GOTOCLAYBLOCK)
        {
            stageGotoClayBlock();
        }
        else if (theStage == Stage.COLLECTCLAY)
        {
            stageCollectClay();
        }
        else if (theStage == Stage.RETURNCLAY)
        {
            stageReturnClay();
        }
        else if (theStage == Stage.USEFURNACE)
        {
            stageUseFurnace();
        }
        else if (theStage == Stage.CANTWORK)
        {
            stageCantWork();
        }
    }

    private void stageCantWork()
    {
        theFolk.statusText = "There's no Clay around here!";
        return;
    }

    private void stageScanForClay()
    {
        if (theFolk.statusText.contains("Arrived") || theFolk.statusText.contains("brick"))
        {
            theFolk.statusText = "Going to dig up some more clay";
        }

        try
        {
        	blockOfClay = findClosestBlockType(theFolk.employedAt, Blocks.clay, 80, true);

            if (blockOfClay == null)
            {
                theStage = Stage.USEFURNACE;
                return;
            }
            else
            {
                theStage = Stage.GOTOCLAYBLOCK;
            }
        }
        catch (Exception e)
        {
       
        }
    }

    private long lastGotocmd = 0l;

    private void stageGotoClayBlock()
    {
        try
        {
            if (theFolk.theEntity != null)
            {
                theFolk.theEntity.swingProgress = 0.0f;
            }

            theFolk.updateLocationFromEntity();
            double dist = theFolk.location.getDistanceTo(blockOfClay);

            if (dist > 4  && System.currentTimeMillis() - lastGotocmd > 10000)
            {
                theFolk.stayPut = false;
                theFolk.gotoXYZ(blockOfClay, null);
                theFolk.stayPut = false;
                lastGotocmd = System.currentTimeMillis();
            }

            theStage = Stage.COLLECTCLAY;
        }
        catch (Exception e)
        {
            
        }
    }

    private int gotoCount = 0;

    private void stageCollectClay()
    {
        this.runDelay = 1000;
        theFolk.isWorking = true;
        theFolk.updateLocationFromEntity();
        double dist = theFolk.location.getDistanceTo(blockOfClay);

        if (dist > 6  && System.currentTimeMillis() - lastGotocmd > 10000)
        {
            theFolk.gotoXYZ(blockOfClay, null);
            theFolk.stayPut = false;
            lastGotocmd = System.currentTimeMillis();
            gotoCount++;

            if (gotoCount > 2)
            {
                gotoCount = 0;
                V3 bs = blockOfClay.clone();
                bs.y++;
                theFolk.beamMeTo(bs);
            }

            return;
        } //not there yet

        if (dist > 6)
        {
            return;
        }

        try
        {
            if (dist < 6)
            {
                //theFolk.stayPut=true;
            }

            gotoCount = 0;
            jobWorld.setBlock(blockOfClay.x.intValue(), blockOfClay.y.intValue(), blockOfClay.z.intValue(), Blocks.air, 0, 0x03);
            mc.theWorld.playSound(blockOfClay.x, blockOfClay.y, blockOfClay.z, "step.sand", 1f, 1f, false);
            theFolk.inventory.add(new ItemStack(Item.getItemFromBlock(Blocks.clay), 1));
            theFolk.statusText = "Diggy diggy clay, got " + theFolk.inventory.size();
            SimukraftReloaded.states.credits -= 0.012;

            if (theFolk.inventory.size() < 64)
            {
                theStage = Stage.SCANFORCLAY;
            }
            else
            {
                theStage = Stage.RETURNCLAY;
                step = 1;
            }
        }
        catch (Exception e)
        {
            
        }
    }

    private void stageReturnClay()
    {
        theFolk.isWorking = false;

        try
        {
            if (step == 1)
            {
                V3 adj = theFolk.employedAt.clone();
                adj.y++;
                theFolk.gotoXYZ(adj, null);
            
                step = 2;
            }
            else if (step == 2)
            {
                if (theFolk.gotoMethod == GotoMethod.WALK)
                {
                    theFolk.updateLocationFromEntity();
                }

                double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

                if (dist < 4)
                {
                    theFolk.stayPut = true;
                    step = 3;
                }
                else
                {
                    if (theFolk.destination == null)
                    {
                        theFolk.gotoXYZ(theFolk.employedAt, null);
                    }
                }
            }
            else if (step == 3)
            {
                factoryChests = inventoriesFindClosest(theFolk.employedAt, 5);
                openCloseChest(factoryChests.get(0), 1000);
                boolean placed = inventoriesTransferFromFolk(theFolk.inventory, factoryChests, null);
        
                theStage = Stage.USEFURNACE;
                step = 1;
            }
        }
        catch (Exception e)
        {
           
        }
    }

    ////  0 = Clay (top)   1=fuel coal/wood/lava    2=output
    private void stageUseFurnace()
    {
        factoryFurnace = findFurnace(theFolk.employedAt);
        factoryChests = inventoriesFindClosest(theFolk.employedAt, 5);

        if (factoryFurnace == null)
        {
       
            SimukraftReloaded.sendChat(theFolk.name + ": Where's my furnace gone!");
            return;
        }

        if (step == 1)
        {
            theFolk.statusText = "Checking furnace fuel";
            ItemStack currentFuel = factoryFurnace.getStackInSlot(1);
            ItemStack gotFuel = null;

            if (currentFuel == null)
            {
                gotFuel = inventoriesGet(factoryChests, new ItemStack(Items.coal, 64), false,false, new ItemStack(Items.coal, 64));

                if (gotFuel == null)
                {
                    gotFuel = inventoriesGet(factoryChests, new ItemStack(Items.lava_bucket, 1), false,false, new ItemStack(Items.lava_bucket, 1));
                }

                if (gotFuel == null)
                {
                    gotFuel = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.log), 64), false,false,new ItemStack(Item.getItemFromBlock(Blocks.log), 64));
                }

                if (gotFuel == null)
                {
                    gotFuel = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.planks), 64), false,false,new ItemStack(Item.getItemFromBlock(Blocks.planks), 1));
                }

                if (gotFuel == null)
                {
                    SimukraftReloaded.sendChat(theFolk.name + " (Brick maker) doesn't have any fuel for their furnace");
                    theStage = Stage.SCANFORCLAY;
                    step = 1;
                    return;
                }
                else
                {
    
                    //put the fuel in the bottom slot
                    factoryFurnace.setInventorySlotContents(1, gotFuel);
                    step = 2;
                    return;
                }
            }
            else
            {
                step = 2;
            }
        }
        else if (step == 2)
        {
            theFolk.statusText = "Adding clay to the furnace";

            if (factoryFurnace != null)
            {
                ItemStack currentClay = factoryFurnace.getStackInSlot(0);
                ItemStack gotClay = null;

                if (currentClay == null)
                {
                    gotClay = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.clay), 64), false,false,new ItemStack(Blocks.clay, 64));

                    if (gotClay != null)
                    {
                        //place clay in top slot
                        factoryFurnace.setInventorySlotContents(0, gotClay);
                   
                    }

                    step = 3;
                    return;
                }
                else
                {
                    gotClay = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.clay), 64 - currentClay.stackSize), false,false,new ItemStack(Blocks.clay, 64 - currentClay.stackSize));

                    if (gotClay != null)
                    {
                        // top up the clay
                    	currentClay.stackSize += gotClay.stackSize;
                        factoryFurnace.setInventorySlotContents(0, currentClay);
                  
                    }

                    step = 3;
                    return;
                }
            }
        }
        else if (step == 3)
        {
            ItemStack currentBricks = factoryFurnace.getStackInSlot(2);

            if (currentBricks != null)
            {
                theFolk.statusText = "Putting bricks into storage";
                boolean placedOk = inventoriesPut(factoryChests, currentBricks, true);

                SimukraftReloaded.states.credits -= (0.005 * currentBricks.stackSize);
                factoryFurnace.setInventorySlotContents(2, null);
            }
            else
            {
                theFolk.statusText = "No bricks been made";

            }

            theStage = Stage.SCANFORCLAY;
        }
    }

    @Override
    public void onArrivedAtWork()
    {
        int dist = 0;
        dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 1)
        {
            theFolk.action = FolkAction.ATWORK;
            theFolk.stayPut = true;
            theFolk.statusText = "Arrived at the factory";
            theStage = Stage.USEFURNACE;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
