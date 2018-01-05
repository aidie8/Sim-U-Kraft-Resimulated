
package ashjack.simukraftreloaded.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelWindmill extends ModelBase
{
    public ModelRenderer Vane1rod;
    public ModelRenderer Vane2rod;
    public ModelRenderer Vane3rod;
    public ModelRenderer Vane4rod;
    public ModelRenderer Vane1main;
    public ModelRenderer Vane2main;
    public ModelRenderer Vane3main;
    public ModelRenderer Vane4main;
    public ModelRenderer WindmillAxle;
    ModelRenderer WindmillTop;
    ModelRenderer WindmillMid;
    ModelRenderer WindmillBase;
    public String renderTexture;
    
  public ModelWindmill()
  {
	renderTexture = "/mods/SatscapeLSD/textures/models/entityWindmill.png";  
    textureWidth = 512;
    textureHeight = 512;
    
    Vane1rod = new ModelRenderer(this, 200, 500);
    Vane1rod.addBox(-96F, -1F, -1F, 96, 2, 2);
    Vane1rod.setRotationPoint(0F, -105F, -43F);
    Vane1rod.setTextureSize(512, 512);
    Vane1rod.mirror = true;
    setRotation(Vane1rod, 0F, 0F, 0F);
    Vane2rod = new ModelRenderer(this, 200, 500);
    Vane2rod.addBox(-96F, -1F, -1F, 96, 2, 2);
    Vane2rod.setRotationPoint(0F, -105F, -43F);
    Vane2rod.setTextureSize(512, 512);
    Vane2rod.mirror = true;
    setRotation(Vane2rod, 0F, 0F, 1.570796F);
    Vane3rod = new ModelRenderer(this, 200, 500);
    Vane3rod.addBox(-96F, -1F, -1F, 96, 2, 2);
    Vane3rod.setRotationPoint(0F, -105F, -43F);
    Vane3rod.setTextureSize(512, 512);
    Vane3rod.mirror = true;
    setRotation(Vane3rod, 0F, 0F, 3.141593F);
    Vane4rod = new ModelRenderer(this, 200, 500);
    Vane4rod.addBox(-96F, -1F, -1F, 96, 2, 2);
    Vane4rod.setRotationPoint(0F, -105F, -43F);
    Vane4rod.setTextureSize(512, 512);
    Vane4rod.mirror = true;
    setRotation(Vane4rod, 0F, 0F, -1.570796F);
    Vane1main = new ModelRenderer(this, 0, 490);
    Vane1main.addBox(-95F, -20F, -1F, 93, 19, 1);
    Vane1main.setRotationPoint(0F, -105F, -43F);
    Vane1main.setTextureSize(512, 512);
    Vane1main.mirror = true;
    setRotation(Vane1main, 0.0872665F, 0F, 0F);
    Vane2main = new ModelRenderer(this, 0, 490);
    Vane2main.addBox(-95F, -20F, -1F, 93, 19, 1);
    Vane2main.setRotationPoint(0F, -105F, -43F);
    Vane2main.setTextureSize(512, 512);
    Vane2main.mirror = true;
    setRotation(Vane2main, 0.0872665F, 0F, 1.570796F);
    Vane3main = new ModelRenderer(this, 0, 490);
    Vane3main.addBox(-95F, -20F, -1F, 93, 19, 1);
    Vane3main.setRotationPoint(0F, -105F, -43F);
    Vane3main.setTextureSize(512, 512);
    Vane3main.mirror = true;
    setRotation(Vane3main, 0.0872665F, 0F, 3.141593F);
    Vane4main = new ModelRenderer(this, 0, 490);
    Vane4main.addBox(-95F, -20F, -1F, 93, 19, 1);
    Vane4main.setRotationPoint(0F, -105F, -43F);
    Vane4main.setTextureSize(512, 512);
    Vane4main.mirror = true;
    setRotation(Vane4main, 0.0872665F, 0F, -1.570796F);
    WindmillAxle = new ModelRenderer(this, 0, 400);
    WindmillAxle.addBox(-8F, -8F, -1F, 16, 16, 26);
    WindmillAxle.setRotationPoint(0F, -105F, -41F);
    WindmillAxle.setTextureSize(512, 512);
    WindmillAxle.mirror = true;
    setRotation(WindmillAxle, 0F, 0F, 0F);
    WindmillTop = new ModelRenderer(this, 0, 300);
    WindmillTop.addBox(-16F, 0F, -16F, 32, 32, 32);
    WindmillTop.setRotationPoint(0F, -120F, 0F);
    WindmillTop.setTextureSize(512, 512);
    WindmillTop.mirror = true;
    setRotation(WindmillTop, 0F, 0F, 0F);
    WindmillMid = new ModelRenderer(this, 0, 140);
    WindmillMid.addBox(-32F, 0F, -32F, 64, 64, 64);
    WindmillMid.setRotationPoint(0F, -88F, 0F);
    WindmillMid.setTextureSize(512, 512);
    WindmillMid.mirror = true;
    setRotation(WindmillMid, 0F, 0F, 0F);
    WindmillBase = new ModelRenderer(this, 0, 0);
    WindmillBase.addBox(-40F, 0F, -40F, 80, 48, 80);
    WindmillBase.setRotationPoint(0F, -24.06667F, 0F);
    WindmillBase.setTextureSize(512, 512);
    WindmillBase.mirror = true;
    setRotation(WindmillBase, 0F, 0F, 0F);
  }
  
  public void render(Entity par1Entity, float par2, float par3, float par4,
			float par5, float par6, float par7) {
    
    Vane1rod.render(1.0f);
    Vane2rod.render(1.0f);
    Vane3rod.render(1.0f);
    Vane4rod.render(1.0f);
    Vane1main.render(1.0f);
    Vane2main.render(1.0f);
    Vane3main.render(1.0f);
    Vane4main.render(1.0f);
    WindmillAxle.render(1.0f);
    WindmillTop.render(1.0f);
    WindmillMid.render(1.0f);
    WindmillBase.render(1.0f);
    super.render(par1Entity, par2, par3, par4, par5, par6, par7);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
  
 

}
