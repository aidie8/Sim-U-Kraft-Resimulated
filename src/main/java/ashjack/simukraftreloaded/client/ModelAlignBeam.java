package ashjack.simukraftreloaded.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;



public class ModelAlignBeam extends ModelBase {

        public ModelRenderer theBeam;
        public String renderTexture="";
        
        public ModelAlignBeam() {
                renderTexture = "/mods/ashjacksimukraftreloaded/textures/models/entityBeam.png";
                theBeam= new ModelRenderer(this, 0, 1); // texture offset: 
        theBeam.addBox(-0.4f,0.0f,-0.4f,2550,1,1);  // len, height, width   , len, height, width
        theBeam.setRotationPoint(-0.0f, 0f, -0.0f); 

        }
        
        @Override
        public void render(Entity par1Entity, float par2, float par3, float par4,
                        float par5, float par6, float par7) {
                
                theBeam.render(0.1f); //scale
                
                super.render(par1Entity, par2, par3, par4, par5, par6, par7);
        }

}