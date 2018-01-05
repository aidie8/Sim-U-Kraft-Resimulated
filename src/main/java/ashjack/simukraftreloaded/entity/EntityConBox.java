package ashjack.simukraftreloaded.entity;

import java.util.ArrayList;
import java.util.Random;

import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.folk.FolkData;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityConBox extends Entity
{
    /** used to rotate the box to make it look cool :-) */
    public float boxYaw = 0f;

    public int textColor = 0xAFFFAF;

    /** reference to the folk -> building being built, as we need the requirements hashmap */
    public FolkData theFolk = null;

    private Long lastCheck = 0l;
    private EntityPlayer closestPlayer;

    /** creates the new Con box and sets up the default values */
    public EntityConBox(World par1World)
    {
        super(par1World);
        this.noClip = true;
        this.ignoreFrustumCheck = true;

        if (!ModSimukraft.proxy.ranStartup)
        {
            SimukraftReloaded.log.info("EntityConBox: Killed system spawned ConBox");
            this.setDead();
        }
    }

    @Override
    public void onUpdate()
    {
        //check to see if the constructor box is still there
        if (System.currentTimeMillis() - lastCheck > 10000)
        {
            if (theFolk != null)
            {
                if (theFolk.theBuilding == null)
                {
                    SimukraftReloaded.log.info("EntityConBox: Removing conBox as building is done");
                    spawnExplosionParticle(this);
                    this.setDead();
                }
            }
            
            ArrayList<V3> conblocks= Job.findClosestBlocks(new V3(this.posX,this.posY,this.posZ,this.dimension)
            	, SimukraftReloadedBlocks.buildingConstructor, 5);
            if (conblocks.size()<1) {
            	this.setDead();
            }
            
            lastCheck = System.currentTimeMillis();
        }

        boxYaw += 1f;
        super.onUpdate();
    }

    //** used by the custom render to find the folk that is the builder - very bodgy */
    public static FolkData getFolk(V3 where)
    {
        V3 con = Job.findClosestBlockType(where, SimukraftReloadedBlocks.buildingConstructor, 6, false);
        FolkData ret = null;

        for (int f = 0; f < SimukraftReloaded.theFolks.size(); f++)
        {
            FolkData fd = SimukraftReloaded.theFolks.get(f);

            if (fd.employedAt != null)
            {
                if (fd.employedAt.isSameCoordsAs(where, true, false))
                {
                    SimukraftReloaded.log.info("EntityConBox: found folk " + fd.name);
                    ret = fd;
                    break;
                }
            }
        }

        return ret;
    }

    private void spawnExplosionParticle(Entity ent)
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

    @Override
    protected void entityInit()
    {
    }

    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return null;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return null;
    }

    public boolean canBePushed()
    {
        return false;
    }
    public boolean canBeCollidedWith()
    {
        return false;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound var1)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound var1)
    {
    }
}
