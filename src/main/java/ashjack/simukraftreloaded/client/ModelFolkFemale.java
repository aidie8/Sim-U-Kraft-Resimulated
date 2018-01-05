package ashjack.simukraftreloaded.client;

import ashjack.simukraftreloaded.entity.EntityFolk;
import ashjack.simukraftreloaded.folk.FolkData;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

/// this class is used for male model too, but the boobies aren't rendered! :-)

public class ModelFolkFemale extends ModelBiped
{
    public ModelRenderer rightTit;  //   :-)
    public ModelRenderer leftTit;   //   :-)
    public ModelRenderer pregnant;  // their pregnancy bump

    public ModelFolkFemale()
    {
        super();
        //awesome variable names :-)
        rightTit = new ModelRenderer(this, 19, 19);
        rightTit.addBox(0F, 0F, 0F, 3 , 3, 4);
        rightTit.setRotationPoint(0.5F, 1.7F + 0f, -4.0F);  // left-right +right   /  up/down +is down   /  forward/back  -isfront
        leftTit = new ModelRenderer(this, 19, 19);
        leftTit.addBox(0F, 0F, 0F, 3 , 3, 4);
        leftTit.setRotationPoint(-3.5F, 1.7F + 0f, -4.0F);
        pregnant = new ModelRenderer(this, 18, 20);
        pregnant.addBox(0F, 0F, -1F, 5, 6, 5);
        pregnant.setRotationPoint(-2.5F, 5.0F, -4.0F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float f5)
    {
        EntityFolk ef = (EntityFolk) entity;
        FolkData fd = ef.theData;

        if (fd != null)
        {
            if (fd.age < 18)
            {
                isChild = true;
            }

            //isChild=true; //test
            // fd.pregnancyStage=0.1f;  //test

            if (fd.gender == 1 && !isChild)   //female adult
            {
                rightTit.render(f5);
                leftTit.render(f5);
            }

            if (fd.pregnancyStage > 0.0f)   // and pregnant
            {
                pregnant.render(f5);
            }
        }

        super.render(entity,  f,  f1, f2, f3,  f4,  f5);
        setRotationAngles(f, f1, f2, f3, f4, f5, entity);
    }
}
