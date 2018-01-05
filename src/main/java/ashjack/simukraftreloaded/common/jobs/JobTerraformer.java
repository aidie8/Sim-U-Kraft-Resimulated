package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobBuilder.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;

public class JobTerraformer extends Job
{
    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient TerraformerType theType;
    private transient int radius;
    private transient ArrayList<IInventory> constructorChests = new ArrayList<IInventory>();
    private transient int totalBlockCount = 0;
    private transient int counter = 0;
    private transient int buckets = 0;

    public JobTerraformer() {}

    public JobTerraformer(FolkData folk)
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
        theFolk.isWorking = false;
    }

    public enum Stage
    {
        IDLE, WAITINGFORRESOURCES, INPROGRESS, COMPLETE;
    }
    public enum TerraformerType
    {
        WATERTODIRT, NATURE, LAWNMOWER, FLATTENIZER, VALUEPACK, GLACIAL, MOISTURIZER, THERMALIZER, DEICER;
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

        if (theStage == Stage.WAITINGFORRESOURCES)
        {
            runDelay = 4000;
        }

        if (theStage == Stage.INPROGRESS)
        {
            if (GameMode.gameMode == GameMode.GAMEMODES.CREATIVE)
            {
                runDelay = 1;
            }
            else
            {
                runDelay = 300;
            }
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        // ////////////////IDLE
        if ((theStage == Stage.IDLE)
                && SimukraftReloaded.isDayTime())
        {
        }
        else if (theStage == Stage.WAITINGFORRESOURCES)
        {
            stageWaitingForResources();
        }
        else if (theStage == Stage.INPROGRESS)
        {
            stageInProgress();
        }
        else if (theStage == Stage.COMPLETE)
        {
            stageComplete();
        }
    }

    private void stageWaitingForResources()
    {
        theFolk.isWorking = false;
        theFolk.statusText = "Checking terraforming resources...";
        constructorChests = inventoriesFindClosest(theFolk.employedAt, 5);

        if (step == 1)
        {
            if (constructorChests.isEmpty())
            {
                theFolk.statusText = "Please place at least ONE chest near to constructor box.";
            }
            else
            {
                theType = theFolk.terraformerType;
                radius = theFolk.terraformerRadius;

                if (theType == null)
                {
                    step = 4;
                }
                else
                {
                    step = 2;
                    constructorChests.get(0).openInventory();
                }
            }
        }
        else if (step == 2)
        {
            constructorChests.get(0).closeInventory();
            theStage = Stage.INPROGRESS;
            step = 1; //first start of inprogress
        }
        else if (step == 3)     // this step triggers mid-terraform - just send them
        {
            // back in to keep checking
            if (vocation == Vocation.TERRAFORMER)
            {
                step = 4;
                theStage = Stage.INPROGRESS;
            }
            else
            {
                theFolk.selfFire();
                return;
            }
        }
        else if (step == 4)
        {
            theFolk.statusText = "Please choose a Terraforming option";
            step = 1;
        }
    }

    private void stageInProgress()
    {
        theFolk.isWorking = true;
        Random rand = new Random();
        boolean hasPlacedTree = false;
        ItemStack is = null;
        constructorChests = inventoriesFindClosest(theFolk.employedAt, 5);

        if (step == 1)
        {
            if (theType == TerraformerType.WATERTODIRT)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.water);
                blockIDs.add(Blocks.water);
                closestBlocks = null;
                setClosestBlocksOfType(theFolk.employedAt, blockIDs, radius, false, true, false);
            }
            else if (theType == TerraformerType.NATURE)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.dirt);
                blockIDs.add(Blocks.grass);
                closestBlocks = null;
                setClosestBlocksOfType(theFolk.employedAt, blockIDs, radius, true, true, false);
            }
            else if (theType == TerraformerType.LAWNMOWER)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.tallgrass);
                blockIDs.add(Blocks.red_flower);
                blockIDs.add(Blocks.yellow_flower);
                closestBlocks = null;
                setClosestBlocksOfType(theFolk.employedAt, blockIDs, radius, false, true, false);
            }
            else if (theType == TerraformerType.FLATTENIZER)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.grass);
                blockIDs.add(Blocks.dirt);
                blockIDs.add(Blocks.tallgrass);
                blockIDs.add(Blocks.stone);
                blockIDs.add(Blocks.sand);
                blockIDs.add(Blocks.sandstone);
                blockIDs.add(Blocks.gravel);
                closestBlocks = null;
                setClosestBlocksOfType(theFolk.employedAt, blockIDs, radius, false, false, false);
            }
            else if (theType == TerraformerType.VALUEPACK)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.air);
                blockIDs.add(Blocks.tallgrass);
                blockIDs.add(Blocks.red_flower);
                blockIDs.add(Blocks.yellow_flower);
                V3 v = new V3(theFolk.employedAt.x, theFolk.employedAt.y - 1, theFolk.employedAt.z, theFolk.employedAt.theDimension);
                closestBlocks = null;
                setClosestBlocksOfType(v, blockIDs, radius, false, true, true);
            }
            else if (theType == TerraformerType.GLACIAL)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.air);
                blockIDs.add(Blocks.tallgrass);
                blockIDs.add(Blocks.water);
                blockIDs.add(Blocks.water);
                V3 v = new V3(theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.theDimension);
                closestBlocks = null;
                setClosestBlocksOfType(v, blockIDs, radius, true, true, false);
            }
            else if (theType == TerraformerType.MOISTURIZER)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.lava);
                blockIDs.add(Blocks.lava);
                //blockIDs.add(Block.waterStill.blockID);
                V3 v = new V3(theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.theDimension);
                closestBlocks = null;
                setClosestBlocksOfType(v, blockIDs, radius, false, true, false);
            }
            else if (theType == TerraformerType.THERMALIZER)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.lava);
                V3 v = new V3(theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.theDimension);
                closestBlocks = null;
                setClosestBlocksOfType(v, blockIDs, radius, false, true, false);
            }
            else if (theType == TerraformerType.DEICER)
            {
                ArrayList<Block> blockIDs = new ArrayList<Block>();
                blockIDs.add(Blocks.snow);
                V3 v = new V3(theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.theDimension);
                closestBlocks = null;
                setClosestBlocksOfType(v, blockIDs, radius, false, true, false);
            }

            step = 2;
        }
        else if (step == 2)
        {
            //searching in progress (thread)
            theFolk.statusText = "Scanning Terrain...";
        }
        else if (step == 3)
        {
            totalBlockCount = closestBlocks.size();

            if (totalBlockCount == 0)
            {
                theFolk.statusText = "Nothing to terraform!";
                SimukraftReloaded.sendChat("There's nothing here that can be terraformed in that way");
                theFolk.selfFire();
                return;
            }
            else
            {
                step = 4;
                theFolk.statusText = "Starting the terraforming process...";
            }
        }
        else if (step == 4)
        {
            int count = 0;

            if (theType == TerraformerType.WATERTODIRT)
            {
                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    ItemStack gotDirt = inventoriesGet(constructorChests, new ItemStack(Blocks.dirt, 1), false,false);

                    if (gotDirt == null)
                    {
                        theFolk.statusText = "I need more dirt!";
                        theStage = Stage.WAITINGFORRESOURCES;
                        step = 1;
                        return;
                    }
                }
            }
            else if (theType == TerraformerType.NATURE)
            {
                counter++;
                hasPlacedTree = false;

                if (counter % 15 == 0)
                {
                    hasPlacedTree = true;

                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        is = inventoriesGet(constructorChests, null, true,false);

                        if (is == null)
                        {
                            theFolk.statusText = "No more saplings, place some in a chest";
                            theStage = Stage.WAITINGFORRESOURCES;
                            step = 1;
                            return;
                        }
                    }
                    else
                    {
                        is = new ItemStack(Blocks.sapling, 1, new Random().nextInt(4));
                    }
                }
            }
            else if (theType == TerraformerType.LAWNMOWER || theType==TerraformerType.DEICER)
            {
                ///nothing needed
            }
            else if (theType == TerraformerType.FLATTENIZER)
            {
                ///nothing needed (only chest)
            }
            else if (theType == TerraformerType.VALUEPACK)
            {
                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    ItemStack gotDirt = inventoriesGet(constructorChests, new ItemStack(Blocks.dirt, 1), false,false);

                    if (gotDirt == null)
                    {
                        theFolk.statusText = "I need more dirt!";
                        theStage = Stage.WAITINGFORRESOURCES;
                        step = 1;
                        return;
                    }
                }
            }
            else if (theType == TerraformerType.GLACIAL
                     || theType == TerraformerType.MOISTURIZER)
            {
                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    if (buckets == 0)
                    {
                        ItemStack gotDirt = inventoriesGet(constructorChests, new ItemStack(Items.water_bucket, 1), false,false);

                        if (gotDirt == null)
                        {
                            theFolk.statusText = "Please place a bucket of water in the chest";
                            theStage = Stage.WAITINGFORRESOURCES;
                            step = 1;
                            return;
                        }
                        else
                        {
                            inventoriesPut(constructorChests, new ItemStack(Items.bucket, 1), true);
                            buckets = 1000;
                        }
                    }
                    else
                    {
                        buckets--;
                    }
                }
            }
            else if (theType == TerraformerType.THERMALIZER)
            {
                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    ItemStack gotStuff = inventoriesGet(constructorChests, new ItemStack(Items.bucket, 1), false,false);

                    if (gotStuff == null)
                    {
                        theFolk.statusText = "I need some empty buckets to put the lava into.";
                        theStage = Stage.WAITINGFORRESOURCES;
                        step = 1;
                        return;
                    }
                }
            }

            //theFolk.swingProgress=rand.nextFloat();
            Double x, y;
            x = (double) totalBlockCount;
            y = (double) closestBlocks.size();
            Double percent = (double)(((x - y) /  x));
            percent = percent * 100;
            theFolk.statusText = "Terraforming, " + percent.intValue() + " % complete";
            V3 v = closestBlocks.get(0);

            if (theType == TerraformerType.WATERTODIRT)
            {
                jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.dirt, 0, 0x03);
                //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.dirt);

                if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                {
                    SimukraftReloaded.states.credits -= 0.009;
                }
            }
            else if (theType == TerraformerType.NATURE)
            {
                if (hasPlacedTree)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue() + 1, v.z.intValue(),
                                      Blocks.sapling, is.getItemDamage(), 0x03);
                    //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.sapling);

                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        SimukraftReloaded.states.credits -= 0.009;
                    }

                    runDelay = 500;
                }
                else
                {
                    int r = rand.nextInt(10);

                    if (r == 2)
                    {
                        jobWorld.setBlock(v.x.intValue(), v.y.intValue() + 1, v.z.intValue(), Blocks.red_flower, 0, 0x03);
                        jobWorld.markBlockForUpdate(v.x.intValue(), v.y.intValue() + 1, v.z.intValue());
                    }
                    else if (r == 5)
                    {
                        jobWorld.setBlock(v.x.intValue(), v.y.intValue() + 1, v.z.intValue(), Blocks.yellow_flower, 0, 0x03);
                    }

                    runDelay = 50;
                }
            }
            else if (theType == TerraformerType.LAWNMOWER)
            {
                ArrayList<ItemStack> minedStacks = translateBlockWhenMined(jobWorld, v);

                if (minedStacks != null)
                {
                    for (int s = 0; s < minedStacks.size(); s++)
                    {
                        ItemStack stack = minedStacks.get(s);

                        if (stack != null)
                        {
                            inventoriesPut(constructorChests, stack, false);
                            //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), stack);
                        }
                    }
                }

                if (mc.theWorld.isRemote)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.air, 0, 0x03);
                    SimukraftReloaded.states.credits -= 0.009;
                }
            }
            else if (theType == TerraformerType.FLATTENIZER)
            {
                ArrayList<ItemStack> minedStacks = translateBlockWhenMined(jobWorld, v);

                if (minedStacks != null)
                {
                    for (int s = 0; s < minedStacks.size(); s++)
                    {
                        ItemStack stack = minedStacks.get(s);

                        if (stack != null)
                        {
                            inventoriesPut(constructorChests, stack, false);
                            //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), stack.itemID);
                        }
                    }
                }

                try
                {
                    if (mc.theWorld.isRemote)
                    {
                        jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.air, 0, 0x03);

                        if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                        {
                            SimukraftReloaded.states.credits -= 0.009;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else if (theType == TerraformerType.VALUEPACK)
            {
                if (mc.theWorld.isRemote)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.dirt, 0, 0x03);
                   // jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.dirt);

                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        SimukraftReloaded.states.credits -= 0.009;
                    }
                }
            }
            else if (theType == TerraformerType.GLACIAL)
            {
                Block blockId = jobWorld.getBlock(v.x.intValue(), v.y.intValue(), v.z.intValue());

                if (blockId == null || blockId == Blocks.tallgrass)
                {
                    Block idBelow = jobWorld.getBlock(v.x.intValue(), v.y.intValue() - 1, v.z.intValue());

                    if (idBelow != null && idBelow != Blocks.ice &&
                            idBelow != Blocks.water &&
                            idBelow != Blocks.water &&
                            idBelow != Blocks.snow)  //non-air block below (some type of ground
                    {
                        if (mc.theWorld.isRemote)
                        {
                            jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.snow, 0, 0x03);
                            //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.snow);

                            if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                            {
                                SimukraftReloaded.states.credits -= 0.009;
                            }
                        }
                    }
                }
                else if (blockId == Blocks.water || blockId == Blocks.water)
                {
                    if (mc.theWorld.isRemote)
                    {
                        jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.ice, 0, 0x03);
                        //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.ice);

                        if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                        {
                            SimukraftReloaded.states.credits -= 0.009;
                        }
                    }
                }
            }
            else if (theType == TerraformerType.MOISTURIZER)
            {
                if (mc.theWorld.isRemote)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.obsidian, 0, 0x03);
                    //jobWorld.playAuxSFX(2001, v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.water);
                    jobWorld.markBlockForUpdate(v.x.intValue(), v.y.intValue(), v.z.intValue());

                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        SimukraftReloaded.states.credits -= 0.009;
                    }
                }
            }
            else if (theType == TerraformerType.THERMALIZER)
            {
                if (mc.theWorld.isRemote)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.air, 0, 0x03);
                    jobWorld.markBlockForUpdate(v.x.intValue(), v.y.intValue(), v.z.intValue());

                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        SimukraftReloaded.states.credits -= 0.009;
                    }

                    inventoriesPut(constructorChests, new ItemStack(Items.lava_bucket, 1), false);
                }
            }
            else if (theType == TerraformerType.DEICER)
            {
                if (mc.theWorld.isRemote)
                {
                    jobWorld.setBlock(v.x.intValue(), v.y.intValue(), v.z.intValue(), Blocks.grass, 0, 0x03);
                    jobWorld.markBlockForUpdate(v.x.intValue(), v.y.intValue(), v.z.intValue());
                    counter++;
                    
                    if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
                    {
                        SimukraftReloaded.states.credits -= 0.009;
                    }
                    
                    if (counter % 4==0) {
                    	inventoriesPut(constructorChests, new ItemStack(Blocks.snow, 1,0), false);
                    }
                }
            }
            

            closestBlocks.remove(0);

            if (closestBlocks.size() == 0)
            {
                theStage = Stage.COMPLETE;
            }
        }
    }

    private void stageComplete()
    {
        theFolk.isWorking = false;
        SimukraftReloaded.sendChat(theFolk.name
                              + " has completed terraforming");
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY,
                              mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
        theFolk.stayPut = false;
        theFolk.terraformerRadius = 1;
        theFolk.terraformerType = null;
        theFolk.selfFire();
        theStage = Stage.IDLE;
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
            theFolk.statusText = "Arrived at the site";
            theStage = Stage.WAITINGFORRESOURCES;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
