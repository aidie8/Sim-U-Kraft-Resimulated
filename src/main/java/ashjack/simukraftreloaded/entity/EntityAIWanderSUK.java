package ashjack.simukraftreloaded.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;

//NOT SURE IF THIS IS USED ANY MORE, was alternative to Vanilla's EntityAIWander class

public class EntityAIWanderSUK extends EntityAIWander
{
    private EntityCreature theFolk;

    public EntityAIWanderSUK(EntityCreature folk, float par2)
    {
        super(folk, par2);
        this.theFolk = folk;
    }

    @Override
    public void startExecuting()
    {
        if (!theFolk.isDead)
        {
            EntityFolk actualFolk = (EntityFolk) theFolk;

            if (actualFolk != null)
            {
                if (actualFolk.theData != null)
                {
                    if (!actualFolk.theData.stayPut)
                    {
                        try
                        {
                            super.startExecuting();
                        }
                        catch (Exception e) {}
                    }
                }
            }
        }
    }

    @Override
    public boolean shouldExecute()
    {
        if (!theFolk.isDead)
        {
            EntityFolk actualFolk = (EntityFolk) theFolk;

            if (actualFolk != null)
            {
                if (actualFolk.theData != null)
                {
                    if (actualFolk.theData.stayPut)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean continueExecuting()
    {
        if (!theFolk.isDead)
        {
            EntityFolk actualFolk = (EntityFolk) theFolk;

            if (actualFolk != null)
            {
                if (actualFolk.theData != null)
                {
                    if (actualFolk.theData.stayPut)
                    {
                        return false;
                    }
                    else
                    {
                        return true;
                    }
                }
            }
        }

        return true;
    }
}
