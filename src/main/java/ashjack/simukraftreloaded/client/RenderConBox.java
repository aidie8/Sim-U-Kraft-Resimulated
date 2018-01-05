package ashjack.simukraftreloaded.client;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import ashjack.simukraftreloaded.entity.EntityConBox;
import ashjack.simukraftreloaded.proxies.CommonProxy.V3;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class RenderConBox extends Render
{
    private static final ResourceLocation myTexture = new ResourceLocation("ashjacksimukraftreloaded", "textures/models/entityConBox.png");

    EntityConBox entity = null;
    ModelConBox modelBox;
    private int actualCount = -1;
    private boolean displayBox = true;

    public RenderConBox(ModelConBox modelBox)
    {
        super();
        this.modelBox = modelBox;
    }

    @Override
    public void doRender(Entity var1, double x, double y, double z,
                         float boxYaw, float TextYaw)
    {
        entity = (EntityConBox) var1;
        //MC 1.6.2
        //int texture = renderManager.renderEngine.getTexture(modelBox.renderTexture);
        //renderManager.renderEngine.bindTexture(texture);
        //GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        this.renderManager.renderEngine.bindTexture(myTexture);
        x = x + Math.sin(entity.boxYaw / 20) / 10;
        z = z + Math.cos(entity.boxYaw / 20) / 10;
        y = y + Math.sin(entity.boxYaw / 10) / 10;
        GL11.glPushMatrix();
        GL11.glTranslatef((float)x + 0.5f, (float)y + 1.5f, (float)z + 0.5f);
        GL11.glRotatef(entity.boxYaw, 0, 1, 0);
        GL11.glScalef(0.5f, 0.5f, 0.5f);
        modelBox.render(entity, 0f, 0f, 0f, 0f, 0f, 0f); // no idea what the parms do :-)
        GL11.glPopMatrix();

        if (entity.theFolk == null)
        {
            entity.theFolk = EntityConBox.getFolk(new V3(entity.posX, entity.posY, entity.posZ, entity.dimension));
        }

        if (entity.theFolk != null)
        {
            if (displayBox)
            {
                if (entity.theFolk.theBuilding != null)
                {
                    if (entity.theFolk.theBuilding.requirements != null && entity.theFolk.theBuilding.requirements.size() > 0)
                    {
                        if (actualCount == -1)
                        {
                            Iterator it = entity.theFolk.theBuilding.requirements.entrySet().iterator();
                            actualCount = 0;

                            while (it.hasNext())
                            {
                                Map.Entry pairs = (Map.Entry) it.next();

                                if (pairs.getKey() != null)
                                {
                                    actualCount++;
                                }
                            }
                        }

                        float offset = (actualCount * 0.2f) + 2.5f;
                        displayText("Blocks required for " + entity.theFolk.theBuilding.displayNameWithoutPK, 0.02F, (float) x + 1, (float) y
                                    + offset, (float) z, 0xEFFFEF);
                        //offset-=0.2f;

                        try
                        {
                            Iterator it = entity.theFolk.theBuilding.requirements.entrySet()
                                          .iterator();

                            while (it.hasNext())
                            {
                                try
                                {
                                    Map.Entry pairs = (Map.Entry) it.next();

                                    if (pairs.getValue() != null)
                                    {
                                        String st = pairs.getValue().toString();
                                        double stacks = Math.ceil((Double.parseDouble(st)) / 64);
                                        String ss = "";

                                        if ((int) stacks == 0)
                                        {
                                            ss = "less than 1 stack";
                                        }
                                        else if ((int) stacks == 1)
                                        {
                                            ss = "1 stack";
                                        }
                                        else
                                        {
                                            ss = (int) stacks + " stacks";
                                        }

                                        ItemStack is = (ItemStack) pairs.getKey();

                                        if (is.stackSize > 0)
                                        {
                                            String itemName = is.getDisplayName();

                                            if (itemName.toLowerCase().contentEquals("oak wood"))
                                            {
                                                itemName = "Logs";
                                            }

                                            if (itemName.toLowerCase().contains("oak wood planks"))
                                            {
                                                itemName = "Planks";
                                            }

                                            String line = pairs.getValue() + " x " + itemName
                                                          + " (" + ss + ")";
                                            displayText(line, 0.02F, (float) x + 1, (float) y
                                                        + offset - (actualCount * 0.2f), (float) z, 0xCFFFCF);
                                            offset -= 0.2f;
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                    displayBox = false;
                                }
                            }
                        }
                        catch (Exception e)
                        {
                            if (entity != null)
                            {
                                entity.setDead();
                                return;
                            } // random NPE, no pattern yet
                        }
                    }
                    else
                    {
                        if (entity.theFolk.theBuilding.buildingComplete)
                        {
                            displayText("Building complete", 0.02F, (float) x + 1, (float) y + 2, (float) z, 0xAFFFAF);
                        }
                        else
                        {
                            displayText("No further requirements", 0.02F, (float) x + 1, (float) y + 2, (float) z, 0xAFFFAF);
                        }
                    }
                }
            }
        }
    }

    private void displayText(String theString, float scale, float xpos, float ypos, float zpos, int col)
    {
        double dist = entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);

        if (dist > 15)
        {
            return;
        }

        FontRenderer fontrenderer = getFontRendererFromRenderManager();
        GL11.glPushMatrix();
        GL11.glTranslatef(xpos, ypos , zpos);
        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        //GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(-scale, -scale, scale);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthMask(false);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthMask(true);
        fontrenderer.drawString(theString, 0, 0, col);
        GL11.glPopMatrix();
    }


	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		if(entity instanceof EntityConBox) {
			return myTexture;
		} else {
			return null;
		}
	}
}
