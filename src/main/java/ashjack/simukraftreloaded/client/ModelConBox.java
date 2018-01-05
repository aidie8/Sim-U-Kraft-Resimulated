package ashjack.simukraftreloaded.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelConBox extends ModelBase
{
    public ModelRenderer theConBox;
    public String renderTexture = "";

    public ModelConBox()
    {
        //renderTexture = "/mods/ashjacksimukraftreloaded/textures/models/entityConBox.png";
        theConBox = new ModelRenderer(this, 0, 0); // texture offset:
        theConBox.addBox(-16f, 0.0f, -16f, 16, 16, 16); // len, height, width   , len, height, width
        theConBox.setRotationPoint(8f, 0f, 8f);
    }

    @Override
    public void render(Entity par1Entity, float par2, float par3, float par4,
                       float par5, float par6, float par7)
    {
        theConBox.render(1 / 16f); //scale
        super.render(par1Entity, par2, par3, par4, par5, par6, par7);
    }
}
