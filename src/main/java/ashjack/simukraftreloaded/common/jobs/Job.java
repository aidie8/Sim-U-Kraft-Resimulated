package ashjack.simukraftreloaded.common.jobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import ashjack.simukraftreloaded.blocks.functionality.TileEntityWindmill;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.building.Building;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public abstract class Job
{
    Minecraft mc = Minecraft.getMinecraft();
    public int step = 1;
    ArrayList<V3> closestBlocks = new ArrayList<V3>();
    public Vocation vocation = null;
    public World jobWorld = null;

    // chest has been opened, this is to close the chest later
    private transient IInventory chestToClose = null;
    private transient Long chestToCloseWhen = 0l;

    public Job()
    {
    }

    /** overridden by subclass, fires when they were on their way to work, but they have not arrived */
    public abstract void onArrivedAtWork();

    /** resets their stage, as it is saved with the folk */
    public abstract void resetJob();

    /** called repeatedly throughout working period, overridden in sub-classes of jobs */
    public void onUpdate()
    {
        if (chestToClose != null)
        {
            if (System.currentTimeMillis() > chestToCloseWhen)
            {
                chestToClose.closeInventory();
                chestToClose = null;
            }
        }
    }

    /** logic called by the subclass to get the folk to work, deals with walking to work properly */
    public void onUpdateGoingToWork(FolkData theFolk)
    {
        if (jobWorld == null)
        {
            try
            {
                jobWorld = MinecraftServer.getServer().worldServerForDimension(theFolk.employedAt.theDimension);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
        }

        if (theFolk.pregnancyStage > 0.0f)  //maternity leave for pregnant ladies
        {
            return;
        }

        if (theFolk.action == FolkAction.ONWAYTOWORK)
        {
            int dist = 0;

            if (theFolk.gotoMethod == GotoMethod.WALK)
            {
                theFolk.stayPut = false;
                theFolk.updateLocationFromEntity();
            }

            dist = theFolk.location.getDistanceTo(theFolk.employedAt);

            if (dist <= 1)
            {
                theFolk.action = FolkAction.ATWORK;
                onArrivedAtWork();
            }
            else if (dist > 1 && dist < 3)
            {
                V3 work = theFolk.employedAt.clone();
                work.y++;
                theFolk.gotoXYZ(work, GotoMethod.SHIFT);
                theFolk.location = work;
            }
        }

        if (SimukraftReloaded.isDayTime() && theFolk.action != FolkAction.ONWAYTOWORK && theFolk.action != FolkAction.ATWORK)
        {
            theFolk.action = FolkAction.ONWAYTOWORK;
            theFolk.stayPut = false;

            if (theFolk.destination == null)
            {
                theFolk.gotoXYZ(theFolk.employedAt, null);
            }
        }
    }

    /** return a count of how much of a particular item/block is in the folks inventory */
    public int getInventoryCount(FolkData theFolk, Item item)
    {
        int ret = 0;

        for (int i = 0; i < theFolk.inventory.size(); i++)
        {
            ItemStack is = theFolk.inventory.get(i);

            if (is.getItem() == item)
            {
                ret += is.stackSize;
            }
        }

        return ret;
    }
    
    public int getInventoryCount(FolkData theFolk, Block item)
    {
        int ret = 0;

        for (int i = 0; i < theFolk.inventory.size(); i++)
        {
            ItemStack is = theFolk.inventory.get(i);

            if (Block.getBlockFromName(is.getDisplayName()) == item)
            {
                ret += is.stackSize;
            }
        }

        return ret;
    }

    /** find the nearest furnace to a location and return it or NULL if no furnace */
    public TileEntityFurnace findFurnace(V3 v)
    {
        TileEntityFurnace ret = null;
        V3 vRet = findClosestBlockType(v, Blocks.furnace , 5, false);

        if (vRet == null)
        {
            vRet = findClosestBlockType(v, Blocks.lit_furnace , 5, false);
        }

        if (vRet != null)
        {
            World theWorld = MinecraftServer.getServer().worldServerForDimension(vRet.theDimension);
            ret = (TileEntityFurnace) theWorld.getTileEntity(vRet.x.intValue(), vRet.y.intValue(), vRet.z.intValue());
        }

        return ret;
    }

    //called by public function
    private static boolean inventoryPut(IInventory chest, ItemStack inStack)
    {
        Boolean placedOK = false;

        if (inStack == null)
        {
            return true;
        }

        for (int q = 1; q <= inStack.stackSize; q++)
        {
            for (int g = 0; g < chest.getSizeInventory(); g++)
            {
                ItemStack is = chest.getStackInSlot(g);

                if (is == null)
                {
                    is = inStack.copy();
                    is.stackSize = 1;
                    chest.setInventorySlotContents(g, is);
                    ItemStack isTest = chest.getStackInSlot(g);

                    if (isTest != null)
                    {
                        // SimukraftReloaded.log("placeIntoInventory: one block of "+is.getDisplayName()+" in null slot "+g);
                        placedOK = true;
                        break; //breaks back into quantity loop
                    }
                    else
                    {
                    	SimukraftReloaded.log.warning("Job: placeIntoInventory() could not place " + is.getDisplayName() + " in null slot " + g);
                        placedOK = false;
                    }
                }
                else if (is.getItem() == inStack.getItem() && is.stackSize < is.getMaxStackSize())
                {
                    int isBefore = chest.getStackInSlot(g).stackSize;
                    is.stackSize++;
                    chest.setInventorySlotContents(g, is);
                    int isAfter =  chest.getStackInSlot(g).stackSize;

                    if (isAfter > isBefore)
                    {
                        // SimukraftReloaded.log("placeIntoInventory: one block of "+is.getDisplayName()+" in slot "+g);
                        placedOK = true;
                        break;
                    }
                    else
                    {
                    	SimukraftReloaded.log.warning("Job: placeIntoInventory() could not inc Stacksize for " + is.getDisplayName() + " in slot " + g);
                        placedOK = false;
                    }
                }
            }
        }

        return placedOK;
    }

    /** take something out of a chest/inventory and return it
     * @param chest - the chest or inventory
     * @param whatItem - the Item type/quantity required etc or NULL to get ANY item
     * @param getRandomItem - used if whatItem is NULL - if false, get the first available item, if true, gets a random item
     * @return the Itemstack of what was pulled out, or NULL if it was unable to get the item
     */
    public static ItemStack inventoriesGet(ArrayList<IInventory> chests, ItemStack whatItem, boolean getRandomItem, boolean compareMeta,ItemStack ignoreId)
    {
        ItemStack retStack = null;

        for (int c = 0; c < chests.size(); c++)
        {
            IInventory chest = chests.get(c);
            retStack = inventoryGet(chest, whatItem, getRandomItem, compareMeta);

            if (retStack != null)
            {
                return retStack;
            }
        }

        return null;
    }
    
    public static ItemStack inventoriesGet(ArrayList<IInventory> chests, ItemStack whatItem, boolean getRandomItem, boolean compareMeta)
    {
        ItemStack retStack = null;

        for (int c = 0; c < chests.size(); c++)
        {
            IInventory chest = chests.get(c);
            retStack = inventoryGet(chest, whatItem, getRandomItem, compareMeta);

            if (retStack != null)
            {
                return retStack;
            }
        }

        return null;
    }

    //called by public function
    private static ItemStack inventoryGet(IInventory chest, ItemStack whatItem, boolean getRandomItem, boolean compareMeta,ItemStack ignoreId)
    {
        if (whatItem != null)
        {
            ItemStack returnStack = whatItem.copy();
            returnStack.stackSize = 0;

            for (int g = 0; g < chest.getSizeInventory(); g++)
            {
                boolean ignore=false;
            	ItemStack chestStack = chest.getStackInSlot(g);
                if (ignoreId != null) {
                	if (chestStack==ignoreId) {
                		ignore=true;
                	}
                }
                
                if (chestStack != null && ignore==false)
                {
                	if (!compareMeta) {
                		chestStack.setItemDamage(whatItem.getItemDamage());
                	}
                    if (chestStack.isItemEqual(whatItem))
                    {
                        while (chestStack.stackSize >= 1)
                        {
                            returnStack.stackSize++;
                            chestStack.stackSize--;

                            if (chestStack.stackSize <= 0)
                            {
                                chest.setInventorySlotContents(g, null);
                            }

                            if (returnStack.stackSize == whatItem.stackSize)
                            {
                                return returnStack;
                            }
                        }
                    }
                }
            }

            if (returnStack.stackSize > 0)
            {
                return returnStack;
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (getRandomItem)
            {
                ItemStack returnStack = null;
                ArrayList<Integer> slots = new ArrayList<Integer>();

                for (int g = 0; g < chest.getSizeInventory(); g++)
                {
                    ItemStack chestStack = chest.getStackInSlot(g);

                    if (chestStack != null)
                    {
                        slots.add(g);
                    }
                }

                if (slots.size() == 0)
                {
                    return null;
                }

                returnStack = chest.getStackInSlot(new Random().nextInt(slots.size()));
                return returnStack;
            }
            else
            {
                ItemStack returnStack = null;

                for (int g = 0; g < chest.getSizeInventory(); g++)
                {
                    ItemStack chestStack = chest.getStackInSlot(g);

                    if (chestStack != null)
                    {
                        returnStack = chestStack.copy();
                        chest.setInventorySlotContents(g, null);
                        return returnStack;
                    }
                }

                return returnStack;
            }
        }
    }

    private static ItemStack inventoryGet(IInventory chest, ItemStack whatItem, boolean getRandomItem, boolean compareMeta)
    {
        if (whatItem != null)
        {
            ItemStack returnStack = whatItem.copy();
            returnStack.stackSize = 0;

            for (int g = 0; g < chest.getSizeInventory(); g++)
            {
                boolean ignore=false;
            	ItemStack chestStack = chest.getStackInSlot(g);
                
                if (chestStack != null && ignore==false)
                {
                	if (!compareMeta) {
                		chestStack.setItemDamage(whatItem.getItemDamage());
                	}
                    if (chestStack.isItemEqual(whatItem))
                    {
                        while (chestStack.stackSize >= 1)
                        {
                            returnStack.stackSize++;
                            chestStack.stackSize--;

                            if (chestStack.stackSize <= 0)
                            {
                                chest.setInventorySlotContents(g, null);
                            }

                            if (returnStack.stackSize == whatItem.stackSize)
                            {
                                return returnStack;
                            }
                        }
                    }
                }
            }

            if (returnStack.stackSize > 0)
            {
                return returnStack;
            }
            else
            {
                return null;
            }
        }
        else
        {
            if (getRandomItem)
            {
                ItemStack returnStack = null;
                ArrayList<Integer> slots = new ArrayList<Integer>();

                for (int g = 0; g < chest.getSizeInventory(); g++)
                {
                    ItemStack chestStack = chest.getStackInSlot(g);

                    if (chestStack != null)
                    {
                        slots.add(g);
                    }
                }

                if (slots.size() == 0)
                {
                    return null;
                }

                returnStack = chest.getStackInSlot(new Random().nextInt(slots.size()));
                return returnStack;
            }
            else
            {
                ItemStack returnStack = null;

                for (int g = 0; g < chest.getSizeInventory(); g++)
                {
                    ItemStack chestStack = chest.getStackInSlot(g);

                    if (chestStack != null)
                    {
                        returnStack = chestStack.copy();
                        chest.setInventorySlotContents(g, null);
                        return returnStack;
                    }
                }

                return returnStack;
            }
        }
    }
    
    /**used to place an itemStack into a set of chests or other inventories, will use all if needed, return false
    if it was unable to place into any of the chests specified due to being full */
    public boolean inventoriesPut(ArrayList<IInventory> chests, ItemStack inStack, boolean doOpenClose)
    {
        boolean placedOK = false;

        for (int i = 0; i < chests.size(); i++)
        {
            IInventory chest = chests.get(i);

            if (doOpenClose)
            {
                openCloseChest(chest, 2000);
            }


            placedOK = inventoryPut(chest, inStack);

            if (placedOK)
            {

                break;
            }

            //}
        }

        return placedOK;
    }

    public static boolean inventoriesPut(ArrayList<IInventory> chests, ItemStack inStack)
    {
        boolean placedOK = false;

        for (int i = 0; i < chests.size(); i++)
        {
            IInventory chest = chests.get(i);


            placedOK = inventoryPut(chest, inStack);

            if (placedOK)
            {

                break;
            }

            //}
        }

        return placedOK;
    }
    
    /** transfer a folks inventory into a set of chests/inventories
     * @param folkInventory the folks inventory to transfer
     * @param toChests the set of chests to transfer into
     * @param specifcItems NULL if any/all items or specify an itemStack if they should only place certain things
     * @return true if successful, false if chests are all full
     */
    public boolean inventoriesTransferFromFolk(ArrayList<ItemStack> folkInventory, ArrayList<IInventory> toChests
            , ItemStack specificItems)
    {
        boolean placed = false;
        boolean okToPlace = false;

        for (int i = 0; i < folkInventory.size(); i++)
        {
            try
            {
                ItemStack folkStack = folkInventory.get(i);

                if (specificItems != null && specificItems.getItem() == folkStack.getItem())
                {
                    okToPlace = true;
                }
                else if (specificItems == null)
                {
                    okToPlace = true;
                }
                else
                {
                    okToPlace = false;
                }

                if (okToPlace)
                {
                    placed = inventoriesPut(toChests, folkStack, true);

                    if (!placed)
                    {
                    	SimukraftReloaded.log.warning("Job: Could not place stack of " + folkStack.getDisplayName() + " in chest");
                        return false;
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        folkInventory.clear();
        return true;
    }

    /** transfer some items/any items from a set of chests into a folks inventory
     * @param folkInventory The folks inventory to transfer into
     * @param fromChests the chests where to get them
     * @param whatItems NULL for any item or an itemstack (inc. quantity) of what they do want
     * @param i  pass in -1 to not ignore any blocks or the blockID of stuff to leave in chest
     * @return true for successfully got at least one stack, false if nothing got
     */
    public boolean inventoriesTransferToFolk(ArrayList<ItemStack> folkInventory, ArrayList<IInventory> fromChests, ItemStack whatItems)
    {
        boolean ret = false;
        int limit = 0;
        ItemStack got = null;

        for (int c = 0; c < fromChests.size(); c++)
        {
            IInventory chest = fromChests.get(c);
            openCloseChest(chest, 2000);
        }

        do
        {
            got = inventoriesGet(fromChests, whatItems, false,false);

            if (got != null)
            {
                folkInventory.add(got);
                ret = true;
            }

            limit++;
        }
        while (got != null && limit < 27);

        return ret;
    }
    
    /** transfer a limited amount of a particular item from a set of chests (IInventory) into the folk's inventory */
    public int inventoriesTransferLimitedToFolk(ArrayList<ItemStack> folkInventory, ArrayList<IInventory> fromChests
    		, ItemStack whatItems, int getQty,boolean doCompareMeta) 
    {
    	int gotSoFar=0;
    	for(IInventory chest: fromChests) {
    		for (int g = 0; g < chest.getSizeInventory(); g++)
            {
    			boolean gotMatch=false;
                ItemStack chestStack = chest.getStackInSlot(g);
                if (chestStack !=null && chestStack.getItem()==whatItems.getItem()) {
                	if (doCompareMeta) {
                		if (chestStack.getItemDamage()==whatItems.getItemDamage()) {
                			gotMatch=true;
                		}
                	} else {
                		gotMatch=true;
                	}
                }
                if (gotMatch) {
                	while(gotSoFar < getQty && chestStack.stackSize > 0) {
                		gotSoFar++;
                		chestStack.stackSize--;
                		folkInventory.add(new ItemStack(Block.getBlockFromItem(chestStack.getItem()),1,chestStack.getItemDamage()));
                	}
                	if (chestStack.stackSize>0) {
                		chest.setInventorySlotContents(g, chestStack);
                	} else {
                		chest.setInventorySlotContents(g, null);
                	}
                }
                if (gotSoFar==getQty) {
                	break;
                }
            }
    	}
    	return gotSoFar;
    }
    
    
    /** returns an int Count of how many of the passed in item are in the chests (counts only, does not take out) */
    public int getItemCountInChests(ArrayList<IInventory> chests, ItemStack is, boolean doCompareMeta) {
    	int ret=0;
    	for(IInventory chest:chests) {
    		for (int g = 0; g < chest.getSizeInventory(); g++)
            {
                ItemStack chestStack = chest.getStackInSlot(g);
                if (chestStack !=null && chestStack.getItem()==is.getItem()) {
                	if (!doCompareMeta) {
                		ret+=chestStack.stackSize;
                	} else {
                		if (chestStack.getItemDamage()==is.getItemDamage()) {
                			ret+=chestStack.stackSize;
                		}
                	}
                }
            }
    	}
    	
    	return ret;
    }
    
   
    /** translate a mined block into the item, Eg Coal ore block into coal item */
    public ArrayList<ItemStack> translateBlockWhenMined(World world, V3 location)
    {
        int i = location.x.intValue();
        int j = location.y.intValue();
        int k = location.z.intValue();
        Block block = world.getBlock(i, j, k);

        if (block == null)
        {
            return null;
        }

        int meta = world.getBlockMetadata(i, j, k);
        return block.getDrops(world, i, j, k, meta, 0);
    }

    /** open and close a chest to simulate folk putting stuff into or out of it */
    public void openCloseChest(IInventory chest, int msDelay)
    {
        chest.openInventory();
        chestToClose = chest;
        chestToCloseWhen = System.currentTimeMillis() + msDelay;
        //chest is closed by Job's update() after msDelay
    }

    /** sets the closestBlocks arraylist with V3 locations in world where the passed in Block types are found */
    public void setClosestBlocksOfType(final V3 startXYZ, final ArrayList<Block> blockIDs, final int distanceLimit, final boolean needsToSeeSky, final boolean scanDownwards, final boolean oneLayerOnly)
    {
        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                World theWorld = MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);
                HashMap hm = new HashMap();
                V3 v;
                boolean skip = false;
                int YdistanceLimit = distanceLimit;

                if (oneLayerOnly)
                {
                    YdistanceLimit = 0;
                }

                for (int yo = 0; yo <= YdistanceLimit; yo++)
                {
                    for (int d = 1; d < distanceLimit; d++)
                    {
                        for (int xo = -d; xo <= d; xo++)
                        {
                            for (int zo = -d; zo <= d; zo++)
                            {
                                int sx = startXYZ.x.intValue() + xo;
                                int sy;

                                if (scanDownwards)
                                {
                                    sy = startXYZ.y.intValue() - yo;
                                }
                                else
                                {
                                    sy = startXYZ.y.intValue() + yo;
                                }

                                int sz = startXYZ.z.intValue() + zo;
                                skip = false;

                                for (int b = 0; b < blockIDs.size(); b++)
                                {
                                    Block blockID = blockIDs.get(b);

                                    if (theWorld == null)
                                    {
                                        return;
                                    }

                                    Block blockInWorld = theWorld.getBlock(sx, sy, sz);

                                    if (blockInWorld == blockID)
                                    {
                                        if (needsToSeeSky)
                                        {
                                            boolean canSeeSky;

                                            if (theWorld.getBlock(sx, sy + 1, sz) == null)
                                            {
                                                canSeeSky = true;
                                            }
                                            else
                                            {
                                                canSeeSky = false;
                                            }

                                            if (canSeeSky)
                                            {
                                                skip = false;
                                            }
                                            else
                                            {
                                                skip = true;
                                            }
                                        }

                                        if (!skip)
                                        {
                                            v = new V3((double) sx, (double) sy, (double) sz, startXYZ.theDimension);

                                            if (!hm.containsKey(v.toString()))
                                            {
                                                hm.put(v.toString(), v);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                closestBlocks = new ArrayList<V3>(hm.values());
                step = 3;
            }
        });
        t.start();
    }

    /** find all the closest inventories/chests within the search area and returns an arraylist of chest-type things */
    public static ArrayList<IInventory> inventoriesFindClosest(V3 startXYZ, int searchDistance)
    {
        ArrayList<IInventory> ret = new ArrayList<IInventory>();

        try
        {
            World theWorld = MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);
            TileEntity te = theWorld.getTileEntity(startXYZ.x.intValue(), startXYZ.y.intValue(), startXYZ.z.intValue());

            if (te != null)
            {
                if (te instanceof IInventory && !(te instanceof TileEntityFurnace) && !(te instanceof TileEntityWindmill))
                {
                    ret.add((IInventory) te);
                }
            }

            for (int d = 1; d < searchDistance; d++)
            {
                for (int yo = -d; yo <= d; yo++)
                {
                    for (int xo = -d; xo <= d; xo++)
                    {
                        for (int zo = -d; zo <= d; zo++)
                        {
                            int sx = startXYZ.x.intValue() + xo;
                            int sy = startXYZ.y.intValue() + yo;
                            int sz = startXYZ.z.intValue() + zo;
                            te = theWorld.getTileEntity(sx, sy, sz);

                            if (te != null)
                            {
                                if (te instanceof IInventory && !(te instanceof TileEntityWindmill)
                                		&& !alreadyGotChest(ret,(IInventory)te))
                                {
                                    ret.add((IInventory)te);
                                }
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            return ret;
        }

        return ret;
    }

    private static boolean alreadyGotChest(ArrayList<IInventory> chests, IInventory chest) {
    	boolean ret=false;
    	for(IInventory ch:chests) {
    		if(ch.toString().contentEquals(chest.toString())) {
    			ret=true;
    			break;
    		}
    	}
    	return ret;
    }
    
    /** searches the 4 adjacent blocks to see if they are air */
    public static V3 findAdjacentSpace(V3 startXYZ, World world) {
        World theWorld=world;
        if (theWorld==null) {
        	theWorld= MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);
        }
        
        V3 test=startXYZ.clone();
        test.x++;
        if (theWorld.isAirBlock(test.x.intValue(), test.y.intValue(), test.z.intValue())) {
        	return test;
        }
        test=startXYZ.clone();
        test.x--;
        if (theWorld.isAirBlock(test.x.intValue(), test.y.intValue(), test.z.intValue())) {
        	return test;
        }
        test=startXYZ.clone();
        test.z++;
        if (theWorld.isAirBlock(test.x.intValue(), test.y.intValue(), test.z.intValue())) {
        	return test;
        }
        test=startXYZ.clone();
        test.z--;
        if (theWorld.isAirBlock(test.x.intValue(), test.y.intValue(), test.z.intValue())) {
        	return test;
        }
        
        return startXYZ; // no space, so return passed in location
        
    }
    
    /** searches up to 10 below the location and a distance of up to 80 for a certain type of block
     * will return null if none is found in that area */
    public static V3 findClosestBlockType(V3 startXYZ, Block block, int searchDistance, boolean mustSeeSky)
    {
        World theWorld = MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);
       // SimukraftReloaded.log("findClosestBlockType: Id="+block.blockID);
        
        if (theWorld.getBlock(startXYZ.x.intValue(), startXYZ.y.intValue(), startXYZ.z.intValue()) == block)
        {
            return startXYZ;
        }

        for (int d = 1; d < searchDistance; d++)
        {
            for (int yo = -searchDistance; yo <= searchDistance; yo++)
            {
                for (int xo = -d; xo <= d; xo++)
                {
                    for (int zo = -d; zo <= d; zo++)
                    {
                        int sx = startXYZ.x.intValue() + xo;
                        int sy = startXYZ.y.intValue() + yo;
                        int sz = startXYZ.z.intValue() + zo;

                        if (theWorld.getBlock(sx, sy, sz) == block)
                        {
                            V3 ret = new V3((double)sx, (double)sy, (double)sz, startXYZ.theDimension);
                            return ret;
                        } else {

                        }
                    }
                }
            }
        }

        return null;
    }

    /** searches on the same Y level only within the searchDistance for the block type and return V3 or null */
    public static V3 findClosestBlockType(V3 startXYZ, Block block, int searchDistance)
    {
        World theWorld = MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);
        
        if (theWorld.getBlock(startXYZ.x.intValue(), startXYZ.y.intValue(), startXYZ.z.intValue()) == block)
        {
            return startXYZ;
        }

        for (int d = 1; d < searchDistance; d++)
        {
            for (int xo = -d; xo <= d; xo++)
            {
                for (int zo = -d; zo <= d; zo++)
                {
                    int sx = startXYZ.x.intValue() + xo;
                    int sy = startXYZ.y.intValue();
                    int sz = startXYZ.z.intValue() + zo;

                    if (theWorld.getBlock(sx, sy, sz) == block)
                    {
                        V3 ret = new V3((double)sx, (double)sy, (double)sz, startXYZ.theDimension);
                        return ret;
                    }
                }
            }
        }

        return null;
    }
    
    
    /** find an arraylist of locations where the specified block is found sorted into closest first */
    public static ArrayList<V3> findClosestBlocks(V3 startXYZ, Block block, int distanceLimit)
    {
        ArrayList<V3> blocksFound = new ArrayList<V3>();
        int count = 0;
        World theWorld = MinecraftServer.getServer().worldServerForDimension(startXYZ.theDimension);

        for (int yo = -distanceLimit; yo <= distanceLimit; yo++)
        {
            for (int xo = -distanceLimit; xo <= distanceLimit; xo++)
            {
                for (int zo = -distanceLimit; zo <= distanceLimit; zo++)
                {
                    try
                    {
                        int sx = startXYZ.x.intValue() + xo;
                        int sy = startXYZ.y.intValue() + yo;
                        int sz = startXYZ.z.intValue() + zo;
                        count++;

                        if (theWorld.getBlock(sx, sy, sz) == block)
                        {
                            V3 v = new V3((double) sx, (double) sy, (double) sz, startXYZ.theDimension);

                            if (!blocksFound.contains(v))
                            {
                                blocksFound.add(v);
                            }
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }

        int ci = 0;
        double cd = 999;

        for (int i = 0; i < blocksFound.size(); i++)
        {
            V3 v = blocksFound.get(i);
            double distance = Math.sqrt((v.x - startXYZ.x) * (v.x - startXYZ.x) + (v.z - startXYZ.z) * (v.z - startXYZ.z));

            if (distance < cd)
            {
                cd = distance;
                ci = i;
            }
        }

        ArrayList<V3> retblocksFound = new ArrayList<V3>();

        if (blocksFound.size() > 0)
        {
            retblocksFound.add(blocksFound.get(ci));

            for (int i = 0; i < blocksFound.size(); i++)
            {
                if (i != ci)
                {
                    retblocksFound.add(blocksFound.get(i));
                }
            }
        }

        return retblocksFound;
    }

    /* tries to mine a block at xyz and places mined stuff into chests, does translations (Coal ore > coal item)
     * Returns true if something was mined */
    public boolean mineBlockIntoChests(ArrayList<IInventory> chests, V3 blockXYZ)
    {
        boolean ret = false;
        ArrayList<ItemStack> minedStacks = translateBlockWhenMined(jobWorld, blockXYZ);

        if (minedStacks != null)
        {
            for (int s = 0; s < minedStacks.size(); s++)
            {
                ItemStack stack = minedStacks.get(s);

                if (stack != null)
                {
                    inventoriesPut(chests, stack, false);
                }
            }

            ret = true;
        }

        return ret;
    }

    /** scans a 3x3 area of the pen to count how many animals are in it (pass in Class of animal) */
    public int getAnimalCountInPen(V3 controlBox, Class animal)
    {
        List list = jobWorld.getEntitiesWithinAABB(animal, AxisAlignedBB.getBoundingBox(
                        controlBox.x, controlBox.y, controlBox.z, controlBox.x + 1.0D, controlBox.y + 1.0D,
                        controlBox.z + 1.0D).expand(3D, 2D, 3D));//look within the pen

        if (list == null)
        {
            return 0;
        }
        else
        {
            return list.size();
        }
    }
    
    
    /** the folks jobs */
    public enum Vocation
    {
        BUILDER, LUMBERJACK, MINER, CROPFARMER, BAKER, SOLDIER, SHEPHERD, GROCER, COURIER,
        MERCHANT, BUTCHER, PIGFARMER, CATTLEFARMER, CHICKENFARMER, TERRAFORMER, GLASSMAKER,
        FISHERMAN, PATHBUILDER, DAIRYFARMER, CHEESEMAKER,BURGERSMANAGER,BURGERSFRYCOOK,BURGERSWAITER, EGGFARMER
        ,BRICKMAKER;

        public static Vocation getVocation(String s)
        {
            try
            {
                if (s.contentEquals("Builder"))
                {
                    return Vocation.BUILDER;
                }
                else if (s.contentEquals("Lumberjack"))
                {
                    return Vocation.LUMBERJACK;
                }
                else if (s.contentEquals("Egg Farmer"))
                {
                    return Vocation.EGGFARMER;
                }
                else if (s.contentEquals("Miner"))
                {
                    return Vocation.MINER;
                }
                else if (s.contentEquals("Crop farmer"))
                {
                    return Vocation.CROPFARMER;
                }
                else if (s.contentEquals("Baker"))
                {
                    return Vocation.BAKER;
                }
                else if (s.contentEquals("Soldier"))
                {
                    return Vocation.SOLDIER;
                }
                else if (s.contentEquals("Shepherd"))
                {
                    return Vocation.SHEPHERD;
                }
                else if (s.contentEquals("Grocer"))
                {
                    return Vocation.GROCER;
                }
                else if (s.contentEquals("Courier"))
                {
                    return Vocation.COURIER;
                }
                else if (s.contentEquals("Merchant"))
                {
                    return Vocation.MERCHANT;
                }
                else if (s.contentEquals("Cattle farmer"))
                {
                    return Vocation.CATTLEFARMER;
                }
                else if (s.contentEquals("Pig farmer"))
                {
                    return Vocation.PIGFARMER;
                }
                else if (s.contentEquals("Chicken farmer"))
                {
                    return Vocation.CHICKENFARMER;
                }
                else if (s.contentEquals("Butcher"))
                {
                    return Vocation.BUTCHER;
                }
                else if (s.contentEquals("Terraformer"))
                {
                    return Vocation.TERRAFORMER;
                }
                else if (s.contentEquals("Glass maker"))
                {
                    return Vocation.GLASSMAKER;
                }
                else if (s.contentEquals("Fisherman"))
                {
                    return Vocation.FISHERMAN;
                }
                else if (s.contentEquals("Path Builder"))
                {
                    return Vocation.PATHBUILDER;
                }
                else if (s.contentEquals("Dairy Farmer"))
                {
                    return Vocation.DAIRYFARMER;
                }
                else if (s.contentEquals("Cheesemaker"))
                {
                    return Vocation.CHEESEMAKER;
                }
                else if (s.contentEquals("Fast Food Manager"))
                {
                    return Vocation.BURGERSMANAGER;
                }
                else if (s.contentEquals("Fast Food Fry Cook"))
                {
                    return Vocation.BURGERSFRYCOOK;
                }
                else if (s.contentEquals("Fast Food Waiter"))
                {
                    return Vocation.BURGERSWAITER;
                }
                else
                {
                    return null;
                }
            }
            catch (Exception e)
            {
                return null;
            }
        }

        @Override
        public String toString()
        {
            if (this == Vocation.BUILDER)
            {
                return "Builder";
            }

            if (this == Vocation.LUMBERJACK)
            {
                return "Lumberjack";
            }
            
            if (this == Vocation.EGGFARMER)
            {
                return "Egg Farmer";
            }

            if (this == Vocation.MINER)
            {
                return "Miner";
            }

            if (this == Vocation.CROPFARMER)
            {
                return "Crop farmer";
            }

            if (this == Vocation.BAKER)
            {
                return "Baker";
            }

            if (this == Vocation.SOLDIER)
            {
                return "Soldier";
            }

            if (this == Vocation.SHEPHERD)
            {
                return "Shepherd";
            }

            if (this == Vocation.GROCER)
            {
                return "Grocer";
            }

            if (this == Vocation.COURIER)
            {
                return "Courier";
            }

            if (this == Vocation.MERCHANT)
            {
                return "Merchant";
            }

            if (this == Vocation.BUTCHER)
            {
                return "Butcher";
            }

            if (this == Vocation.PIGFARMER)
            {
                return "Pig farmer";
            }

            if (this == Vocation.CATTLEFARMER)
            {
                return "Cattle farmer";
            }

            if (this == Vocation.CHICKENFARMER)
            {
                return "Chicken farmer";
            }

            if (this == Vocation.TERRAFORMER)
            {
                return "Terraformer";
            }

            if (this == Vocation.GLASSMAKER)
            {
                return "Glass maker";
            }
            
            if (this == Vocation.BRICKMAKER)
            {
                return "Brick maker";
            }

            if (this == Vocation.FISHERMAN)
            {
                return "Fisherman";
            }

            if (this == Vocation.PATHBUILDER)
            {
                return "Path Builder";
            }
            if (this == Vocation.DAIRYFARMER)
            {
                return "Dairy Farmer";
            }
            if (this == Vocation.CHEESEMAKER)
            {
                return "Cheesemaker";
            }
            if (this == Vocation.BURGERSMANAGER)
            {
                return "Fast Food Manager";
            }
            if (this == Vocation.BURGERSFRYCOOK)
            {
                return "Fast Food Fry Cook";
            }
            if (this == Vocation.BURGERSWAITER)
            {
                return "Fast Food Waiter";
            }
            
            
            else
            {
                return "";
            }
        }
    }
    
    public static V3 getNearestBuildingForFolk(String searchWord, FolkData folk)
    {
    	ArrayList<Building> ret=new ArrayList<Building>();
    	Building shortestDist = null;

        for (int x = 0; x < SimukraftReloaded.theBuildings.size(); x++)
        {
            Building b = (Building) SimukraftReloaded.theBuildings.get(x);

            if (b.displayName.toLowerCase().contains(searchWord.toLowerCase()))
            {
            	if(shortestDist.primaryXYZ == null)
            	{
            		shortestDist = b;
            	}
            	if(folk.location.getDistanceTo(shortestDist.primaryXYZ) < b.primaryXYZ.getDistanceTo(folk.location))
            	{
            		shortestDist = b;
            	}
            }
        }
    	return shortestDist.primaryXYZ;
    }
}
