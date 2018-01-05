package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobBurgersManager.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class JobBurgersFryCook extends Job {

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
    
	public JobBurgersFryCook(FolkData folk) {
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
	 public enum Stage
	    {
	        IDLE, ARRIVEDATSTORE,MAKEFOOD, NOINGREDIANTS
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
	        else if(theStage==Stage.NOINGREDIANTS) {
	        	runDelay=30000;
	        }
	        else if(theStage==Stage.MAKEFOOD) {
	        	runDelay=15000;
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
	        	theStage=Stage.MAKEFOOD;
	        }else if (theStage==Stage.MAKEFOOD) {
	        	stageMakeFood();
	        }else if (theStage==Stage.NOINGREDIANTS) {
	        	stageNoIngrediants();
	        }


	        if (!SimukraftReloaded.isDayTime())
	        {
	            theStage = Stage.IDLE;
	        }

	        timeSinceLastRun = System.currentTimeMillis();
	    }

	    
		private void stageNoIngrediants() {
			theFolk.statusText="I can't cook without ingredients!";
			theStage=Stage.MAKEFOOD;
			step=1;
		}

		private ItemStack isMakeFood=null;   // meta  0=cheese slice   1=hamburger   2=fries   3=cheeseburger
		private int tryMeta=3;
		private void stageMakeFood() {
			ArrayList<V3> ch=theStore.getSpecialBlocks(0); //raw materials chest
			if (ch.isEmpty()) { theStage=Stage.NOINGREDIANTS; return;}
			ArrayList<IInventory> chestsIn=inventoriesFindClosest(ch.get(0), 3);
			if (chestsIn.isEmpty()) {theStage=Stage.NOINGREDIANTS; return; }
			ArrayList<V3> ch2=theStore.getSpecialBlocks(2); //output chest
			if (ch.isEmpty()) { theStage=Stage.NOINGREDIANTS; return;}
			ArrayList<IInventory> chestsOut=inventoriesFindClosest(ch2.get(0), 3);
			if (chestsIn.isEmpty()) {theStage=Stage.NOINGREDIANTS; return; }
			
			ArrayList<V3> back=theStore.getSpecialBlocks(1);
            if (!back.isEmpty()) {
            	theFolk.gotoXYZ(back.get(0), null);
            	try {theFolk.destination.destinationAcc=0.3d;}catch(Exception e){} //NPEs if already there 
            }
            
			
			if (step==1) {
				if (tryMeta==3) { //try for a cheeseburger
					int c=getItemCountInChests(chestsIn, new ItemStack(SimukraftReloadedItems.itemFood,1,0), true);
					if (c==0) {tryMeta=1; return;}
					c=getItemCountInChests(chestsIn, new ItemStack(Items.beef,1), false);
					if (c==0) {tryMeta=2; return;}
					c=getItemCountInChests(chestsIn, new ItemStack(Items.bread,1), false);
					if (c==0) {tryMeta=2;  return;}
					isMakeFood=new ItemStack(SimukraftReloadedItems.itemFood,1,3);
					step=2;
					theFolk.statusText="Cooking up a Cheeseburger";
					
				} else if (tryMeta==1) {  //try for a burger
					int c=getItemCountInChests(chestsIn, new ItemStack(Items.beef,1), false);
					if (c==0) {tryMeta=2; return;}
					c=getItemCountInChests(chestsIn, new ItemStack(Items.bread,1), false);
					if (c==0) {tryMeta=2;  return;}
					isMakeFood=new ItemStack(SimukraftReloadedItems.itemFood,1,1);
					step=2;
					theFolk.statusText="Cooking a tasty Hamburger";
					
				} else if (tryMeta==2) {  //try for fries
					int c=getItemCountInChests(chestsIn, new ItemStack(Items.potato), false);
					if (c==0) {tryMeta=3; return; }
					isMakeFood=new ItemStack(SimukraftReloadedItems.itemFood,1,2);
					step=2;
					theFolk.statusText="Cooking a serving of Fries";
				}
				if(step==1) {
					theStage=Stage.NOINGREDIANTS;
				} else {
					theFolk.isWorking=true;
				}
			
			} else if (step==2) {
				if (isMakeFood.getItemDamage()==3) { //cheeseburger
					inventoriesGet(chestsIn, new ItemStack(SimukraftReloadedItems.itemFood,1,0), false, true);//cheese slice
					inventoriesGet(chestsIn, new ItemStack(Items.bread,1), false, false); //bread
					inventoriesGet(chestsIn, new ItemStack(Items.beef,1), false,false); //raw beef
					tryMeta=1;
					
				}else if (isMakeFood.getItemDamage()==1) { //burger
					inventoriesGet(chestsIn, new ItemStack(Items.bread,1), false, false); //bread
					inventoriesGet(chestsIn, new ItemStack(Items.beef,1), false,false); //raw beef
					tryMeta=2;
					
				}else if (isMakeFood.getItemDamage()==2) { //fries
					inventoriesGet(chestsIn, new ItemStack(Items.potato,1), false, false); //potatoes
					tryMeta=3;
				}
				inventoriesPut(chestsOut, isMakeFood, true);
				theFolk.isWorking=false;
				step=1;
				theFolk.statusText="Checking Ingredients";
				SimukraftReloaded.states.credits -=0.45;
			}
			 
			
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
	            ArrayList<V3> back=theStore.getSpecialBlocks(1);
	            if (!back.isEmpty()) {
	            	theFolk.gotoXYZ(back.get(0), null);
	            	step=1;
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
