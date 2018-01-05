package ashjack.simukraftreloaded.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.entity.EntityFolk;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderFolk extends RenderBiped
{
    //constructor
    public RenderFolk(ModelBiped modelbase)
    {
        super(modelbase, 1f);
        //this.mainModel=modelbase;
        //renderPassModel=modelbase;
    }

    @Override     // new in 1.6.2
    protected ResourceLocation getEntityTexture(Entity entity) {
        if (entity instanceof EntityFolk) {
        	EntityFolk theFolk = (EntityFolk)entity;
        	ResourceLocation myTexture = new ResourceLocation("ashjacksimukraftreloaded", "skins/" + theFolk.getTexture());
        	return myTexture;
        } else {
        	return null;
        }
    }

    @Override
    public void doRender(Entity par1Entity, double par2, double par4,
                         double par6, float par8, float par9)
    {
        super.doRender(par1Entity, par2, par4, par6, par8, par9);
        doRenderFolk((EntityFolk)par1Entity, par2, par4, par6, par8, par9);
        //doRenderLiving((EntityLiving) par1Entity, par2, par4, par6, par8, par9);
    }

    public void doRenderLiving(EntityLiving entityliving, double d, double d1,
                               double d2, float f, float f1)
    {
        double d3 = d1 - (double) entityliving.yOffset;
        doRenderLiving(entityliving, d, d3, d2, f, f1);
        doRenderFolk((EntityFolk) entityliving, d, d3, d2, f, f1);
    }

    private void doRenderFolk(EntityFolk entityFolk, double d,
                              double d1, double d2, float f, float f1)
    {
        float f2 = 1.6F;
        float f3 = 0.01666667F * f2;
        float f6 = 0.2F;

        if (entityFolk.theData != null)
        {
            if (entityFolk != null)
            {
                double dist = entityFolk.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);

                if (dist < 40)
                {
                    if (entityFolk.theData.age < 18)
                    {
                        displayText(entityFolk.theData.name + " (" + entityFolk.theData.age + ")", 0.03F, 0xFFFFFFFF, (float) d, (float) d1 + f3 + f6 - 0.4f, (float) d2, entityFolk);
                        displayText(entityFolk.theData.statusText, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 - 0.7f, (float) d2, entityFolk);
                        displayText(entityFolk.theData.status4, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 - 1.0f, (float) d2, entityFolk);
                    }
                    else
                    {
                        if (dist >= 4)
                        {
                            displayText(entityFolk.theData.name + " (" + entityFolk.theData.age + ")", 0.03F, 0xFFFFFFFF, (float) d, (float) d1 + f3 + f6, (float) d2, entityFolk);
                            displayText(entityFolk.theData.statusText, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 - 0.3f, (float) d2, entityFolk);
                        }
                        else
                        {
                            displayText(entityFolk.theData.name + " (" + entityFolk.theData.age + ")", 0.03F, 0xFFFFFFFF, (float) d, (float) d1 + f3 + f6 + 1.5f, (float) d2, entityFolk);
                            displayText(entityFolk.theData.statusText, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 + 1.2f, (float) d2, entityFolk);
                            displayText(entityFolk.theData.status1, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 + 0.9f, (float) d2, entityFolk);
                            displayText(entityFolk.theData.status2, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 + 0.6f, (float) d2, entityFolk);
                            displayText(entityFolk.theData.status3, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 + 0.3f, (float) d2, entityFolk);
                            displayText(entityFolk.theData.status4, 0.02F, 0xFFFFFF00, (float) d, (float) d1 + f3 + f6 + 0.0f, (float) d2, entityFolk);
                        }
                    }
                }
            }
        }
    }

    private void displayText(String s, float f, int i, float f1, float f2,
                             float f3, EntityFolk entitybuilder)
    {
        FontRenderer fontrenderer = getFontRendererFromRenderManager();
        GL11.glPushMatrix();
        GL11.glTranslatef(f1, f2 + 2.3F, f3);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-f, -f, f);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        Tessellator tessellator = Tessellator.instance;
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        tessellator.startDrawingQuads();
        int j = fontrenderer.getStringWidth(s) / 2;
        tessellator.setColorRGBA_F(0.0F, 0.0F, 0.0F, 0.25F);
        tessellator.addVertex(-j - 1, -1D, 0.0D);
        tessellator.addVertex(-j - 1, 8D, 0.0D);
        tessellator.addVertex(j + 1, 8D, 0.0D);
        tessellator.addVertex(j + 1, -1D, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, i);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, i);
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
    }
}
