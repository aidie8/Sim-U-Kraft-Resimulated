package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class JobShepherd extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112207904191941L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient ArrayList<IInventory> farmChests = new ArrayList<IInventory>();
    private transient EntitySheep sheepToShear = null;
    private transient boolean isShearing = false;

    public JobShepherd()
    {
    }
    public JobShepherd(FolkData folk)
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
            theFolk.gotoXYZ(theFolk.employedAt, GotoMethod.BEAM);
        }
    }

    public enum Stage
    {
        IDLE, ARRIVEDATFARM, WAITINGFORWOOL, SHEARING, CANTWORK;
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
        theFolk.isWorking = false;
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

        if (theStage == Stage.WAITINGFORWOOL)
        {
            runDelay = 15000;
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
        else if (theStage == Stage.ARRIVEDATFARM)
        {
            theStage = Stage.WAITINGFORWOOL;
        }
        else if (theStage == Stage.WAITINGFORWOOL)
        {
            stageWaiting();
        }
        else if (theStage == Stage.SHEARING)
        {
            stageShearing();
        }
        else if (theStage == Stage.CANTWORK)
        {
            stageCantWork();
        }
    }

    private void stageWaiting()
    {
        Random rand = new Random();
        theFolk.isWorking = false;
        theFolk.statusText = "Sharpening Shears";
        theFolk.stayPut = false;
        List list = jobWorld.getEntitiesWithinAABB(EntitySheep.class, AxisAlignedBB.getBoundingBox(
                        theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.x + 1.0D, theFolk.employedAt.y + 1.0D,
                        theFolk.employedAt.z + 1.0D).expand(3D, 2D, 3D));//look within the pen
        Double playerdist = mc.thePlayer.getDistance(theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z);

        if (playerdist > 60)
        {
            try
            {
                sheepToShear = (EntitySheep) list.get(0);
                sheepToShear.setSheared(false);
            }
            catch (Exception e)
            {
                // if player is out of range for a while, sheep will despawn
                placeWoolIntoAChest(1, 1); //so compensate by placing 1 block into chest
            }
        }
        else
        {
            //they're not regrowing their wool in 1.3.2, so this fixes that
            if (list.size() > 0)
            {
               try {
	            	int r = rand.nextInt(list.size() - 1);
	                sheepToShear = (EntitySheep) list.get(r);
	                sheepToShear.setSheared(false);
               } catch(Exception e){} // NPE if you kill a sheep
            }
        }

        for (int s = 0; s < list.size(); s++)
        {
            sheepToShear = (EntitySheep) list.get(s);

            if (!sheepToShear.getSheared())
            {
                theStage = Stage.SHEARING;
                step = 1;
                break;
            }
        }
    }

    private void stageShearing()
    {
        theFolk.stayPut = false;

        if (step == 1)
        {
            if (theFolk.getDistanceToPlayer() < 50)
            {
                theFolk.gotoXYZ(new V3(sheepToShear.posX, sheepToShear.posY, sheepToShear.posZ, theFolk.employedAt.theDimension), GotoMethod.WALK);
            }

            step = 2;
        }
        else if (step == 2)
        {
            step = 3;

            if (theFolk.theEntity != null)
            {
                theFolk.theEntity.faceEntity(sheepToShear, 1f, 1f);
            }
        }
        else if (step == 3)
        {
            theFolk.statusText = "Shearing "+FolkData.generateName(0, true, "") + " the sheep!";
            sheepToShear.setSheared(true);

            if (theFolk.theEntity != null)
            {
                theFolk.theEntity.faceEntity(sheepToShear, 1f, 1f);
                theFolk.isWorking = true;
                mc.theWorld.playSound(theFolk.theEntity.posX, theFolk.theEntity.posY, theFolk.theEntity.posZ, "ashjacksimukraftreloaded:shears", 1f, 1f, false);
                Thread t = new Thread(new Runnable()
                {
                    public void run()
                    {
                        isShearing = true;

                        for (int d = 0; d < 12; d++)
                        {
                            spawnExplosionParticle(sheepToShear);

                            try
                            {
                                Thread.sleep(150);
                            }
                            catch (Exception e) {}
                        }

                        isShearing = false;
                        theFolk.statusText = "Watching the herd";
                    }
                });
                t.start();
                theFolk.theEntity.faceEntity(sheepToShear, 1f, 1f);
                step = 4;
            }
        }
        else if (step == 4)
        {
            Random ra = new Random();
            int count = ra.nextInt(3) + 1; // 1 to 3
            placeWoolIntoAChest(sheepToShear.getFleeceColor(), count);
            theStage = Stage.WAITINGFORWOOL;
            theFolk.isWorking = false;
            step = 1;
        }
    }

    public void spawnExplosionParticle(Entity ent)
    {
        Random rand = new Random();

        for (int var1 = 0; var1 < 20; ++var1)
        {
            double var2 = rand.nextGaussian() * 0.02D;
            double var4 = rand.nextGaussian() * 0.02D;
            double var6 = rand.nextGaussian() * 0.02D;
            double var8 = 10.0D;

            try
            {
                ModSimukraft.proxy.getClientWorld().spawnParticle("explode", ent.posX + (double)(rand.nextFloat() * 1 * 2.0F) - (double)1 - var2 * var8, ent.posY + (double)(rand.nextFloat() * 1) - var4 * var8, ent.posZ + (double)(rand.nextFloat() * 1 * 2.0F) - (double)1 - var6 * var8, var2, var4, var6);
            }
            catch (Exception e) {}
        }
    }

    private void stageCantWork()
    {
        theFolk.statusText = "Can't work, the chests are full of wool";
        theFolk.isWorking = false;
    }

    private boolean placeWoolIntoAChest(int metaColor, int amount)
    {
        SimukraftReloaded.states.credits -= (0.02f * amount);
        farmChests = inventoriesFindClosest(theFolk.employedAt, 4);
        inventoriesPut(farmChests, new ItemStack(Blocks.wool, amount, metaColor), true);
        return true;
    }

    public void spawnSheepIfNeeded(V3 controlBox)
    {
        List list = jobWorld.getEntitiesWithinAABB(EntitySheep.class, AxisAlignedBB.getBoundingBox(
                        controlBox.x, controlBox.y, controlBox.z, controlBox.x + 1.0D, controlBox.y + 1.0D,
                        controlBox.z + 1.0D).expand(3D, 2D, 3D));//look within the pen

        Random ra = new Random();
        EntitySheep sheep;

        if (list.size() > 0 && list.size() < 6)
        {
            for (int fuck = 0; fuck < 6 - list.size(); fuck++)
            {
                sheep = new EntitySheep(jobWorld);
                sheep.setLocationAndAngles(controlBox.x, controlBox.y + 1, controlBox.z, 0f, 0f);
                sheep.setFleeceColor(ra.nextInt(12) + 1);
                jobWorld.spawnEntityInWorld(sheep);
            }
        }
        else if (list.size() == 0)
        {
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x - 1, controlBox.y + 1, controlBox.z - 1, 0f, 0f);
            sheep.setFleeceColor(ra.nextInt(12) + 1);
            jobWorld.spawnEntityInWorld(sheep);
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x, controlBox.y + 1, controlBox.z - 1, 0f, 0f);
            sheep.setFleeceColor(ra.nextInt(12) + 1);
            jobWorld.spawnEntityInWorld(sheep);
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x + 1, controlBox.y + 1, controlBox.z - 1, 0f, 0f);
            sheep.setFleeceColor(ra.nextInt(12) + 1);
            jobWorld.spawnEntityInWorld(sheep);
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x + 1, controlBox.y + 1, controlBox.z, 0f, 0f);
            sheep.setFleeceColor(ra.nextInt(12) + 1);
            jobWorld.spawnEntityInWorld(sheep);
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x + 1, controlBox.y + 1, controlBox.z + 1, 0f, 0f);
            sheep.setFleeceColor(ra.nextInt(12) + 1);
            jobWorld.spawnEntityInWorld(sheep);
            sheep = new EntitySheep(jobWorld);
            sheep.setLocationAndAngles(controlBox.x + 2, controlBox.y + 1, controlBox.z + 2, 0f, 0f);
            jobWorld.spawnEntityInWorld(sheep);
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
            theFolk.statusText = "Arrived at the sheep farm";
            theStage = Stage.ARRIVEDATFARM;
            spawnSheepIfNeeded(theFolk.employedAt);
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, GotoMethod.BEAM);
        }
    }
}
