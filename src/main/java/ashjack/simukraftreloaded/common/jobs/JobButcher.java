package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JobButcher extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112207904271422L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    //private transients
    private transient float pay = 0f;
    private transient ArrayList<IInventory> chestsAtFarm = new ArrayList<IInventory>();
    private transient ArrayList<IInventory> chestsAtShop = new ArrayList<IInventory>();
    private transient int currentFarmNum = 0;
    private transient Building farm = null;
    private transient boolean onRoute = false;

    public JobButcher() {}

    public JobButcher(FolkData folk)
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
        IDLE, ARRIVEDATSHOP, GOINGTOMEATFARM, COLLECTINGMEAT, GOBACKTOSTORE, SELLINGMEAT;
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
            theFolk.action = FolkAction.ATWORK;
            runDelay = 11000;
        }
        else
        {
            runDelay = 3000;
        }

        if (theStage == Stage.SELLINGMEAT)
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
            theStage = Stage.GOINGTOMEATFARM;
        }
        else if (theStage == Stage.GOINGTOMEATFARM)
        {
            stageGoingToFarm();
        }
        else if (theStage == Stage.COLLECTINGMEAT)
        {
            stageCollectingMeat();
        }
        else if (theStage == Stage.GOBACKTOSTORE)
        {
            stageGoBackToStore();
        }
        else if (theStage == Stage.SELLINGMEAT)
        {
            stageSellingMeat();
        }

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        if (theStage == Stage.ARRIVEDATSHOP)
        {
            theFolk.action = FolkAction.ATWORK;
            runDelay = 11000;
        }
        else
        {
            runDelay = 3000;
        }

        if (theStage == Stage.SELLINGMEAT)
        {
            runDelay = 10000;
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();
    }

    private void stageGoingToFarm()
    {
        theFolk.statusText = "Fetching meat from livestock farms";
        theFolk.action = FolkAction.ATWORK;

        if (!onRoute)
        {
            farm = getCurrentFarm();

            try {
	            if (farm == null || farm.primaryXYZ==null)
	            {
	                theStage = Stage.GOBACKTOSTORE;
	            }
	            else
	            {
	                onRoute = true;
	                theFolk.gotoXYZ(farm.primaryXYZ, GotoMethod.BEAM);
	            }
            } catch(Exception e){e.printStackTrace(); theStage = Stage.GOBACKTOSTORE;}
            
        }
        else
        {
            int dist = theFolk.location.getDistanceTo(farm.primaryXYZ);

            if (dist < 3)
            {
                if (theFolk.theEntity != null)
                {
                    theFolk.theEntity.motionX = 0;
                    theFolk.theEntity.motionZ = 0;
                }

                onRoute = false;
                theStage = Stage.COLLECTINGMEAT;
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

    private void stageCollectingMeat()
    {
        theFolk.statusText = "Collecting Meat from chests";
        theFolk.action = FolkAction.ATWORK;

        if (step == 1)
        {
            chestsAtFarm.clear();
            chestsAtFarm = inventoriesFindClosest(farm.primaryXYZ, 5);

            if (chestsAtFarm.size() > 0)
            {
                step = 2;
            }
        }
        else if (step == 2)
        {
            inventoriesTransferToFolk(theFolk.inventory, chestsAtFarm, new ItemStack(Items.chicken, 1, 640));
            inventoriesTransferToFolk(theFolk.inventory, chestsAtFarm, new ItemStack(Items.porkchop, 1, 640));
            inventoriesTransferToFolk(theFolk.inventory, chestsAtFarm, new ItemStack(Items.beef, 1, 640));
            step = 3;
        }
        else if (step == 3)
        {
            theStage = Stage.GOINGTOMEATFARM;
        }
    }

    private void stageGoBackToStore()
    {
        theFolk.action = FolkAction.ATWORK;
        theFolk.statusText = "Taking meat back to butchers shop";

        if (!onRoute)
        {
            onRoute = true;
            theFolk.gotoXYZ(theFolk.employedAt, GotoMethod.BEAM);
        }
        else
        {
            double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist < 2)
            {
                onRoute = false;

                if (theFolk.theEntity != null)
                {
                    theFolk.theEntity.motionX = 0;
                    theFolk.theEntity.motionZ = 0;
                }

                theFolk.stayPut = true;
                theFolk.statusText = "Unloading meat";
                int meat1 = getInventoryCount(theFolk, Items.porkchop);
                int meat2 = getInventoryCount(theFolk, Items.chicken);
                int meat3 = getInventoryCount(theFolk, Items.beef);
                pay = (float)((meat1 + meat2 + meat3) * 0.03);
                chestsAtShop =  inventoriesFindClosest(theFolk.employedAt, 3);
                inventoriesTransferFromFolk(theFolk.inventory, chestsAtShop, null);
                theStage = Stage.SELLINGMEAT;
                step = 1;
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

    private void stageSellingMeat()
    {
        theFolk.action = FolkAction.ATWORK;
        theFolk.statusText = "Selling meat to customers";

        if (step == 1)
        {
            chestsAtShop =  inventoriesFindClosest(theFolk.employedAt, 3);
            openCloseChest(chestsAtShop.get(0), 2000);

            if (pay > 0)
            {
            	SimukraftReloaded.states.credits -= pay;
                SimukraftReloaded.sendChat(theFolk.name
                                      + " has collected meat and has been paid "
                                      + SimukraftReloaded.displayMoney(pay) + " Sim-u-credits.");
                mc.theWorld.playSound(mc.thePlayer.posX,
                                      mc.thePlayer.posY, mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
            }

            step = 2;
        }
        else if (step == 2)
        {
            if (mc.getIntegratedServer().worldServers[0].getWorldTime() % 24000 > 11600)
            {
                step = 3;
            }

            theFolk.updateLocationFromEntity();
            double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist > 8)
            {
                theFolk.beamMeTo(theFolk.employedAt);
            }
        }
        else if (step == 3)
        {
            theFolk.statusText = "Closing the shop";
            FolkData folk;
            int inChest;
            int got;
            int sell = 0;
            boolean notEnough = false;
            ItemStack piece = null;
            chestsAtShop =  inventoriesFindClosest(theFolk.employedAt, 3);

            for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
            {
                piece = inventoriesGet(chestsAtShop, new ItemStack(Items.porkchop, 1), false,false);

                if (piece == null)
                {
                    piece = inventoriesGet(chestsAtShop, new ItemStack(Items.chicken, 1), false,false);
                }

                if (piece == null)
                {
                    piece = inventoriesGet(chestsAtShop, new ItemStack(Items.beef, 1), false,false);
                }

                if (piece != null)
                {
                    folk = (FolkData) SimukraftReloaded.theFolks.get(f);
                    folk.levelFood = 10;
                    sell++;
                }
            }

            if (sell > 0)
            {
                SimukraftReloaded.sendChat(theFolk.name + " has sold " + sell
                                      + " pieces of meat to folks today.");
            }

            step = 4;
        }
        else if (step == 4)
        {
            //just hang about until home time
        }
    }

    /** returns the next farm to visit or NULL if we've done all the farms */
    private Building getCurrentFarm()
    {
        boolean found = false;

        while (!found)
        {
            try
            {
                Building farm = (Building) SimukraftReloaded.theBuildings.get(currentFarmNum);

                if (farm.displayNameWithoutPK.contains("Cattle Farm") ||
                        farm.displayNameWithoutPK.contains("Pig Farm") ||
                        farm.displayNameWithoutPK.contains("Chicken Farm"))
                {
                    found = true;
                    currentFarmNum++;
                    return farm;
                }

                currentFarmNum++;

                if (currentFarmNum > SimukraftReloaded.theBuildings.size() - 1)
                {
                    return null;
                }
            }
            catch (Exception e)
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
            theFolk.statusText = "Arrived at the shop";
            theStage = Stage.ARRIVEDATSHOP;
            currentFarmNum = 0;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
