package ashjack.simukraftreloaded.common.jobs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.folk.FolkData.FolkAction;
import ashjack.simukraftreloaded.folk.FolkData.GotoMethod;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;

public class JobLivestockFarmer extends Job implements Serializable
{
    private static final long serialVersionUID = -1177112209988279141L;

    public Vocation vocation = null;
    public FolkData theFolk = null;
    public Stage theStage;
    transient public int runDelay = 1000;
    transient public long timeSinceLastRun = 0;

    private ArrayList<IInventory> farmChests = new ArrayList<IInventory>();
    EntityAnimal redShirt = null; //star trek reference....geddit?! :-)

    public JobLivestockFarmer() {}

    public JobLivestockFarmer(FolkData folk)
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
        IDLE, ARRIVEDATFARM, WAITINGFORMATUREANIMAL, SLAUGHTERING, CANTWORK;
    }

    public void resetJob()
    {
        theStage = Stage.IDLE;
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

        if (theStage == Stage.WAITINGFORMATUREANIMAL)
        {
            runDelay = 20000;
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
        else if (theStage == Stage.WAITINGFORMATUREANIMAL)
        {
            stageWaiting();
        }
        else if (theStage == Stage.SLAUGHTERING)
        {
            stageSlaughtering();
        }
        else if (theStage == Stage.CANTWORK)
        {
            stageCantWork();
        }
    }

    private void stageArrived()
    {
        vocation = theFolk.vocation;
        theStage = Stage.WAITINGFORMATUREANIMAL;
        theFolk.statusText = "Starting work on the farm";
        int count = 0;

        if (vocation == Vocation.CATTLEFARMER)
        {
            count = getAnimalCountInPen(theFolk.employedAt, EntityCow.class);

            if (count < 2)
            {
                spawnAnimals(theFolk.employedAt, "Cow" , 6 - count);
            }
        }
        else if (vocation == Vocation.CHICKENFARMER)
        {
            count = getAnimalCountInPen(theFolk.employedAt, EntityChicken.class);

            if (count < 2)
            {
                spawnAnimals(theFolk.employedAt, "Chicken" , 6 - count);
            }
        }
        else if (vocation == Vocation.PIGFARMER)
        {
            count = getAnimalCountInPen(theFolk.employedAt, EntityPig.class);

            if (count < 2)
            {
                spawnAnimals(theFolk.employedAt, "Pig" , 6 - count);
            }
        }
    }

    private void stageWaiting()
    {
        vocation = theFolk.vocation;
        theFolk.updateLocationFromEntity();
        double dist = theFolk.location.getDistanceTo(theFolk.employedAt);

        if (dist > 10)
        {
            theFolk.beamMeTo(theFolk.employedAt);
        }

        List list = null;

        if (vocation == Vocation.CATTLEFARMER)
        {
            theFolk.statusText = "Feeding the cows";
            list = jobWorld.getEntitiesWithinAABB(EntityCow.class, AxisAlignedBB.getBoundingBox(
                    theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.x + 1.0D, theFolk.employedAt.y + 1.0D,
                    theFolk.employedAt.z + 1.0D).expand(4D, 2D, 4D));//look within the pen
        }
        else if (vocation == Vocation.CHICKENFARMER)
        {
            theFolk.statusText = "Feeding the chickens";
            list = jobWorld.getEntitiesWithinAABB(EntityChicken.class, AxisAlignedBB.getBoundingBox(
                    theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.x + 1.0D, theFolk.employedAt.y + 1.0D,
                    theFolk.employedAt.z + 1.0D).expand(4D, 2D, 4D));
        }
        else if (vocation == Vocation.PIGFARMER)
        {
            theFolk.statusText = "Feeding the pigs";
            list = jobWorld.getEntitiesWithinAABB(EntityPig.class, AxisAlignedBB.getBoundingBox(
                    theFolk.employedAt.x, theFolk.employedAt.y, theFolk.employedAt.z, theFolk.employedAt.x + 1.0D, theFolk.employedAt.y + 1.0D,
                    theFolk.employedAt.z + 1.0D).expand(4D, 2D, 4D));
        }

        // count how many adult animals there are
        int adultCount = 0;
        EntityAnimal animal = null;

        if (list != null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                animal = (EntityAnimal) list.get(i);

                if (!animal.isChild())
                {
                    adultCount++;
                    redShirt = animal;
                }
            }
        }
        else
        {

            return;
        }

        ///more than 2....slaughter them!
        if (adultCount > 2)
        {
            //theFolk.gotoXYZ(new V3(redShirt.posX,redShirt.posY,redShirt.posZ));
            theStage = Stage.SLAUGHTERING;
            // less than 2, procreate!
        }
        else if (adultCount <= 2 && list.size() < 10)
        {
            EntityAnimal a1 = null;
            EntityAnimal a2 = null;

            for (int i = 0; i < list.size(); i++)
            {
                animal = (EntityAnimal) list.get(i);

                if (!animal.isChild())
                {
                    if (a1 == null)
                    {
                        a1 = animal;
                    }
                    else if (a2 == null)
                    {
                        a2 = animal;
                    }
                }
            }

            if (a1 != null && a2 != null)
            {
                a2.setPathToEntity(jobWorld.getPathEntityToEntity(a2, a1, 20f, true, true, true, true));
                this.procreate(a1, new V3(a1.posX, a1.posY, a1.posZ, theFolk.location.theDimension));
            }

            theStage = Stage.WAITINGFORMATUREANIMAL;
            theFolk.statusText = "Raking the manure";
        }
    }

    private void stageSlaughtering()
    {
        Random rand = new Random();
        theFolk.statusText = "Off with their head!";

        //mc.theWorld.playSound(redShirt.posX,redShirt.posY,redShirt.posZ, redShirt. getHurtSound(), 1f, 1f);
        if (theFolk.theEntity != null)
        {
            theFolk.theEntity.faceEntity(redShirt, 1f, 1f);
        }

        redShirt.setHealth(0);
        theFolk.gotoXYZ(theFolk.employedAt, null);
        int quant = 0;
        farmChests = inventoriesFindClosest(theFolk.employedAt, 5);
        boolean ok=true;
        
        if (vocation == Vocation.CATTLEFARMER)
        {
            quant = rand.nextInt(2) + 1;
            ok=inventoriesPut(farmChests, new ItemStack(Items.beef, quant,0), true);
            inventoriesPut(farmChests, new ItemStack(Items.leather, 1,0), false);
        }
        else if (vocation == Vocation.PIGFARMER)
        {
            quant = rand.nextInt(2) + 1;
            ok=inventoriesPut(farmChests, new ItemStack(Items.porkchop, quant,0), true);
        }
        else if (vocation == Vocation.CHICKENFARMER)
        {
            quant = rand.nextInt(2) + 1;
            ok=inventoriesPut(farmChests, new ItemStack(Items.chicken, quant,0), true);
            inventoriesPut(farmChests, new ItemStack(Items.feather, 1,0), false);
        }

        if (!ok) {
        	theStage=Stage.CANTWORK;
        	SimukraftReloaded.sendChat(theFolk.name+"'s livestock farm chests are full!");
        	return;
        }
        
        SimukraftReloaded.states.credits -= (0.02f * quant);
        theStage = Stage.WAITINGFORMATUREANIMAL;
    }

    private void stageCantWork()
    {
        theFolk.statusText = "Can't work, the chests are full of meat";
    }

    private void procreate(EntityAnimal parentAnimal, V3 pos)
    {
        EntityAgeable babyAnimal = parentAnimal.createChild(parentAnimal);
        Random rand = new Random();

        if (babyAnimal != null)
        {
            parentAnimal.createChild(babyAnimal);
            babyAnimal.setGrowingAge(-3000);
            babyAnimal.setLocationAndAngles(parentAnimal.posX, parentAnimal.posY, parentAnimal.posZ, parentAnimal.rotationYaw, parentAnimal.rotationPitch);

            for (int var3 = 0; var3 < 7; ++var3)
            {
                double d = rand.nextGaussian() * 0.02D;
                double d1 = rand.nextGaussian() * 0.02D;
                double d2 = rand.nextGaussian() * 0.02D;
                mc.theWorld.spawnParticle("heart", (pos.x + (double)(rand.nextFloat() * 1 * 2.0F)) - (double)1, pos.y + 0.5D + (double)(rand.nextFloat() * 1), (pos.z + (double)(rand.nextFloat() * 1 * 2.0F)) - (double)1, d, d1, d2);
            }

            parentAnimal.worldObj.spawnEntityInWorld(babyAnimal);
        }
    }


    private void spawnAnimals(V3 controlBox, String animal, int count)
    {
        EntityAnimal newAnimal = null;

        for (int c = 1; c <= count; c++)
        {
            if (animal.contentEquals("Pig"))
            {
                newAnimal = new EntityPig(jobWorld);
            }
            else if (animal.contentEquals("Cow"))
            {
                newAnimal = new EntityCow(jobWorld);
            }
            else if (animal.contentEquals("Chicken"))
            {
                newAnimal = new EntityChicken(jobWorld);
            }

            newAnimal.setLocationAndAngles(controlBox.x, controlBox.y + 1, controlBox.z, 0f, 0f);

            if (!jobWorld.isRemote)
            {
                jobWorld.spawnEntityInWorld(newAnimal);
            }

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
            theFolk.statusText = "Arrived at the farm";
            theStage = Stage.ARRIVEDATFARM;
        }
        else
        {
            theFolk.gotoXYZ(theFolk.employedAt, null);
        }
    }
}
