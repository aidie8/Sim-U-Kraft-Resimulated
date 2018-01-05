package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.List;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job.Vocation;
import ashjack.simukraftreloaded.common.jobs.JobMiner.Stage;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class JobSoldier extends Job implements Serializable
{

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private transient Entity badGuy;
    private transient int count = 60;
    private transient long timeSinceLastBTB = 0; //back to base every so often
    public transient int kills = 0;

    public JobSoldier()
    {
    }

    public JobSoldier(FolkData folk)
    {
        theFolk = folk;

        if (theStage == null)
        {
            theStage = Stage.IDLE;
        }

        if (theFolk == null)
        {
            return;
        } // is null when first employing, this is for next day(s)

        if (theFolk.destination == null)
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }

    public enum Stage
    {
        IDLE, ONPATROL, ATTACKING;
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();

        //super.onUpdateGoingToWork(theFolk);

        if (System.currentTimeMillis() - timeSinceLastRun < runDelay)
        {
            return;
        }

        timeSinceLastRun = System.currentTimeMillis();

        // ////////////////IDLE
        if (theStage == Stage.IDLE)
        {
            theStage = Stage.ONPATROL;

            if (theFolk.destination == null)
            {
                theFolk.gotoXYZ(theFolk.employedAt, null);
            }
        }
        else if (theStage == Stage.ONPATROL)
        {
            stageOnPatrol();
        }
        else if (theStage == Stage.ATTACKING)
        {
            stageAttacking();
        }
    }

    Random rand = new Random();

    private void stageOnPatrol()
    {
        theFolk.isWorking = false;
        theFolk.stayPut = false;
        theFolk.action = FolkAction.ATWORK;
        theFolk.statusText = "Patroling town";
        runDelay = 10000;

        if (jobWorld == null)
        {
            jobWorld = MinecraftServer.getServer().worldServerForDimension(theFolk.employedAt.theDimension);
        }

        if (System.currentTimeMillis() - timeSinceLastBTB > 120000)  //every 2 minutes
        {
            if (theFolk.isSpawned()) {
            	EntityPlayer player=jobWorld.getClosestPlayer(theFolk.theEntity.posX,theFolk.theEntity.posY
        			,theFolk.theEntity.posZ,50);
            	if (player !=null) {
            		theFolk.gotoXYZ(new V3(player.posX,player.posY,player.posZ,player.dimension), GotoMethod.WALK);
            	}
            }
            timeSinceLastBTB = System.currentTimeMillis();
            runDelay = 60000;
        }
        else
        {
            int xo = rand.nextInt(60) - 30;
            int zo = rand.nextInt(60) - 30;
            V3 wanderTo = new V3(theFolk.location.x + xo, theFolk.location.y - 1, theFolk.location.z + zo, theFolk.location.theDimension);

            while (jobWorld.getBlock(wanderTo.x.intValue(), wanderTo.y.intValue(), wanderTo.z.intValue()) != null
                    && wanderTo.y < 255)
            {
                wanderTo.y++;
            }

            theFolk.gotoXYZ(wanderTo, GotoMethod.WALK);
        }

        List list = jobWorld
                    .getEntitiesWithinAABBExcludingEntity(
                        mc.thePlayer,
                        AxisAlignedBB.getBoundingBox(theFolk.employedAt.x,
                                theFolk.employedAt.y, theFolk.employedAt.z,
                                theFolk.employedAt.x + 1.0D,
                                theFolk.employedAt.y + 1.0D,
                                theFolk.employedAt.z + 1.0D).expand(100D, 5D, 100D));

        try
        {

            badGuy = findClosestHostileMob(list);

            if (badGuy != null)
            {
                runDelay = 1000;
                theStage = Stage.ATTACKING;
                count = 100;
                theFolk.statusText = "Going to attack a " + badGuy.toString().split("'")[1];

                if (theFolk.isSpawned())
                {
                    theFolk.gotoXYZ(new V3(badGuy.posX , badGuy.posY, badGuy.posZ, theFolk.theEntity.dimension), GotoMethod.WALK);
                }
                else
                {
                    theFolk.gotoXYZ(new V3(badGuy.posX , badGuy.posY, badGuy.posZ, theFolk.theEntity.dimension), GotoMethod.SHIFT);
                }

                return;
            }
        }
        catch (Exception e)
        {
        }
    }

    private void stageAttacking()
    {
        try
        {
            runDelay = 200;

            if (theFolk.theEntity == null)
            {
                return;
            }

            theFolk.stayPut = false;
            int distance = (int) theFolk.theEntity.getDistanceToEntity(badGuy);

            if (theFolk.destination == null)
            {
                if (theFolk.isSpawned())
                {
                    theFolk.gotoXYZ(new V3(badGuy.posX , badGuy.posY, badGuy.posZ, theFolk.theEntity.dimension), GotoMethod.WALK);
                }
                else
                {
                    theFolk.gotoXYZ(new V3(badGuy.posX , badGuy.posY, badGuy.posZ, theFolk.theEntity.dimension), GotoMethod.SHIFT);
                }
            }

            distance = (int) theFolk.theEntity.getDistanceToEntity(badGuy);
            count--;

            if (count <= 0)
            {
                theStage = Stage.ONPATROL;
            }

            if (distance > 5)
            {
                return;
            } // dont run the rest until they're close enough

            distance = (int) theFolk.theEntity.getDistanceToEntity(badGuy);

            if (distance < 4)
            {
                theFolk.isWorking = true;
                runDelay = 50;

                badGuy.attackEntityFrom(DamageSource.generic, 3);
            }

            if (badGuy.isDead)
            {
                theStage = Stage.ONPATROL;
                runDelay = (int)(((11 - theFolk.levelSoldier) * 500) * (11 - theFolk.levelSoldier));
                //L1   ((11-1)*500) *(11-1)  = 50000
                //L10   ((11-10)*500) *(11-10) = 500
                kills++;

                if (theFolk.levelSoldier < 10.0f)
                {
                    theFolk.levelSoldier += 0.03f;

                    if (theFolk.levelSoldier > 10.0f)
                    {
                        theFolk.levelSoldier = 10.0f;
                    }
                }

                theFolk.statusText = "Killed " + badGuy.toString().split("'")[1];
                theFolk.isWorking = false;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
    }

    private Entity findClosestHostileMob(List<Entity> mobs)
    {
        Entity closestBadGuy = null;
        int dist = 9999;

        if (!theFolk.isSpawned())
        {
            return null;
        }

        for (int j = 0; j < mobs.size(); j++)
        {
            Entity entity1 = (Entity) mobs.get(j);

            if (entity1 instanceof EntityMob || entity1 instanceof IMob)
            {
            	
            	PathEntity path=jobWorld.getEntityPathToXYZ(theFolk.theEntity, (int)entity1.posX,(int)entity1.posY,(int)entity1.posZ
						 , 40F, true, true, true, true);
            	if (path !=null) {
            		closestBadGuy=entity1;
            		break;
            	}
            	
            }
        }

        return closestBadGuy;
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
            theFolk.statusText = "Reporting for duty";
            theStage = Stage.ONPATROL;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
