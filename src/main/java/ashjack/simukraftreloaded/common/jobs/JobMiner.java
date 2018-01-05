package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.blocks.functionality.MiningBox;
import ashjack.simukraftreloaded.common.jobs.JobBuilder.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.game.GameMode;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class JobMiner extends Job implements Serializable
{

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;
    private transient int step = 1;
    private long timeSinceLastChestFullMessage=0;

    transient Long timeSinceLastGoto = 0l;
    transient ArrayList<IInventory> miningChests = null;
    String mineDir = "";
    /**
     * if its blank, acts as a flag to say we are vertically mining, otherwise
     * horizontal direction +x -x +z -z
     */
    String mineHorizontalDir = "";
    V3 vNextMineableBlock = null;
    transient boolean swingToggle = true;

    private boolean isChestsFull = false;
    private MiningBox theMiningBox;

    public JobMiner()
    {
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
        theFolk.isWorking = false;
    }

    public JobMiner(FolkData folk)
    {
        theFolk = folk;

        if (theStage == null)
        {
            theStage = Stage.IDLE;
        }

        theMiningBox = MiningBox.getMiningBlockByBoxXYZ(folk.employedAt);

        if (theFolk.destination == null)
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }

        // see if we're horizontally mining
        if (theMiningBox == null)
        {
            SimukraftReloaded.sendChat("There's a problem with the mining box that "
                                  + theFolk.name + " was using. Please replace it");
            theFolk.selfFire();
            return;
        }

        if (theMiningBox.marker1XYZ != null && theMiningBox.marker2XYZ == null)
        {
            V3 xyz = theMiningBox.location;
            int mbX = xyz.x.intValue();
            int mbZ = xyz.z.intValue();
            xyz = theMiningBox.marker1XYZ;
            int mX = xyz.x.intValue();
            int mZ = xyz.z.intValue();

            if (mbX < mX)
            {
                mineHorizontalDir = "+x";
            }
            else if (mbX > mX)
            {
                mineHorizontalDir = "-x";
            }
            else if (mbZ < mZ)
            {
                mineHorizontalDir = "+z";
            }
            else if (mbZ > mZ)
            {
                mineHorizontalDir = "-z";
            }
        }
    }

    @Override
    public void onUpdate()
    {
        //theFolk.levelMiner=10;
        super.onUpdate();

        try
        {
            if (!SimukraftReloaded.isDayTime())
            {
                theStage = Stage.IDLE;
                theFolk.action = FolkAction.WANDER;
                theFolk.statusText = "Finished work for the day";
                theFolk.isWorking = false;
                return;
            }

            super.onUpdateGoingToWork(theFolk);
        }
        catch (Exception e) {}

        if (theStage == Stage.WAITINGFORCHEST)
        {
            runDelay = 6000;
        }

        if (theStage == Stage.BEAMINGDOWN)
        {
            runDelay = 4000;
        }

        if (theStage == Stage.MINING)
        {
            runDelay = (int)(2000 / theFolk.levelMiner);

            if (GameMode.gameMode == GameMode.GAMEMODES.CREATIVE)
            {
                runDelay = 10;
            }
        }

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        if (theFolk.vocation != Vocation.MINER)
        {
            theFolk.selfFire();
            return;
        }

        // //////////////// IDLE
        if (theStage == Stage.IDLE && SimukraftReloaded.isDayTime())
        {
            theStage = Stage.WAITINGFORCHEST;
        }
        else if (theStage == Stage.WAITINGFORCHEST)
        {
            stageWaitingForChest();
        }
        else if (theStage == Stage.BEAMINGDOWN)
        {
            stageBeamingDown();
        }
        else if (theStage == Stage.MINING)
        {
            stageMining();
        }
        else if (theStage == Stage.BEAMINGUP)
        {
            stageBeamingUp();
        }
    }

    @Override
    // this will probably need to be identical in all sub-classes?
    public void onArrivedAtWork()
    {
        int dist = 0;
        dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist <= 1)
        {
            theFolk.action = FolkAction.ATWORK;
            theFolk.stayPut = true;
            theFolk.statusText = "Arrived at the mine";
            step = 1;
            theStage = Stage.WAITINGFORCHEST;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }

    private void stageWaitingForChest()
    {
        theFolk.stayPut = true;
        theFolk.isWorking = false;

        if (isChestsFull)
        {
        	if (System.currentTimeMillis() - timeSinceLastChestFullMessage > 120000)  {
        		SimukraftReloaded.sendChat(theFolk.name+"'s chest is full, they've stopped mining.");
        		timeSinceLastChestFullMessage=System.currentTimeMillis();
        	}
            theFolk.statusText = "All chests are full, empty them or add more chests";
            miningChests = inventoriesFindClosest(theFolk.employedAt, 5);
            ItemStack is = new ItemStack(Blocks.dirt, 1);
            Boolean placedOk = inventoriesPut(miningChests, is, true);

            if (placedOk)
            {
                inventoriesGet(miningChests, is, false,false);
                theFolk.inventory.clear();
                isChestsFull = false;
                theStage = Stage.BEAMINGDOWN;
                setNextMineableBlock();
            }
        }
        else
        {
            theFolk.statusText = "Checking for storage chests...";
            // / check for chest next to primary marker
            miningChests = inventoriesFindClosest(theFolk.employedAt, 5);

            if (miningChests.size() == 0)
            {
                theFolk.statusText = "Please place at least one chest near the mining box";
            }
            else
            {
                theStage = Stage.BEAMINGDOWN;

                if (theFolk.theEntity != null)
                {
                    try {
	                	if (theFolk.gender == 0)
	                    {
	                        mc.theWorld.playSound(theFolk.location.x,
	                                              theFolk.location.y, theFolk.location.z,
	                                              "ashjacksimukraftreloaded:readym", 1f, 1f, false);
	                    }
	                    else
	                    {
	                        mc.theWorld.playSound(theFolk.location.x,
	                                              theFolk.location.y, theFolk.location.z,
	                                              "ashjacksimukraftreloaded:readyf", 1f, 1f, false);
	                    }
                    } catch(Exception e) {} // playSound can NPE when switching dimensions
                }
            }
        }
    }

    private void stageBeamingDown()
    {
        if (vNextMineableBlock == null)
        {
            setNextMineableBlock();
        }

        if (vNextMineableBlock == null)
        {
            return;
        }

        theFolk.updateLocationFromEntity();

        if (theFolk.location.getDistanceTo(vNextMineableBlock) < 10 || vNextMineableBlock.y <= 20)
        {
            theStage = Stage.MINING;
            theFolk.stayPut = true;
            return;
        }

        if (step == 1)
        {
            if (theFolk.beamingTo == null)
            {
                theFolk.statusText = "Beam me down, Scotty!";

                if (theFolk.theEntity != null)
                {
                    if (theFolk.gender == 0)
                    {
                        mc.theWorld.playSound(theFolk.location.x,
                                              theFolk.location.y, theFolk.location.z,
                                              "ashjacksimukraftreloaded:beamm", 1f, 1f, false);
                    }
                    else
                    {
                        mc.theWorld.playSound(theFolk.location.x,
                                              theFolk.location.y, theFolk.location.z,
                                              "ashjacksimukraftreloaded:beamf", 1f, 1f, false);
                    }
                }

                theFolk.stayPut = true;
                theFolk.beamMeTo(vNextMineableBlock.clone());
                step = 2;
            }
        }
        else if (step == 2)
        {
            if (theFolk.destination == null)
            {
                theStage = Stage.MINING;
                return;
            }
        }
    }

    // TODO: never hits this, need to beam them back up before sunset
    private void stageBeamingUp()
    {
        theFolk.beamMeTo(theFolk.employedAt.clone());
        theStage = Stage.IDLE;
        theFolk.action = FolkAction.WANDER;
        SimukraftReloaded.sendChat(theFolk.name
                              + " has finished their shift down the mine.");
        mc.theWorld.playSound(mc.thePlayer.posX, mc.thePlayer.posY,
                              mc.thePlayer.posZ, "ashjacksimukraftreloaded:cash", 1f, 1f, false);
    }

    /**
     * finds the next mineable block and sets the class field V3 with it's
     * location so it can be mined
     */
    private void setNextMineableBlock()
    {
        int mx, my, mz;
        int xxx = 0, yyy = 0, zzz = 0;
        V3 m1 = theMiningBox.marker1XYZ;
        V3 m2 = theMiningBox.marker2XYZ;
        V3 m3 = theMiningBox.marker3XYZ;

        if (m1 == null)
        {
        	SimukraftReloaded
            .sendChat("There was a problem with the mine. The markers don't seem to be placed correctly, see Manual/Wiki");
            return;
        }

        if (mineHorizontalDir.contentEquals(""))   // / VERTICAL MINING
        {
            mx = m1.x.intValue();
            my = m1.y.intValue() - 1;
            mz = m1.z.intValue();

            if (m2.x.intValue() == m1.x.intValue())
            {
                if (m2.z.intValue() > mz)
                {
                    mineDir = "z+";
                }
                else
                {
                    mineDir = "z-";
                }
            }
            else if (m2.z.intValue() == m1.z.intValue())
            {
                if (m2.x.intValue() > mx)
                {
                    mineDir = "x+";
                }
                else
                {
                    mineDir = "x-";
                }
            }

            Block id = null;
            int idmeta = 0;
            int ftbCount = 0, ltrCount = 0;
            int xo = 0, zo = 0;

            if (m1.x.intValue() == m2.x.intValue())
            {
                ltrCount = Math.abs(m2.z.intValue() - m1.z.intValue()) - 1;
            }
            else
            {
                ltrCount = Math.abs(m2.x.intValue() - m1.x.intValue()) - 1;
            }

            if (m1.x.intValue() == m3.x.intValue())
            {
                ftbCount = Math.abs(m3.z.intValue() - m1.z.intValue()) - 1;
            }
            else
            {
                ftbCount = Math.abs(m3.x.intValue() - m1.x.intValue()) - 1;
            }

            gotABlock: for (int l = my; l > 0; l--)
            {
                for (int ftb = 0; ftb <= ftbCount; ftb++)
                {
                    for (int ltr = 1; ltr <= ltrCount; ltr++)
                    {
                        if (mineDir.contentEquals("x+"))
                        {
                            xo = ltr;
                            zo = -ftb;
                        }
                        else if (mineDir.contentEquals("x-"))
                        {
                            xo = -ltr;
                            zo = ftb;
                        }
                        else if (mineDir.contentEquals("z+"))
                        {
                            xo = ftb;
                            zo = ltr;
                        }
                        else if (mineDir.contentEquals("z-"))
                        {
                            xo = -ftb;
                            zo = -ltr;
                        }

                        xxx = mx + xo;
                        yyy = l;
                        zzz = mz + zo;
                        id = jobWorld.getBlock(xxx, yyy, zzz);
                        idmeta = jobWorld.getBlockMetadata(xxx, yyy, zzz);
                        
                        if (id == Blocks.bedrock)
                        {
                        	SimukraftReloaded
                            .sendChat(theFolk.name
                                      + " has retired from mining, as the mine has now reached bedrock.");
                            theFolk.beamMeTo(theFolk.employedAt);
                            theFolk.selfFire();
                            return;
                        }

                        try {
	                        Block block = id;
	                        if (id != Blocks.air && id != Blocks.water
	                                && id != Blocks.water
	                                && id != Blocks.lava
	                                && id != Blocks.lava
	                                && !block.toString().toLowerCase().contains("oil"))
	                        {
	                            break gotABlock;
	                        }
                        } catch(Exception e) { break gotABlock; }
                    }
                }
            }

            // /reaches here when a mineable block has been found
            try
            {
                vNextMineableBlock = new V3((double) xxx, (double) yyy,
                                            (double) zzz, theFolk.employedAt.theDimension);
            }
            catch (Exception e) {}  // NPE when miner has been fired last tick
        }
        else     // / HORIZONTAL MINING
        {
            V3 vMine = new V3(m1.x, m1.y, m1.z, theFolk.employedAt.theDimension);

            if (mineHorizontalDir.contentEquals("+x"))
            {
                vMine.x++;
            }
            else if (mineHorizontalDir.contentEquals("-x"))
            {
                vMine.x--;
            }
            else if (mineHorizontalDir.contentEquals("+z"))
            {
                vMine.z++;
            }
            else if (mineHorizontalDir.contentEquals("-z"))
            {
                vMine.z--;
            }

            V3 vMineable = new V3();
            Block id = null;
            int meta = 0;
            int xo = 0;
            int yo = 0;
            int zo = 0;
            boolean flagFound = false;
            gotABlock2:

            for (int ftb = 0; ftb < 1024; ftb++)
            {
                for (int btt = 0; btt < theMiningBox.size; btt++)
                {
                    for (int ltr = 0; ltr < theMiningBox.size; ltr++)
                    {
                        if (mineHorizontalDir.contentEquals("+x"))
                        {
                            xo = ftb;
                            zo = ltr;
                        }
                        else if (mineHorizontalDir.contentEquals("-x"))
                        {
                            xo = -ftb;
                            zo = -ltr;
                        }
                        else if (mineHorizontalDir.contentEquals("+z"))
                        {
                            xo = -ltr;
                            zo = ftb;
                        }
                        else if (mineHorizontalDir.contentEquals("-z"))
                        {
                            xo = ltr;
                            zo = -ftb;
                        }

                        yo = btt;

                        try
                        {
                            if (theFolk.employedAt == null)
                            {
                                theStage = Stage.IDLE;
                                return;
                            }

                            vMineable = new V3(vMine.x + xo, vMine.y + yo, vMine.z
                                               + zo, theFolk.employedAt.theDimension);
                            id = jobWorld.getBlock(vMineable.x.intValue(),
                                                     vMineable.y.intValue(),
                                                     vMineable.z.intValue());
                            meta = jobWorld.getBlockMetadata(vMineable.x.intValue(),
                                                             vMineable.y.intValue(),
                                                             vMineable.z.intValue());
                            
                            if (ftb % 10==0 && btt==Math.floor(theMiningBox.size/2) && ltr==0) {
                            	V3 lightbox=vMineable.clone();
                            	
                            	if (mineHorizontalDir.contentEquals("+x"))
                                {
                            		lightbox.z--;
                                }
                                else if (mineHorizontalDir.contentEquals("-x"))
                                {
                                	lightbox.z++;
                                }
                                else if (mineHorizontalDir.contentEquals("+z"))
                                {
                                	lightbox.x++;
                                }
                                else if (mineHorizontalDir.contentEquals("-z"))
                                {
                                	lightbox.x--;
                                }
                            	
                            	Block lbid = jobWorld.getBlock(lightbox.x.intValue(),
                            			lightbox.y.intValue(),
                            			lightbox.z.intValue());
                            	
                            	if (miningChests.size() > 0 && lbid !=SimukraftReloadedBlocks.lightBox)
                                {
                                   ItemStack light=null;
                                   int lightmeta=0;
                                   while (light==null && lightmeta <8) {
                                   	light= inventoriesGet(miningChests, new ItemStack(SimukraftReloadedBlocks.lightBox,1,lightmeta), false,true);
                                   	lightmeta++;
                                   }
                                    if (light != null)
                                    {
                                    	SimukraftReloaded.log.info("Light box placed at "+lightbox.toString());
                                    	jobWorld.setBlock(lightbox.x.intValue(),lightbox.y.intValue(),lightbox.z.intValue()
                            			,SimukraftReloadedBlocks.lightBox,light.getItemDamage(),0x03);
                                    }
                                }
                            }

                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        // / should not hit bedrock, but just in case
                        if (id == Blocks.bedrock)
                        {
                        	SimukraftReloaded
                            .sendChat(theFolk.name
                                      + " has retired from mining, hit bedrock, try mining horizontally higher up");
                            theFolk.beamMeTo(theFolk.employedAt);
                            theFolk.selfFire();
                            return;
                        }

                        try {
	                        Block block = id;
	                        if (id != Blocks.air && id != Blocks.water
	                                && id != Blocks.water
	                                && id != Blocks.lava
	                                && id != Blocks.lava
	                        		&& !block.toString().toLowerCase().contains("oil"))
	                        {
	                            flagFound = true;
	                            break gotABlock2;
	                        }
                        } catch(Exception e) { flagFound=true; break gotABlock2; }
                    }
                }
            }

            if (!flagFound)
            {
            	SimukraftReloaded
                .sendChat(theFolk.name
                          + " has retired from mining, as the horizontal mine has reached it's 1 Km limit. Should you need a longer mine, simply start a new one at the end.");
                theFolk.beamMeTo(theFolk.employedAt);
                theFolk.isWorking = false;
                theFolk.selfFire();
                return;
            }

            vNextMineableBlock = vMineable.clone();
        } // end of horizontal mining code
    }

    /** the actual mining stage, remove blocks, put them into the chest */
    private void stageMining()
    {
        theFolk.isWorking = true;

        if (theFolk.theEntity != null)
        {
            theFolk.theEntity.dimension = theFolk.employedAt.theDimension;
        }
        else
        {
            theFolk.location.theDimension = theFolk.employedAt.theDimension;
        }

        theFolk.action = FolkAction.ATWORK;

        if (theFolk.isSpawned())
        {
            if (System.currentTimeMillis() - timeSinceLastGoto > 7000)
            {
                theFolk.updateLocationFromEntity();

                if (theFolk.location.y -  vNextMineableBlock.y > 4)
                {
                    vNextMineableBlock.doNotTimeout = false;

                    if (vNextMineableBlock.y > 20)
                    {
                        theFolk.gotoXYZ(vNextMineableBlock, GotoMethod.BEAM);
                    }
                    else
                    {
                        theFolk.stayPut = true;
                    }
                }
                else
                {
                    vNextMineableBlock.doNotTimeout = true;

                    if (vNextMineableBlock.y > 20)
                    {
                        theFolk.stayPut = false;
                        theFolk.gotoXYZ(vNextMineableBlock, GotoMethod.WALK);
                    }
                    else
                    {
                        theFolk.stayPut = true;
                    }
                }

                timeSinceLastGoto = System.currentTimeMillis();
                theFolk.timeStartedGotoing = System.currentTimeMillis();
            }
        }

        Thread t = new Thread(new Runnable()
        {
            public void run()
            {
                for (int d = 0; d < 5; d++)
                {
                    try
                    {
                        jobWorld.playSound(vNextMineableBlock.x,
                                           vNextMineableBlock.y, vNextMineableBlock.z,
                                           "dig.stone", 1f, 1f, false);
                    }
                    catch (Exception e)
                    {
                    }

                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (Exception e)
                    {
                    }
                }
            }
        });
        t.start();

        // remove the block and replace with air
        if (lastMinedBlockName.contentEquals(""))
        {
            theFolk.statusText = "Diggy diggy hole!";
        }

		///IMPORTANT: There's a bug here, vanilla blocks are fine, but Modded blocks are not retaining
		///Their META value, so odd items/blocks are mined up.
		
        int id = 0;
        int idmeta = 0;
        id = Block.getIdFromBlock(jobWorld.getBlock(
                 vNextMineableBlock.x.intValue(),
                 vNextMineableBlock.y.intValue(),
                 vNextMineableBlock.z.intValue()));
        idmeta = jobWorld.getBlockMetadata(
                     vNextMineableBlock.x.intValue(),
                     vNextMineableBlock.y.intValue(),
                     vNextMineableBlock.z.intValue());
        //jobWorld.playAuxSFX(2001, vNextMineableBlock.x.intValue(),
                            //vNextMineableBlock.y.intValue(), vNextMineableBlock.z.intValue(), id );

        if (jobWorld == null)
        {
            return;
        }

        // /translate id if needed  -- BUG mentioned above could be here
        ArrayList<ItemStack> minedStacks = translateBlockWhenMined(jobWorld, vNextMineableBlock);
        jobWorld.setBlock(
            vNextMineableBlock.x.intValue(),
            vNextMineableBlock.y.intValue(),
            vNextMineableBlock.z.intValue(), Blocks.air, 0, 0x03);

        if (theFolk.theEntity != null)
        {
            try
            {
                mc.theWorld.spawnParticle("explode",
                                          vNextMineableBlock.x.intValue(),
                                          vNextMineableBlock.y.intValue(),
                                          vNextMineableBlock.z.intValue(), 0.1f, 0.3f, 0);
                mc.theWorld.spawnParticle("explode",
                                          vNextMineableBlock.x.intValue(),
                                          vNextMineableBlock.y.intValue(),
                                          vNextMineableBlock.z.intValue(), 0, 0.2f, 0);
                mc.theWorld.spawnParticle("explode",
                                          vNextMineableBlock.x.intValue(),
                                          vNextMineableBlock.y.intValue(),
                                          vNextMineableBlock.z.intValue(), 0, 0.1f, 0.1f);
            }
            catch (Exception e) {}
        }

        if (GameMode.gameMode != GameMode.GAMEMODES.CREATIVE)
        {
            SimukraftReloaded.states.credits -= (0.012f);
            int b4 = (int)Math.floor(theFolk.levelMiner);

            if (theFolk.levelMiner < 10.0f)
            {
                theFolk.levelMiner += (0.001 / b4);
            }

            int aft = (int)Math.floor(theFolk.levelMiner);

            if (b4 != aft)
            {
                SimukraftReloaded.sendChat(theFolk.name + " has just levelled up to Miner Level " + aft);
            }
        }
        else
        {
            theFolk.levelMiner = 10.0f;
        }

        // /place glass cover
        if (theFolk.employedAt==null) {return;}
        	
        if (theFolk.employedAt.y - vNextMineableBlock.y > 3)
        {
            if (theMiningBox.addGlassCover
                    && mineHorizontalDir.contentEquals(""))
            {
                Block gid = jobWorld.getBlock(
                              vNextMineableBlock.x.intValue(),
                              theFolk.employedAt.y.intValue(),
                              vNextMineableBlock.z.intValue());

                if (gid == null)
                {
                    if (miningChests.size() > 0)
                    {
                        ItemStack glass = inventoriesGet(miningChests, new ItemStack(Blocks.glass, 1), false,false);

                        if (glass != null)
                        {
                            jobWorld.setBlock(
                                vNextMineableBlock.x.intValue(),
                                theFolk.employedAt.y.intValue(),
                                vNextMineableBlock.z.intValue(),
                                Blocks.glass, 0, 0x03);
                        }
                    }
                }
            }
            else
            {
                jobWorld.setBlock(
                    vNextMineableBlock.x.intValue(),
                    theFolk.employedAt.y.intValue(),
                    vNextMineableBlock.z.intValue(), Blocks.air, 0, 0x02);
            }
        }

        // /// see if we want to keep or discard this block
        boolean keep = false;

        if (theMiningBox.discards == 0)
        {
            keep = true;
        }

        if (theMiningBox.discards == 1)
        {
            if (id ==  Block.getIdFromBlock(Blocks.dirt) || id == Block.getIdFromBlock(Blocks.grass))
            {
                keep = false;
            }
            else
            {
                keep = true;
            }
        }

        if (theMiningBox.discards == 2)
        {
            if (id ==  Block.getIdFromBlock(Blocks.dirt) || id ==  Block.getIdFromBlock(Blocks.grass)
                    || id ==  Block.getIdFromBlock(Blocks.stone)
                    || id ==  Block.getIdFromBlock(Blocks.cobblestone))
            {
                keep = false;
            }
            else
            {
                keep = true;
            }
        }

        if (theMiningBox.discards == 3)
        {
            if (id ==  Block.getIdFromBlock(Blocks.dirt) || id ==  Block.getIdFromBlock(Blocks.grass)
                    || id ==  Block.getIdFromBlock(Blocks.sand))
            {
                keep = false;
            }
            else
            {
                keep = true;
            }
        }

        if (theMiningBox.discards == 4)
        {
            if (id ==  Block.getIdFromBlock(Blocks.dirt) || id ==  Block.getIdFromBlock(Blocks.grass)
                    || id ==  Block.getIdFromBlock(Blocks.stone)
                    || id ==  Block.getIdFromBlock(Blocks.cobblestone)
                    || id ==  Block.getIdFromBlock(Blocks.sand))
            {
                keep = false;
            }
            else
            {
                keep = true;
            }
        }

        // override keeps
        try {
	        Block block = Block.getBlockById(id);
	        if (id ==  Block.getIdFromBlock(Blocks.water) || id ==  Block.getIdFromBlock(Blocks.water)
	                || id ==  Block.getIdFromBlock(Blocks.lava)
	                || id ==  Block.getIdFromBlock(Blocks.lava)
	                || id ==  Block.getIdFromBlock(Blocks.tallgrass)
	                || block.toString().toLowerCase().contains("oil"))
	        {
	            keep = false;
	        }
        } catch(Exception e) {keep=false;}
        
        boolean placedOk = true;

        if (keep)
        {
            // /place block into chest
            if (minedStacks != null)
            {
                miningChests = Job.inventoriesFindClosest(theFolk.employedAt, 5);

                for (int s = 0; s < minedStacks.size(); s++)
                {
                    ItemStack stack = minedStacks.get(s);

                    if (stack != null)
                    {
                        lastMinedBlockName = stack.getDisplayName();
                        theFolk.statusText = "Diggy diggy hole, mining " + lastMinedBlockName + "!";
                        placedOk = inventoriesPut(miningChests, stack, false);
                    }
                }
            }
        }
        else     // end if we're keeping this block
        {
            //theFolk.statusText = "Diggy diggy hole!";
        }

        if (!placedOk)
        {
            isChestsFull = true;
            theStage = Stage.WAITINGFORCHEST;
            theFolk.inventory.clear();
            theFolk.inventory.add(new ItemStack(Block.getBlockById(id), idmeta, 1));
        }

        setNextMineableBlock();
    } // end of stage mining

    private String lastMinedBlockName = "";

    public enum Stage
    {
        IDLE, WAITINGFORCHEST, BEAMINGDOWN, MINING, BEAMINGUP;
    }
}
