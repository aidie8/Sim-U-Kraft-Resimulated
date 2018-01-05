package ashjack.simukraftreloaded.client.Gui.folk;

import java.util.ArrayList;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import ashjack.simukraftreloaded.core.ModSimukraft;
import ashjack.simukraftreloaded.folk.FolkData;

public class GuiShowEmployees extends GuiScreen
{
    ArrayList folks;
    private int mouseCount = 0;
    private int folkOffset=0;
    private int folksOnAPage=0;
    
    @Override
    public void initGui()
    {
        folks = FolkData.getFolkUnemployed(true);

        showPage();

        super.initGui();
    }
    
    private void showPage() {
    	try
        {
    		buttonList.clear();
            int y = 30;
            boolean more=false;
            int count=0;
            
            if (folkOffset<0) {folkOffset=0;}
            
            for (int f = folkOffset; f < folks.size(); f++)
            {
                buttonList.add(new GuiButton(f, width - 55, y, 50, 20, "Fire"));
                y += 20;

                if ((y + 20) > (height - 50))
                {
                	more=true;
                    break;
                }
                count++;
            }
            
            if (folksOnAPage==0) {
            	folksOnAPage=count+1	;
            }
        
            if (folkOffset>0) {
            	buttonList.add(new GuiButton(1000,0,0,50,20,"<"));
    		} 
            if (more) {
            	buttonList.add(new GuiButton(1001,width-50,0,50,20,">"));
            }
            
            //SimukraftReloaded.log.info("folkOffset="+folkOffset+"   folksOnAPage="+folksOnAPage);
            
        }
    	catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    

    @Override
    public void drawScreen(int i, int j, float f)
    {
        if (mouseCount < 10)
        {
            mouseCount++;
            Mouse.setGrabbed(false);
        }

        drawDefaultBackground();
        drawCenteredString(fontRendererObj, "Employees", width / 2, 17, 0xffffff);
        int y = 35;

        if (folkOffset<0) {folkOffset=0;}
        
        for (int ff = folkOffset; ff < this.folks.size(); ff++)
        {
            FolkData folk = (FolkData) folks.get(ff);
            //name
            drawString(fontRendererObj, folk.name, 2, y, 0xA0ffff);

            //vocation and dimension
            if (folk.employedAt == null)
            {
                drawString(fontRendererObj, "Unemployed", 110, y, 0xff1010);
            }
            else
            {
                String dime = "";

                if (folk.employedAt.theDimension == 0)
                {
                    dime = "Overworld";
                }
                else if (folk.employedAt.theDimension == 1)
                {
                    dime = "End";
                }
                else if (folk.employedAt.theDimension == -1)
                {
                    dime = "Nether";
                }
                else
                {
                    dime = "Dim " + folk.employedAt.theDimension;
                }

                String voc = folk.vocation.toString() + " (" + dime + ")";
                drawString(fontRendererObj, voc, 110, y, 0xA0ffff);
            }

            //their status
            String status = "";

            try
            {
                status = folk.action.toString() + ", " + folk.statusText;
            }
            catch (Exception e) {}

            if (status.contains("at home"))
            {
                status = "Relaxing at home";
            }

            drawString(fontRendererObj, status, 250, y, 0xA0ffff);
            y += 20;

            if ((y + 20) > (height - 50))
            {
                break;
            }
        }

        super.drawScreen(i, j, f);
    }

    @Override
    public void actionPerformed(GuiButton guibutton)
    {
        if (guibutton.id==1000) // page back
        {
        	folkOffset-=folksOnAPage;
        	showPage();
        }
        else if (guibutton.id==1001)  { //page forward
        	folkOffset+=folksOnAPage;
        	showPage();
        }
        else
        {
            FolkData folk = (FolkData) folks.get(guibutton.id);
            folk.selfFire();
            guibutton.enabled = false;
        }
    }

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    @Override
    public void onGuiClosed()
    {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void keyTyped(char c, int i)
    {
        if (i == 1)
        {
            mc.displayGuiScreen(null);
            mc.setIngameFocus();
            return;
        }

        //((GuiButton)buttonList.get(0)).enabled = theGuiTextField1.getText().trim().length() > 0;
    }

    @Override
    public void mouseClicked(int i, int j, int k)
    {
        //theGuiTextField1.mouseClicked(i, j, k);
        super.mouseClicked(i, j, k);
    }
}
