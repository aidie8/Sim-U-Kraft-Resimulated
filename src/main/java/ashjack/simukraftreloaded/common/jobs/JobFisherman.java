package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobShepherd.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;

public class JobFisherman extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112207254191941L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;
    transient private long timeSinceLastCaughtFish = 0;
    transient private int fishCount = 0;

    private transient ArrayList<IInventory> dockChests = new ArrayList<IInventory>();

    public JobFisherman(FolkData folk)
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
        IDLE, ARRIVEDATDOCK, FISHING, CAUGHTFISH, SELLINGFISH, CANTWORK;
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

        if (theStage == Stage.CAUGHTFISH)
        {
            runDelay = 20000 + new Random().nextInt(20000);
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

        // ////////////////IDLE
        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
        }
        else if (theStage == Stage.ARRIVEDATDOCK)
        {
            stageArrived();
        }
        else if (theStage == Stage.FISHING)
        {
            stageFishing();
        }
        else if (theStage == Stage.CAUGHTFISH)
        {
            stageCaughtFish();
        }
        else if (theStage == Stage.SELLINGFISH)
        {
            stageSellingFish();
        }
        else if (theStage == Stage.CANTWORK)
        {
            stageCantWork();
        }
    }

    private void stageArrived()
    {
        V3 water = Job.findClosestBlockType(theFolk.employedAt, Blocks.water, 5, false);

        if (water == null)
        {
            theStage = Stage.CANTWORK;
            SimukraftReloaded.sendChat(theFolk.name + " (Fisherman) can't find any fish in the area");
        }
        else
        {
            theStage = Stage.FISHING;
            theFolk.statusText = "Casting out my line";
            fishCount = 0;

            if (!jobWorld.isRemote)
            {
                //jobWorld.spawnEntityInWorld(new EntityFishHook(jobWorld));
            }
        }
    }

    private void stageCantWork()
    {
        theFolk.statusText = "Ain't no fish 'round here!";
    }

    private void stageFishing()
    {
        theFolk.updateLocationFromEntity();
        int dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 3)
        {
            theFolk.stayPut = true;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
            return;
        }

        theFolk.statusText = "Fishing";

        if (System.currentTimeMillis() - timeSinceLastCaughtFish > 50000)
        {
            theStage = Stage.CAUGHTFISH;
        }

        if (mc.getIntegratedServer().worldServers[0].getWorldTime() % 24000 > 11980)
        {
            theStage = Stage.SELLINGFISH;
            step = 1;
        }
    }

    private void stageCaughtFish()
    {
        timeSinceLastCaughtFish = System.currentTimeMillis();
        theFolk.statusText = "Caught a fish, yay!";
        fishCount++;
        SimukraftReloaded.states.credits -= (0.02f);
        dockChests = inventoriesFindClosest(theFolk.employedAt, 4);

        if (dockChests.size() == 0)
        {
            theFolk.statusText = "Damn! someone stole my fishing chests!";
            SimukraftReloaded.sendChat(theFolk.name + " (fisherman) can't find any chests at the dock!");

            if (theFolk.theEntity != null)
            {
                theFolk.theEntity.dropItem(Items.fish, 1);
            }
        }
        else
        {
            inventoriesPut(dockChests, new ItemStack(Items.fish, 1), true);
        }

        theStage = Stage.FISHING;
        theFolk.updateLocationFromEntity();
        int dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 3)
        {
            theFolk.stayPut = true;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
            return;
        }
    }

    private void stageSellingFish()
    {
        if (mc.getIntegratedServer().worldServers[0].getWorldTime() % 24000 < 11600)
        {
            theStage = Stage.IDLE;
            return;
        }

        theFolk.statusText = "All done for today, caught " + fishCount + " fish!";

        if (step == 1)
        {
            int sell = 0;
            ItemStack fishStack = null;

            if (SimukraftReloaded.theFolks.size() > 1)
            {
                sell = SimukraftReloaded.theFolks.size() + 1;
                dockChests = inventoriesFindClosest(theFolk.employedAt, 4);
                fishStack = inventoriesGet(dockChests, new ItemStack(Items.fish, sell), false,false);
            }

            FolkData folk;

            if (fishStack == null)
            {
                //SimukraftReloaded.sendChat(theFolk.name +" did not have any fish to sell today, might do better tomorrow.");
                theStage = Stage.IDLE;
                return;
            }
            else
            {
                SimukraftReloaded.sendChat(theFolk.name + " caught " + fishCount + " fish today and has sold "
                                      + fishStack.stackSize + " to folks.");

                for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
                {
                    folk = (FolkData) SimukraftReloaded.theFolks.get(f);

                    if (fishStack.stackSize > 0)
                    {
                        folk.levelFood = 10;
                        fishStack.stackSize--;
                    }
                }
            }

            step = 2;
        }
        else if (step == 2)
        {
            //just hang out until sunset
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
            theFolk.statusText = "Arrived at the dock";
            theStage = Stage.ARRIVEDATDOCK;
            timeSinceLastCaughtFish = System.currentTimeMillis();
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }

    @Override
    public void resetJob()
    {
        theStage = Stage.IDLE;
        fishCount = 0;
    }
}
