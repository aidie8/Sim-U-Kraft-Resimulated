package ashjack.simukraftreloaded.client;

import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ashjack.simukraftreloaded.blocks.functionality.TileEntityWindmill;
import ashjack.simukraftreloaded.common.jobs.Job;
import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedBlocks;
import ashjack.simukraftreloaded.core.registry.SimukraftReloadedItems;
import ashjack.simukraftreloaded.entity.EntityWindmill;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;

public class RenderWindmill extends Render {

	 private static final ResourceLocation[] myTextures = new ResourceLocation[16];
	    EntityWindmill entity = null;
	    ModelWindmill modelWindmill;
	    
	    public RenderWindmill(ModelWindmill modelWindmill)
	    {
	        super();
	        this.modelWindmill = modelWindmill;
	        for(int c=0;c<16;c++) {
	        	myTextures[c]=new ResourceLocation("ashjacksimukraftreloaded", "textures/models/entityWindmill"+c+".png");
	        }
	    }
	    
	    @Override
	    public void doRender(Entity theEntity, double x, double y, double z,
	                         float yaw, float pitch)
	    {
	        entity = (EntityWindmill) theEntity;
	        if (this.entity==null) {return;}
	        
	        int meta=-1;
	        
	        V3 v3=Job.findClosestBlockType(new V3((int)entity.posX,(int)entity.posY-1,(int)entity.posZ), SimukraftReloadedItems.windmill, 5);
	        if (v3 !=null) {
	        	TileEntityWindmill teWindmill = (TileEntityWindmill)entity.worldObj.getTileEntity(v3.x.intValue(),v3.y.intValue(),v3.z.intValue());
	        	if (teWindmill !=null) {
	        		//meta=teWindmill.meta;
	        	}
	        }
	        if (meta==-1) {meta=0;} // not loaded yet, so just default to white
	        
	        this.renderManager.renderEngine.bindTexture(myTextures[meta]);			
			
			 GL11.glPushMatrix();			
			 GL11.glTranslatef((float)x, (float)y, (float)z);
			 GL11.glScalef(0.0666f,0.0666f,0.0666f);
			 GL11.glRotatef(180, 1.0f, 0f,0);        // angle, x, y, z
			 GL11.glRotatef(yaw, 0,1.0f,0f);
			 		 
		     
			 modelWindmill.Vane1rod.rotateAngleZ=entity.sailRotation;
			 modelWindmill.Vane2rod.rotateAngleZ=entity.sailRotation+1.570796F;
			 modelWindmill.Vane3rod.rotateAngleZ=entity.sailRotation+3.141593F;
			 modelWindmill.Vane4rod.rotateAngleZ=entity.sailRotation+4.712389F;
			 modelWindmill.Vane1main.rotateAngleZ=entity.sailRotation;
			 modelWindmill.Vane2main.rotateAngleZ=entity.sailRotation+1.570796F;
			 modelWindmill.Vane3main.rotateAngleZ=entity.sailRotation+3.141593F;
			 modelWindmill.Vane4main.rotateAngleZ=entity.sailRotation+4.712389F;
			 modelWindmill.WindmillAxle.rotateAngleZ=entity.sailRotation;
			 
			modelWindmill.render(entity, 0f,0f,0f,0f,0f,0f);
			GL11.glPopMatrix();
	        

	    }

		@Override
		protected ResourceLocation getEntityTexture(Entity entity) {
			return null;  //use bind above instead?
		}
}
