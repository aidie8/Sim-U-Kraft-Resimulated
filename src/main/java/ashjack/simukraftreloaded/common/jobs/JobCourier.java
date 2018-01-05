package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;

import ashjack.simukraftreloaded.common.CourierTask;
import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class JobCourier extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112207901844141L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient ArrayList<CourierTask> courierTasks = new ArrayList<CourierTask>(); //subset of total tasks
    private transient ArrayList<IInventory> chests = new ArrayList<IInventory>(); //all the chests at each point
    private transient int currentTask = 0;
    private transient long timeSinceLastCycle = 0l;
    private transient V3 pickup;
    private transient V3 dropoff;
    private transient boolean onRoute = false;

    public JobCourier() {}

    public JobCourier(FolkData folk)
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
        IDLE, ATDEPOT, GOINGTOPICKUP, PICKINGUP, GOINGTODROPOFF, DROPPINGOFF;
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

        if (theStage == Stage.ATDEPOT)
        {
            runDelay = 15000;
        }
        else
        {
            runDelay = 3000;
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        // ////////////////IDLE
        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
        	this.onUpdateGoingToWork(theFolk);
        }
        else if (theStage == Stage.ATDEPOT)
        {
            stageAtDepot();
        }
        else if (theStage == Stage.GOINGTOPICKUP)
        {
            stageGoingToPickup();
        }
        else if (theStage == Stage.PICKINGUP)
        {
            stagePickingUp();
        }
        else if (theStage == Stage.GOINGTODROPOFF)
        {
            stageGoingToDropoff();
        }
        else if (theStage == Stage.DROPPINGOFF)
        {
            stageDroppingOff();
        }
    }

    private void stageAtDepot()
    {
        if (System.currentTimeMillis() - timeSinceLastCycle < (60000 * 3))
        {
            return;
        }

        currentTask = 0;
        courierTasks.clear();

        for (int t = 0; t < SimukraftReloaded.theCourierTasks.size(); t++)
        {
            CourierTask task = SimukraftReloaded.theCourierTasks.get(t);

            if (task != null && task.pickup !=null)
            {
                if (task.folkname.contentEquals(theFolk.name))
                {
                    try   // NPE when player has removed chest
                    {
                     //   task.pickup = CourierTask.getCourierPoint(task.pickup.name);
                     //   task.dropoff = CourierTask.getCourierPoint(task.dropoff.name);
                       courierTasks.add(task);

                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (courierTasks.size() == 0)
        {
            theFolk.statusText = "No pickups or deliveries to do!";
            return;
        }
        else
        {
            theStage = Stage.GOINGTOPICKUP;
            onRoute = false;
        }
    }

    private void stageGoingToPickup()
    {
        if (!onRoute)
        {
            CourierTask task = courierTasks.get(currentTask);
            pickup = task.pickup;

            if (pickup != null)
            {
                theFolk.statusText = "On my way to " + pickup.name + " (pick-up)";
                V3 d = pickup.clone();
                d.y++;
                theFolk.gotoXYZ(d, GotoMethod.BEAM);
                onRoute = true;
            }
            else
            {
                theStage = Stage.IDLE;
            }
        }
        else
        {
            if (theFolk.gotoMethod == GotoMethod.WALK)
            {
                theFolk.updateLocationFromEntity();
            }

            double dist = theFolk.location.getDistanceTo(pickup);

            if (dist < 3)
            {
                theStage = Stage.PICKINGUP;
                onRoute = false;
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

    private void stagePickingUp()
    {
        CourierTask task = courierTasks.get(currentTask);
        V3 pickup = task.pickup;
        chests.clear();
        chests = inventoriesFindClosest(pickup, 4);

        if (chests.size() == 0)
        {
            ///no chests there, so delete this task for next day
            SimukraftReloaded.log.warning("JobCourier: StagePickingup() No chest at pickup:" + pickup.name + ", removing task.");
            currentTask++;

            if (currentTask >= courierTasks.size())
            {
                currentTask = 0;
                theStage = Stage.IDLE; // go back to depot and wait
            }
            else
            {
                onRoute = false;
                theStage = Stage.GOINGTOPICKUP;
            }
        }
        else       ///chest(s) found at pickup point
        {
            theFolk.stayPut = true;
            theFolk.action = FolkAction.ATWORK;
            theFolk.statusText = "Picking up items";
            /// so now move all contents of all chests into this folk's inventory
            SimukraftReloaded.log.info("JobCourier: pickupStage() "+theFolk.name + "(courier) found " + chests.size() + " chests at " + pickup.name);
            inventoriesTransferToFolk(theFolk.inventory, chests, null);
        }

        if (theFolk.inventory.size() == 0)  ///nothing picked up
        {
            currentTask++;

            if (currentTask >= courierTasks.size())
            {
                currentTask = 0;
                theStage = Stage.IDLE; // go back to depot and wait
                timeSinceLastCycle = System.currentTimeMillis();
            }
            else
            {
                theStage = Stage.GOINGTOPICKUP; //no drop off required, so go to next pickup
            }
        }
        else
        {
            theStage = Stage.GOINGTODROPOFF;
            theFolk.statusText = "Going to drop-off point";
            onRoute = false;
        }
    }

    private void stageGoingToDropoff()
    {
        CourierTask task = courierTasks.get(currentTask);
        if ( task !=null && task.dropoff !=null) {
        	dropoff = task.dropoff.clone();
        } else {
        	theStage=Stage.ATDEPOT;
        	theFolk.gotoXYZ(theFolk.employedAt, null);
        	if (task !=null) {courierTasks.remove(task);}
        }

        if (dropoff == null)
        {
            dropoff = theFolk.employedAt; //it will be null when it's the depot
            dropoff.name = "The depot";
        }

        if (!onRoute)
        {
            theFolk.statusText = "On my way to " + dropoff.name + " (drop-off)";
            V3 d = dropoff.clone();
            if (d==null) {
            	d=theFolk.employedAt.clone();
            }
            d.y++;
            theFolk.beamMeTo(d);
            theFolk.gotoXYZ(d, GotoMethod.BEAM);
            onRoute = true;
        }
        else
        {
            if (theFolk.gotoMethod == GotoMethod.WALK)
            {
                theFolk.updateLocationFromEntity();
            }

            double dist = theFolk.location.getDistanceTo(dropoff);

            if (dist < 4)
            {
                theStage = Stage.DROPPINGOFF;
                onRoute = false;
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

    private void stageDroppingOff()
    {
        CourierTask task = courierTasks.get(currentTask);
        V3 dropoff = task.dropoff;

        if (dropoff == null)
        {
            //dropoff is depot
            dropoff = theFolk.employedAt;
            dropoff.name = "The Depot";
        }

        chests.clear();
        chests = inventoriesFindClosest(dropoff, 5);

        if (chests.size() == 0)
        {
            SimukraftReloaded.log.warning("JobCourierL dropoff() No chest found at dropoff");
            currentTask++;

            if (currentTask >= courierTasks.size())
            {
                currentTask = 0;
                theStage = Stage.IDLE; // go back to depot and wait
            }
            else
            {
                theStage = Stage.GOINGTOPICKUP;
            }
        }
        else      // at least one chest here
        {
            theFolk.stayPut = true;
            theFolk.statusText = "Dropping items off";
            theFolk.action = FolkAction.ATWORK;
            SimukraftReloaded.log.info("JobCourier: "+theFolk.name + " found " + chests.size() + " chests at " + dropoff.name);

            while (theFolk.inventory.size() > 0)
            {
                int oldSize = theFolk.inventory.size();
                SimukraftReloaded.states.credits -= 0.11f;
                ItemStack invItem = theFolk.inventory.get(0);

                if (theFolk.inventory.size() > 1)
                {
                    theFolk.statusText = theFolk.inventory.size() + " stacks of items to unload";
                }
                else
                {
                    theFolk.statusText = "Last load...";
                }

                boolean placed = inventoriesTransferFromFolk(theFolk.inventory, chests, null);

                if (!placed)
                {
                    SimukraftReloaded.sendChat(theFolk.name + " (Courier) can't place items into chest at "
                                          + dropoff.name + " because it's full, add more chests there, or empty them.");
                    break;
                }
            }
        }

        currentTask++;

        if (currentTask >= courierTasks.size())
        {
            theFolk.statusText = "Checking my task list";
            currentTask = 0;
            timeSinceLastCycle = System.currentTimeMillis();
            theFolk.stayPut = false;
            theFolk.gotoXYZ(theFolk.employedAt, null);
            theStage = Stage.IDLE; // go back to depot and wait
        }
        else
        {
            theStage = Stage.GOINGTOPICKUP;
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
            theFolk.statusText = "Arrived at the depot";
            theStage = Stage.ATDEPOT;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
