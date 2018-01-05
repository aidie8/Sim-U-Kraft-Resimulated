package ashjack.simukraftreloaded.entity;

import ashjack.simukraftreloaded.blocks.BlockMarker;
import ashjack.simukraftreloaded.blocks.functionality.Marker;
import ashjack.simukraftreloaded.proxies.CommonProxy;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;


public class EntityAlignBeam extends Entity {

        public float yaw=0f;
        public String caption="x";
        private Marker theMarker=null;
        
        public EntityAlignBeam(World par1World) {
                super(par1World);
            this.ignoreFrustumCheck=true;
            //stop MC from spawning beams on start up.
            if(!BlockMarker.hasPlaced) {
                this.setDead();
            }
            setSize(0.1f, 100f);
        }
        

        
        @Override
        public boolean canBeCollidedWith() {
                return false;
        }



        @Override
        public boolean canBePushed() {
                return false;
        }



        @Override
        public void moveEntity(double par1, double par3, double par5) {
                this.motionX=0.0;
                this.motionY=0.0;
                this.motionZ=0.0;
                return;  //never move this entity
        }



        @Override
        public void onEntityUpdate() {
                // TODO Auto-generated method stub
                //super.onEntityUpdate();
        }



        @Override
        public void setVelocity(double par1, double par3, double par5) {
                // TODO Auto-generated method stub
                //super.setVelocity(par1, par3, par5);
                super.setVelocity(0.0d, 0.0d,0.0d);
        }



        @Override
        public void onUpdate() {
                if (caption.contentEquals("x")) {

                        theMarker=BlockMarker.getMarker(new V3(this.posX,this.posY,this.posZ,this.dimension));
                        if (theMarker !=null) {
                                caption=theMarker.caption;
                        }
                }
                
                //super.onUpdate();
                if (theMarker !=null) {
                        this.posY=theMarker.y;
                }
                
                
        }
        
        @Override
        protected void entityInit() {
                noClip=true;
        }

        ///this stops it jumping up for no apparent reason
    	public void setPositionAndRotation2(double par1, double par3, double par5, float par7, float par8, int par9) {
    		
    	}

        
        @Override
        protected void readEntityFromNBT(NBTTagCompound var1) {
                // nothing to do here
                
        }

        @Override
        protected void writeEntityToNBT(NBTTagCompound var1) {
                // nothing to do here
                
        }


}