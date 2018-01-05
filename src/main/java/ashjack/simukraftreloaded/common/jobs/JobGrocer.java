package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.FarmingBox.FarmType;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class JobGrocer extends Job implements Serializable
{
    private static final long serialVersionUID = -1177119265904279141L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient float pay = 0f;
    private transient ArrayList<IInventory> grocerChests = new ArrayList<IInventory>();
    private transient ArrayList<IInventory> farmChests = new ArrayList<IInventory>();
    private transient int currentFarmNum = 0;
    private transient FarmingBox farm = null;
    private transient boolean onRoute = false;

    public JobGrocer() {}

    public void resetJob()
    {
        theStage = Stage.IDLE;
    }

    public JobGrocer(FolkData folk)
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
        IDLE, ARRIVEDATSHOP, GOINGTOFOODFARM, COLLECTINGFOOD, GOBACKTOSTORE, SELLINGFOOD;
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

        if (theStage == Stage.ARRIVEDATSHOP)
        {
            runDelay = 10000;
            theFolk.action = FolkAction.ATWORK;
        }

        if (theStage == Stage.COLLECTINGFOOD)
        {
            runDelay = 4000;
        }

        if (theStage == Stage.SELLINGFOOD)
        {
            runDelay = 10000;
            theFolk.action = FolkAction.ATWORK;
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        // ////////////////IDLE
        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
        }
        else if (theStage == Stage.ARRIVEDATSHOP)
        {
            stageArrived();
        }
        else if (theStage == Stage.GOINGTOFOODFARM)
        {
            stageGoingToFoodFarm();
        }
        else if (theStage == Stage.COLLECTINGFOOD)
        {
            stageCollectingFood();
        }
        else if (theStage == Stage.GOBACKTOSTORE)
        {
            stageGoBackToStore();
        }
        else if (theStage == Stage.SELLINGFOOD)
        {
            stageSellingFood();
        }
    }

    private void stageArrived()
    {
        currentFarmNum = 0;
        theFolk.stayPut = true;
        theStage = Stage.GOINGTOFOODFARM;
    }

    private void stageGoingToFoodFarm()
    {
        theFolk.statusText = "Fetching fresh food from farms";

        if (!onRoute)
        {
            farm = getCurrentFarm();

            if (farm == null)
            {
                theStage = Stage.GOBACKTOSTORE;
            }
            else
            {
                onRoute = true;
                theFolk.gotoXYZ(farm.getLocation(), null);
            }
        }
        else
        {
            double dist = 0;

            try
            {
                if (theFolk.gotoMethod == GotoMethod.WALK)
                {
                    theFolk.updateLocationFromEntity();
                }

                dist = theFolk.location.getDistanceTo(farm.location);
            }
            catch (Exception e)
            {
                theStage = Stage.GOBACKTOSTORE;
            }

            if (dist < 3)
            {
                if (theFolk.theEntity != null)
                {
                    theFolk.theEntity.motionX = 0;
                    theFolk.theEntity.motionZ = 0;
                }

                onRoute = false;
                theStage = Stage.COLLECTINGFOOD;
                step = 1;
                theFolk.stayPut = true;
                return;
            }
            else
            {
                if (theFolk.destination == null)
                {
                    onRoute = false;
                }
            }
        }
    }

    private void stageCollectingFood()
    {
        theFolk.statusText = "Collecting Fresh Food";

        if (step == 1)
        {
            if (farm == null)
            {
                theStage = Stage.GOINGTOFOODFARM;
                return;
            }

            farmChests = inventoriesFindClosest(farm.getLocation(), 5);

            if (farmChests.size() > 0)
            {
                theFolk.stayPut = true;
                farmChests.get(0).openInventory();
                step = 2;
            }
        }
        else if (step == 2)
        {
            inventoriesTransferToFolk(theFolk.inventory , farmChests, new ItemStack(Items.melon, 640));
            inventoriesTransferToFolk(theFolk.inventory , farmChests, new ItemStack(Blocks.pumpkin, 640));
            inventoriesTransferToFolk(theFolk.inventory , farmChests, new ItemStack(Items.carrot, 640));
            inventoriesTransferToFolk(theFolk.inventory , farmChests, new ItemStack(Items.potato, 640));
            step = 3;
        }
        else if (step == 3)
        {
            farmChests.get(0).closeInventory();
            theStage = Stage.GOINGTOFOODFARM;
        }
    }

    private void stageGoBackToStore()
    {
        theFolk.statusText = "Taking fresh food back to store";

        if (!onRoute)
        {
            onRoute = true;
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
        else
        {
            if (theFolk.gotoMethod == GotoMethod.WALK)
            {
                theFolk.updateLocationFromEntity();
            }

            double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist <= 1)
            {
                if (theFolk.theEntity != null)
                {
                    theFolk.theEntity.motionX = 0;
                    theFolk.theEntity.motionZ = 0;
                }

                onRoute = false;
                theStage = Stage.SELLINGFOOD;
                step = 1;
                theFolk.stayPut = true;
                return;
            }
            else
            {
                if (theFolk.destination == null)
                {
                    onRoute = false;
                }
            }
        }
    }

    private void stageSellingFood()
    {
        grocerChests = inventoriesFindClosest(theFolk.employedAt, 5);

        if (step == 1)
        {
            theFolk.statusText = "Unloading fresh food";
            int pumpkins = getInventoryCount(theFolk, Blocks.pumpkin);
            int melons = getInventoryCount(theFolk, Items.melon);
            int carrots = getInventoryCount(theFolk, Items.carrot); //carrots
            int potatoes = getInventoryCount(theFolk, Items.potato); //potatoes
            pay = (float)(pumpkins * 0.20);
            pay += (float)(melons * 0.05);
            pay += (float)(carrots * 0.05);
            pay += (float)(potatoes * 0.05);
            inventoriesTransferFromFolk(theFolk.inventory, grocerChests, null);
            SimukraftReloaded.states.credits -= pay;
            step = 2;
        }
        else if (step == 2)
        {
            theFolk.updateLocationFromEntity();
            int dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist > 2 && theFolk.destination == null)
            {
                theFolk.gotoXYZ(theFolk.employedAt, null);
            }

            theFolk.statusText = "Selling fresh food";

            if (MinecraftServer.getServer().worldServers[0].getWorldTime() % 24000 > 11600)
            {
                step = 3;
            }
        }
        else if (step == 3)
        {
            theFolk.statusText = "Closing the shop";
            int sell = 0;
            ItemStack piece = null;

            for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
            {
                gotFood:

                for (int c = 0; c < grocerChests.size(); c++)
                {
                    IInventory chest = grocerChests.get(c);

                    for (int g = 0; g < chest.getSizeInventory(); g++)
                    {
                        ItemStack chestStack = chest.getStackInSlot(g);

                        try
                        {
                            int count = new Random().nextInt(3) + 1;
                            ItemFood food = (ItemFood) chestStack.getItem();
                            piece = inventoriesGet(grocerChests, new ItemStack(chestStack.getItem(), count), false,false); //grab 1 piece
                            FolkData folk = (FolkData) SimukraftReloaded.theFolks.get(f);
                            folk.levelFood = 10;
                            sell += count;
                            break gotFood;
                        }
                        catch (Exception e)
                        {
                            // no item or NOT food
                        }
                    }
                }

                /*
                piece=inventoriesGet(grocerChests, new ItemStack(Item.melon,1),false);
                if (piece==null) {
                	piece=inventoriesGet(grocerChests, new ItemStack(Block.pumpkin,1),false);
                }
                if (piece==null) {
                	piece=inventoriesGet(grocerChests, new ItemStack(Item.carrot,1),false);
                }
                if (piece==null) {
                	piece=inventoriesGet(grocerChests, new ItemStack(Item.potato,1),false);
                }
                if (piece !=null) {
                	FolkData folk = (FolkData) ModSimukraft.theFolks.get(f);
                	folk.levelFood=10;
                	sell++;
                }
                */
            }

            if (sell > 0)
            {
                SimukraftReloaded.sendChat(theFolk.name + "(grocer) has sold " + sell + " items of food to folks today.");
            }
            else
            {
                SimukraftReloaded.sendChat(theFolk.name + " has no produce to sell to folks today.");
            }

            step = 4;
        }
        else if (step == 4)
        {
            //just hang about until home time
        }
    }
    
    

    /** returns the next farm to visit or NULL if we've done all the farms */
    private FarmingBox getCurrentFarm()
    {
        boolean found = false;
        FarmingBox farm = null;

        while (!found)
        {
            try
            {
                farm = (FarmingBox) SimukraftReloaded.theFarmingBoxes.get(currentFarmNum);
            }
            catch (Exception e)
            {
                return null;
            }

            if (farm == null)
            {
                return null ;
            }

            if (farm.farmType == FarmType.MELON || farm.farmType == FarmType.PUMPKIN ||
                    farm.farmType == FarmType.CARROT || farm.farmType == FarmType.POTATO)
            {
                found = true;
                currentFarmNum++;
                return farm;
            }

            currentFarmNum++;

            if (currentFarmNum > SimukraftReloaded.theFarmingBoxes.size() - 1)
            {
                return null;
            }
        }

        return null;
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
            theFolk.statusText = "Arrived at the store";
            theStage = Stage.ARRIVEDATSHOP;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
