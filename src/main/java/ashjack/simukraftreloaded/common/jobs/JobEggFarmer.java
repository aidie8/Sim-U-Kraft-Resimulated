package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobBurgersManager.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JobEggFarmer extends Job{
	
	public Vocation vocation = null;
    public Stage theStage;
    public FolkData theFolk;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;
    private ArrayList<IInventory> farmChests = new ArrayList<IInventory>();
    
    public JobEggFarmer(FolkData folk) {
        theFolk = folk;

          if (theStage == null)
          {
              theStage = Stage.IDLE;
          }

          if (theFolk == null)
          {
              return;   // is null when first employing
          }

          if (theFolk.destination == null)
          {
              theFolk.gotoXYZ(theFolk.employedAt, null);
          }
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

    if (theStage == Stage.FEEDINGCHICKENS)
    {
        runDelay = 40000;
    } else {
    	runDelay=10000;
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
    else if (theStage == Stage.ARRIVEDATFARM)
    {
        stageArrived();
    }
    else if (theStage == Stage.FEEDINGCHICKENS)
    {
        stageWaiting();
    }
    else if (theStage == Stage.COLLECTINGEGGS)
    {
        stageCollectingEggs();
    }
    else if (theStage== Stage.STORINGEGGS) {
    	stageStoringEggs();
    }
    else if (theStage == Stage.CANTWORK)
    {
        stageCantWork();
    }
}
    
    

private void stageArrived() {
    vocation = theFolk.vocation;
    theStage = Stage.FEEDINGCHICKENS;
    theFolk.statusText = "Feeding Chickens";
    int count = 0;

    count = getAnimalCountInPen(theFolk.employedAt, EntityChicken.class);

    if (count < 6)
    {
        spawnHens(theFolk.employedAt, 6 - count);
    }
}

private void stageWaiting() {
    theFolk.updateLocationFromEntity();
    double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

    if (dist > 10)
    {
        theFolk.beamMeTo(theFolk.employedAt);
    }
    theFolk.statusText="Raking Manure";
    theStage=Stage.COLLECTINGEGGS;
}
 
private void stageCollectingEggs() {
	theFolk.statusText="Collecting Eggs";
	theStage=Stage.STORINGEGGS;
	theFolk.isWorking=true;
}

private void stageStoringEggs() {
	Random rand=new Random();
	int c=rand.nextInt(7);
	theFolk.statusText="Storing Eggs in refrigerated chest";
	theFolk.isWorking=false;
	farmChests = inventoriesFindClosest(theFolk.employedAt, 5);
	if (farmChests.size()>0) {
		boolean ok=inventoriesPut(farmChests,new ItemStack(Items.egg,c+1, 0) , true);
		if (!ok) {
			SimukraftReloaded.sendChat(theFolk.name+"'s egg farm chests are full of eggs!");
			theFolk.statusText = "Can't work, the chests are full";
			theStage=Stage.CANTWORK;
		} else {
			SimukraftReloaded.states.credits -= 0.05f;
	        theStage = Stage.FEEDINGCHICKENS;
		}
	} else {
		theFolk.statusText="Who stole my egg chests!";
		theStage=Stage.CANTWORK;
	}
	
}

private void stageCantWork() {
	//status set when stage is activated
}
    
    public enum Stage
    {
        IDLE, ARRIVEDATFARM, FEEDINGCHICKENS, COLLECTINGEGGS, STORINGEGGS, CANTWORK;
    }

    @Override
	public void onArrivedAtWork() {
        int dist = 0;
        dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 1)
        {
            theFolk.action = FolkAction.ATWORK;
            theFolk.stayPut = true;
            theFolk.statusText = "Arrived at the farm";
            theStage = Stage.ARRIVEDATFARM;
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
	}
	
    private void spawnHens(V3 controlBox, int count)
    {
        EntityAnimal newAnimal = null;

        for (int c = 1; c <= count; c++)
        {
          
            newAnimal = new EntityChicken(jobWorld);
            newAnimal.setLocationAndAngles(controlBox.x+1, controlBox.y + 1, controlBox.z, 0f, 0f);

            if (!jobWorld.isRemote)
            {
                jobWorld.spawnEntityInWorld(newAnimal);
            }
        }
    }

}
