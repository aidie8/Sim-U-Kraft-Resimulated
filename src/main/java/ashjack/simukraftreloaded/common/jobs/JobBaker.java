package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.blocks.functionality.FarmingBox;
import ashjack.simukraftreloaded.blocks.functionality.FarmingBox.FarmType;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class JobBaker extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112153304279141L;

    //general job variables
    public Vocation vocation = null;
    public Stage theStage;
    public FolkData theFolk;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    // baker variables
    private transient float pay = 0f;
    private transient ArrayList<IInventory> bakeryChests = null;
    private transient ArrayList<IInventory> farmChests = new ArrayList<IInventory>();
    private transient int currentFarmNum = 0;
    private transient FarmingBox farm = null;

    public JobBaker() {}

    public JobBaker(FolkData folk)
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

    public void resetJob()
    {
        theStage = Stage.IDLE;
    }

    public enum Stage
    {
        IDLE, ARRIVEDATSHOP, GOINGTOWHEATFARM, COLLECTINGWHEAT, GOBACKTOBAKERY, MAKEBREAD, SELLINGBREAD;
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
        }

        if (theStage == Stage.COLLECTINGWHEAT)
        {
            runDelay = 1000;
        }

        if (theStage == Stage.SELLINGBREAD)
        {
            runDelay = 10000;
        }

        if (theStage == Stage.MAKEBREAD)
        {
            runDelay = 10000;
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
            theStage = Stage.GOINGTOWHEATFARM;
            step = 1;
        }
        else if (theStage == Stage.GOINGTOWHEATFARM)
        {
            stageGoingToWheatFarm();
        }
        else if (theStage == Stage.COLLECTINGWHEAT)
        {
            stageCollectingWheat();
        }
        else if (theStage == Stage.GOBACKTOBAKERY)
        {
            stageGoBackToBakery();
        }
        else if (theStage == Stage.MAKEBREAD)
        {
            stageMakeBread();
        }
        else if (theStage == Stage.SELLINGBREAD)
        {
            stageSellingBread();
        }
    }

    private void stageGoingToWheatFarm()
    {
        theFolk.statusText = "Fetching wheat from farms";

        if (theFolk.destination == null && step == 1)
        {
            farm = getCurrentFarm();

            if (farm == null)
            {
                theStage = Stage.GOBACKTOBAKERY;
                step = 1;
            }
            else
            {
                theFolk.gotoXYZ(farm.getLocation(), null);
                runDelay = 1000;
                step = 2;
            }
        }

        if (step == 2)
        {
            double dist = 0;

            if (farm != null)
            {
                runDelay = 1000;

                if (theFolk.gotoMethod == GotoMethod.WALK)
                {
                    theFolk.updateLocationFromEntity();
                }

                dist = theFolk.location.getDistanceTo(farm.getLocation());

                if (dist <= 1)
                {
                    theStage = Stage.COLLECTINGWHEAT;
                    step = 1;
                    theFolk.stayPut = true;

                    if (theFolk.theEntity != null)
                    {
                        theFolk.theEntity.motionX = 0;
                        theFolk.theEntity.motionZ = 0;
                    }

                    runDelay = 1000;
                    return;
                }
                else
                {
                }
            }
            else
            {
                theStage = Stage.GOBACKTOBAKERY;
            }
        }
    }

    private void stageCollectingWheat()
    {
        theFolk.statusText = "Collecting Wheat";
        runDelay = 1000;

        if (step == 1)
        {
            farmChests = inventoriesFindClosest(farm.getLocation(), 5);

            if (farmChests.size() > 0)
            {
                farmChests.get(0).openInventory();
                step = 2;
            }
        }
        else if (step == 2)
        {
            farmChests = inventoriesFindClosest(farm.getLocation(), 5);
            inventoriesTransferToFolk(theFolk.inventory, farmChests, new ItemStack(Items.wheat, 640));
            step = 3;
        }
        else if (step == 3)
        {
            farmChests.get(0).closeInventory();
            theStage = Stage.GOINGTOWHEATFARM;
            step = 1;
        }
    }

    private void stageGoBackToBakery()
    {
        theFolk.statusText = "Taking wheat back to bakery";

        if (theFolk.destination == null && step == 1)
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
            runDelay = 100;
            step = 2;
        }

        if (step == 2)
        {
            if (theFolk.gotoMethod == GotoMethod.WALK)
            {
                theFolk.updateLocationFromEntity();
            }

            int dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist <= 1)
            {
                theStage = Stage.MAKEBREAD;
                step = 1;
                theFolk.stayPut = true;
                return;
            }
        }
    }

    private void stageMakeBread()
    {
        theFolk.statusText = "Baking bread";
        bakeryChests = inventoriesFindClosest(theFolk.employedAt, 4);

        if (bakeryChests == null || bakeryChests.size() == 0)
        {
            theFolk.statusText = "Who stole the chest from my bakery!!";
            return;
        }

        if (step == 1)
        {
            // just bakin'
            theFolk.updateLocationFromEntity();
            int dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist > 1)
            {
                theFolk.beamMeTo(theFolk.employedAt);
            }

            step = 2;
        }
        else if (step == 2)
        {
            int wheat = getInventoryCount(theFolk, Items.wheat);
            int bread = (int) Math.floor(wheat / 3);
            pay = (float)(bread * 0.20);
            bakeryChests = inventoriesFindClosest(theFolk.employedAt, 4);

            try
            {
                bakeryChests.get(0).openInventory();
            }
            catch (Exception e) {}

            inventoriesPut(bakeryChests, new ItemStack(Items.bread, bread), true);
            theFolk.inventory.clear();
            step = 3;
        }
        else if (step == 3)
        {
            bakeryChests.get(0).closeInventory();
            theFolk.statusText = "Selling bread to customers";
            theFolk.stayPut = true;

            if (theFolk.theEntity != null)
            {
                if (theFolk.gender == 0)
                {
                    mc.theWorld.playSound(theFolk.location.x, theFolk.location.y, theFolk.location.z, "ashjacksimukraftreloaded:bakerm", 1f, 1f, false);
                }
                else
                {
                    mc.theWorld.playSound(theFolk.location.x, theFolk.location.y, theFolk.location.z, "ashjacksimukraftreloaded:bakerf", 1f, 1f, false);
                }
            }

            theStage = Stage.SELLINGBREAD;
            step = 1;
        }
    }

    private void stageSellingBread()
    {
        if (step == 1)
        {
            if (pay > 0)
            {
            	SimukraftReloaded.states.credits -= pay;
                SimukraftReloaded.sendChat(theFolk.name
                                      + " has made some bread and has been paid "
                                      + SimukraftReloaded.displayMoney(pay) + " Sim-u-credits.");
                mc.theWorld.playSound(mc.thePlayer.posX,
                                      mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f,
                                      1f, false);
            }

            step = 2;
        }
        else if (step == 2)
        {
            if (MinecraftServer.getServer().worldServers[0].getWorldTime() % 24000 > 11600)
            {
                step = 3;
            }
        }
        else if (step == 3)
        {
            theFolk.statusText = "Closing the shop";
            int sell = 0;
            ItemStack breadStack = null;

            if (SimukraftReloaded.theFolks.size() > 1)
            {
                sell = SimukraftReloaded.theFolks.size() + 1 + new Random().nextInt(SimukraftReloaded.theFolks.size());
                bakeryChests = inventoriesFindClosest(theFolk.employedAt, 4);
                breadStack = inventoriesGet(bakeryChests, new ItemStack(Items.bread, sell), false,false);
            }

            FolkData folk;

            if (breadStack == null)
            {
                SimukraftReloaded.sendChat(theFolk.name + " did not have any bread to sell today, do you have an active wheat farm?");
            }
            else
            {
                SimukraftReloaded.sendChat(theFolk.name + " has sold " + breadStack.stackSize
                                      + " loaves of bread to folks today.");

                for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
                {
                    folk = (FolkData) SimukraftReloaded.theFolks.get(f);

                    if (breadStack.stackSize > 0)
                    {
                        folk.levelFood = 10;
                        breadStack.stackSize--;
                    }
                }
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
        FarmingBox farm;

        while (!found)
        {
            try
            {
                farm = (FarmingBox) SimukraftReloaded.theFarmingBoxes.get(currentFarmNum);

                if (farm.farmType == FarmType.WHEAT)
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
            catch (Exception e)
            {
                return null;   // catches when they have 1 farm only
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
            theFolk.statusText = "Arrived at the Bakery";
            theStage = Stage.ARRIVEDATSHOP;
            currentFarmNum = 0;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
