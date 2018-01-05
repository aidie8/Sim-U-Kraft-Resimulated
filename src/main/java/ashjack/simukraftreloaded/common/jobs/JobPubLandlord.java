package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobBaker.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JobPubLandlord extends Job{

	 public Vocation vocation = null;
	 public Stage theStage;
	 public FolkData theFolk;
	 transient public int runDelay = 1000;
	 transient public long timeSinceLastRun = 0;
	 
	 public int breweryNumber = 0;
	 public Building brewerySingle = null;
	 public ArrayList<Building> breweryBuildings;
	 
	 public JobPubLandlord() {}
	 
	 public JobPubLandlord(FolkData folk)
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
	        IDLE, ARRIVEDATPUB, GOINGTOBREWERY, COLLECTINGBEER, GOBACKTOPUB, BOTTLINGBEER, SELLINGBEER;
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

	        if (theStage == Stage.ARRIVEDATPUB)
	        {
	            runDelay = 10000;
	        }

	        if (theStage == Stage.COLLECTINGBEER)
	        {
	            runDelay = 1000;
	        }

	        if (theStage == Stage.SELLINGBEER)
	        {
	            runDelay = 10000;
	        }

	        if (theStage == Stage.BOTTLINGBEER)
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
	        else if (theStage == Stage.ARRIVEDATPUB)
	        {
	            theStage = Stage.GOINGTOBREWERY;
	            step = 1;
	        }
	        else if (theStage == Stage.GOINGTOBREWERY)
	        {
	            stageGoingToBrewery();
	        }
	        else if (theStage == Stage.GOBACKTOPUB)
	        {
	            stageGoBackToPub();
	        }
	        else if (theStage == Stage.BOTTLINGBEER)
	        {
	            stageBottlingBeer();
	        }
	        else if (theStage == Stage.SELLINGBEER)
	        {
	            stageSellingBeer();
	        }
	    }

		private void stageSellingBeer() 
		{
			
		}

		private void stageBottlingBeer() 
		{
			
		}

		private void stageGoBackToPub() 
		{
			
		}

		private void stageGoingToBrewery() 
		{
			if (step<4) {
	    		doPickup("brewery",new ItemStack(SimukraftReloadedItems.itemDrinkBeer,1,0).getItem(),true);
	    	} else {
	    		theStage=Stage.GOINGTOBREWERY;
	    		step=1;
	    	}
		}
	
		private void doPickup(String buildingSearch,Item pickUpItem, boolean doCompareMeta) {
	    	if (step==1) {
	    		breweryNumber=0;
	    		breweryBuildings.clear();
	    		ArrayList<Building> brewery=Building.getBuildingBySearch("Brewery",true);	
	    		if (breweryBuildings.size() ==0) { step=4; return; }
	    		
	    		theFolk.gotoXYZ(breweryBuildings.get(breweryNumber).primaryXYZ, null);
	    		theFolk.statusText="On my way to the brewery";
	    		step=2;
	    	
	    	} else if (step==2) {
	    		if (theFolk.destination ==null) {
	    			step=3;
	    		}
	    	
	    	} else if (step==3) {
	    		theFolk.statusText="Buying items at the "+breweryBuildings.get(breweryNumber).displayName;
	    		ArrayList<IInventory> chests=inventoriesFindClosest(breweryBuildings.get(breweryNumber).primaryXYZ, 5);
	    		if (!chests.isEmpty()) {
		    		int count=getItemCountInChests(chests, new ItemStack(pickUpItem, 1), doCompareMeta);
		    		int buy=count/4;
		    		if (buy>0) {
		    			SimukraftReloaded.log.info("JobBurgersManager: buying "+buy+" out of "+count+" items");
		    			inventoriesTransferLimitedToFolk(theFolk.inventory, chests
		    					, new ItemStack(pickUpItem, 1),buy,doCompareMeta);
		    			
		    		
		    		}
	    		}
	    		breweryNumber++;
	    		if (breweryNumber <= (breweryBuildings.size()-1)) {
	    			theFolk.gotoXYZ(breweryBuildings.get(breweryNumber).primaryXYZ, null);
	        		step=2;
	    		} else {
	    			step=4;
	    		}
	    	}
	    }

		@Override
		public void onArrivedAtWork() {
			// TODO Auto-generated method stub
			
		}

}
