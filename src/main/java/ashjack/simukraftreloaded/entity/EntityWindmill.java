package ashjack.simukraftreloaded.entity;

import java.util.Random;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloaded;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityWindmill extends Entity {

	public float sailRotation=0.0f;
	private float sailSpeed=0.00f;
	private float sailSpeedModifer=0.0f;
	
	public EntityWindmill(World par1World) {
		super(par1World);
		setSize(5,8);
		this.ignoreFrustumCheck=true;
		noClip=true;
		sailSpeedModifer=new Random().nextFloat()/100;
	}


	@Override
	public void setDead() {
		//Minecraft.getMinecraft().sndManager.stopEntitySound(this);
		SimukraftReloaded.log.info("EntityWindmill: setDead() called");
		super.setDead();
	}



	@Override
	protected void entityInit() {
		
	}

	@Override
	public void onUpdate() {
		//this.kill();
		//Minecraft.getMinecraft().sndManager.playEntitySound("ashjacksimukraftreloaded:windmill", this,0.6f,1.0f,false);
		
		if (this.worldObj.isRaining()) {
			if (sailSpeed <0.1f) {
				sailSpeed+=0.001f;
			} else if (sailSpeed >0.1f) {
				sailSpeed-=0.001f;
			}

		} else {
			//// hold on 0.02 in clear weather
			if (sailSpeed <0.02f) {
				sailSpeed+=0.0001f;
			} else if (sailSpeed >0.02f) {
				sailSpeed-=0.001f;
			}
		}
		sailRotation+=(sailSpeed+sailSpeedModifer);
		super.onUpdate();
	}

	@Override
	public void onEntityUpdate() {
		//super.setVelocity(0, 0, 0);
	}
	
	
	public boolean isEntityInsideOpaqueBlock() {
		return false;
	}
	
	
	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
		
	}
	
	
    public AxisAlignedBB getCollisionBox(Entity par1Entity)
    {
        return par1Entity.boundingBox;
    }

    public AxisAlignedBB getBoundingBox()
    {
        return this.boundingBox;
    }

    public boolean canBeCollidedWith()
    {
    	return !this.isDead;
    }

    @Override
	public boolean canBePushed() {
		return false;
	}
    
   
	@Override
	protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
       // super.readFromNBT(nbttagcompound);
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        //super.writeToNBT(nbttagcompound);

	}
	
}
