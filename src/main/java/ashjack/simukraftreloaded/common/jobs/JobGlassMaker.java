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

public class JobGlassMaker extends Job implements Serializable
{
    private static final long serialVersionUID = 1177111222904279141L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient V3 blockOfSand = null;
    private transient ArrayList<IInventory> factoryChests = new ArrayList<IInventory>();
    private transient TileEntityFurnace factoryFurnace = null;

    public JobGlassMaker() {}

    public JobGlassMaker(FolkData folk)
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
        IDLE, SCANFORSAND, GOTOSANDBLOCK, COLLECTSAND, RETURNSAND, USEFURNACE, CANTWORK;
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
            theStage = Stage.SCANFORSAND;
            return;
        }
        else if (theStage == Stage.COLLECTSAND ||
                 theStage == Stage.GOTOSANDBLOCK ||
                 theStage == Stage.SCANFORSAND)
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
        else if (theStage == Stage.SCANFORSAND)
        {
            stageScanForSand();
        }
        else if (theStage == Stage.GOTOSANDBLOCK)
        {
            stageGotoSandBlock();
        }
        else if (theStage == Stage.COLLECTSAND)
        {
            stageCollectSand();
        }
        else if (theStage == Stage.RETURNSAND)
        {
            stageReturnSand();
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
        theFolk.statusText = "There's no sand around here!";
        return;
    }

    private void stageScanForSand()
    {
        if (theFolk.statusText.contains("Arrived") || theFolk.statusText.contains("glass"))
        {
            theFolk.statusText = "Going to dig up some more sand";
        }

        try
        {
            blockOfSand = findClosestBlockType(theFolk.employedAt, Blocks.sand, 80, true);

            if (blockOfSand == null)
            {
                theStage = Stage.USEFURNACE;
                return;
            }
            else
            {
                theStage = Stage.GOTOSANDBLOCK;
            }
        }
        catch (Exception e)
        {
       
        }
    }

    private long lastGotocmd = 0l;

    private void stageGotoSandBlock()
    {
        try
        {
            if (theFolk.theEntity != null)
            {
                theFolk.theEntity.swingProgress = 0.0f;
            }

            theFolk.updateLocationFromEntity();
            double dist = theFolk.location.getDistanceTo(blockOfSand);

            if (dist > 4  && System.currentTimeMillis() - lastGotocmd > 10000)
            {
                theFolk.stayPut = false;
                theFolk.gotoXYZ(blockOfSand, null);
                theFolk.stayPut = false;
                lastGotocmd = System.currentTimeMillis();
            }

            theStage = Stage.COLLECTSAND;
        }
        catch (Exception e)
        {
            
        }
    }

    private int gotoCount = 0;

    private void stageCollectSand()
    {
        this.runDelay = 1000;
        theFolk.isWorking = true;
        theFolk.updateLocationFromEntity();
        double dist = theFolk.location.getDistanceTo(blockOfSand);

        if (dist > 6  && System.currentTimeMillis() - lastGotocmd > 10000)
        {
            theFolk.gotoXYZ(blockOfSand, null);
            theFolk.stayPut = false;
            lastGotocmd = System.currentTimeMillis();
            gotoCount++;

            if (gotoCount > 2)
            {
                gotoCount = 0;
                V3 bs = blockOfSand.clone();
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
            jobWorld.setBlock(blockOfSand.x.intValue(), blockOfSand.y.intValue(), blockOfSand.z.intValue(), Blocks.air, 0, 0x03);
            mc.theWorld.playSound(blockOfSand.x, blockOfSand.y, blockOfSand.z, "step.sand", 1f, 1f, false);
            theFolk.inventory.add(new ItemStack(Item.getItemFromBlock(Blocks.sand), 1));
            theFolk.statusText = "Diggy diggy sand, got " + theFolk.inventory.size();
            SimukraftReloaded.states.credits -= 0.012;

            if (theFolk.inventory.size() < 64)
            {
                theStage = Stage.SCANFORSAND;
            }
            else
            {
                theStage = Stage.RETURNSAND;
                step = 1;
            }
        }
        catch (Exception e)
        {
            
        }
    }

    private void stageReturnSand()
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

    ////  0 = sand (top)   1=fuel coal/wood/lava    2=output
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
                    SimukraftReloaded.sendChat(theFolk.name + " (Glass maker) doesn't have any fuel for their furnace");
                    theStage = Stage.SCANFORSAND;
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
            theFolk.statusText = "Adding sand to the furnace";

            if (factoryFurnace != null)
            {
                ItemStack currentSand = factoryFurnace.getStackInSlot(0);
                ItemStack gotSand = null;

                if (currentSand == null)
                {
                    gotSand = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.sand), 64), false,false,new ItemStack(Blocks.sand, 64));

                    if (gotSand != null)
                    {
                        //place sand in top slot
                        factoryFurnace.setInventorySlotContents(0, gotSand);
                   
                    }

                    step = 3;
                    return;
                }
                else
                {
                    gotSand = inventoriesGet(factoryChests, new ItemStack(Item.getItemFromBlock(Blocks.sand), 64 - currentSand.stackSize), false,false,new ItemStack(Blocks.sand, 64 - currentSand.stackSize));

                    if (gotSand != null)
                    {
                        // top up the sand
                        currentSand.stackSize += gotSand.stackSize;
                        factoryFurnace.setInventorySlotContents(0, currentSand);
                  
                    }

                    step = 3;
                    return;
                }
            }
        }
        else if (step == 3)
        {
            ItemStack currentGlass = factoryFurnace.getStackInSlot(2);

            if (currentGlass != null)
            {
                theFolk.statusText = "Putting glass into storage";
                boolean placedOk = inventoriesPut(factoryChests, currentGlass, true);

                SimukraftReloaded.states.credits -= (0.005 * currentGlass.stackSize);
                factoryFurnace.setInventorySlotContents(2, null);
            }
            else
            {
                theFolk.statusText = "No glass been made";

            }

            theStage = Stage.SCANFORSAND;
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
