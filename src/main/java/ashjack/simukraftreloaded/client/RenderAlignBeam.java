package ashjack.simukraftreloaded.client;


import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import ashjack.simukraftreloaded.entity.EntityAlignBeam;


public class RenderAlignBeam extends Render {
        private static final ResourceLocation myTexture = new ResourceLocation("ashjacksimukraftreloaded","textures/models/entityBeam.png");
        EntityAlignBeam entity = null;
        ModelAlignBeam modelBeam;
        
        public RenderAlignBeam(ModelAlignBeam modelBeam) {
                super();
                this.modelBeam=modelBeam;
        }

        @Override
        public void doRender(Entity theEntity, double x, double y, double z,
                        float yaw, float pitch) {
                entity=(EntityAlignBeam) theEntity;
                
                //MC 1.6.2
                //int texture = renderManager.renderEngine.getTexture(modelBeam.renderTexture);
        //renderManager.renderEngine.bindTexture(texture);
                //GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
                this.renderManager.renderEngine.bindTexture(myTexture);
                
        Float s=1 - ( (float)Math.sin(System.currentTimeMillis()/50)/20); //pulsing beam
        
        GL11.glPushMatrix();
        //GL11.glDisable(2896); //disable lighting
        GL11.glTranslatef((float)x+0.51f, (float)y, (float)z+0.51f);
        GL11.glRotatef(yaw, 0, 1, 0);
        GL11.glScalef(s,s,s);
                modelBeam.render(entity, 0f,0f,0f,0f,0f,0f); // no idea what the parms do :-)
                GL11.glPopMatrix();
                
                
                if (!entity.caption.contentEquals("") && yaw==0) {
                        displayText(entity.caption, 0.03F, 0xFFFFFFFF, (float) x, (float) y+0.5f , (float) z, entity);
                }
        }

        private void displayText(String s, float f, int i, float f1, float f2,
                        float f3, Entity theBeamEntity) {
                FontRenderer fontrenderer = getFontRendererFromRenderManager();
                GL11.glPushMatrix();
                GL11.glTranslatef(f1, f2 , f3);
                GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                GL11.glScalef(-f, -f, f);
                GL11.glDisable(2896 /* GL_LIGHTING */);
                GL11.glDepthMask(false);
                GL11.glDisable(2929 /* GL_DEPTH_TEST */);
                GL11.glEnable(3042 /* GL_BLEND */);
                GL11.glBlendFunc(770, 771);
                Tessellator tessellator = Tessellator.instance;
                GL11.glDisable(3553 /* GL_TEXTURE_2D */);
                tessellator.startDrawingQuads();
                int j = fontrenderer.getStringWidth(s) / 2;
                tessellator.setColorRGBA_F(1.0F, 0.0F, 0.0F, 0.25F); //red and partly transparent background color
                tessellator.addVertex(-j - 1, -1D, 0.0D);
                tessellator.addVertex(-j - 1, 8D, 0.0D);
                tessellator.addVertex(j + 1, 8D, 0.0D);
                tessellator.addVertex(j + 1, -1D, 0.0D);
                tessellator.draw();
                GL11.glEnable(3553 /* GL_TEXTURE_2D */);
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, i);
                GL11.glEnable(2929 /* GL_DEPTH_TEST */);
                GL11.glDepthMask(true);
                fontrenderer.drawString(s, -fontrenderer.getStringWidth(s) / 2, 0, -1);
                GL11.glEnable(2896 /* GL_LIGHTING */);
                GL11.glDisable(3042 /* GL_BLEND */);
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glPopMatrix();
        }


		@Override
		protected ResourceLocation getEntityTexture(Entity entity) {
			if (entity instanceof EntityAlignBeam) {
				return myTexture;
			} else {
				return null;
			}
		}


}