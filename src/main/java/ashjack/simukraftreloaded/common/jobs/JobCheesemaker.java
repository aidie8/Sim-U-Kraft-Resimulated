package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

/*
Specialblock meta values for this job:
0=milk block inside tank
1=cheese block (in tubs)
2=ironblock shutters to release milk
3=waypoint empty milk
4=waypoint 2x stir and scrap
5=waypoint serve customers/cutting cheese into slices
*/
public class JobCheesemaker extends Job {
	public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    public int runDelay = 1000;
    private ArrayList<IInventory> chestsAtDairy = new ArrayList<IInventory>();
    private int currentFarmNum = 0;
    private Building farm = null;
    private long timeSinceLastRun=0;
    private Building theCheeseFactory=null;

    
	public JobCheesemaker(FolkData folk) {
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
        IDLE, ARRIVEDATFACTORY, GOINGTODAIRYFARM, COLLECTINGMILK, GOINGTOTANK,EMPTYINGMILK,STIRING,HARVESTCHEESE,SLICECHEESE
    }
    
    @Override
    public void onUpdate()
    {
        super.onUpdate();
        if(theCheeseFactory ==null) {
        	theCheeseFactory=Building.getBuilding(theFolk.employedAt);
        }
        if (theCheeseFactory==null) {
        	Building.loadAllBuildings();
        	theCheeseFactory=Building.getBuilding(theFolk.employedAt);
        }
        if (theCheeseFactory==null) {
        	theFolk.selfFire();
        	SimukraftReloaded.sendChat("There was a problem with the Cheese factory, try re-starting Minecraft");
        	return;
        }

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        super.onUpdateGoingToWork(theFolk);

        if (theStage == Stage.ARRIVEDATFACTORY)
        {
            theFolk.action = FolkAction.ATWORK;
            runDelay = 11000;
        }
        else if (theStage==Stage.EMPTYINGMILK) {
        	runDelay=1000;
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
        else if (theStage == Stage.ARRIVEDATFACTORY)
        {
        	stageArrivedAtFactory();
        }
        else if (theStage == Stage.GOINGTODAIRYFARM)
        {
            stageGoingToDairyFarm();
        }
        else if (theStage == Stage.COLLECTINGMILK)
        {
            stageCollectingMilk();
        }
        else if (theStage == Stage.GOINGTOTANK)
        {
           stageGoingToTank();
        }
        else if (theStage == Stage.EMPTYINGMILK)
        {
           stageEmptyingMilk();
        }
        else if (theStage == Stage.STIRING)
        {
           stageStiring();
        }
        else if (theStage == Stage.HARVESTCHEESE)
        {
           stageHarvestCheese();
        }
        else if (theStage == Stage.SLICECHEESE)
        {
           stageSliceCheese();
        }

        if (!SimukraftReloaded.isDayTime())
        {
            theStage = Stage.IDLE;
        }

        timeSinceLastRun = System.currentTimeMillis();
    }

    /*private void openCloseMilkShutters(boolean open) { TODO Get this working
    	ArrayList<V3> shutters=theCheeseFactory.getSpecialBlocks(7);
    	if (shutters==null || shutters.size()<1) {
    		theFolk.selfFire();
        	SimukraftReloaded.sendChat("There was a problem with the Cheese factory, try re-building it - no shutters");
        	return;
    	}
    	Block newId=null;
    	if (open) {
    		newId=null;
    	} else {
    		newId=Blocks.iron_block;
    	}
    	
    	Block current=jobWorld.getBlock(shutters.get(0).x.intValue(),shutters.get(0).y.intValue(),shutters.get(0).z.intValue());
    	
    	if (current !=newId) {
    		if (open && current==ModSimukraft.blockFluidMilk) { return; } //opening and it already has milk flowing
     		
			for(V3 v3:shutters) {
				jobWorld.setBlock(v3.x.intValue(),v3.y.intValue(),v3.z.intValue(),Blocks.air,0,0x03);
			}
			mc.theWorld.playSound(theFolk.location.x,
	                theFolk.location.y, theFolk.location.z,
	                "ashjacksimukraftreloaded:cheesemachine", 1f, 1f, false);
    	}
    }*/
    
	private void stageArrivedAtFactory() {
		//openCloseMilkShutters(false);
		
		try {
		ArrayList<V3> cheesechest=theCheeseFactory.getSpecialBlocks(5);
		ArrayList<IInventory> chests=inventoriesFindClosest(cheesechest.get(0), 4);
		inventoriesTransferToFolk(theFolk.inventory, chests, new ItemStack(Items.milk_bucket,64));
		} catch(Exception e) {}
		
		theStage=Stage.GOINGTODAIRYFARM;
		currentFarmNum=-1;
	}

	private void stageGoingToDairyFarm() {
		theFolk.statusText="Going to collect milk";
		currentFarmNum++;
		ArrayList<Building> dairyFarms=Building.getBuildingBySearch("Dairy Farm",true);
		if (!dairyFarms.isEmpty() && (dairyFarms.size()-1) <=currentFarmNum) {
			farm=dairyFarms.get(currentFarmNum);
			theFolk.gotoXYZ(farm.primaryXYZ, GotoMethod.BEAM);
			theStage=Stage.COLLECTINGMILK;
			step=1;
			return;
		} else {
			if (dairyFarms.isEmpty()) {
				SimukraftReloaded.sendChat(theFolk.name+" has retired, as there are no dairy farms");
				theFolk.selfFire();
				return;
			
			}else { //finished visiting all dairy farms
				theStage=Stage.GOINGTOTANK;
			}
		}
	}

	private void stageCollectingMilk() {
		theFolk.statusText="Collecting milk";
		if (step==1) {
			if(theFolk.destination==null && theFolk.location.getDistanceTo(farm.primaryXYZ) <5) {
				step=2;
				theFolk.isWorking=true;
			} else {
				SimukraftReloaded.log.info("JobCheeseMaker: not arrived at farm yet");
			}
		} else if (step==2) {
			chestsAtDairy=Job.inventoriesFindClosest(farm.primaryXYZ, 5);
			if (chestsAtDairy.isEmpty()) {
	        	SimukraftReloaded.sendChat(theFolk.name+": Can't find any chests at the dairy farm, I quit!");
	        	theFolk.selfFire();
	        	return;
			}
			
			//will grab it all, but in single inventory slots
			inventoriesTransferToFolk(theFolk.inventory, chestsAtDairy, new ItemStack(Items.milk_bucket,1)); 
			
			if (theFolk.inventory ==null || theFolk.inventory.isEmpty()) {
				SimukraftReloaded.sendChat(theFolk.name +" hasn't found any milk at the dairy today.");
				theStage=Stage.SLICECHEESE;
				step=1;
				theFolk.isWorking=false;
				theFolk.statusText="No Milk to process, gonna be an easy day today!";
				return;
			}
			
			ArrayList<V3> tanktop=theCheeseFactory.getSpecialBlocks(3);
			if (!tanktop.isEmpty()) {
				theFolk.gotoXYZ(tanktop.get(0), GotoMethod.BEAM);
				theStage=Stage.GOINGTOTANK;
				step=1;
				theFolk.isWorking=false;
			} else {
				SimukraftReloaded.log.warning("JobCheesemaker: no tank top point");
				theFolk.selfFire();
			}
			
		}
	}

	private void stageGoingToTank() {
		if(step==1) {
			if(theFolk.destination==null) {
				step=2;
				theFolk.statusText="Preparing to fill the tank";
				//openCloseMilkShutters(false);
			} else {
				SimukraftReloaded.log.info("JobCheeseMaker: not arrived at back yet");
			}
		} else if (step==2) {
			theStage=Stage.EMPTYINGMILK;
			step=1;
		}
		
	}

	private void stageEmptyingMilk() {
		if (step==1) {
			if (theFolk.inventory !=null && !theFolk.inventory.isEmpty()) {
				if (theFolk.inventory.size()>1) {
					theFolk.statusText="Emptying "+theFolk.inventory.size()+" buckets of milk";
				} else {
					theFolk.statusText="Emptied all the milk";
				}
				theFolk.isWorking=true;
				theFolk.stayPut=true;
				step=2;
			} else {
				step=3;
			}
			
		} else if (step==2) {
			ArrayList<V3> milkblocks=theCheeseFactory.getSpecialBlocks(0);
			int lightID = Block.getIdFromBlock(SimukraftReloadedBlocks.lightBox);
			SimukraftReloaded.log.info(Integer.toString(lightID));
			boolean filledOk=false;
			for(V3 milkBlock:milkblocks) {
				Block id=jobWorld.getBlock(milkBlock.x.intValue(), milkBlock.y.intValue(),milkBlock.z.intValue());
				int meta=jobWorld.getBlockMetadata(milkBlock.x.intValue(), milkBlock.y.intValue(),milkBlock.z.intValue());
				if (id==Blocks.air || (id ==SimukraftReloadedBlocks.blockFluidMilk && meta ==1)) {
					jobWorld.setBlock(milkBlock.x.intValue(), milkBlock.y.intValue(), milkBlock.z.intValue()
							, SimukraftReloadedBlocks.blockFluidMilk, 0, 0x03);
					try {theFolk.inventory.remove(0);}catch(Exception e){SimukraftReloaded.log.info("Caught Exception" + e.toString());}  //just in case!
					filledOk=true;
					break;
				}
			}
			if (filledOk) {
				theFolk.isWorking=false;
				step=1;
			} else {  //tank is full
				theStage=Stage.STIRING;
				step=1;
				theFolk.isWorking=false;

				ArrayList<V3> cheesechest=theCheeseFactory.getSpecialBlocks(5);
				ArrayList<IInventory> chests=inventoriesFindClosest(cheesechest.get(0), 4);
				inventoriesTransferFromFolk(theFolk.inventory, chests, null);
			}
			
		} else if(step==3) {
			theStage=Stage.STIRING;
			step=1;
			theFolk.isWorking=false;

		}
		
	}
	
	
	private boolean tubToggle=true;
	private int stirCount=0;
	private V3 currentStirPos;
	
	private void stageStiring() {
		
		ArrayList<V3> stirPositions=theCheeseFactory.getSpecialBlocks(4);
		
		if (step==1) {   // goto the other tub waypoint
			theFolk.statusText="Checking milk viscosity";
			if (!stirPositions.isEmpty()) {
				if (tubToggle) {
					theFolk.gotoXYZ(currentStirPos=stirPositions.get(0),null);
				} else {
					theFolk.gotoXYZ(currentStirPos=stirPositions.get(1), null);
				}
				tubToggle= !tubToggle;
				stirCount=0;
				step=2;
			} else {
				SimukraftReloaded.sendChat("There's a problem with the cheese factory, place a building constructor down and re-build it");
				theFolk.selfFire();
			}
		
		} else if (step==2) {  //on their way to tub
			if (theFolk.destination==null) {
				step=3;
				//openCloseMilkShutters(true);
			}
		
		} else if (step==3) {  //actual stiring/spawning of cheese and remove milk
			String say="";
			switch(stirCount) {
			case 0: say="Stirring the milk"; break;
			case 1: say="Adding top secret ingredient"; break;
			case 2: say="Adding bacterial culture"; break;
			case 3: say="Removing unwanted spores"; break;
			case 4: say="Checking fermentation progress"; break;
			case 5: say="Adding Rennet"; break;
			case 6: say="Reticulating Cheese splines"; break;
			}
			theFolk.statusText=say;
			theFolk.isWorking=true;
			theFolk.stayPut=true;
			if (stirCount==6) {
				transformMilkToCheese(currentStirPos);
			}
			stirCount++;
			if (stirCount >6) {
				theFolk.isWorking=false;
				step=1;
			}
			if (MinecraftServer.getServer().worldServers[0].getWorldTime() % 24000 > 9900) {
				step=1;
				theFolk.isWorking=false;
				theStage=Stage.HARVESTCHEESE;
			}
			
		}
		
	}
	
	private void transformMilkToCheese(V3 currentStirPos) {
		ArrayList<V3> milkBlocks=theCheeseFactory.getSpecialBlocks(0);
		ArrayList<V3> cheeseBlocks=theCheeseFactory.getSpecialBlocks(1);

		if (milkBlocks.isEmpty() || cheeseBlocks.isEmpty()) {
			theFolk.selfFire();
        	SimukraftReloaded.sendChat("There was a problem with the Cheese factory, try re-building it - no milk blocks");
        	return;
		}
		
		boolean placedCheese=false;
		int milkGotCount=0;
		for(int m=milkBlocks.size()-1;m>0;m--) {
			V3 milk=milkBlocks.get(m);
			Block id=jobWorld.getBlock(milk.x.intValue(), milk.y.intValue(), milk.z.intValue());
			int meta=jobWorld.getBlockMetadata(milk.x.intValue(), milk.y.intValue(), milk.z.intValue());
			if (id==SimukraftReloadedBlocks.blockFluidMilk && meta==0) {
				jobWorld.setBlock(milk.x.intValue(), milk.y.intValue(), milk.z.intValue(),id,0,0x03);
				milkGotCount++;
				if (milkGotCount>1) { break; }
			}
		}
		if (milkGotCount>0) {
			for(V3 cheese:cheeseBlocks) {
				Block id=jobWorld.getBlock(cheese.x.intValue(), cheese.y.intValue(), cheese.z.intValue());
				int dist=cheese.getDistanceTo(currentStirPos);
				if (id != SimukraftReloadedBlocks.blockCheese && dist<5) {
					jobWorld.setBlock(cheese.x.intValue(), cheese.y.intValue(), cheese.z.intValue()
							,SimukraftReloadedBlocks.blockCheese,0,0x03);
					placedCheese=true;
					break;
				}
			}
		}
		
		if (milkGotCount==0 || !placedCheese) {
			step=1;
			theFolk.isWorking=false;
			theStage=Stage.HARVESTCHEESE;
		}
	}
	
	
	
	private void stageHarvestCheese() {
		ArrayList<V3> cheeseBlocks=theCheeseFactory.getSpecialBlocks(1);
		ArrayList<V3> stirPositions=theCheeseFactory.getSpecialBlocks(4);
		
		theFolk.statusText="Extracting Cheese blocks";
		if (step==1) {
			theFolk.gotoXYZ(stirPositions.get(0), null);
			step=2;
			//openCloseMilkShutters(false);
		
		} else if (step==2) {
			if (theFolk.destination==null) {
				step=3;
				theFolk.isWorking=true;
			}
		
		} else if (step==3) {
			theFolk.isWorking=false;
			boolean gotBlock=false;
			for(V3 block:cheeseBlocks) {
				Block id=jobWorld.getBlock(block.x.intValue(),block.y.intValue(),block.z.intValue());
				if(stirPositions.get(0).getDistanceTo(block) <5 && id==SimukraftReloadedBlocks.blockCheese) {
					gotBlock=true;
					theFolk.inventory.add(new ItemStack(SimukraftReloadedBlocks.blockCheese));
					jobWorld.setBlock(block.x.intValue(),block.y.intValue(),block.z.intValue(),id,0,0x03);
					theFolk.isWorking=true;
					SimukraftReloaded.states.credits -=0.45;
					break;
				}
			}
			if (!gotBlock) {
				theFolk.isWorking=false;
				step=4;
				theFolk.gotoXYZ(stirPositions.get(1), null);
			}
		
		} else if (step==4) {
			if (theFolk.destination==null) {
				step=5;
				theFolk.isWorking=true;
			}
		
		} else if (step==5) {
			theFolk.isWorking=false;
			boolean gotBlock=false;
			for(V3 block:cheeseBlocks) {
				Block id=jobWorld.getBlock(block.x.intValue(),block.y.intValue(),block.z.intValue());
				if(stirPositions.get(1).getDistanceTo(block) <5 && id==SimukraftReloadedBlocks.blockCheese) {
					gotBlock=true;
					theFolk.inventory.add(new ItemStack(SimukraftReloadedBlocks.blockCheese));
					jobWorld.setBlock(block.x.intValue(),block.y.intValue(),block.z.intValue(),id,0,0x03);
					break;
				}
			}
			if (!gotBlock) {
				step=1;
				theStage=Stage.SLICECHEESE;
				theFolk.statusText="Counting cheese blocks";
			}
		}
	}
	
	private void stageSliceCheese() {
		ArrayList<V3> slicewaypoint=theCheeseFactory.getSpecialBlocks(5);
		if (slicewaypoint.isEmpty()) {
			theFolk.selfFire();
	    	SimukraftReloaded.sendChat("There was a problem with the Cheese factory, try re-building it - waypoint issue");
	    	return;
		}
		if (step==1) {
			theFolk.gotoXYZ(slicewaypoint.get(0), null);
			step=2;
		
		} else if(step==2) {
			if (theFolk.destination==null) {
				step=3;
				theFolk.stayPut=true;
			}
		
		} else if(step==3) {
			ArrayList<IInventory> chests=Job.inventoriesFindClosest(slicewaypoint.get(0), 4);
			if (chests.isEmpty()) {
				SimukraftReloaded.sendChat(theFolk.name+": Someone has stolen the chest in the cheese factory, I quit!");
				theFolk.selfFire();
			}
			inventoriesTransferFromFolk(theFolk.inventory, chests, null);
			step=4;
		
		} else if(step==4) {
			ArrayList<IInventory> chests=Job.inventoriesFindClosest(slicewaypoint.get(0), 4);
			theFolk.statusText="Slicing cheese";
			ItemStack cheese=inventoriesGet(chests, new ItemStack(SimukraftReloadedBlocks.blockCheese,1), false, false);
			if (cheese !=null) {
				boolean placedOK=inventoriesPut(chests, new ItemStack(SimukraftReloadedItems.itemFood, 9, 0), true); //9 x cheese slices
				if (!placedOK) {
					SimukraftReloaded.sendChat(theFolk.name+"'s chest at the cheese factory is full of cheese!");
					theFolk.selfFire();
				}
			} else {
				step=5;
			}
		
		} else if (step==5) {
			theFolk.statusText="I love cheese!";
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
            theFolk.statusText = "Arrived at the factory";
            theStage = Stage.ARRIVEDATFACTORY;
            currentFarmNum = 0;
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
