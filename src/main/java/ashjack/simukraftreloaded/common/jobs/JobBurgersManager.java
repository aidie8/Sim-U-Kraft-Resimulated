package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JobBurgersManager extends Job {
	
	
	/*
	Specialblock meta values for this job:
	0=Raw materials chest waypoint
	1=Frycook waypoint
	2=Waiter waypoint
	*/
	public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    public int runDelay = 1000;
    private long timeSinceLastRun=0;
    private Building theStore=null;
    private int currentPickup=0;
    private ArrayList<Building> pickupBuildings=new ArrayList<Building>();
    
    public int beefCount;
    public int cheeseCount;
    public int breadCount;
    public int potatoCount;
    
    public boolean placedBeef;
    public boolean placedCheese;
    public boolean placedBread;
    public boolean placedPotato;
    
    
	public JobBurgersManager(FolkData folk) {
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

	// Visits: Bakery, Grocery,Cheese Factory, Butchers
    public enum Stage
    {
        IDLE, ARRIVEDATSTORE, PICKUPBAKERY, PICKUPGROCERY, PICKUPCHEESE,PICKUPBUTCHERS, DROPOFF,HANGINGOUT
    }
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if(theStore ==null) {
        	theStore=Building.getBuilding(theFolk.employedAt);
        }
        if (theStore==null) {
        	//wait until building has loaded
        	return;
        }

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        super.onUpdateGoingToWork(theFolk);

        if (theStage == Stage.ARRIVEDATSTORE)
        {
            theFolk.action = FolkAction.ATWORK;
            runDelay = 11000;
        }
        else if (theStage==Stage.HANGINGOUT) {
        	runDelay=30000;
        }
        else
        {
            runDelay = 5000;
        }
        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }
      

        // ////////////////IDLE
        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
        }
        else if (theStage == Stage.ARRIVEDATSTORE)
        {
        	theFolk.statusText="Checking my errands list";
        	theStage=Stage.PICKUPBAKERY;
        	step=1;
        } else if (theStage==Stage.PICKUPBAKERY) {
        	stagePickupBakery();
        } else if (theStage==Stage.PICKUPGROCERY) {
        	stagePickupGrocery();
        } else if (theStage==Stage.PICKUPCHEESE) {
        	stagePickupCheese();
        } else if (theStage==Stage.PICKUPBUTCHERS) {
        	stagePickupButchers();
        } else if (theStage==Stage.DROPOFF) {
        	stageDropoff();
        } else if (theStage==Stage.HANGINGOUT) {
        	stageHangingOut();
        }

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        timeSinceLastRun = System.currentTimeMillis();
    }
    
    /** generic do pick up for each type of building */
    private void doPickup(String buildingSearch,Block pickUpItem, boolean doCompareMeta) {
    	if (step==1) {
    		currentPickup=0;
    		pickupBuildings.clear();
    		pickupBuildings=Building.getBuildingBySearch(buildingSearch, true);
    		if (pickupBuildings.size() ==0) { step=4; return; }
    		
    		theFolk.gotoXYZ(pickupBuildings.get(currentPickup).primaryXYZ, null);
    		theFolk.statusText="On my way to the "+pickupBuildings.get(currentPickup).displayName;
    		step=2;
    	
    	} else if (step==2) {
    		if (theFolk.destination ==null) {
    			step=3;
    		}
    	
    	} else if (step==3) {
    		theFolk.statusText="Buying items at the "+pickupBuildings.get(currentPickup).displayName;
    		ArrayList<IInventory> chests=inventoriesFindClosest(pickupBuildings.get(currentPickup).primaryXYZ, 5);
    		if (!chests.isEmpty()) {
	    		int count=getItemCountInChests(chests, new ItemStack(pickUpItem, 1), doCompareMeta);
	    		int buy=count/4;
	    		if (buy>0) {
	    			SimukraftReloaded.log.info("JobBurgersManager: buying "+buy+" out of "+count+" items");
	    			inventoriesTransferLimitedToFolk(theFolk.inventory, chests
	    					, new ItemStack(pickUpItem, 1, pickUpItem.damageDropped(1)),buy,doCompareMeta);
	    			
	    		
	    		}
    		}
    		currentPickup++;
    		if (currentPickup <= (pickupBuildings.size()-1)) {
    			theFolk.gotoXYZ(pickupBuildings.get(currentPickup).primaryXYZ, null);
        		theFolk.statusText="On my way to the "+pickupBuildings.get(currentPickup).displayName;
        		step=2;
    		} else {
    			step=4;
    		}
    	}
    }
    
    private void doPickup(String buildingSearch,Item pickUpItem, boolean doCompareMeta) {
    	if (step==1) {
    		currentPickup=0;
    		pickupBuildings.clear();
    		pickupBuildings=Building.getBuildingBySearch(buildingSearch, true);
    		if (pickupBuildings.size() ==0) { step=4; return; }
    		
    		theFolk.gotoXYZ(pickupBuildings.get(currentPickup).primaryXYZ, null);
    		theFolk.statusText="On my way to the "+pickupBuildings.get(currentPickup).displayName;
    		step=2;
    	
    	} else if (step==2) {
    		if (theFolk.destination ==null) {
    			step=3;
    		}
    	
    	} else if (step==3) {
    		theFolk.statusText="Buying items at the "+pickupBuildings.get(currentPickup).displayName;
    		ArrayList<IInventory> chests=inventoriesFindClosest(pickupBuildings.get(currentPickup).primaryXYZ, 5);
    		SimukraftReloaded.log.info(chests.toString());
    		if (!chests.isEmpty()) {
    			SimukraftReloaded.log.info("Chest not empty");
    			
	    		int count=getItemCountInChests(chests, new ItemStack(pickUpItem, 1), doCompareMeta);
	    		
	    		SimukraftReloaded.log.info(Integer.toString(count));
	    		int buy=count/4;
	    		SimukraftReloaded.log.info(Integer.toString(buy));
	    		if (buy>0) {
	    			
	    			if(pickUpItem == Items.potato)
		    		{
		    			potatoCount = count/4;
		    		}
		    		if(pickUpItem == Items.bread)
		    		{
		    			breadCount = count/4;
		    		}
		    		if(pickUpItem == Items.beef)
		    		{
		    			beefCount = count/4;
		    		}
		    		if(pickUpItem == SimukraftReloadedItems.itemFood || pickUpItem == SimukraftReloadedItems.itemFoodCheese)
		    		{
		    			cheeseCount = count/4;
		    		}
	    			
		    		SimukraftReloaded.log.info("JobBurgersManager: buying "+buy+" out of "+count+" items");
	    			
	    			for(int i=0; i<chests.get(0).getSizeInventory(); i++)
	    			{
	    				if(chests.get(0).getStackInSlot(i)==new ItemStack(pickUpItem)) //.contains(pickUpItem) || chests.contains(new ItemStack(pickUpItem)))
	    				{
	    					inventoriesTransferLimitedToFolk(theFolk.inventory, chests
	    					, new ItemStack(pickUpItem, 1, 0),buy,doCompareMeta);
	    					break;
	    				}
	    			}
	    		
	    		}
    		}
    		currentPickup++;
    		if (currentPickup <= (pickupBuildings.size()-1)) {
    			theFolk.gotoXYZ(pickupBuildings.get(currentPickup).primaryXYZ, null);
        		theFolk.statusText="On my way to the "+pickupBuildings.get(currentPickup).displayName;
        		step=2;
    		} else {
    			step=4;
    		}
    	}
    }

    private void stagePickupBakery() {
    	if (step<4) {
    		doPickup("bakery",new ItemStack(Items.bread).getItem(),false);
    	} else {
    		theStage=Stage.PICKUPGROCERY;
    		step=1;
    	}
    }

    private void stagePickupGrocery() {
    	if (step<4) {
    		doPickup("grocery",new ItemStack(Items.potato).getItem(),false);
    	} else {
    		theStage=Stage.PICKUPCHEESE;
    		step=1;
    	}
    }
    
    private void stagePickupCheese() {
    	if (step<4) {
    		doPickup("cheese factory",new ItemStack(SimukraftReloadedItems.itemFood,1,0).getItem(),true);
    	} else {
    		theStage=Stage.PICKUPBUTCHERS;
    		step=1;
    	}
    }
    
    private void stagePickupButchers() {
    	if (step<4) {
    		doPickup("butchers",new ItemStack(Items.beef, 1, 0).getItem(),false);
    	} else {
    		theStage=Stage.DROPOFF;
    		step=1;
    	}
    }
    
    private void stageDropoff() {
    	if (step==1) {
    		theFolk.statusText="On my way back to the store";
    		ArrayList<V3> back=theStore.getSpecialBlocks(0);
    		if (!back.isEmpty()) {
    			theFolk.gotoXYZ(back.get(0), null);
    			step=2;
    		}
    	
    	} else if (step==2) {
    		if (theFolk.destination==null) {
    			step=3;
    		}
    	
    	} else if (step==3) {
    		SimukraftReloaded.log.info("Unloading ingredients");
    		theFolk.statusText="Unloading ingredients";
    		ArrayList<V3> back=theStore.getSpecialBlocks(0);
    		SimukraftReloaded.log.info(back.get(0).toString());
    		ArrayList<IInventory> backstoreChests=inventoriesFindClosest(back.get(0), 3);
    		SimukraftReloaded.log.info(backstoreChests.get(0).toString());
    			boolean ok=inventoriesTransferFromFolk(theFolk.inventory, backstoreChests, null);
    			if (!ok) 
    			{
    				SimukraftReloaded.sendChat(theFolk.name+": The chest in the kitchen at the Fast food store is full!");
    			}
    			
    			
    			
    		for(int i=0; i<backstoreChests.get(0).getSizeInventory(); i++)
    		{
    			ItemStack chest = (backstoreChests.get(0).getStackInSlot(i));
    			
    			if(backstoreChests.get(0).getStackInSlot(i) != null)
    			{
    			
    				if(chest.getItem() == Items.beef && chest.stackSize < 64-beefCount)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.beef, chest.stackSize + beefCount));
    					placedBeef = true;
    					beefCount = 0;
    				}
    				
    				if(chest.getItem() == Items.bread && chest.stackSize < 64-breadCount)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.bread, chest.stackSize + breadCount));
    					placedBread = true;
    					breadCount = 0;
    				}
    			
    				if(chest.getItem() == SimukraftReloadedItems.itemFood && chest.stackSize < 64-cheeseCount)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(SimukraftReloadedItems.itemFood, chest.stackSize + cheeseCount, 0));
    					placedCheese = true;
    					cheeseCount = 0;
    				}
    			
    				if(chest.getItem() == Items.potato && chest.stackSize < 64-potatoCount)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.potato, chest.stackSize + potatoCount));
    					placedPotato = true;
    					potatoCount = 0;
    				}
    			}
    			
    			if(backstoreChests.get(0).getStackInSlot(i) == null)
    			{
    				if(placedBeef == false && beefCount > 0)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.beef, beefCount));
    					placedBeef = true;
    					beefCount = 0;
    					return;
    				}
    				else if(placedBread == false && breadCount > 0)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.bread, breadCount));
    					placedBread = true;
    					breadCount = 0;
    					return;
    				}
    				else if(placedCheese == false && cheeseCount > 0)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(SimukraftReloadedItems.itemFood, cheeseCount, 0));
    					placedCheese = true;
    					cheeseCount = 0;
    					return;
    				}
    				else if(placedPotato == false && potatoCount > 0)
    				{
    					backstoreChests.get(0).setInventorySlotContents(i, new ItemStack(Items.potato, potatoCount));
    					placedPotato = true;
    					potatoCount = 0;
    					return;
    				}
    				
    			}
    		}
    		
    		theStage=Stage.HANGINGOUT;
    		step=0;
    		SimukraftReloaded.states.credits -=2.45;
    	}
    }
    
    private void stageHangingOut() {
    	if (step %2==0) {
    		theFolk.gotoXYZ(theStore.primaryXYZ, GotoMethod.WALK);
    	} else {
    		theFolk.gotoXYZ(theStore.getSpecialBlocks(0).get(0), GotoMethod.WALK);
    	}
    	
    	String say="";
		switch(step) {
		case 0: say="Counting today's takings"; break;
		case 1: say="Cancelling staff leave"; break;
		case 2: say="Being very bossy"; break;
		case 3: say="Doing my taxes"; break;
		case 4: say="Disciplining staff"; break;
		case 5: say="Reducing staff wages"; break;
		case 6: say="Adjusting menu font"; break;
		}
		step++;
		if (step>6) {step=0;}
		theFolk.statusText=say;
    }
    
	@Override
	public void onArrivedAtWork() {
        int dist = 0;
        dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 1)
        {
            theFolk.action = FolkAction.ATWORK;
            theFolk.stayPut = true;
            theFolk.statusText = "Arrived at the store";
            theStage = Stage.ARRIVEDATSTORE;
            ArrayList<V3> back=theStore.getSpecialBlocks(0);
            if (!back.isEmpty()) {
            	theFolk.gotoXYZ(back.get(0), null);
            }
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
	}

	@Override
	public void resetJob() {
		theStage = Stage.IDLE;

	}

}
